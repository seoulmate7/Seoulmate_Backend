package com.nexus.seoulmate.aws.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.nexus.seoulmate.aws.entity.Uuid;
import com.nexus.seoulmate.aws.entity.UuidRepository;
import com.nexus.seoulmate.aws.config.AmazonConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.net.URL;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class AmazonS3Manager {

    private final AmazonS3 s3;
    private final AmazonConfig amazonConfig;
    private final UuidRepository uuidRepository;

    public S3UploadResult upload(MultipartFile file, S3Folder folder) {
        try {
            String original = file.getOriginalFilename();
            String ext = extractExtension(original);
            String uuid = UUID.randomUUID().toString();
            String key = folder.getPrefix() + uuid + ext;

            ObjectMetadata meta = new ObjectMetadata();
            meta.setContentLength(file.getSize());
            meta.setContentType(file.getContentType() != null ? file.getContentType() : "application/octet-stream");

            try (InputStream is = file.getInputStream()) {
                PutObjectRequest put = new PutObjectRequest(amazonConfig.getBucket(), key, is, meta);
                s3.putObject(put);
            }

            URL url = s3.getUrl(amazonConfig.getBucket(), key);

            uuidRepository.save(Uuid.builder().uuid(uuid).build());

            return S3UploadResult.builder()
                    .key(key)
                    .url(url.toString())
                    .originalName(original)
                    .size(file.getSize())
                    .contentType(meta.getContentType())
                    .build();

        } catch (Exception e) {
            log.error("S3 upload failed", e);
            throw new RuntimeException("S3 upload failed", e);
        }
    }

    public void delete(String key) {
        s3.deleteObject(amazonConfig.getBucket(), key);
    }

    public String getObjectUrl(String key) {
        return s3.getUrl(amazonConfig.getBucket(), key).toString();
    }

    private String extractExtension(String filename) {
        if (filename == null) return "";
        int dot = filename.lastIndexOf('.');
        return (dot >= 0) ? filename.substring(dot) : "";
    }

}


package com.nexus.seoulmate.aws.s3;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.nexus.seoulmate.aws.entity.Uuid;
import com.nexus.seoulmate.aws.entity.UuidRepository;
import com.nexus.seoulmate.aws.config.AmazonConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class AmazonS3Manager {

    private final AmazonS3 amazonS3;
    private final AmazonConfig amazonConfig;
    private final UuidRepository uuidRepository;

    public String generatePresignedUrl(String originalFileName, String contentType) {

        String uuid = UUID.randomUUID().toString();

        String extension = " ";
        int dot = originalFileName.indexOf('.');
        if (dot > 0) {
            extension = originalFileName.substring(dot);
        }

        String keyName = uuid + extension;

        Date expiration = new Date(System.currentTimeMillis() + 1000 * 60 * 5);

        GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(amazonConfig.getBucket(), keyName)
                .withMethod(HttpMethod.PUT)
                .withExpiration(expiration)
                .withContentType(contentType);

        URL url = amazonS3.generatePresignedUrl(request);

        uuidRepository.save(Uuid.builder().uuid(uuid).build());
        return url.toString();
    }

}


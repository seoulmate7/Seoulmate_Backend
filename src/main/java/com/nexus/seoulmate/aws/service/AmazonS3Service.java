package com.nexus.seoulmate.aws.service;

import com.nexus.seoulmate.aws.s3.AmazonS3Manager;
import com.nexus.seoulmate.aws.s3.S3Folder;
import com.nexus.seoulmate.aws.s3.S3UploadResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class AmazonS3Service {

    private final AmazonS3Manager s3Manager;

    public S3UploadResult uploadProfile(MultipartFile file) {
        return s3Manager.upload(file, S3Folder.PROFILE);
    }

    public S3UploadResult uploadCertificate(MultipartFile file) {
        return s3Manager.upload(file, S3Folder.CERTIFICATE);
    }

    public S3UploadResult uploadMeeting(MultipartFile file) {
        return s3Manager.upload(file, S3Folder.MEETING);
    }

    public void deleteObject(String key) {
        s3Manager.delete(key);
    }
}

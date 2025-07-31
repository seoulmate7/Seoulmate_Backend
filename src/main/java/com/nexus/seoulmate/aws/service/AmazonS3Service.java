package com.nexus.seoulmate.aws.service;

import com.nexus.seoulmate.aws.s3.AmazonS3Manager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AmazonS3Service {

    private final AmazonS3Manager amazonS3Manager;

    public String createPresignedUrl(String originalFileName, String contentType) {
        return amazonS3Manager.generatePresignedUrl(originalFileName, contentType);
    }
}

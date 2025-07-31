package com.nexus.seoulmate.aws.controller;

import com.nexus.seoulmate.aws.service.AmazonS3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/s3")
public class AmazonS3Controller {

    private final AmazonS3Service amazonS3Service;

    @PostMapping("/presigned-url")
    public ResponseEntity<String> generatePresignedUrl(
            @RequestParam String fileName,
            @RequestParam String contentType
    ) {
        String url = amazonS3Service.createPresignedUrl(fileName, contentType);
        return ResponseEntity.ok(url);
    }
}

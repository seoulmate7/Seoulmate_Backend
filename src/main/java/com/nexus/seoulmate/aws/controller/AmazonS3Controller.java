package com.nexus.seoulmate.aws.controller;

import com.nexus.seoulmate.aws.s3.S3UploadResult;
import com.nexus.seoulmate.aws.service.AmazonS3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/s3")
public class AmazonS3Controller {

    private final AmazonS3Service amazonS3Service;

    @PostMapping(value = "/upload/profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<S3UploadResult> uploadProfile(@RequestPart("file") MultipartFile file) {
        return ResponseEntity.ok(amazonS3Service.uploadProfile(file));
    }

    @PostMapping(value = "/upload/certificate", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<S3UploadResult> uploadCertificate(@RequestPart("file") MultipartFile file) {
        return ResponseEntity.ok(amazonS3Service.uploadCertificate(file));
    }

    @PostMapping(value = "/upload/meeting", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<S3UploadResult> uploadMeeting(@RequestPart("file") MultipartFile file) {
        return ResponseEntity.ok(amazonS3Service.uploadMeeting(file));
    }

    @DeleteMapping("/object")
    public ResponseEntity<Void> deleteObject(@RequestParam String key) {
        amazonS3Service.deleteObject(key);
        return ResponseEntity.noContent().build();
    }
}

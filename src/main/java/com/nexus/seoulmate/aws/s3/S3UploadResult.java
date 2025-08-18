package com.nexus.seoulmate.aws.s3;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class S3UploadResult {
    private final String key;
    private final String url;
    private final String originalName;
    private final long size;
    private final String contentType;
}

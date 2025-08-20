package com.nexus.seoulmate.aws.s3;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum S3Folder {
    PROFILE("profile/"),
    CERTIFICATE("certificate/"),
    MEETING("meeting/"),
    AUDIO("audio/");

    private final String prefix;
}
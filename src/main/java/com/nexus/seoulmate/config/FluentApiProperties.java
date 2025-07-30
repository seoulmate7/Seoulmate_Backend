package com.nexus.seoulmate.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter // Fixme : setter 사용 안할 수 있는 방법 있는지 ...
@Component
@ConfigurationProperties(prefix = "fluent.api")
public class FluentApiProperties {
    private String key;
    private String username;
    private String password;
}
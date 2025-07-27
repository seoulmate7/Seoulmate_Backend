package com.nexus.seoulmate.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "fluent.api")
public class FluentApiProperties {
    private String key;
    private String username;
    private String password;

    public String getKey() { return key; }
    public void setKey(String key) { this.key = key; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
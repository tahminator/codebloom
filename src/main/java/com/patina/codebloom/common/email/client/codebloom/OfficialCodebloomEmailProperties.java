package com.patina.codebloom.common.email.client.codebloom;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@ConfigurationProperties(prefix = "codebloom.email")
@Getter
@Setter
public class OfficialCodebloomEmailProperties {
    private String host;
    private String port;
    private String type;
    private String username;
    private String password;
}

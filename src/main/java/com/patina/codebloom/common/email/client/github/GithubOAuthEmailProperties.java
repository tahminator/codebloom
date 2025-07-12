package com.patina.codebloom.common.email.client.github;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@ConfigurationProperties(prefix = "github.email")
@Getter
@Setter
public class GithubOAuthEmailProperties {
    private String host;
    private String port;
    private String type;
    private String username;
    private String password;
}

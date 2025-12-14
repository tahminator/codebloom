package com.patina.codebloom.api.auth.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "security.actuator")
public record SecurityActuatorProperties(String username, String password) {}

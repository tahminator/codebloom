package com.patina.codebloom.common.env;

import java.util.List;

import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.stereotype.Component;

@Component
public class Env {
    private final Environment environment;

    public Env(final Environment environment) {
        this.environment = environment;
    }

    public boolean isProfileActive(final String profile) {
        return environment.acceptsProfiles(Profiles.of(profile));
    }

    public boolean isProd() {
        return isProfileActive("prod");
    }

    public boolean isStg() {
        return isProfileActive("stg");
    }

    public boolean isDev() {
        return isProfileActive("dev");
    }

    public boolean isCi() {
        return isProfileActive("ci");
    }

    public List<String> getActiveProfiles() {
        return List.of(environment.getActiveProfiles());
    }
}

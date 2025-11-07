package com.patina.codebloom.utilities;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.patina.codebloom.common.security.annotation.ProtectedResolver;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final ProtectedResolver protectorResolver;

    public WebConfig(final ProtectedResolver protectorResolver) {
        this.protectorResolver = protectorResolver;
    }

    @Override
    public void addArgumentResolvers(final List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(protectorResolver);
    }
}

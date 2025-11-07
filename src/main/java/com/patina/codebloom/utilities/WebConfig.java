package com.patina.codebloom.utilities;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.patina.codebloom.common.security.annotation.ProtectorResolver;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final ProtectorResolver protectorResolver;

    public WebConfig(final ProtectorResolver protectorResolver) {
        this.protectorResolver = protectorResolver;
    }

    @Override
    public void addArgumentResolvers(final List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(protectorResolver);
    }
}

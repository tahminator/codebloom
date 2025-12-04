package com.patina.codebloom.utilities;

import com.patina.codebloom.common.security.annotation.ProtectedResolver;
import java.util.List;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final ProtectedResolver protectedResolver;

    public WebConfig(final ProtectedResolver protectedResolver) {
        this.protectedResolver = protectedResolver;
    }

    @Override
    public void addArgumentResolvers(final List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(protectedResolver);
    }
}

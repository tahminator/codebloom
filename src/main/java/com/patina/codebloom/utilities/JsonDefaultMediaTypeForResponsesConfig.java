package com.patina.codebloom.utilities;

import java.util.List;

import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Overrides default content type to be application/json, unless explicitly
 * stated.
 */
@Configuration
public class JsonDefaultMediaTypeForResponsesConfig implements WebMvcConfigurer {
    @Override
    public void extendMessageConverters(final List<HttpMessageConverter<?>> converters) {
        for (HttpMessageConverter<?> converter : converters) {
            if (converter instanceof MappingJackson2HttpMessageConverter jacksonConverter) {
                jacksonConverter.setSupportedMediaTypes(
                                List.of(MediaType.APPLICATION_JSON, MediaType.ALL));
            }
        }
    }
}

package com.patina.codebloom.common.openapi;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.patina.codebloom.api.auth.CustomOpenApiAuthItems;

import io.swagger.v3.oas.models.OpenAPI;

@Configuration
public class OpenApiConfig {
        @Bean
        public OpenAPI customOpenAPI() {
                OpenAPI openAPI = new OpenAPI();

                openAPI.path("/api/auth/flow/discord", CustomOpenApiAuthItems.discordOAuthFlow);

                openAPI.path("/api/auth/flow/callback/discord", CustomOpenApiAuthItems.discordOAuthFlowCallback);

                return openAPI;
        }
}
package com.patina.codebloom.utilities;

import com.patina.codebloom.api.auth.CustomOpenApiAuthItems;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        OpenAPI openAPI = new OpenAPI();

        openAPI.path("/api/auth/flow/discord", CustomOpenApiAuthItems.getDiscordOAuthFlow());

        openAPI.path("/api/auth/flow/callback/discord", CustomOpenApiAuthItems.getDiscordOAuthFlowCallback());

        return openAPI;
    }
}

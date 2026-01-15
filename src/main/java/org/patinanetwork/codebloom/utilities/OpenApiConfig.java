package org.patinanetwork.codebloom.utilities;

import io.swagger.v3.oas.models.OpenAPI;
import org.patinanetwork.codebloom.api.auth.CustomOpenApiAuthItems;
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

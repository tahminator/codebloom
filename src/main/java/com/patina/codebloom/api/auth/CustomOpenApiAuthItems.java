package com.patina.codebloom.api.auth;

import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;

/**
 * These endpoints do not get automatically scanned and picked up by Springdoc, so they are manually entered.
 */
public class CustomOpenApiAuthItems {

    private static PathItem discordOAuthFlow = new PathItem().get(
        new io.swagger.v3.oas.models.Operation()
            .addTagsItem("Authentication Routes")
            .summary("Start Discord OAuth authentication flow")
            .description(
                "Initiates the OAuth flow by redirecting the user to Discord for authentication. This is a Redirect route that does redirects as responses."
            )
            .responses(
                new ApiResponses().addApiResponse(
                    "302",
                    new ApiResponse().description(
                        "Redirect to Discord's authentication page"
                    )
                )
            )
    );

    public static PathItem getDiscordOAuthFlow() {
        return discordOAuthFlow;
    }

    private static PathItem discordOAuthFlowCallback = new PathItem().get(
        new io.swagger.v3.oas.models.Operation()
            .addTagsItem("Authentication Routes")
            .summary("Discord OAuth authentication flow callback")
            .description(
                """
                This route is the callback endpoint for the Discord OAuth authentication flow.
                Once the user successfully authenticates with Discord, they are redirected back to this endpoint
                where the server will then handle the authentication logic. This is a Redirect route that does
                redirects as responses.
                """
            )
            .responses(
                new ApiResponses()
                    .addApiResponse(
                        "302",
                        new ApiResponse().description(
                            "Redirect to `/dashboard` on successful authentication."
                        )
                    )
                    .addApiResponse(
                        "302 ",
                        new ApiResponse().description(
                            "Redirect to `/login?success=false&message=This is my message` on unsuccessful authentication."
                        )
                    )
            )
    );

    public static PathItem getDiscordOAuthFlowCallback() {
        return discordOAuthFlowCallback;
    }
}

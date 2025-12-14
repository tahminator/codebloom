package com.patina.codebloom.api.auth.security;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

/**
 * This is where the OAuth provider lives.
 *
 * @see <a href= "https://github.com/tahminator/codebloom/tree/main/docs/auth.md">Authentication Documentation</a>
 */
@Configuration
@EnableConfigurationProperties(SecurityActuatorProperties.class)
@EnableWebSecurity
public class SecurityConfig {

    private final AuthenticationSuccessHandler customAuthenticationSuccessHandler;

    public SecurityConfig(final AuthenticationSuccessHandler customAuthenticationSuccessHandler) {
        this.customAuthenticationSuccessHandler = customAuthenticationSuccessHandler;
    }

    @Bean
    UserDetailsService userDetailsService(SecurityActuatorProperties props) {
        return new InMemoryUserDetailsManager(User.withUsername(props.username())
                .password("{noop}" + props.password())
                .roles("ACTUATOR")
                .build());
    }

    /**
     * Security filter chain for actuator endpoints with HTTP Basic authentication. This needs to be processed first
     * (Order 1) to prevent OAuth from being applied.
     */
    @Bean
    @Order(1)
    public SecurityFilterChain actuatorSecurityFilterChain(final HttpSecurity http) throws Exception {
        http.securityMatcher("/actuator/**")
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth.anyRequest().hasRole("ACTUATOR"))
                .httpBasic(basic -> {});

        return http.build();
    }

    /**
     * The authorization endpoint is used to get redirected to the OAuth login page. The redirection endpoint is the
     * callback endpoint on our server that then handles the authentication logic. This handles all other requests with
     * OAuth.
     */
    @Bean
    @Order(2)
    public SecurityFilterChain oauthSecurityFilterChain(final HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .oauth2Login(oauth2 -> oauth2.authorizationEndpoint(auth -> auth
                                // This baseUri implicitly has {registrationId} at the end of it. That's why
                                // /api/auth/flow/discord still works.
                                .baseUri("/api/auth/flow"))
                        .redirectionEndpoint(auth -> auth.baseUri("/api/auth/flow/callback/{registrationId}"))
                        .successHandler(customAuthenticationSuccessHandler));

        return http.build();
    }

    // Remove the default login form that comes with SpringBoot Security.
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring().requestMatchers("/login");
    }
}

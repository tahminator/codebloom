package com.patina.codebloom.common.jwt.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.patina.codebloom.common.jwt.JWTClient;
import com.patina.codebloom.common.jwt.JWTProperties;
import java.time.Duration;
import java.time.Instant;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

@EnableConfigurationProperties(JWTProperties.class)
@Component
public class JWTClientImpl implements JWTClient {

    private final JWTProperties jwtProperties;
    private final JWTVerifier jwtVerifier;
    private final ObjectMapper objectMapper;
    private final Algorithm algorithm;

    JWTClientImpl(final JWTProperties jwtProperties) throws Exception {
        this.jwtProperties = jwtProperties;

        if (this.jwtProperties.getKey() == null) {
            throw new Exception("JWT Properties Secret key is missing.");
        }

        objectMapper = new ObjectMapper();
        registerObjectMapperPlugins();

        algorithm = Algorithm.HMAC256(this.jwtProperties.getKey());
        jwtVerifier = JWT.require(algorithm).build();
    }

    private void registerObjectMapperPlugins() {
        objectMapper.registerModule(new JavaTimeModule());
    }

    /** Create the JWT token, along with when it should expire. */
    public <T> String encode(final T obj, final Duration expiresIn) throws JsonProcessingException {
        String payload = objectMapper.writeValueAsString(obj);
        return JWT.create()
                .withClaim("payload", payload)
                .withExpiresAt(Instant.now().plus(expiresIn))
                .sign(algorithm);
    }

    /** Create the JWT token, default of 15 minute expiration. */
    public <T> String encode(final T obj) throws JsonProcessingException {
        return encode(obj, Duration.ofMinutes(15L));
    }

    /** Parse the JWT token back into a valid Object. Will throw if expired or unable to verify JWT. */
    public <T> T decode(final String token, final Class<T> clazz)
            throws JsonProcessingException, JWTVerificationException {
        DecodedJWT decodedJWT = jwtVerifier.verify(token);
        String payloadString = decodedJWT.getClaim("payload").asString();
        if (payloadString == null) {
            return null;
        }

        return objectMapper.readValue(payloadString, clazz);
    }
}

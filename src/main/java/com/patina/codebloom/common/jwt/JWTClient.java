package com.patina.codebloom.common.jwt;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.time.Duration;

/**
 * Create and read JWTs.
 */
public interface JWTClient {
    /**
     * Create the JWT token, along with when it should expire.
     */
    <T> String encode(T obj, Duration expiresIn) throws JsonProcessingException;

    /**
     * Create the JWT token, default of 15 minute expiration.
     */
    <T> String encode(T obj) throws JsonProcessingException;

    /**
     * Parse the JWT token back into a valid Object. Will throw if expired or unable
     * to verify JWT.
     */
    <T> T decode(String token, Class<T> clazz)
        throws JsonProcessingException, JWTVerificationException;
}

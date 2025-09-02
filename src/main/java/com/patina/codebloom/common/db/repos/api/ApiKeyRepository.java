package com.patina.codebloom.common.db.repos.api;

import java.util.List;

import com.patina.codebloom.common.db.models.api.ApiKey;

public interface ApiKeyRepository {
    ApiKey getApiKeyById(String id);

    ApiKey getApiKeyByHash(String hash);

    List<ApiKey> getAllApiKeys();

    /**
     * @note - The provided object's methods will be overridden with any returned
     * data from the database.
     *
     * @param userTag - required fields:
     * <ul>
     * <li>apiKey</li>
     * <li>id</li>
     * </ul>
     */
    void createApiKey(ApiKey apiKey);

    /**
     * @note - The provided object's methods will be overridden with any returned
     * data from the database.
     *
     * @param agent - overridden fields:
     * <ul>
     * <li>id</li>
     * <li>apiKey</li>
     * <li>hash</li>
     * </ul>
     */
    boolean updateApiKeyById(ApiKey apiKey);

    boolean deleteApiKeyById(String id);

    boolean deleteApiKeyByHash(String hash);
}

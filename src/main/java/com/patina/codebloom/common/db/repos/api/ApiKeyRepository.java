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
     * @param apiKey - required fields:
     * <ul>
     * <li>id</li>
     * <li>apiKeyHash</li>
     * <li>expiresAt</li>
     * <li>updatedBy</li>
     * </ul>
     */
    void createApiKey(ApiKey apiKey);

    /**
     * @note - The provided object's methods will be overridden with any returned
     * data from the database.
     *
     * @param apiKey - required fields:
     * <ul>
     * <li>apiKeyHash</li>
     * <li>expiresAt</li>
     * <li>updatedAt</li>
     * <li>updatedBy</li>
     * </ul>
     */
    boolean updateApiKeyById(ApiKey apiKey);

    boolean deleteApiKeyById(String id);

    boolean deleteApiKeyByHash(String hash);
}

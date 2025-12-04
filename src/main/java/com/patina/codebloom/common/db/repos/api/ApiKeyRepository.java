package com.patina.codebloom.common.db.repos.api;

import com.patina.codebloom.common.db.models.api.ApiKey;
import java.util.List;

public interface ApiKeyRepository {
    ApiKey getApiKeyById(String id);

    ApiKey getApiKeyByHash(String hash);

    List<ApiKey> getAllApiKeys();

    /**
     * @note - The provided object's methods will be overridden with any returned data from the database.
     * @param apiKey - required fields:
     *     <ul>
     *       <li>apiKeyHash
     *       <li>expiresAt
     *       <li>updatedBy
     *     </ul>
     */
    void createApiKey(ApiKey apiKey);

    /**
     * @note - The provided object's methods will be overridden with any returned data from the database.
     * @param apiKey - required fields:
     *     <ul>
     *       <li>apiKeyHash
     *       <li>expiresAt
     *       <li>updatedAt
     *       <li>updatedBy
     *     </ul>
     */
    boolean updateApiKeyById(ApiKey apiKey);

    boolean deleteApiKeyById(String id);

    boolean deleteApiKeyByHash(String hash);
}

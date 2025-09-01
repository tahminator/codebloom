package com.patina.codebloom.common.db.repos.api.access;

import java.util.List;

import com.patina.codebloom.common.db.models.api.access.ApiKeyAccess;

public interface ApiKeyAccessRepository {
    ApiKeyAccess getApiKeyAccessById(String id);

    List<ApiKeyAccess> getApiKeyAccessesByApiKeyId(String apiKeyId);

    /**
     * @note - The provided object's methods will be overridden with any returned
     * data from the database.
     * 
     * @param userTag - required fields:
     * <ul>
     * <li>apiKeyAccess</li>
     * <li>apiKeyId</li>
     * </ul>
     */

    void createApiKeyAccess(ApiKeyAccess apiKeyAccess);

    boolean updateApiKeyAccessById(ApiKeyAccess apiKeyAccess);

    boolean deleteApiKeyAccesesByApiKeyId(String apiKeyId);

    boolean deleteApiKeyAccessById(String id);
}

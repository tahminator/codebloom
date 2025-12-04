package com.patina.codebloom.common.db.repos.api.access;

import com.patina.codebloom.common.db.models.api.access.ApiKeyAccess;
import java.util.List;

public interface ApiKeyAccessRepository {
    ApiKeyAccess getApiKeyAccessById(String id);

    List<ApiKeyAccess> getApiKeyAccessesByApiKeyId(String apiKeyId);

    /**
     * @note - The provided object's methods will be overridden with any returned data from the database.
     * @param userTag - required fields:
     *     <ul>
     *       <li>apiKeyAccess
     *       <li>apiKeyId
     *     </ul>
     */
    void createApiKeyAccess(ApiKeyAccess apiKeyAccess);

    /**
     * @note - The provided object's methods will be overridden with any returned data from the database.
     * @param agent - overridden fields:
     *     <ul>
     *       <li>apiKeyId
     *       <li>apiKeyAccess
     *     </ul>
     */
    boolean updateApiKeyAccessById(ApiKeyAccess apiKeyAccess);

    boolean deleteApiKeyAccessesByApiKeyId(String apiKeyId);

    boolean deleteApiKeyAccessById(String id);
}

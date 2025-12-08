package com.patina.codebloom.common.db.models.api;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "ApiKeyAccessEnum")
public enum ApiKeyAccessEnum {
    GWC_READ_BY_USER,
    TEST_VALUE,
}

package org.patinanetwork.codebloom.common.db.models.job;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "JobStatus")
public enum JobStatus {
    COMPLETE,
    PROCESSING,
    INCOMPLETE,
}

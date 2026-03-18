package org.patinanetwork.codebloom.common.dto.user.metrics;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;
import org.patinanetwork.codebloom.common.db.models.user.UserMetrics;

@Getter
@Jacksonized
@Builder
@ToString
@EqualsAndHashCode
public class MetricsDto {
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String id;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String userId;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private int points;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private OffsetDateTime createdAt;

    public static MetricsDto fromUserMetrics(final UserMetrics userMetrics) {
        return MetricsDto.builder()
                .id(userMetrics.getId())
                .userId(userMetrics.getUserId())
                .points(userMetrics.getPoints())
                .createdAt(userMetrics.getCreatedAt())
                .build();
    }
}

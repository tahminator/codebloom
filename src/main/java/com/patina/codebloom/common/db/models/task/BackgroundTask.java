package com.patina.codebloom.common.db.models.task;

import java.time.OffsetDateTime;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
@EqualsAndHashCode
public class BackgroundTask {

    private String id;
    private BackgroundTaskEnum task;
    private OffsetDateTime completedAt;
}

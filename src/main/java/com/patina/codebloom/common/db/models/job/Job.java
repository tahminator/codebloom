package com.patina.codebloom.common.db.models.job;

import java.time.OffsetDateTime;

import com.patina.codebloom.common.db.helper.annotations.NotNullColumn;
import com.patina.codebloom.common.db.helper.annotations.NullColumn;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class Job {
    @NotNullColumn
    private String id;

    @NotNullColumn
    private OffsetDateTime createdAt;

    @NullColumn
    private OffsetDateTime processedAt;

    @NullColumn
    private OffsetDateTime completedAt;

    @NotNullColumn
    private JobStatus status;

    @NotNullColumn
    private String questionId;
}
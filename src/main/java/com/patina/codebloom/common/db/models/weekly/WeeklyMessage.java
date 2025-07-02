package com.patina.codebloom.common.db.models.weekly;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public class WeeklyMessage {
    private String id;
    private LocalDateTime createdAt;
}

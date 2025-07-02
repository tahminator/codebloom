package com.patina.codebloom.common.db.models.weekly;

import java.time.LocalDateTime;

import groovy.transform.EqualsAndHashCode;
import groovy.transform.ToString;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

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

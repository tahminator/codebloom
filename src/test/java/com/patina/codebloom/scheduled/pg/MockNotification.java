package com.patina.codebloom.scheduled.pg;

import org.postgresql.PGNotification;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
@EqualsAndHashCode
public class MockNotification implements PGNotification {
    private String name;
    // CHECKSTYLE:OFF
    private int PID;
    // CHECKSTYLE:ON
    private String parameter;
}

package org.patinanetwork.codebloom.scheduled.pg;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.postgresql.PGNotification;

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

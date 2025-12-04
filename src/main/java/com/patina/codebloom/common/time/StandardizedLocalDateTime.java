package com.patina.codebloom.common.time;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class StandardizedLocalDateTime {

    /**
     * Java uses the underlying clock of the platform the server is running on to determine how LocalDateTime is stored,
     * and more specifically, how they are formatted into a string. This will cause errors, so if you ever want to use
     * StandardizedLocalDateTime.now(), use .now() from this class instead.
     */
    public static LocalDateTime now() {
        return LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
    }
}

package com.patina.codebloom.common.time;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

public class StandardizedOffsetDateTime {
    /**
     * Returns the current time in UTC with milliseconds precision.
     */
    public static OffsetDateTime now() {
        return OffsetDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.MILLIS);
    }

    /**
     * Normalizes the given OffsetDateTime by converting it to UTC and truncating to
     * milliseconds precision.
     */
    public static OffsetDateTime normalize(final OffsetDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.withOffsetSameInstant(ZoneOffset.UTC).truncatedTo(ChronoUnit.MILLIS);
    }
}

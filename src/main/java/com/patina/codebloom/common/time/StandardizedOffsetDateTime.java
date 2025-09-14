package com.patina.codebloom.common.time;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

public class StandardizedOffsetDateTime {
    public static OffsetDateTime nowOffset() {
        return OffsetDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.MILLIS);
    }
    public static OffsetDateTime from(OffsetDateTime dateTime) {
        return dateTime.withOffsetSameInstant(ZoneOffset.UTC).truncatedTo(ChronoUnit.MILLIS);
    }
}

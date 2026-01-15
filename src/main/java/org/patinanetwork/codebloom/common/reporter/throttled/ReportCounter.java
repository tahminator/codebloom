package org.patinanetwork.codebloom.common.reporter.throttled;

import java.util.concurrent.atomic.AtomicInteger;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReportCounter {
    private final AtomicInteger count;
    private final long expireTime;
}

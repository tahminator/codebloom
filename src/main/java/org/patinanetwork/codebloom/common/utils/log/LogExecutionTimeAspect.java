package org.patinanetwork.codebloom.common.utils.log;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.patinanetwork.codebloom.common.env.Env;
import org.patinanetwork.codebloom.common.reporter.Reporter;
import org.patinanetwork.codebloom.common.reporter.report.Report;
import org.patinanetwork.codebloom.common.reporter.report.location.Location;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LogExecutionTimeAspect {

    private final Reporter reporter;
    private final Env env;

    public LogExecutionTimeAspect(Reporter reporter, Env env) {
        this.reporter = reporter;
        this.env = env;
    }

    @Around("@annotation(logExecutionTime)")
    public Object logExecTime(final ProceedingJoinPoint joinPoint, LogExecutionTime logExecutionTime) throws Throwable {
        long start = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long elapsed = System.currentTimeMillis() - start;

        log.info("{} executed in {} ms", joinPoint.getSignature(), elapsed);

        if (logExecutionTime.reportToDiscord()) {
            reporter.log(
                    "logExecTime",
                    Report.builder()
                            .data("%s executed in %s ms".formatted(joinPoint.getSignature(), elapsed))
                            .environments(env.getActiveProfiles())
                            .location(Location.BACKEND)
                            .build());
        }

        return result;
    }
}

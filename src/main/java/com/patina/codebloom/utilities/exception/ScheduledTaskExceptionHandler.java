package com.patina.codebloom.utilities.exception;

import java.util.concurrent.Executors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;

import com.patina.codebloom.common.env.Env;
import com.patina.codebloom.common.reporter.ErrorReporter;
import com.patina.codebloom.common.reporter.report.Report;
import com.patina.codebloom.common.reporter.report.location.Location;

/**
 * Add {@link ErrorReporter} to Spring Boot `@Scheduled` methods.
 */
@Configuration
public class ScheduledTaskExceptionHandler {

    private final ErrorReporter errorReporter;
    private final Env env;

    public ScheduledTaskExceptionHandler(final ErrorReporter errorReporter, final Env env) {
        this.errorReporter = errorReporter;
        this.env = env;
    }

    /***
     * Default Spring Boot task scheduler:
     * https://github.com/spring-projects/spring-framework/blob/main/spring-context/src/main/java/org/springframework/scheduling/config/ScheduledTaskRegistrar.java#L420-L421
     */
    @Bean
    public TaskScheduler taskScheduler() {
        ConcurrentTaskScheduler scheduler = new ConcurrentTaskScheduler(Executors.newSingleThreadScheduledExecutor());

        scheduler.setErrorHandler(throwable -> {
            if (env.isProd()) {
                errorReporter.report(Report.builder()
                                .environments(env.getActiveProfiles())
                                .location(Location.BACKEND)
                                .stackTrace(ErrorReporter.getStackTraceAsBytes(throwable))
                                .build());
            }

            throwable.printStackTrace();
        });

        return scheduler;
    }
}

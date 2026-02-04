package org.patinanetwork.codebloom;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableAsync
@Slf4j
public class CodebloomApplication {

    public static void main(final String[] args) {
        Thread.setDefaultUncaughtExceptionHandler(
                (thread, throwable) -> log.error("Uncaught exception in thread: {}", thread.getName(), throwable));

        SpringApplication.run(CodebloomApplication.class, args);
    }
}

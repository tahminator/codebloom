package com.patina.codebloom.utilities.generator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Profile("dev")
@Component
@Slf4j
public class JSTypesGenerator implements CommandLineRunner {
    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public void run(final String... args) throws Exception {
        log.info("Type generation command starting...");
        Process process = new ProcessBuilder("just", "type-gen")
                        .inheritIO()
                        .start();
        int exitCode = process.waitFor();
        if (exitCode == 0) {
            log.info("Type generation command completed successfully.");
        } else {
            log.error("Type generation command failed with exit code {}", exitCode);
            int springExitCode = SpringApplication.exit(applicationContext, () -> exitCode);
            System.exit(springExitCode);
        }
    }
}

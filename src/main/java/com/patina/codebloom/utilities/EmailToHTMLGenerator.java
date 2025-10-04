package com.patina.codebloom.utilities;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Profile("dev")
@Component
@Slf4j
public class EmailToHTMLGenerator {
    @Autowired
    private ApplicationContext applicationContext;

    public void run(final String... args) throws Exception {
        log.info("Generating HTML from React email template.");
        Process process = new ProcessBuilder("just", "email-gen")
                        .inheritIO()
                        .start();
        int exitCode = process.waitFor();
        if (exitCode == 0) {
            log.info("HTML generation completed successfully.");
        } else {
            log.error("HTML generation command failed with exit code {}", exitCode);
            int springExitCode = SpringApplication.exit(applicationContext, () -> exitCode);
            System.exit(springExitCode);
        }
    }
}

package com.patina.codebloom.config;

import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.patina.codebloom.jda.JDAInitializer;

/**
 * Tests that don't require JDA should extend this class, which will
 * significantly speed up the startup time of the Spring test.
 *
 * You only need to apply this to tests that use
 * {@code org.springframework.boot.test.context.SpringBootTest}
 */
public class NoJdaRequired {
    @MockitoBean
    private JDAInitializer jdaInitializer;
}

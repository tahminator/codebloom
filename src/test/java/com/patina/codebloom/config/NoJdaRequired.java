package com.patina.codebloom.config;

import com.patina.codebloom.jda.JDAInitializer;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

/**
 * Tests that don't require JDA should extend this class, which will significantly speed up the startup time of the
 * Spring test.
 *
 * <p>You only need to extend this class on tests that use {@code org.springframework.boot.test.context.SpringBootTest}
 * and don't need {@code com.patina.codebloom.jda.client.JDAClient}
 */
public class NoJdaRequired {

    @MockitoBean
    private JDAInitializer jdaInitializer;
}

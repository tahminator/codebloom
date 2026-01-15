package org.patinanetwork.codebloom.config;

import javax.sql.DataSource;
import org.patinanetwork.codebloom.common.env.Env;
import org.patinanetwork.codebloom.common.reporter.Reporter;
import org.patinanetwork.codebloom.scheduled.pg.NotifyListener;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

/**
 * Can mock the {@code org.patinanetwork.codebloom.scheduled.pg.JobNotifyListener} so it won't start the loop on Spring
 * startup.
 */
@TestConfiguration
public class TestJobNotifyListener {

    private final DataSource ds;
    private final Reporter reporter;
    private final Env env;

    public TestJobNotifyListener(final DataSource ds, final Reporter reporter, final Env env) {
        this.ds = ds;
        this.reporter = reporter;
        this.env = env;
    }

    @Bean
    @Primary
    public NotifyListener notifyListener() {
        // loop should never start, and as such will also never stop.
        return new NotifyListener(ds, reporter, env, null, null) {
            @Override
            protected void init() {
                return;
            }

            @Override
            protected void shutdown() {
                return;
            }
        };
    }
}

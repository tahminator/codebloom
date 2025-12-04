package com.patina.codebloom.config;

import com.patina.codebloom.common.db.DbConnection;
import com.patina.codebloom.common.env.Env;
import com.patina.codebloom.common.reporter.Reporter;
import com.patina.codebloom.scheduled.pg.NotifyListener;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

/**
 * Can mock the {@code com.patina.codebloom.scheduled.pg.JobNotifyListener} so it won't start the loop on Spring
 * startup.
 */
@TestConfiguration
public class TestJobNotifyListener {

    private DbConnection dbConn;
    private Reporter reporter;
    private Env env;

    public TestJobNotifyListener(final DbConnection dbConn, final Reporter reporter, final Env env) {
        this.dbConn = dbConn;
        this.reporter = reporter;
        this.env = env;
    }

    @Bean
    @Primary
    public NotifyListener notifyListener() {
        // loop should never start, and as such will also never stop.
        return new NotifyListener(dbConn, reporter, env, null, null) {
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

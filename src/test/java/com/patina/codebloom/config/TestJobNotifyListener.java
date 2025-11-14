package com.patina.codebloom.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import com.patina.codebloom.common.db.DbConnection;
import com.patina.codebloom.common.env.Env;
import com.patina.codebloom.common.reporter.Reporter;

/**
 * Can mock the {@code com.patina.codebloom.scheduled.pg.JobNotifyListener} so
 * it won't start the loop on Spring startup.
 */
@TestConfiguration
public class TestJobNotifyListener {
    // private DbConnection dbConn;
    // private Reporter reporter;
    // private Env env;
    //
    // public TestJobNotifyListener(final DbConnection dbConn, final Reporter
    // reporter, final Env env) {
    // this.dbConn = dbConn;
    // this.reporter = reporter;
    // this.env = env;
    // }
    //
    // @Bean
    // @Primary
    // public JobNotifyListener jobNotifyListener() {
    // return new JobNotifyListener(dbConn, null, reporter, env) {
    // @Override
    // protected void init() {
    // return;
    // };
    //
    // @Override
    // protected void shutdown() {
    // return;
    // }
    // };
    // }
}

package com.patina.codebloom.scheduled.pg;

import java.sql.Connection;
import java.sql.Statement;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.postgresql.PGConnection;
import org.postgresql.PGNotification;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.patina.codebloom.common.db.DbConnection;
import com.patina.codebloom.common.env.Env;
import com.patina.codebloom.common.reporter.Reporter;
import com.patina.codebloom.common.reporter.report.Report;
import com.patina.codebloom.common.reporter.report.location.Location;
import com.patina.codebloom.scheduled.leetcode.LeetcodeQuestionProcessService;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@Profile("!ci")
public class JobNotifyListener {
    private static final ExecutorService VTPOOL = Executors.newVirtualThreadPerTaskExecutor();

    private final LeetcodeQuestionProcessService leetcodeQuestionProcessService;
    private final Reporter reporter;
    private final Env env;
    private final Connection conn;

    public JobNotifyListener(final DbConnection dbConn,
                    final LeetcodeQuestionProcessService leetcodeQuestionProcessService,
                    final Reporter reporter,
                    final Env env) {
        this.leetcodeQuestionProcessService = leetcodeQuestionProcessService;
        this.reporter = reporter;
        this.env = env;
        this.conn = dbConn.getConn();
    }

    @PostConstruct
    void init() {
        VTPOOL.submit(this::listenLoop);
    }

    void listenLoop() {
        while (true) {
            try {
                PGConnection pgConn = conn.unwrap(PGConnection.class);

                try (Statement stmt = conn.createStatement()) {
                    stmt.execute("LISTEN \"jobInsertChannel\"");
                    log.info("Subscribed to jobInsertChannel");

                    while (true) {
                        Optional<PGNotification[]> notifications = Optional.ofNullable(pgConn.getNotifications(500));
                        notifications.ifPresent(notis -> {
                            for (var n : notis) {
                                String payload = n.getParameter();
                                handleNotification(payload);
                            }
                        });

                        Thread.sleep(1000);
                    }
                }
            } catch (Exception e) {
                log.error("Failed to listen to notifications", e);
                reporter.error(Report.builder()
                                .environments(env.getActiveProfiles())
                                .location(Location.BACKEND)
                                .data(Reporter.throwableToString(e))
                                .build());

                try {
                    Thread.sleep(5000);
                } catch (InterruptedException _) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }
    }

    void handleNotification(final String payload) {
        log.info("PAYLOAD CALLED! - {}", payload);
        leetcodeQuestionProcessService.drainQueue();
    }

    void shutdown() {
        VTPOOL.close();
    }
}

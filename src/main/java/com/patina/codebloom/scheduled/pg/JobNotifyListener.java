package com.patina.codebloom.scheduled.pg;

import java.sql.Connection;
import java.sql.Statement;
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
import jakarta.annotation.PreDestroy;
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
    protected void init() {
        VTPOOL.submit(this::listenLoop);
    }

    @PreDestroy
    protected void shutdown() {
        VTPOOL.shutdownNow();
    }

    // used for testing only, hence package-private
    static void forceShutDown() {
        VTPOOL.shutdownNow();
    }

    void listenLoop() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                PGConnection pgConn = conn.unwrap(PGConnection.class);

                try (Statement stmt = conn.createStatement()) {
                    stmt.execute("LISTEN \"jobInsertChannel\"");
                    log.info("Subscribed to jobInsertChannel");

                    while (!Thread.currentThread().isInterrupted()) {
                        PGNotification[] notifications = pgConn.getNotifications(500);

                        if (notifications != null) {
                            for (var n : notifications) {
                                String payload = n.getParameter();
                                handleNotification(payload);
                            }
                        }

                        Thread.sleep(1000);
                    }
                }
            } catch (Exception e) {
                if (Thread.currentThread().isInterrupted()) {
                    log.info("Listener interrupted, closing...");
                    break;
                }

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
        leetcodeQuestionProcessService.drainQueue();
    }
}

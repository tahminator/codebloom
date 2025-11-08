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
import com.patina.codebloom.scheduled.leetcode.LeetcodeQuestionProcessService;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@Profile("!ci")
public class JobNotifyListener {
    private static final ExecutorService VTPOOL = Executors.newVirtualThreadPerTaskExecutor();

    private final LeetcodeQuestionProcessService leetcodeQuestionProcessService;
    private final Connection conn;

    public JobNotifyListener(final DbConnection dbConn, final LeetcodeQuestionProcessService leetcodeQuestionProcessService) {
        this.leetcodeQuestionProcessService = leetcodeQuestionProcessService;
        this.conn = dbConn.getConn();
    }

    @PostConstruct
    public void init() {
        VTPOOL.submit(this::listen);
    }

    private void listen() {
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
            e.printStackTrace();
        }
    }

    private void handleNotification(final String payload) {
        log.info("PAYLOAD CALLED! - {}", payload);
        leetcodeQuestionProcessService.drainQueue();
    }
}

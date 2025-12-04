package com.patina.codebloom.scheduled.pg;

import com.patina.codebloom.common.db.DbConnection;
import com.patina.codebloom.common.env.Env;
import com.patina.codebloom.common.reporter.Reporter;
import com.patina.codebloom.common.reporter.report.Report;
import com.patina.codebloom.common.reporter.report.location.Location;
import com.patina.codebloom.scheduled.pg.handler.JobNotifyHandler;
import com.patina.codebloom.scheduled.pg.handler.LobbyNotifyHandler;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.sql.Connection;
import java.sql.Statement;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.PGConnection;
import org.postgresql.PGNotification;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@Profile("!ci | thread")
public class NotifyListener {

    private final ExecutorService vtpool;
    private final List<PgChannel> channels;

    private final Env env;
    private final Connection conn;
    private final Reporter reporter;

    private final JobNotifyHandler jobNotifyHandler;
    private final LobbyNotifyHandler lobbyNotifyHandler;

    public NotifyListener(
            final DbConnection dbConn,
            final Reporter reporter,
            final Env env,
            final JobNotifyHandler jobNotifyHandler,
            final LobbyNotifyHandler lobbyNotifyHandler) {
        this.channels = PgChannel.list();
        this.vtpool = Executors.newVirtualThreadPerTaskExecutor();
        this.reporter = reporter;
        this.env = env;
        this.conn = dbConn.getConn();
        this.jobNotifyHandler = jobNotifyHandler;
        this.lobbyNotifyHandler = lobbyNotifyHandler;
    }

    @PostConstruct
    protected void init() {
        vtpool.submit(this::listenLoop);
    }

    @PreDestroy
    protected void shutdown() {
        vtpool.shutdownNow();
    }

    private void listenLoop() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                PGConnection pgConn = conn.unwrap(PGConnection.class);

                try (Statement stmt = conn.createStatement()) {
                    for (var c : channels) {
                        stmt.execute(String.format("LISTEN \"%s\"", c.getChannelName()));
                        log.info(String.format("Subscribed to %s", c.getChannelName()));
                    }

                    while (!Thread.currentThread().isInterrupted()) {
                        PGNotification[] notifications = pgConn.getNotifications(500);

                        if (notifications != null) {
                            for (var n : notifications) {
                                switch (PgChannel.fromChannelName(n.getName())) {
                                    case INSERT_JOB -> jobNotifyHandler.handle(n.getParameter());
                                    case UPSERT_LOBBY -> lobbyNotifyHandler.handle(n.getParameter());
                                    default ->
                                        throw new UnsupportedOperationException(
                                                "a notification has been received that cannot be handled by the backend");
                                }
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
}

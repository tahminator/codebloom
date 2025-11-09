package com.patina.codebloom.scheduled.pg;

// CHECKSTYLE:OFF
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
// CHECKSTYLE:ON

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.postgresql.PGConnection;
import org.postgresql.PGNotification;

import com.patina.codebloom.common.db.DbConnection;
import com.patina.codebloom.common.env.Env;
import com.patina.codebloom.common.reporter.Reporter;
import com.patina.codebloom.scheduled.leetcode.LeetcodeQuestionProcessService;

public class JobNotifyListenerTest {
    private final DbConnection dbConn = mock(DbConnection.class);
    private final LeetcodeQuestionProcessService leetcodeQuestionProcessService = mock(LeetcodeQuestionProcessService.class);
    private final Reporter reporter = mock(Reporter.class);
    private final Env env = mock(Env.class);

    private final PGConnection pgConn = mock(PGConnection.class);
    private final Connection conn = mock(Connection.class);
    private final Statement stmt = mock(Statement.class);

    private JobNotifyListener jobNotifyListener;

    public JobNotifyListenerTest() {
        setup();
        jobNotifyListener = spy(new JobNotifyListener(dbConn, leetcodeQuestionProcessService, reporter, env));
    }

    void setup() {
        try {
            when(dbConn.getConn()).thenReturn(conn);
            when(conn.unwrap(any())).thenReturn(pgConn);
            when(conn.createStatement()).thenReturn(stmt);
        } catch (SQLException e) {
            fail(e);
        }
    }

    @AfterAll
    static void cleanup() {
        // force clean up or else the virtual thread will run indefinitely.
        JobNotifyListener.forceShutDown();
    }

    @Test
    void expectOneNotificationsToTriggerNotificationCallback() throws Exception {
        PGNotification[] mockNotifications = new PGNotification[] {
                MockNotification.builder()
                                .PID(1)
                                .name("Mock notification")
                                .parameter("HEY")
                                .build()
        };

        when(pgConn.getNotifications(anyInt())).thenReturn(mockNotifications);

        jobNotifyListener.init();

        Thread.sleep(500);

        verify(jobNotifyListener, times(1)).listenLoop();
        verify(jobNotifyListener, times(1)).handleNotification(argThat(mockNotifications[0].getParameter()::equals));
    }

    @Test
    void expectMultipleNotificationsToTriggerNotificationCallback() throws Exception {
        PGNotification[] mockNotifications = new PGNotification[] {
                MockNotification.builder()
                                .PID(1)
                                .name("Mock notification")
                                .parameter("N/A")
                                .build(),
                MockNotification.builder()
                                .PID(2)
                                .name("Mock notification 2")
                                .parameter("{\"id\": 5}")
                                .build()
        };

        when(pgConn.getNotifications(anyInt())).thenReturn(mockNotifications);

        jobNotifyListener.init();

        Thread.sleep(500);

        verify(jobNotifyListener, times(1)).listenLoop();
        verify(jobNotifyListener, times(2)).handleNotification(any());
    }
}

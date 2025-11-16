package com.patina.codebloom.scheduled.pg;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.Test;

public class PgChannelTest {

    @Test
    void testEnumValues() {
        assertEquals("jobInsertChannel", PgChannel.INSERT_JOB.getChannelName());
        assertEquals("upsertLobbyChannel", PgChannel.UPSERT_LOBBY.getChannelName());
    }

    @Test
    void testList() {
        List<PgChannel> channels = PgChannel.list();

        assertEquals(2, channels.size());
        assertTrue(channels.contains(PgChannel.INSERT_JOB));
        assertTrue(channels.contains(PgChannel.UPSERT_LOBBY));
    }

    @Test
    void testFromChannelNameValid() {
        assertEquals(PgChannel.INSERT_JOB, PgChannel.fromChannelName("jobInsertChannel"));
        assertEquals(PgChannel.UPSERT_LOBBY, PgChannel.fromChannelName("upsertLobbyChannel"));
    }

    @Test
    void testFromChannelNameInvalid() {
        assertThrows(NoSuchElementException.class, () -> {
            PgChannel.fromChannelName("nonExistentChannel");
        });
    }

    @Test
    void testFromChannelNameNull() {
        assertThrows(NoSuchElementException.class, () -> {
            PgChannel.fromChannelName(null);
        });
    }

    @Test
    void testFromChannelNameEmpty() {
        assertThrows(NoSuchElementException.class, () -> {
            PgChannel.fromChannelName("");
        });
    }

    @Test
    void testToString() {
        String insertJobString = PgChannel.INSERT_JOB.toString();
        String upsertLobbyString = PgChannel.UPSERT_LOBBY.toString();

        assertTrue(insertJobString.contains("INSERT_JOB"));
        assertTrue(upsertLobbyString.contains("UPSERT_LOBBY"));
    }
}

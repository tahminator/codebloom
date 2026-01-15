package org.patinanetwork.codebloom.playwright;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.patinanetwork.codebloom.common.db.models.usertag.Tag;
import org.patinanetwork.codebloom.common.url.ServerUrlUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

// TODO: Maybe replace these with E2E at some point.
@SpringBootTest
public class PlaywrightClientTest {

    @MockitoBean
    private ServerUrlUtils serverUrlUtils;

    private PlaywrightClient playwrightClient;

    @Autowired
    public PlaywrightClientTest(final PlaywrightClient playwrightClient) {
        this.playwrightClient = playwrightClient;
    }

    @Test
    void testGetCodebloomLeaderboardScreenshotSuccess() {
        when(serverUrlUtils.getUrl()).thenReturn("https://stg.codebloom.patinanetwork.org");

        byte[] screenshot = playwrightClient.getCodebloomLeaderboardScreenshot(1, Tag.Rpi);

        assertNotNull(screenshot, "Screenshot should not be null");
        assertTrue(screenshot.length > 0, "Screenshot should have content");

        verify(serverUrlUtils).getUrl();
    }

    @Test
    void testGetCodebloomLeaderboardScreenshotDifferentPage() {
        when(serverUrlUtils.getUrl()).thenReturn("https://stg.codebloom.patinanetwork.org");

        byte[] screenshot = playwrightClient.getCodebloomLeaderboardScreenshot(2, Tag.Baruch);

        assertNotNull(screenshot, "Screenshot should not be null");
        assertTrue(screenshot.length > 0, "Screenshot should have content");

        verify(serverUrlUtils).getUrl();
    }

    @Test
    void testGetCodebloomLeaderboardScreenshotDifferentTag() {
        when(serverUrlUtils.getUrl()).thenReturn("https://stg.codebloom.patinanetwork.org");

        byte[] screenshot = playwrightClient.getCodebloomLeaderboardScreenshot(1, Tag.Gwc);

        assertNotNull(screenshot, "Screenshot should not be null");
        assertTrue(screenshot.length > 0, "Screenshot should have content");

        verify(serverUrlUtils).getUrl();
    }
}

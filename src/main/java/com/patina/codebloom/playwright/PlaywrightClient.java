package com.patina.codebloom.playwright;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Browser.NewContextOptions;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.ScreenshotType;
import com.patina.codebloom.common.db.models.usertag.Tag;
import com.patina.codebloom.common.url.ServerUrlUtils;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PlaywrightClient {

    private static final String USER_AGENT =
        "Mozilla/5.0 (Linux; U; Android 4.4.1; SAMSUNG SM-J210G Build/KTU84P) AppleWebKit/536.31 (KHTML, like Gecko) Chrome/48.0.2090.359 Mobile Safari/601.9";

    private final ServerUrlUtils serverUrlUtils;

    public PlaywrightClient(final ServerUrlUtils serverUrlUtils) {
        this.serverUrlUtils = serverUrlUtils;
    }

    private <T> T withPage(final Function<Page, T> consumer) {
        try (
            Playwright playwright = Playwright.create();
            Browser browser = playwright
                .firefox()
                .launch(
                    new BrowserType.LaunchOptions()
                        .setHeadless(true)
                        .setTimeout(40000)
                );
            BrowserContext context = browser.newContext(
                new NewContextOptions()
                    .setUserAgent(USER_AGENT)
                    .setStorageState(null)
            );
            Page page = context.newPage();
        ) {
            return consumer.apply(page);
        }
    }

    public byte[] getCodebloomLeaderboardScreenshot(
        final int page,
        final Tag tag
    ) {
        log.info("Loading page {} for screenshot...", page);
        return withPage(p -> {
            p.navigate(
                "%s/leaderboard?%s=true&page=%s".formatted(
                    serverUrlUtils.getUrl(),
                    tag.name().toLowerCase(),
                    page
                )
            );
            p.waitForTimeout(5_000);
            byte[] screenshot = p.screenshot(
                new Page.ScreenshotOptions()
                    .setType(ScreenshotType.PNG)
                    .setFullPage(true)
            );
            return screenshot;
        });
    }
}

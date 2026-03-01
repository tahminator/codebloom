package org.patinanetwork.codebloom.playwright;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Browser.NewContextOptions;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import jakarta.annotation.PostConstruct;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PlaywrightProvider {

    private final boolean headless;

    public PlaywrightProvider(@Value("${playwright.headless}") final boolean headless) {
        this.headless = headless;
    }

    @PostConstruct
    void init() {
        log.info("Playwright headless={}", headless);
    }

    public <T> T withPage(final String userAgent, final Function<Page, T> consumer) {
        NewContextOptions options = new NewContextOptions();
        if (userAgent != null) {
            options.setUserAgent(userAgent);
        }

        try (Playwright playwright = Playwright.create();
                Browser browser = playwright
                        .firefox()
                        .launch(new BrowserType.LaunchOptions()
                                .setHeadless(headless)
                                .setTimeout(40000));
                BrowserContext context = browser.newContext(options);
                Page page = context.newPage()) {
            return consumer.apply(page);
        }
    }

    public <T> T withPage(final Function<Page, T> consumer) {
        return withPage(null, consumer);
    }
}

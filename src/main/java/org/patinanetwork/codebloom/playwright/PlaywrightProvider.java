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

    private static final String USER_AGENT =
            "Mozilla/5.0 (Linux; U; Android 4.4.1; SAMSUNG SM-J210G Build/KTU84P) AppleWebKit/536.31 (KHTML, like Gecko) Chrome/48.0.2090.359 Mobile Safari/601.9";

    private final boolean headless;

    public PlaywrightProvider(@Value("${playwright.headless}") boolean headless) {
        this.headless = headless;
    }

    @PostConstruct
    void init() {
        log.info("Playwright headless={}", headless);
    }

    public <T> T withPage(final Function<Page, T> consumer) {
        try (Playwright playwright = Playwright.create();
                Browser browser = playwright
                        .firefox()
                        .launch(new BrowserType.LaunchOptions()
                                .setHeadless(headless)
                                .setTimeout(40000));
                BrowserContext context = browser.newContext(new NewContextOptions().setUserAgent(USER_AGENT));
                Page page = context.newPage()) {
            return consumer.apply(page);
        }
    }
}

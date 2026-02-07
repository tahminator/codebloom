package org.patinanetwork.codebloom.jda;

import jakarta.annotation.PostConstruct;
import net.dv8tion.jda.api.JDA;
import org.springframework.stereotype.Component;

@Component
public class JDAEventListenerInitializer {
    private final JDAClientManager jdaClientManager;
    private final JDAEventListener jdaEventListener;

    public JDAEventListenerInitializer(
            final JDAClientManager jdaClientManager, final JDAEventListener jdaEventListener) {
        this.jdaClientManager = jdaClientManager;
        this.jdaEventListener = jdaEventListener;
    }

    @PostConstruct
    public void wireUp() {
        JDA client = jdaClientManager.getClient();
        if (client == null) {
            return;
        }

        client.addEventListener(jdaEventListener);
    }
}

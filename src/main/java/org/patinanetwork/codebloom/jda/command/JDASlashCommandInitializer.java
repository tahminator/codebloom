package org.patinanetwork.codebloom.jda.command;

import jakarta.annotation.PostConstruct;
import net.dv8tion.jda.api.JDA;
import org.patinanetwork.codebloom.jda.JDAClientManager;
import org.springframework.stereotype.Component;

@Component
public class JDASlashCommandInitializer {
    private final JDA jda;
    private final JDASlashCommandHandler jdaCommandHandler;

    public JDASlashCommandInitializer(
            final JDAClientManager jdaClientManager, final JDASlashCommandHandler jdaCommandHandler) {
        this.jda = jdaClientManager.getClient();
        this.jdaCommandHandler = jdaCommandHandler;
    }

    @PostConstruct
    public void setup() {
        jda.addEventListener(jdaCommandHandler);
    }
}

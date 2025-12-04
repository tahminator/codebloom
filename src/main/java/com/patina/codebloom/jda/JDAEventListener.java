package com.patina.codebloom.jda;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Component;

/** Do not remove this. JDA requires at least one listener in order to function. */
@Component
public class JDAEventListener extends ListenerAdapter {

    public void say(final SlashCommandInteractionEvent event, final String content) {
        event.reply(content).queue(); // This requires no permissions!
    }
}

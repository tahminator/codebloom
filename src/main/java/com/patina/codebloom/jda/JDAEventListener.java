package com.patina.codebloom.jda;

import org.springframework.stereotype.Component;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

@Component
public class JDAEventListener extends ListenerAdapter {
    public void say(final SlashCommandInteractionEvent event, final String content) {
        event.reply(content).queue(); // This requires no permissions!
    }
}

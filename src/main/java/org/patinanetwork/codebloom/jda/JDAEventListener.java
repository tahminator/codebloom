package org.patinanetwork.codebloom.jda;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Component;
import org.patinanetwork.codebloom.common.components.DiscordClubManager;

/** Do not remove this. JDA requires at least one listener in order to function. */
@Component
public class JDAEventListener extends ListenerAdapter {

    private final DiscordClubManager discordClubManager;

    public JDAEventListener(final DiscordClubManager discordClubManager) {
        this.discordClubManager = discordClubManager;
    }

    public void say(final SlashCommandInteractionEvent event, final String content) {
        event.reply(content).queue(); // This requires no permissions!
    }

    @Override
    public void onReady(final ReadyEvent event) {
        for (Guild guild: event.getJDA().getGuilds()) {
            guild.upsertCommand("leaderboard", "Shows the current weekly leaderboard").queue();
        }
    }

    @Override
    public void onSlashCommandInteraction(final SlashCommandInteractionEvent event) {
        if (event.getName().equals("leaderboard")) {
            discordClubManager.sendWeeklyLeaderboardUpdateDiscordMessageForClub(event.getGuild().getId());
        }
    }
}

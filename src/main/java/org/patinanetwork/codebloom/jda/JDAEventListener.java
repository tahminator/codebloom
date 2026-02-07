package org.patinanetwork.codebloom.jda;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.patinanetwork.codebloom.common.components.DiscordClubManager;
import org.springframework.stereotype.Component;

/** Do not remove this. JDA requires at least one listener in order to function. */
@Component
public class JDAEventListener extends ListenerAdapter {

    private final DiscordClubManager discordClubManager;

    public JDAEventListener(final DiscordClubManager discordClubManager, final JDAClientManager clientManager) {
        this.discordClubManager = discordClubManager;
    }

    public void say(final SlashCommandInteractionEvent event, final String content) {
        event.reply(content).queue(); // This requires no permissions!
    }

    @Override
    public void onReady(final ReadyEvent event) {
        for (Guild guild : event.getJDA().getGuilds()) {
            guild.upsertCommand("leaderboard", "Shows the current weekly leaderboard")
                    .queue();
        }
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (!event.getName().equals("leaderboard")) {
            return;
        }

        if (event.getGuild() == null) {
            event.reply("This command can only be used in a server.")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        MessageCreateData message = discordClubManager.buildWeeklyLeaderboardMessageForClub(
                event.getGuild().getId());

        event.reply(message).queue();
    }
}

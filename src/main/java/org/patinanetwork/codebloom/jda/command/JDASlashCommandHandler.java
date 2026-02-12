package org.patinanetwork.codebloom.jda.command;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.patinanetwork.codebloom.common.components.DiscordClubManager;
import org.springframework.stereotype.Component;

@Component
public class JDASlashCommandHandler extends ListenerAdapter {

    private final DiscordClubManager discordClubManager;

    public JDASlashCommandHandler(DiscordClubManager discordClubManager) {
        this.discordClubManager = discordClubManager;
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        switch (JDASlashCommand.fromCommand(event.getName())) {
            case LEADERBOARD -> handleLeaderboardSlashCommand(event);
            default -> throw new IllegalArgumentException("Unknown slash command: " + event.getName());
        }
    }

    private void handleLeaderboardSlashCommand(SlashCommandInteractionEvent event) {
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

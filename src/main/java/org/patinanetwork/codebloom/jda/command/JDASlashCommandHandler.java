package org.patinanetwork.codebloom.jda.command;

import java.awt.Color;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.patinanetwork.codebloom.common.components.DiscordClubManager;
import org.patinanetwork.codebloom.common.simpleredis.SimpleRedis;
import org.patinanetwork.codebloom.common.simpleredis.SimpleRedisProvider;
import org.patinanetwork.codebloom.common.simpleredis.SimpleRedisSlot;
import org.springframework.stereotype.Component;

@Component
public class JDASlashCommandHandler extends ListenerAdapter {

    private final DiscordClubManager discordClubManager;
    private final SimpleRedis<Long> simpleRedis;

    public JDASlashCommandHandler(
            DiscordClubManager discordClubManager, final SimpleRedisProvider simpleRedisProvider) {
        this.discordClubManager = discordClubManager;
        this.simpleRedis = simpleRedisProvider.select(SimpleRedisSlot.JDA_COOLDOWN);
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

        String guildId = event.getGuild().getId();

        if (simpleRedis.containsKey(guildId)) {
            long timeThen = simpleRedis.get(guildId);
            long timeNow = System.currentTimeMillis();
            long difference = (timeNow - timeThen) / 1000;

            if (difference < 600) {
                long remainingTime = 600 - difference;
                long minutes = remainingTime / 60;
                long seconds = remainingTime % 60;

                EmbedBuilder embed = new EmbedBuilder()
                        .setTitle("Please try again in **" + minutes + " minutes and " + seconds + " seconds**.")
                        .setColor(new Color(69, 129, 103));

                event.replyEmbeds(embed.build()).setEphemeral(true).queue();

                return;
            }
        }

        simpleRedis.put(guildId, System.currentTimeMillis());

        MessageCreateData message = discordClubManager.buildLeaderboardMessageForClub(
                event.getGuild().getId(), false);

        event.reply(message).queue();
    }
}

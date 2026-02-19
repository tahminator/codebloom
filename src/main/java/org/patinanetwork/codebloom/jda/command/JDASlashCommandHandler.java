package org.patinanetwork.codebloom.jda.command;

import java.awt.Color;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.patinanetwork.codebloom.common.components.DiscordClubManager;
import org.patinanetwork.codebloom.common.components.LeaderboardException;
import org.patinanetwork.codebloom.common.dto.refresh.RefreshResultDto;
import org.patinanetwork.codebloom.common.simpleredis.SimpleRedis;
import org.patinanetwork.codebloom.common.simpleredis.SimpleRedisProvider;
import org.patinanetwork.codebloom.common.simpleredis.SimpleRedisSlot;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class JDASlashCommandHandler extends ListenerAdapter {

    private final DiscordClubManager discordClubManager;
    private final SimpleRedis<Long> simpleRedis;
    private final ExecutorService pool;

    public JDASlashCommandHandler(
            DiscordClubManager discordClubManager, final SimpleRedisProvider simpleRedisProvider) {
        this.discordClubManager = discordClubManager;
        this.simpleRedis = simpleRedisProvider.select(SimpleRedisSlot.JDA_COOLDOWN);
        this.pool = Executors.newVirtualThreadPerTaskExecutor();
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        switch (JDASlashCommand.fromCommand(event.getName())) {
            case LEADERBOARD -> handleLeaderboardSlashCommand(event);
            case REFRESH -> handleRefreshSlashCommand(event);
            default -> throw new IllegalArgumentException("Unknown slash command: " + event.getName());
        }
    }

    /**
     * In memory rate limiter.
     *
     * @param event Slash command event
     * @param time Time to reuse in seconds
     */
    private long handleRedis(SlashCommandInteractionEvent event, final long time) {
        String guildId = event.getGuild().getId();
        String command = event.getName();
        String key = "refresh".equals(command)
                ? guildId + ":" + command + ":" + event.getUser().getId()
                : guildId + ":" + command;

        if (simpleRedis.containsKey(key)) {
            long timeThen = simpleRedis.get(key);
            long timeNow = System.currentTimeMillis();
            long difference = (timeNow - timeThen) / 1000;

            if (difference < time) {
                long remainingTime = time - difference;

                return remainingTime;
            }
        }

        simpleRedis.put(key, System.currentTimeMillis());
        return 0;
    }

    private void handleRefreshSlashCommand(SlashCommandInteractionEvent event) {
        event.deferReply().setEphemeral(true).queue();

        if (event.getGuild() == null) {
            MessageEmbed message = new EmbedBuilder()
                    .setTitle("This command can only be used in a server")
                    .setColor(Color.RED)
                    .build();
            event.getHook().sendMessageEmbeds(message).queue();
            return;
        }
        long remainingTime = handleRedis(event, (long) 5 * 60);
        long minutes = remainingTime / 60;
        long seconds = remainingTime % 60;

        if (remainingTime != 0) {
            String description = String.format("""
                        Please wait %s minutes and %s seconds before refreshing!
                        """, minutes, seconds);

            MessageEmbed message = new EmbedBuilder()
                    .setTitle("⏳ You are refreshing too quickly!")
                    .setDescription(description)
                    .setColor(Color.ORANGE)
                    .build();

            event.getHook().editOriginalEmbeds(message).queue();
            return;
        }
        String guildId = event.getGuild().getId();
        String userId = event.getUser().getId();
        var future = pool.submit(() -> {
            try {
                RefreshResultDto result = discordClubManager.refreshSubmissions(guildId, userId);

                String description = String.format(
                        """
                        After refreshing your submissions, you currently have %s points.

                        For the leaderboard `%s`, you are currently #%s globally and #%s among all %s members.
                        """,
                        result.getScore(),
                        result.getLeaderboardName(),
                        result.getGlobalRank(),
                        result.getClubRank(),
                        result.getClubName());

                MessageEmbed message = new EmbedBuilder()
                        .setTitle("Submissions Refreshed")
                        .setDescription(description)
                        .setColor(new Color(69, 129, 103))
                        .build();

                event.getHook().editOriginalEmbeds(message).queue();
            } catch (LeaderboardException e) {
                MessageEmbed message = new EmbedBuilder()
                        .setTitle(e.getTitle())
                        .setDescription(e.getDescription())
                        .setColor(new Color(24, 162, 184))
                        .build();
                event.getHook().editOriginalEmbeds(message).queue();
                log.error("Refresh for club failed ", e);
            }
        });

        try {
            future.get(2500, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (TimeoutException | ExecutionException exception) {
            String description = """
                        Hmm, the refresh operation is taking longer than expected :(

                        We’ll keep trying behind the scenes. On completion, this message will update to reflect your points and rank.
                    """;
            MessageEmbed message = new EmbedBuilder()
                    .setTitle("Submissions Refreshed")
                    .setDescription(description)
                    .setColor(new Color(24, 162, 184))
                    .build();
            event.getHook().editOriginalEmbeds(message).queue();
            log.info("Refresh for club taking longer than expected ", exception);
        }
    }

    private void handleLeaderboardSlashCommand(SlashCommandInteractionEvent event) {
        if (event.getGuild() == null) {
            event.reply("This command can only be used in a server.")
                    .setEphemeral(true)
                    .queue();
            return;
        }
        long remainingTime = handleRedis(event, (long) 10 * 60);
        long minutes = remainingTime / 60;
        long seconds = remainingTime % 60;

        String title = String.format("""
            Please try again in %s minutes and %s seconds.
            """, minutes, seconds);
        if (remainingTime != 0) {
            MessageEmbed message = new EmbedBuilder()
                    .setTitle(title)
                    .setColor(new Color(69, 129, 103))
                    .build();
            event.replyEmbeds(message).setEphemeral(true).queue();
            return;
        }
        String serverId = event.getGuild().getId();
        MessageCreateData message = discordClubManager.buildLeaderboardMessageForClub(serverId, false);

        event.reply(message).setEphemeral(true).queue();
    }
}

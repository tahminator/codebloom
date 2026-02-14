package org.patinanetwork.codebloom.jda;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.patinanetwork.codebloom.common.components.DiscordClubManager;
import org.patinanetwork.codebloom.common.simpleredis.SimpleRedis;
import org.patinanetwork.codebloom.common.simpleredis.SimpleRedisProvider;
import org.patinanetwork.codebloom.common.simpleredis.SimpleRedisSlot;
import org.patinanetwork.codebloom.jda.command.JDASlashCommandHandler;

@ExtendWith(MockitoExtension.class)
class JDASlashCommandHandlerTest {

    @Mock
    private DiscordClubManager discordClubManager;

    @Mock
    private SimpleRedis<Long> simpleRedis;

    @Mock
    private SimpleRedisProvider simpleRedisProvider;

    private JDASlashCommandHandler handler;

    @BeforeEach
    void setUp() {
        when(simpleRedisProvider.select(SimpleRedisSlot.JDA_COOLDOWN)).thenReturn(simpleRedis);
        handler = new JDASlashCommandHandler(discordClubManager, simpleRedisProvider);
    }

    @Test
    void testOnSlashCommandInteractionNoGuild() {
        SlashCommandInteractionEvent event = mock(SlashCommandInteractionEvent.class);
        ReplyCallbackAction replyAction = mock(ReplyCallbackAction.class);

        when(event.getName()).thenReturn("leaderboard");
        when(event.getGuild()).thenReturn(null);

        when(event.reply("This command can only be used in a server.")).thenReturn(replyAction);
        when(replyAction.setEphemeral(true)).thenReturn(replyAction);

        handler.onSlashCommandInteraction(event);

        verify(event).reply("This command can only be used in a server.");
        verify(replyAction).setEphemeral(true);
        verify(replyAction).queue();
        verifyNoInteractions(discordClubManager);
    }

    @Test
    void testOnSlashCommandInteractionSuccess() {
        SlashCommandInteractionEvent event = mock(SlashCommandInteractionEvent.class);
        ReplyCallbackAction replyAction = mock(ReplyCallbackAction.class);
        Guild guild = mock(Guild.class);

        when(event.getName()).thenReturn("leaderboard");
        when(event.getGuild()).thenReturn(guild);
        when(guild.getId()).thenReturn("guild-123");

        when(simpleRedis.containsKey("guild-123")).thenReturn(false);

        MessageCreateData message = mock(MessageCreateData.class);
        when(discordClubManager.buildLeaderboardMessageForClub("guild-123", false))
                .thenReturn(message);

        when(event.reply(eq(message))).thenReturn(replyAction);

        handler.onSlashCommandInteraction(event);

        verify(simpleRedis).put(eq("guild-123"), anyLong());
        verify(discordClubManager).buildLeaderboardMessageForClub("guild-123", false);
        verify(event).reply(message);
        verify(replyAction).queue();
    }

    @Test
    void testOnSlashCommandInteractionCooldown() {
        SlashCommandInteractionEvent event = mock(SlashCommandInteractionEvent.class);
        ReplyCallbackAction replyAction = mock(ReplyCallbackAction.class);
        Guild guild = mock(Guild.class);

        when(event.getName()).thenReturn("leaderboard");
        when(event.getGuild()).thenReturn(guild);
        when(guild.getId()).thenReturn("guild-123");

        when(simpleRedis.containsKey("guild-123")).thenReturn(true);
        when(simpleRedis.get("guild-123")).thenReturn(System.currentTimeMillis());

        when(event.replyEmbeds(any(MessageEmbed.class))).thenReturn(replyAction);
        when(replyAction.setEphemeral(true)).thenReturn(replyAction);

        handler.onSlashCommandInteraction(event);

        verify(event).replyEmbeds(any(MessageEmbed.class));
        verify(replyAction).setEphemeral(true);
        verify(replyAction).queue();

        verifyNoInteractions(discordClubManager);
        verify(simpleRedis, never()).put(eq("guild-123"), anyLong()); // because you return before put
    }
}

package org.patinanetwork.codebloom.jda;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.util.List;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.requests.restaction.CommandCreateAction;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.patinanetwork.codebloom.common.components.DiscordClubManager;

@ExtendWith(MockitoExtension.class)
class JDAEventListenerTest {

    @Mock
    private DiscordClubManager discordClubManager;

    @Mock
    private JDAClientManager clientManager; // unused but required by constructor

    private JDAEventListener listener;

    @BeforeEach
    void setUp() {
        listener = new JDAEventListener(discordClubManager, clientManager);
    }

    @Test
    void testOnSlashCommandInteractionNoGuild() {
        SlashCommandInteractionEvent event = mock(SlashCommandInteractionEvent.class);
        ReplyCallbackAction replyAction = mock(ReplyCallbackAction.class);

        when(event.getName()).thenReturn("leaderboard");
        when(event.getGuild()).thenReturn(null);
        when(event.reply(any(String.class))).thenReturn(replyAction);
        when(replyAction.setEphemeral(true)).thenReturn(replyAction);

        listener.onSlashCommandInteraction(event);

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

        MessageCreateData message = mock(MessageCreateData.class);
        when(discordClubManager.buildWeeklyLeaderboardMessageForClub("guild-123"))
                .thenReturn(message);

        when(event.reply(eq(message))).thenReturn(replyAction);

        listener.onSlashCommandInteraction(event);

        verify(discordClubManager).buildWeeklyLeaderboardMessageForClub("guild-123");
        verify(event).reply(message);
        verify(replyAction).queue();
    }

    @Test
    void testOnReady() {
        ReadyEvent readyEvent = mock(ReadyEvent.class);
        JDA jda = mock(JDA.class);

        Guild guild1 = mock(Guild.class);

        CommandCreateAction action1 = mock(CommandCreateAction.class);

        when(readyEvent.getJDA()).thenReturn(jda);
        when(jda.getGuilds()).thenReturn(List.of(guild1));

        when(guild1.upsertCommand("leaderboard", "Shows the current weekly leaderboard"))
                .thenReturn(action1);

        listener.onReady(readyEvent);

        verify(guild1).upsertCommand("leaderboard", "Shows the current weekly leaderboard");
        verify(action1).queue();
    }
}

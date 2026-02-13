package org.patinanetwork.codebloom.jda;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.patinanetwork.codebloom.common.components.DiscordClubManager;
import org.patinanetwork.codebloom.jda.command.JDASlashCommandHandler;

@ExtendWith(MockitoExtension.class)
class JDASlashCommandHandlerTest {

    @Mock
    private DiscordClubManager discordClubManager;

    private JDASlashCommandHandler handler;

    @BeforeEach
    void setUp() {
        handler = new JDASlashCommandHandler(discordClubManager);
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

        MessageCreateData message = mock(MessageCreateData.class);
        when(discordClubManager.buildWeeklyLeaderboardMessageForClub("guild-123"))
                .thenReturn(message);

        when(event.reply(eq(message))).thenReturn(replyAction);

        handler.onSlashCommandInteraction(event);

        verify(discordClubManager).buildWeeklyLeaderboardMessageForClub("guild-123");
        verify(event).reply(message);
        verify(replyAction).queue();
    }
}

package org.patinanetwork.codebloom.jda;

import static org.mockito.Mockito.*;

import java.util.List;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.patinanetwork.codebloom.jda.command.JDASlashCommand;

@ExtendWith(MockitoExtension.class)
class JDACommandRegisterHandlerTest {

    private JDACommandRegisterHandler listener;

    @BeforeEach
    void setUp() {
        listener = new JDACommandRegisterHandler();
    }

    @Test
    void testOnReady() {
        ReadyEvent readyEvent = mock(ReadyEvent.class);
        JDA jda = mock(JDA.class);

        Guild guild1 = mock(Guild.class);

        CommandListUpdateAction action1 = mock(CommandListUpdateAction.class);

        when(readyEvent.getJDA()).thenReturn(jda);
        when(jda.getGuilds()).thenReturn(List.of(guild1));
        when(guild1.updateCommands()).thenReturn(action1);
        when(action1.addCommands(any(SlashCommandData[].class))).thenReturn(action1);

        listener.onReady(readyEvent);

        verify(guild1).updateCommands();

        ArgumentCaptor<SlashCommandData[]> commandsCaptor = ArgumentCaptor.forClass(SlashCommandData[].class);
        verify(action1).addCommands(commandsCaptor.capture());
        verify(action1).queue();

        SlashCommandData[] capturedCommands = commandsCaptor.getValue();
        assert capturedCommands.length == JDASlashCommand.list().size();
        assert capturedCommands[0].getName().equals("leaderboard");
    }
}

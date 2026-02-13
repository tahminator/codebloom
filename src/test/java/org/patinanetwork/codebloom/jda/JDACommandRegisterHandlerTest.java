package org.patinanetwork.codebloom.jda;

import static org.mockito.Mockito.*;

import java.util.List;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.requests.restaction.CommandCreateAction;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

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

        CommandCreateAction action1 = mock(CommandCreateAction.class);

        when(readyEvent.getJDA()).thenReturn(jda);
        when(jda.getGuilds()).thenReturn(List.of(guild1));

        when(guild1.upsertCommand("leaderboard", "Shows the current weekly leaderboard"))
                .thenReturn(action1);

        CommandListUpdateAction clearAction = mock(CommandListUpdateAction.class);
        when(guild1.updateCommands()).thenReturn(clearAction);

        listener.onReady(readyEvent);

        verify(guild1).upsertCommand("leaderboard", "Shows the current weekly leaderboard");
        verify(action1).queue();
    }
}

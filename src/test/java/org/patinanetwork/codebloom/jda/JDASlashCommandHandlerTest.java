package org.patinanetwork.codebloom.jda;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageCreateAction;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageEditAction;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.patinanetwork.codebloom.common.components.DiscordClubManager;
import org.patinanetwork.codebloom.common.components.LeaderboardException;
import org.patinanetwork.codebloom.common.dto.refresh.RefreshResultDto;
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
    private ExecutorService executor;

    @BeforeEach
    void setUp() {
        executor = Executors.newVirtualThreadPerTaskExecutor();
        when(simpleRedisProvider.select(SimpleRedisSlot.JDA_COOLDOWN)).thenReturn(simpleRedis);
        handler = new JDASlashCommandHandler(discordClubManager, simpleRedisProvider, executor);
    }

    @AfterEach
    void tearDown() {
        executor.shutdownNow();
    }

    @Test
    void testLeaderboardNoGuild() {
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
    void testLeaderboardSuccess() {
        SlashCommandInteractionEvent event = mock(SlashCommandInteractionEvent.class);
        ReplyCallbackAction replyAction = mock(ReplyCallbackAction.class);
        Guild guild = mock(Guild.class);

        when(event.getName()).thenReturn("leaderboard");
        when(event.getGuild()).thenReturn(guild);
        when(guild.getId()).thenReturn("guild-123");

        when(simpleRedis.containsKey("guild-123:leaderboard")).thenReturn(false);

        MessageCreateData message = mock(MessageCreateData.class);
        when(discordClubManager.buildLeaderboardMessageForClub("guild-123", false))
                .thenReturn(message);

        when(event.reply(eq(message))).thenReturn(replyAction);
        when(replyAction.setEphemeral(true)).thenReturn(replyAction);

        handler.onSlashCommandInteraction(event);

        verify(simpleRedis).put(eq("guild-123:leaderboard"), anyLong());
        verify(discordClubManager).buildLeaderboardMessageForClub("guild-123", false);
        verify(event).reply(message);
        verify(replyAction).setEphemeral(true);
        verify(replyAction).queue();
    }

    @Test
    void testLeaderboardCooldown() {
        SlashCommandInteractionEvent event = mock(SlashCommandInteractionEvent.class);
        ReplyCallbackAction cooldownReplyAction = mock(ReplyCallbackAction.class);
        Guild guild = mock(Guild.class);

        when(event.getName()).thenReturn("leaderboard");
        when(event.getGuild()).thenReturn(guild);
        when(guild.getId()).thenReturn("guild-123");

        when(simpleRedis.containsKey("guild-123:leaderboard")).thenReturn(true);
        when(simpleRedis.get("guild-123:leaderboard")).thenReturn(System.currentTimeMillis());

        when(event.replyEmbeds(any(MessageEmbed.class))).thenReturn(cooldownReplyAction);
        when(cooldownReplyAction.setEphemeral(true)).thenReturn(cooldownReplyAction);

        handler.onSlashCommandInteraction(event);

        verify(event).replyEmbeds(any(MessageEmbed.class));
        verify(cooldownReplyAction).setEphemeral(true);
        verify(cooldownReplyAction).queue();

        verify(simpleRedis, never()).put(eq("guild-123:leaderboard"), anyLong());
        verifyNoInteractions(discordClubManager);
        verify(event, never()).reply(any(MessageCreateData.class));
    }

    @Test
    void testLeaderboardCooldownExpired() {
        SlashCommandInteractionEvent event = mock(SlashCommandInteractionEvent.class);
        ReplyCallbackAction replyAction = mock(ReplyCallbackAction.class);
        Guild guild = mock(Guild.class);

        when(event.getName()).thenReturn("leaderboard");
        when(event.getGuild()).thenReturn(guild);
        when(guild.getId()).thenReturn("guild-123");

        when(simpleRedis.containsKey("guild-123:leaderboard")).thenReturn(true);
        when(simpleRedis.get("guild-123:leaderboard")).thenReturn(System.currentTimeMillis() - 11 * 60 * 1000);

        MessageCreateData message = mock(MessageCreateData.class);
        when(discordClubManager.buildLeaderboardMessageForClub("guild-123", false))
                .thenReturn(message);
        when(event.reply(eq(message))).thenReturn(replyAction);
        when(replyAction.setEphemeral(true)).thenReturn(replyAction);

        handler.onSlashCommandInteraction(event);

        verify(simpleRedis).put(eq("guild-123:leaderboard"), anyLong());
        verify(event, never()).replyEmbeds(any(MessageEmbed.class));
        verify(discordClubManager).buildLeaderboardMessageForClub("guild-123", false);
        verify(event).reply(message);
        verify(replyAction).setEphemeral(true);
        verify(replyAction).queue();
    }

    @Test
    void testRefreshNoGuild() {
        SlashCommandInteractionEvent event = mock(SlashCommandInteractionEvent.class);
        ReplyCallbackAction deferAction = mock(ReplyCallbackAction.class);
        InteractionHook hook = mock(InteractionHook.class);
        WebhookMessageCreateAction<Message> sendAction = mock(WebhookMessageCreateAction.class);

        when(event.getName()).thenReturn("refresh");
        when(event.deferReply()).thenReturn(deferAction);
        when(deferAction.setEphemeral(true)).thenReturn(deferAction);
        when(event.getGuild()).thenReturn(null);
        when(event.getHook()).thenReturn(hook);
        when(hook.sendMessageEmbeds(any(MessageEmbed.class))).thenReturn(sendAction);

        handler.onSlashCommandInteraction(event);

        verify(event).deferReply();
        verify(deferAction).setEphemeral(true);
        verify(deferAction).queue();
        verify(hook).sendMessageEmbeds(any(MessageEmbed.class));
        verify(sendAction).queue();
        verifyNoInteractions(discordClubManager);
    }

    @Test
    void testRefreshSuccess() throws LeaderboardException {
        SlashCommandInteractionEvent event = mock(SlashCommandInteractionEvent.class);
        ReplyCallbackAction deferAction = mock(ReplyCallbackAction.class);
        InteractionHook hook = mock(InteractionHook.class);
        WebhookMessageEditAction<Message> editAction = mock(WebhookMessageEditAction.class);
        Guild guild = mock(Guild.class);
        User user = mock(User.class);

        when(event.getName()).thenReturn("refresh");
        when(event.deferReply()).thenReturn(deferAction);
        when(deferAction.setEphemeral(true)).thenReturn(deferAction);
        when(event.getGuild()).thenReturn(guild);
        when(event.getUser()).thenReturn(user);
        when(guild.getId()).thenReturn("guild-456");
        when(user.getId()).thenReturn("user-789");
        when(event.getHook()).thenReturn(hook);

        when(simpleRedis.containsKey("guild-456:refresh:user-789")).thenReturn(false);

        RefreshResultDto result = RefreshResultDto.builder()
                .score(42)
                .globalRank(5)
                .clubRank(2)
                .leaderboardName("Week 10")
                .clubName("TestClub")
                .build();
        when(discordClubManager.refreshSubmissions("guild-456", "user-789")).thenReturn(result);
        when(hook.editOriginalEmbeds(any(MessageEmbed.class))).thenReturn(editAction);

        handler.onSlashCommandInteraction(event);

        verify(event).deferReply();
        verify(deferAction).setEphemeral(true);
        verify(deferAction).queue();
        verify(simpleRedis).put(eq("guild-456:refresh:user-789"), anyLong());
        verify(discordClubManager).refreshSubmissions("guild-456", "user-789");
        verify(hook).editOriginalEmbeds(any(MessageEmbed.class));
        verify(editAction).queue();
    }

    @Test
    void testRefreshCooldown() {
        SlashCommandInteractionEvent event = mock(SlashCommandInteractionEvent.class);
        ReplyCallbackAction deferAction = mock(ReplyCallbackAction.class);
        InteractionHook hook = mock(InteractionHook.class);
        WebhookMessageEditAction<Message> editEmbedsAction = mock(WebhookMessageEditAction.class);
        Guild guild = mock(Guild.class);
        User user = mock(User.class);

        when(event.getName()).thenReturn("refresh");
        when(event.deferReply()).thenReturn(deferAction);
        when(deferAction.setEphemeral(true)).thenReturn(deferAction);
        when(event.getGuild()).thenReturn(guild);
        when(event.getUser()).thenReturn(user);
        when(guild.getId()).thenReturn("guild-456");
        when(user.getId()).thenReturn("user-789");
        when(event.getHook()).thenReturn(hook);

        when(simpleRedis.containsKey("guild-456:refresh:user-789")).thenReturn(true);
        when(simpleRedis.get("guild-456:refresh:user-789")).thenReturn(System.currentTimeMillis());

        when(hook.editOriginalEmbeds(any(MessageEmbed.class))).thenReturn(editEmbedsAction);

        handler.onSlashCommandInteraction(event);

        verify(event).deferReply();
        verify(hook).editOriginalEmbeds(any(MessageEmbed.class));
        verify(editEmbedsAction).queue();
        verify(simpleRedis, never()).put(eq("guild-456:refresh:user-789"), anyLong());
        verifyNoInteractions(discordClubManager);
    }

    @Test
    void testRefreshCooldownExpired() throws LeaderboardException {
        SlashCommandInteractionEvent event = mock(SlashCommandInteractionEvent.class);
        ReplyCallbackAction deferAction = mock(ReplyCallbackAction.class);
        InteractionHook hook = mock(InteractionHook.class);
        WebhookMessageEditAction<Message> editAction = mock(WebhookMessageEditAction.class);
        Guild guild = mock(Guild.class);
        User user = mock(User.class);

        when(event.getName()).thenReturn("refresh");
        when(event.deferReply()).thenReturn(deferAction);
        when(deferAction.setEphemeral(true)).thenReturn(deferAction);
        when(event.getGuild()).thenReturn(guild);
        when(event.getUser()).thenReturn(user);
        when(guild.getId()).thenReturn("guild-456");
        when(user.getId()).thenReturn("user-789");
        when(event.getHook()).thenReturn(hook);

        when(simpleRedis.containsKey("guild-456:refresh:user-789")).thenReturn(true);
        when(simpleRedis.get("guild-456:refresh:user-789")).thenReturn(System.currentTimeMillis() - 6 * 60 * 1000);

        RefreshResultDto result = RefreshResultDto.builder()
                .score(10)
                .globalRank(3)
                .clubRank(1)
                .leaderboardName("Week 5")
                .clubName("Club")
                .build();
        when(discordClubManager.refreshSubmissions("guild-456", "user-789")).thenReturn(result);
        when(hook.editOriginalEmbeds(any(MessageEmbed.class))).thenReturn(editAction);

        handler.onSlashCommandInteraction(event);

        verify(simpleRedis).put(eq("guild-456:refresh:user-789"), anyLong());
        verify(discordClubManager).refreshSubmissions("guild-456", "user-789");
        verify(hook).editOriginalEmbeds(any(MessageEmbed.class));
        verify(editAction).queue();
    }

    @Test
    void testRefreshDiscordClubException() throws LeaderboardException {
        SlashCommandInteractionEvent event = mock(SlashCommandInteractionEvent.class);
        ReplyCallbackAction deferAction = mock(ReplyCallbackAction.class);
        InteractionHook hook = mock(InteractionHook.class);
        var editEmbedsAction = mock(WebhookMessageEditAction.class);
        Guild guild = mock(Guild.class);
        User user = mock(User.class);

        when(event.getName()).thenReturn("refresh");
        when(event.deferReply()).thenReturn(deferAction);
        when(deferAction.setEphemeral(true)).thenReturn(deferAction);
        when(event.getGuild()).thenReturn(guild);
        when(event.getUser()).thenReturn(user);
        when(guild.getId()).thenReturn("guild-456");
        when(user.getId()).thenReturn("user-789");
        when(event.getHook()).thenReturn(hook);

        when(simpleRedis.containsKey("guild-456:refresh:user-789")).thenReturn(false);

        when(discordClubManager.refreshSubmissions("guild-456", "user-789"))
                .thenThrow(new LeaderboardException("Error Title", "Error Description"));
        when(hook.editOriginalEmbeds(any(MessageEmbed.class))).thenReturn(editEmbedsAction);

        handler.onSlashCommandInteraction(event);

        verify(simpleRedis).put(eq("guild-456:refresh:user-789"), anyLong());
        verify(discordClubManager).refreshSubmissions("guild-456", "user-789");
        verify(hook).editOriginalEmbeds(any(MessageEmbed.class));
        verify(editEmbedsAction).queue();
    }

    @Test
    void testUnknownSlashCommandThrowsException() {
        SlashCommandInteractionEvent event = mock(SlashCommandInteractionEvent.class);
        when(event.getName()).thenReturn("unknown-command");

        assertThrows(IllegalArgumentException.class, () -> handler.onSlashCommandInteraction(event));

        verifyNoInteractions(discordClubManager);
    }
}

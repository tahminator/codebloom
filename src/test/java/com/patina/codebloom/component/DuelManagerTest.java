package com.patina.codebloom.component;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.github.javafaker.Faker;
import com.patina.codebloom.common.components.DuelManager;
import com.patina.codebloom.common.db.models.lobby.Lobby;
import com.patina.codebloom.common.db.models.lobby.LobbyStatus;
import com.patina.codebloom.common.db.models.lobby.player.LobbyPlayer;
import com.patina.codebloom.common.db.models.user.User;
import com.patina.codebloom.common.db.repos.lobby.LobbyQuestionRepository;
import com.patina.codebloom.common.db.repos.lobby.LobbyRepository;
import com.patina.codebloom.common.db.repos.lobby.player.LobbyPlayerRepository;
import com.patina.codebloom.common.db.repos.lobby.player.question.LobbyPlayerQuestionRepository;
import com.patina.codebloom.common.db.repos.question.QuestionRepository;
import com.patina.codebloom.common.db.repos.question.questionbank.QuestionBankRepository;
import com.patina.codebloom.common.db.repos.user.UserRepository;
import com.patina.codebloom.common.dto.lobby.DuelData;
import com.patina.codebloom.common.dto.lobby.LobbyDto;
import com.patina.codebloom.common.dto.user.UserDto;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

public class DuelManagerTest {

    private final DuelManager duelManager;
    private final Faker faker;

    private LobbyRepository lobbyRepository = mock(LobbyRepository.class);
    private LobbyQuestionRepository lobbyQuestionRepository = mock(LobbyQuestionRepository.class);
    private LobbyPlayerRepository lobbyPlayerRepository = mock(LobbyPlayerRepository.class);
    private LobbyPlayerQuestionRepository lobbyPlayerQuestionRepository = mock(LobbyPlayerQuestionRepository.class);
    private QuestionRepository questionRepository = mock(QuestionRepository.class);
    private QuestionBankRepository questionBankRepository = mock(QuestionBankRepository.class);
    private UserRepository userRepository = mock(UserRepository.class);

    public DuelManagerTest() {
        this.duelManager = new DuelManager(
                lobbyRepository,
                lobbyQuestionRepository,
                lobbyPlayerRepository,
                lobbyPlayerQuestionRepository,
                questionRepository,
                questionBankRepository,
                userRepository);
        this.faker = Faker.instance();
    }

    private Lobby.LobbyBuilder randomPartialLobby() {
        return Lobby.builder()
                .id(java.util.UUID.randomUUID().toString())
                .joinCode(faker.code().isbn10(false).toUpperCase())
                .createdAt(OffsetDateTime.now());
    }

    @Test
    void testGenerateDuelDataSuccess() {
        String lobbyId = java.util.UUID.randomUUID().toString();
        Lobby mockLobby = randomPartialLobby()
                .id(lobbyId)
                .status(LobbyStatus.ACTIVE)
                .expiresAt(OffsetDateTime.now().plusHours(1))
                .playerCount(2)
                .winnerId(Optional.empty())
                .build();

        when(lobbyRepository.findLobbyById(lobbyId)).thenReturn(Optional.of(mockLobby));

        DuelData result = duelManager.generateDuelData(lobbyId);

        assertNotNull(result);
        assertNotNull(result.getLobby());
        assertEquals(lobbyId, result.getLobby().getId());
        assertEquals(mockLobby.getJoinCode(), result.getLobby().getJoinCode());
        assertEquals(LobbyStatus.ACTIVE, result.getLobby().getStatus());
        assertEquals(2, result.getLobby().getPlayerCount());
        assertNull(result.getLobby().getWinnerId());
        verify(lobbyRepository, times(1)).findLobbyById(lobbyId);
    }

    @Test
    void testGenerateDuelDataWithWinner() {
        String lobbyId = java.util.UUID.randomUUID().toString();
        String winnerId = java.util.UUID.randomUUID().toString();
        Lobby mockLobby = randomPartialLobby()
                .id(lobbyId)
                .status(LobbyStatus.COMPLETED)
                .expiresAt(OffsetDateTime.now())
                .playerCount(2)
                .winnerId(Optional.of(winnerId))
                .build();

        when(lobbyRepository.findLobbyById(lobbyId)).thenReturn(Optional.of(mockLobby));

        DuelData result = duelManager.generateDuelData(lobbyId);

        assertNotNull(result);
        assertEquals(winnerId, result.getLobby().getWinnerId());
        assertEquals(LobbyStatus.COMPLETED, result.getLobby().getStatus());
    }

    @Test
    void testGenerateDuelDataCallsRepositoryOnce() {
        String lobbyId = java.util.UUID.randomUUID().toString();
        Lobby mockLobby = randomPartialLobby()
                .id(lobbyId)
                .status(LobbyStatus.ACTIVE)
                .expiresAt(OffsetDateTime.now())
                .playerCount(1)
                .build();

        when(lobbyRepository.findLobbyById(lobbyId)).thenReturn(Optional.of(mockLobby));

        duelManager.generateDuelData(lobbyId);

        verify(lobbyRepository, times(1)).findLobbyById(lobbyId);
    }

    @Test
    void testGenerateDuelDataMapsAllLobbyFields() {
        String lobbyId = java.util.UUID.randomUUID().toString();
        OffsetDateTime createdAt = OffsetDateTime.now().minusHours(2);
        OffsetDateTime expiresAt = OffsetDateTime.now().plusHours(1);
        String winnerId = java.util.UUID.randomUUID().toString();

        Lobby mockLobby = randomPartialLobby()
                .id(lobbyId)
                .status(LobbyStatus.CLOSED)
                .createdAt(createdAt)
                .expiresAt(expiresAt)
                .playerCount(4)
                .winnerId(Optional.of(winnerId))
                .build();

        when(lobbyRepository.findLobbyById(lobbyId)).thenReturn(Optional.of(mockLobby));

        DuelData result = duelManager.generateDuelData(lobbyId);
        LobbyDto resultDto = result.getLobby();

        assertEquals(lobbyId, resultDto.getId());
        assertEquals(mockLobby.getJoinCode(), resultDto.getJoinCode());
        assertEquals(LobbyStatus.CLOSED, resultDto.getStatus());
        assertEquals(createdAt, resultDto.getCreatedAt());
        assertEquals(expiresAt, resultDto.getExpiresAt());
        assertEquals(4, resultDto.getPlayerCount());
        assertEquals(winnerId, resultDto.getWinnerId());
    }

    @Test
    void testGenerateDuelDataReturnsNewInstanceEachTime() {
        String lobbyId = java.util.UUID.randomUUID().toString();
        Lobby mockLobby = randomPartialLobby()
                .id(lobbyId)
                .status(LobbyStatus.ACTIVE)
                .expiresAt(OffsetDateTime.now())
                .playerCount(2)
                .build();

        when(lobbyRepository.findLobbyById(lobbyId)).thenReturn(Optional.of(mockLobby));

        DuelData result1 = duelManager.generateDuelData(lobbyId);
        DuelData result2 = duelManager.generateDuelData(lobbyId);

        assertNotNull(result1);
        assertNotNull(result2);
        assertEquals(result1.getLobby().getId(), result2.getLobby().getId());
        verify(lobbyRepository, times(2)).findLobbyById(lobbyId);
    }

    @Test
    void testBuildPlayersInLobbyConvertsUsersToUserDtos() {
        String lobbyId = java.util.UUID.randomUUID().toString();
        String playerId1 = java.util.UUID.randomUUID().toString();
        String playerId2 = java.util.UUID.randomUUID().toString();

        LobbyPlayer player1 = LobbyPlayer.builder()
                .id(java.util.UUID.randomUUID().toString())
                .lobbyId(lobbyId)
                .playerId(playerId1)
                .points(0)
                .build();

        LobbyPlayer player2 = LobbyPlayer.builder()
                .id(java.util.UUID.randomUUID().toString())
                .lobbyId(lobbyId)
                .playerId(playerId2)
                .points(0)
                .build();

        User user1 = User.builder()
                .id(playerId1)
                .discordId("123456789")
                .discordName("TestUser1")
                .admin(false)
                .verifyKey("key1")
                .build();

        User user2 = User.builder()
                .id(playerId2)
                .discordId("987654321")
                .discordName("TestUser2")
                .admin(true)
                .verifyKey("key2")
                .build();

        when(lobbyPlayerRepository.findPlayersByLobbyId(lobbyId)).thenReturn(List.of(player1, player2));
        when(userRepository.getUserById(playerId1)).thenReturn(user1);
        when(userRepository.getUserById(playerId2)).thenReturn(user2);

        when(lobbyRepository.findLobbyById(lobbyId)).thenReturn(Optional.empty());
        when(lobbyQuestionRepository.findLobbyQuestionsByLobbyId(lobbyId)).thenReturn(List.of());

        DuelData result = duelManager.generateDuelData(lobbyId);

        assertNotNull(result.getPlayers());
        assertEquals(2, result.getPlayers().size());

        List<UserDto> players = result.getPlayers();
        UserDto resultUser1 = players.stream()
                .filter(u -> u.getId().equals(playerId1))
                .findFirst()
                .orElse(null);
        UserDto resultUser2 = players.stream()
                .filter(u -> u.getId().equals(playerId2))
                .findFirst()
                .orElse(null);

        assertNotNull(resultUser1);
        assertEquals(playerId1, resultUser1.getId());
        assertEquals("123456789", resultUser1.getDiscordId());
        assertEquals("TestUser1", resultUser1.getDiscordName());
        assertFalse(resultUser1.isAdmin());

        assertNotNull(resultUser2);
        assertEquals(playerId2, resultUser2.getId());
        assertEquals("987654321", resultUser2.getDiscordId());
        assertEquals("TestUser2", resultUser2.getDiscordName());
        assertTrue(resultUser2.isAdmin());

        verify(lobbyPlayerRepository, times(2)).findPlayersByLobbyId(lobbyId);
        verify(userRepository, times(1)).getUserById(playerId1);
        verify(userRepository, times(1)).getUserById(playerId2);
    }
}

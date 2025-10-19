package com.patina.codebloom.db.lobby.player;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.patina.codebloom.common.db.models.lobby.Lobby;
import com.patina.codebloom.common.db.models.lobby.LobbyStatus;
import com.patina.codebloom.common.db.models.lobby.player.LobbyPlayer;
import com.patina.codebloom.common.db.models.lobby.player.LobbyPlayerQuestion;
import com.patina.codebloom.common.db.repos.lobby.LobbyRepository;
import com.patina.codebloom.common.db.repos.lobby.player.LobbyPlayerRepository;
import com.patina.codebloom.common.db.repos.lobby.player.LobbyPlayerQuestionRepository;
import com.patina.codebloom.common.time.StandardizedOffsetDateTime;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation.class)
@Slf4j
public class LobbyPlayerQuestionRepositoryTest {
    private LobbyPlayerQuestionRepository lobbyPlayerQuestionRepository;
    private LobbyPlayerRepository lobbyPlayerRepository;
    private LobbyRepository lobbyRepository;
    private LobbyPlayerQuestion testLobbyPlayerQuestion;
    private LobbyPlayer testLobbyPlayer;
    private Lobby testLobby;
    private String mockPlayerId = "ed3bfe18-e42a-467f-b4fa-07e8da4d2555";
    private String mockQuestionId = "c9857a8a-9d0b-4d2e-b73c-3af2425bdca6";
    private String mockJoinCode = "QUESTION-TEST-" + UUID.randomUUID().toString().substring(0, 8);

    @Autowired
    public LobbyPlayerQuestionRepositoryTest(final LobbyPlayerQuestionRepository lobbyPlayerQuestionRepository,
                    final LobbyPlayerRepository lobbyPlayerRepository,
                    final LobbyRepository lobbyRepository) {
        this.lobbyPlayerQuestionRepository = lobbyPlayerQuestionRepository;
        this.lobbyPlayerRepository = lobbyPlayerRepository;
        this.lobbyRepository = lobbyRepository;
    }

    @BeforeAll
    void setup() {
        testLobby = Lobby.builder()
                        .joinCode(mockJoinCode)
                        .status(LobbyStatus.AVAILABLE)
                        .expiresAt(StandardizedOffsetDateTime.now().plusHours(1))
                        .playerCount(1)
                        .winnerId(null)
                        .build();

        lobbyRepository.createLobby(testLobby);

        testLobbyPlayer = LobbyPlayer.builder()
                        .lobbyId(testLobby.getId())
                        .playerId(mockPlayerId)
                        .points(100)
                        .build();

        lobbyPlayerRepository.createLobbyPlayer(testLobbyPlayer);

        testLobbyPlayerQuestion = LobbyPlayerQuestion.builder()
                        .lobbyPlayerId(testLobbyPlayer.getId())
                        .questionId(mockQuestionId)
                        .points(50)
                        .build();

        lobbyPlayerQuestionRepository.createLobbyPlayerQuestion(testLobbyPlayerQuestion);
    }

    @AfterAll
    void cleanup() {
        // Tests Delete By Lobby Player Id
        boolean isSuccessful = lobbyPlayerQuestionRepository.deleteLobbyPlayerQuestionByLobbyPlayerId(testLobbyPlayer.getId())
                        && lobbyPlayerRepository.deleteLobbyPlayerById(testLobbyPlayer.getId())
                        && lobbyRepository.deleteLobbyById(testLobby.getId());

        if (!isSuccessful) {
            fail("Failed to delete test lobby player question");
        }
    }

    @Test
    @Order(1)
    void testFindLobbyPlayerQuestionById() {
        LobbyPlayerQuestion foundLobbyPlayerQuestion = lobbyPlayerQuestionRepository.findLobbyPlayerQuestionById(testLobbyPlayerQuestion.getId());
        assertNotNull(foundLobbyPlayerQuestion);
        assertEquals(foundLobbyPlayerQuestion, testLobbyPlayerQuestion);
    }

    @Test
    @Order(2)
    void testFindQuestionsByLobbyPlayerId() {
        List<LobbyPlayerQuestion> lobbyPlayerQuestions = lobbyPlayerQuestionRepository.findQuestionsByLobbyPlayerId(testLobbyPlayer.getId());
        assertNotNull(lobbyPlayerQuestions);
        assertTrue(lobbyPlayerQuestions.contains(testLobbyPlayerQuestion));
    }

    @Test
    @Order(3)
    void testFindLobbyPlayerQuestionsByQuestionId() {
        List<LobbyPlayerQuestion> lobbyPlayerQuestions = lobbyPlayerQuestionRepository.findLobbyPlayerQuestionsByQuestionId(mockQuestionId);
        assertNotNull(lobbyPlayerQuestions);
        assertTrue(lobbyPlayerQuestions.contains(testLobbyPlayerQuestion));
    }

    @Test
    @Order(4)
    void testUpdateLobbyPlayerQuestion() {
        int newPoints = 250;
        testLobbyPlayerQuestion.setPoints(newPoints);

        boolean updateResult = lobbyPlayerQuestionRepository.updateLobbyPlayerQuestion(testLobbyPlayerQuestion);
        assertTrue(updateResult);

        LobbyPlayerQuestion updatedLobbyPlayerQuestion = lobbyPlayerQuestionRepository.findLobbyPlayerQuestionById(testLobbyPlayerQuestion.getId());
        assertNotNull(updatedLobbyPlayerQuestion);
        assertEquals(newPoints, updatedLobbyPlayerQuestion.getPoints());
    }

    @Test
    @Order(5)
    void testDeleteLobbyPlayerQuestionById() {
        LobbyPlayerQuestion deletableLobbyPlayerQuestion = LobbyPlayerQuestion.builder()
                        .lobbyPlayerId(testLobbyPlayer.getId())
                        .questionId(mockQuestionId)
                        .points(200)
                        .build();

        lobbyPlayerQuestionRepository.createLobbyPlayerQuestion(deletableLobbyPlayerQuestion);

        LobbyPlayerQuestion found = lobbyPlayerQuestionRepository.findLobbyPlayerQuestionById(deletableLobbyPlayerQuestion.getId());
        assertNotNull(found);
        assertEquals(deletableLobbyPlayerQuestion.getId(), found.getId());

        boolean deleted = lobbyPlayerQuestionRepository.deleteLobbyPlayerQuestionById(deletableLobbyPlayerQuestion.getId());
        assertTrue(deleted);

        LobbyPlayerQuestion deletedFetched = lobbyPlayerQuestionRepository.findLobbyPlayerQuestionById(deletableLobbyPlayerQuestion.getId());
        assertNull(deletedFetched);
    }
}
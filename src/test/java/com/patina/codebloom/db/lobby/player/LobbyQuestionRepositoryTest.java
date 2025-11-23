package com.patina.codebloom.db.lobby.player;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;
import java.util.Optional;
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
import com.patina.codebloom.common.db.models.lobby.LobbyQuestion;
import com.patina.codebloom.common.db.models.lobby.LobbyStatus;
import com.patina.codebloom.common.db.models.question.bank.QuestionBank;
import com.patina.codebloom.common.db.models.question.QuestionDifficulty;
import com.patina.codebloom.common.db.repos.lobby.LobbyRepository;
import com.patina.codebloom.common.db.repos.lobby.LobbyQuestionRepository;
import com.patina.codebloom.common.db.repos.question.questionbank.QuestionBankRepository;
import com.patina.codebloom.common.time.StandardizedOffsetDateTime;
import com.patina.codebloom.db.BaseRepositoryTest;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation.class)
@Slf4j
public class LobbyQuestionRepositoryTest extends BaseRepositoryTest {
    private LobbyQuestionRepository lobbyQuestionRepository;
    private LobbyRepository lobbyRepository;
    private QuestionBankRepository questionBankRepository;
    private LobbyQuestion testLobbyQuestion;
    private Lobby testLobby;
    private QuestionBank testQuestionBank;
    private String mockJoinCode = "LOBBY-Q-TEST-" + UUID.randomUUID().toString().substring(0, 8);

    @Autowired
    public LobbyQuestionRepositoryTest(final LobbyQuestionRepository lobbyQuestionRepository,
                    final LobbyRepository lobbyRepository,
                    final QuestionBankRepository questionBankRepository) {
        this.lobbyQuestionRepository = lobbyQuestionRepository;
        this.lobbyRepository = lobbyRepository;
        this.questionBankRepository = questionBankRepository;
    }

    @BeforeAll
    void setup() {
        testLobby = Lobby.builder()
                        .joinCode(mockJoinCode)
                        .status(LobbyStatus.AVAILABLE)
                        .expiresAt(StandardizedOffsetDateTime.now().plusHours(1))
                        .playerCount(1)
                        .build();

        lobbyRepository.createLobby(testLobby);

        testQuestionBank = QuestionBank.builder()
                        .questionSlug("test-question")
                        .questionDifficulty(QuestionDifficulty.Easy)
                        .questionTitle("Test Question")
                        .questionNumber(1234)
                        .questionLink("https://leetcode.com/problems/test-question")
                        .acceptanceRate(50.0f)
                        .build();

        questionBankRepository.createQuestion(testQuestionBank);

        testLobbyQuestion = LobbyQuestion.builder()
                        .lobbyId(testLobby.getId())
                        .questionBankId(testQuestionBank.getId())
                        .userSolvedCount(0)
                        .createdAt(StandardizedOffsetDateTime.now())
                        .build();

        lobbyQuestionRepository.createLobbyQuestion(testLobbyQuestion);
    }

    @AfterAll
    void cleanup() {
        boolean isSuccessful = lobbyQuestionRepository.deleteLobbyQuestionById(testLobbyQuestion.getId())
                        && lobbyRepository.deleteLobbyById(testLobby.getId())
                        && questionBankRepository.deleteQuestionById(testQuestionBank.getId());

        if (!isSuccessful) {
            fail("Failed to delete test lobby question");
        }
    }

    @Test
    @Order(1)
    void testFindLobbyQuestionById() {
        var foundLobbyQuestion = lobbyQuestionRepository.findLobbyQuestionById(testLobbyQuestion.getId());
        assertTrue(foundLobbyQuestion.isPresent());
        assertEquals(testLobbyQuestion, foundLobbyQuestion.get());
    }

    @Test
    @Order(2)
    void testFindLobbyQuestionsByLobbyId() {
        List<LobbyQuestion> lobbyQuestions = lobbyQuestionRepository.findLobbyQuestionsByLobbyId(testLobby.getId());
        assertNotNull(lobbyQuestions);
        assertTrue(lobbyQuestions.contains(testLobbyQuestion));
    }

    @Test
    @Order(3)
    void testFindLobbyQuestionByLobbyIdAndQuestionBankId() {
        List<LobbyQuestion> lobbyQuestions = lobbyQuestionRepository.findLobbyQuestionByLobbyIdAndQuestionBankId(testLobby.getId(), testQuestionBank.getId());
        assertNotNull(lobbyQuestions);
        assertTrue(lobbyQuestions.contains(testLobbyQuestion));
    }

    @Test
    @Order(4)
    void testFindMostRecentLobbyQuestionByLobbyId() {
        Optional<LobbyQuestion> mostRecentQuestion = lobbyQuestionRepository.findMostRecentLobbyQuestionByLobbyId(testLobby.getId());
        assertTrue(mostRecentQuestion.isPresent());
        assertEquals(testLobbyQuestion, mostRecentQuestion.get());
    }

    @Test
    @Order(5)
    void testFindAllLobbyQuestions() {
        List<LobbyQuestion> allQuestions = lobbyQuestionRepository.findAllLobbyQuestions();
        assertNotNull(allQuestions);
        assertTrue(allQuestions.contains(testLobbyQuestion));
    }

    @Test
    @Order(6)
    void testUpdateQuestionLobby() {
        int newSolvedCount = 5;
        testLobbyQuestion.setUserSolvedCount(newSolvedCount);

        boolean updateResult = lobbyQuestionRepository.updateQuestionLobby(testLobbyQuestion);
        assertTrue(updateResult);

        LobbyQuestion updatedLobbyQuestion = lobbyQuestionRepository.findLobbyQuestionById(testLobbyQuestion.getId()).orElseThrow();
        assertEquals(testLobbyQuestion, updatedLobbyQuestion);
    }
}

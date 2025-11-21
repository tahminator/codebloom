package com.patina.codebloom.common.components;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.patina.codebloom.common.db.repos.lobby.LobbyRepository;
import com.patina.codebloom.common.dto.lobby.DuelData;
import com.patina.codebloom.common.dto.lobby.LobbyDto;
import com.patina.codebloom.common.db.models.question.Question;
import org.springframework.stereotype.Component;
import com.patina.codebloom.common.db.repos.lobby.player.LobbyPlayerRepository;
import com.patina.codebloom.common.db.repos.lobby.player.question.LobbyPlayerQuestionRepository;
import com.patina.codebloom.common.db.repos.question.QuestionRepository;
import com.patina.codebloom.common.db.models.lobby.player.LobbyPlayerQuestion;

@Component
public class DuelManager {

    private final LobbyRepository lobbyRepository;
    private final LobbyPlayerRepository lobbyPlayerRepository;
    private final LobbyPlayerQuestionRepository lobbyPlayerQuestionRepository;
    private final QuestionRepository questionRepository;

    public DuelManager(final LobbyRepository lobbyRepository,
                    final LobbyPlayerRepository lobbyPlayerRepository,
                    final LobbyPlayerQuestionRepository lobbyPlayerQuestionRepository,
                    final QuestionRepository questionRepository) {
        this.lobbyRepository = lobbyRepository;
        this.lobbyPlayerRepository = lobbyPlayerRepository;
        this.lobbyPlayerQuestionRepository = lobbyPlayerQuestionRepository;
        this.questionRepository = questionRepository;
    }

    public DuelData generateDuelData(final String lobbyId) {
        var fetchedLobby = lobbyRepository
            .findLobbyById(lobbyId)
            .map(LobbyDto::fromLobby)
            .orElse(null);

        List<String> uniqueQuestionIds = lobbyPlayerQuestionRepository.findUniqueQuestionIdsByLobbyId(lobbyId);

        List<Question> questions = uniqueQuestionIds.stream()
                        .map(questionRepository::getQuestionById)
                        .collect(Collectors.toList());

        return DuelData.builder()
                        .lobby(fetchedLobby)
                        .questions(questions)
                        .playerQuestions(buildPlayerSolvedQuestionsMap(lobbyId))
                        .build();
    }
    private Map<String, List<Question>> buildPlayerSolvedQuestionsMap(final String lobbyId) {
        Map<String, List<Question>> playerQuestionsMap = new HashMap<>();

        var lobbyPlayers = lobbyPlayerRepository.findPlayersByLobbyId(lobbyId);

        for (var player : lobbyPlayers) {
            var lobbyPlayerQuestions = lobbyPlayerQuestionRepository.findQuestionsByLobbyPlayerId(player.getId());

            List<Question> playerQuestions = lobbyPlayerQuestions.stream()
                            .filter(lpq -> lpq.getQuestionId().isPresent())
                            .map(lpq -> questionRepository.getQuestionById(lpq.getQuestionId().get()))
                            .collect(Collectors.toList());

            playerQuestionsMap.put(player.getPlayerId(), playerQuestions);
        }

        return playerQuestionsMap;
    }
}

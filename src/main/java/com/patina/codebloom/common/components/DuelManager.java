package com.patina.codebloom.common.components;

import com.patina.codebloom.common.db.models.question.Question;
import com.patina.codebloom.common.db.models.question.bank.QuestionBank;
import com.patina.codebloom.common.db.repos.lobby.LobbyQuestionRepository;
import com.patina.codebloom.common.db.repos.lobby.LobbyRepository;
import com.patina.codebloom.common.db.repos.lobby.player.LobbyPlayerRepository;
import com.patina.codebloom.common.db.repos.lobby.player.question.LobbyPlayerQuestionRepository;
import com.patina.codebloom.common.db.repos.question.QuestionRepository;
import com.patina.codebloom.common.db.repos.question.questionbank.QuestionBankRepository;
import com.patina.codebloom.common.dto.lobby.DuelData;
import com.patina.codebloom.common.dto.lobby.LobbyDto;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class DuelManager {

    private final LobbyRepository lobbyRepository;
    private final LobbyQuestionRepository lobbyQuestionRepository;
    private final LobbyPlayerRepository lobbyPlayerRepository;
    private final LobbyPlayerQuestionRepository lobbyPlayerQuestionRepository;
    private final QuestionRepository questionRepository;
    private final QuestionBankRepository questionBankRepository;

    public DuelManager(
        final LobbyRepository lobbyRepository,
        final LobbyQuestionRepository lobbyQuestionRepository,
        final LobbyPlayerRepository lobbyPlayerRepository,
        final LobbyPlayerQuestionRepository lobbyPlayerQuestionRepository,
        final QuestionRepository questionRepository,
        final QuestionBankRepository questionBankRepository
    ) {
        this.lobbyRepository = lobbyRepository;
        this.lobbyQuestionRepository = lobbyQuestionRepository;
        this.lobbyPlayerRepository = lobbyPlayerRepository;
        this.lobbyPlayerQuestionRepository = lobbyPlayerQuestionRepository;
        this.questionRepository = questionRepository;
        this.questionBankRepository = questionBankRepository;
    }

    public DuelData generateDuelData(final String lobbyId) {
        var fetchedLobby = lobbyRepository
            .findLobbyById(lobbyId)
            .map(LobbyDto::fromLobby)
            .orElse(null);
            
        List<QuestionBank> lobbyQuestions = lobbyQuestionRepository
            .findLobbyQuestionsByLobbyId(lobbyId)
            .stream()
            .map(lq ->
                questionBankRepository.getQuestionById(lq.getQuestionBankId())
            )
            .collect(Collectors.toList());

        return DuelData.builder()
            .lobby(fetchedLobby)
            .questions(lobbyQuestions)
            .playerQuestions(buildPlayerSolvedQuestionsMap(lobbyId))
            .build();
    }

    private Map<String, List<QuestionBank>> buildPlayerSolvedQuestionsMap(
        final String lobbyId
    ) {
        Map<String, List<QuestionBank>> playerQuestionsMap = new HashMap<>();

        var lobbyPlayers = lobbyPlayerRepository.findPlayersByLobbyId(lobbyId);

        for (var player : lobbyPlayers) {
            var lobbyPlayerQuestions =
                lobbyPlayerQuestionRepository.findQuestionsByLobbyPlayerId(
                    player.getId()
                );

            List<QuestionBank> playerQuestions = lobbyPlayerQuestions
                .stream()
                .filter(lpq -> lpq.getQuestionId().isPresent())
                .map(lpq -> {
                    Question question = questionRepository.getQuestionById(
                        lpq.getQuestionId().get()
                    );
                    return questionBankRepository.getQuestionBySlug(
                        question.getQuestionSlug()
                    );
                })
                .collect(Collectors.toList());

            playerQuestionsMap.put(player.getPlayerId(), playerQuestions);
        }

        return playerQuestionsMap;
    }
}

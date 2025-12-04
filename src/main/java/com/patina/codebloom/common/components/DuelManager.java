package com.patina.codebloom.common.components;

import com.patina.codebloom.common.db.repos.lobby.LobbyQuestionRepository;
import com.patina.codebloom.common.db.repos.lobby.LobbyRepository;
import com.patina.codebloom.common.db.repos.lobby.player.LobbyPlayerRepository;
import com.patina.codebloom.common.db.repos.lobby.player.question.LobbyPlayerQuestionRepository;
import com.patina.codebloom.common.db.repos.question.QuestionRepository;
import com.patina.codebloom.common.db.repos.question.questionbank.QuestionBankRepository;
import com.patina.codebloom.common.db.repos.user.UserRepository;
import com.patina.codebloom.common.dto.lobby.DuelData;
import com.patina.codebloom.common.dto.lobby.LobbyDto;
import com.patina.codebloom.common.dto.question.QuestionBankDto;
import com.patina.codebloom.common.dto.question.QuestionDto;
import com.patina.codebloom.common.dto.user.UserDto;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
    private final UserRepository userRepository;

    public DuelManager(
            final LobbyRepository lobbyRepository,
            final LobbyQuestionRepository lobbyQuestionRepository,
            final LobbyPlayerRepository lobbyPlayerRepository,
            final LobbyPlayerQuestionRepository lobbyPlayerQuestionRepository,
            final QuestionRepository questionRepository,
            final QuestionBankRepository questionBankRepository,
            final UserRepository userRepository) {
        this.lobbyRepository = lobbyRepository;
        this.lobbyQuestionRepository = lobbyQuestionRepository;
        this.lobbyPlayerRepository = lobbyPlayerRepository;
        this.lobbyPlayerQuestionRepository = lobbyPlayerQuestionRepository;
        this.questionRepository = questionRepository;
        this.questionBankRepository = questionBankRepository;
        this.userRepository = userRepository;
    }

    public DuelData generateDuelData(final String lobbyId) {
        var fetchedLobby =
                lobbyRepository.findLobbyById(lobbyId).map(LobbyDto::fromLobby).orElse(null);

        List<QuestionBankDto> lobbyQuestions = lobbyQuestionRepository.findLobbyQuestionsByLobbyId(lobbyId).stream()
                .map(lq -> questionBankRepository.getQuestionById(lq.getQuestionBankId()))
                .map(QuestionBankDto::fromQuestionBank)
                .collect(Collectors.toList());

        return DuelData.builder()
                .lobby(fetchedLobby)
                .questions(lobbyQuestions)
                .players(buildPlayersInLobby(lobbyId))
                .playerQuestions(buildPlayerSolvedQuestionsMap(lobbyId))
                .build();
    }

    private Map<String, List<QuestionDto>> buildPlayerSolvedQuestionsMap(final String lobbyId) {
        Map<String, List<QuestionDto>> playerQuestionsMap = new HashMap<>();

        var lobbyPlayers = lobbyPlayerRepository.findPlayersByLobbyId(lobbyId);

        for (var player : lobbyPlayers) {
            var lobbyPlayerQuestions = lobbyPlayerQuestionRepository.findQuestionsByLobbyPlayerId(player.getId());

            List<QuestionDto> playerQuestions = lobbyPlayerQuestions.stream()
                    .filter(lpq -> lpq.getQuestionId().isPresent())
                    .map(lpq -> questionRepository.getQuestionById(
                            lpq.getQuestionId().get()))
                    .map(QuestionDto::fromQuestion)
                    .collect(Collectors.toList());

            playerQuestionsMap.put(player.getPlayerId(), playerQuestions);
        }

        return playerQuestionsMap;
    }

    private List<UserDto> buildPlayersInLobby(final String lobbyId) {
        var lobbyPlayers = lobbyPlayerRepository.findPlayersByLobbyId(lobbyId);

        return lobbyPlayers.stream()
                .map(lobbyPlayer -> userRepository.getUserById(lobbyPlayer.getPlayerId()))
                .filter(Objects::nonNull)
                .map(UserDto::fromUser)
                .collect(Collectors.toList());
    }
}

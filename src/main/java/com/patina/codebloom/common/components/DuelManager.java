package com.patina.codebloom.common.components;

import com.patina.codebloom.common.db.models.lobby.LobbyStatus;
import com.patina.codebloom.common.db.models.lobby.player.LobbyPlayer;
import com.patina.codebloom.common.db.repos.lobby.LobbyQuestionRepository;
import com.patina.codebloom.common.db.repos.lobby.LobbyRepository;
import com.patina.codebloom.common.db.repos.lobby.player.LobbyPlayerRepository;
import com.patina.codebloom.common.db.repos.lobby.player.question.LobbyPlayerQuestionRepository;
import com.patina.codebloom.common.db.repos.question.QuestionRepository;
import com.patina.codebloom.common.db.repos.question.questionbank.QuestionBankRepository;
import com.patina.codebloom.common.dto.lobby.DuelData;
import com.patina.codebloom.common.dto.lobby.LobbyDto;
import com.patina.codebloom.common.dto.question.QuestionBankDto;
import com.patina.codebloom.common.dto.question.QuestionDto;
import com.patina.codebloom.common.time.StandardizedOffsetDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
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
            final QuestionBankRepository questionBankRepository) {
        this.lobbyRepository = lobbyRepository;
        this.lobbyQuestionRepository = lobbyQuestionRepository;
        this.lobbyPlayerRepository = lobbyPlayerRepository;
        this.lobbyPlayerQuestionRepository = lobbyPlayerQuestionRepository;
        this.questionRepository = questionRepository;
        this.questionBankRepository = questionBankRepository;
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

    public void endDuel(final String lobbyId) throws DuelException {
        try {
            var activeLobby = lobbyRepository
                    .findLobbyById(lobbyId)
                    .orElseThrow(() -> new DuelException(HttpStatus.NOT_FOUND, "Lobby cannot be found."));

            if (activeLobby.getExpiresAt().isAfter(StandardizedOffsetDateTime.now())) {
                throw new DuelException(HttpStatus.CONFLICT, "This lobby has not completed it's duration yet");
            }

            var lobbyPlayers = lobbyPlayerRepository.findPlayersByLobbyId(activeLobby.getId());

            var winner = lobbyPlayers.stream()
                    .max(Comparator.comparing(LobbyPlayer::getPoints))
                    .orElseThrow(() -> new DuelException(
                            HttpStatus.NOT_FOUND, "No winner can be found because there are no players in the duel."));

            activeLobby.setWinnerId(Optional.of(winner.getId()));
            activeLobby.setExpiresAt(StandardizedOffsetDateTime.now());
            activeLobby.setStatus(LobbyStatus.COMPLETED);

            lobbyRepository.updateLobby(activeLobby);
        } catch (DuelException e) {
            throw e;
        } catch (Exception e) {
            throw new DuelException(e);
        }
    }
}

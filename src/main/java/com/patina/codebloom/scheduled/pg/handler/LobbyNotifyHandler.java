package com.patina.codebloom.scheduled.pg.handler;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.patina.codebloom.common.components.DuelData;
import com.patina.codebloom.common.components.DuelManager;
import com.patina.codebloom.common.dto.ApiResponder;
import com.patina.codebloom.common.utils.sse.SseWrapper;

import lombok.extern.slf4j.Slf4j;

@Component
@Profile("!ci")
@Slf4j
public class LobbyNotifyHandler {
    private final ConcurrentHashMap<String, SseWrapper<ApiResponder<DuelData>>> partyIdToSseEmitter;
    private final DuelManager duelManager;

    public LobbyNotifyHandler(final DuelManager duelManager) {
        this.partyIdToSseEmitter = new ConcurrentHashMap<>();
        this.duelManager = duelManager;
    }

    private ApiResponder<DuelData> getData(final String partyId) {
        try {
            DuelData duelData = duelManager.generateDuelData(partyId);
            return ApiResponder.success("Data retrieved!", duelData);
        } catch (Exception e) {
            log.error("failed to get duel data", e);
            return ApiResponder.failure(String.format("Something went wrong: %s", e.getMessage()));
        }
    }

    @Async
    public void register(final String partyId, final SseWrapper<ApiResponder<DuelData>> sseEmitter) {
        partyIdToSseEmitter.computeIfAbsent(partyId, _ -> sseEmitter);
        try {
            sseEmitter.sendData(getData(partyId));
        } catch (Exception e) {
            sseEmitter.completeWithError(e);
            partyIdToSseEmitter.remove(partyId);
        }
    }

    public void deregister(final String partyId) {
        partyIdToSseEmitter.remove(partyId);
    }

    @Async
    public void handle(final String partyId) throws IOException {
        if (!partyIdToSseEmitter.containsKey(partyId)) {
            log.error("Failed to find SSE emitter for the given party");
            return;
        }
        var sseEmitter = partyIdToSseEmitter.get(partyId);
        if (sseEmitter != null) {
            sseEmitter.sendData(getData(partyId));
        }
    }
}

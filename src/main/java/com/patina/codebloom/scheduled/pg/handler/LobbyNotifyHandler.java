package com.patina.codebloom.scheduled.pg.handler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.google.common.annotations.VisibleForTesting;
import com.patina.codebloom.common.components.DuelData;
import com.patina.codebloom.common.components.DuelManager;
import com.patina.codebloom.common.dto.ApiResponder;
import com.patina.codebloom.common.utils.sse.SseWrapper;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Component
@Profile("!ci | thread")
@Slf4j
public class LobbyNotifyHandler {
    @VisibleForTesting
    @Getter(AccessLevel.PACKAGE)
    private final ConcurrentHashMap<String, Set<SseWrapper<ApiResponder<DuelData>>>> partyIdToSseEmitters;
    private final DuelManager duelManager;

    public LobbyNotifyHandler(final DuelManager duelManager) {
        this.partyIdToSseEmitters = new ConcurrentHashMap<>();
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
        var emitterSet = partyIdToSseEmitters.computeIfAbsent(partyId, _ -> ConcurrentHashMap.newKeySet());
        if (!emitterSet.contains(sseEmitter)) {
            emitterSet.add(sseEmitter);
        }
        try {
            sseEmitter.sendData(getData(partyId));
        } catch (Exception e) {
            sseEmitter.completeWithError(e);
            emitterSet.remove(sseEmitter);
            if (emitterSet.isEmpty()) {
                partyIdToSseEmitters.remove(partyId);
            }
        }
    }

    public void deregister(final String partyId) {
        partyIdToSseEmitters.remove(partyId);
    }

    @Async
    public void handle(final String partyId) throws IOException {
        var emitterSet = partyIdToSseEmitters.get(partyId);
        if (emitterSet == null || emitterSet.isEmpty()) {
            log.error("Failed to find SSE emitters for the given party");
            return;
        }

        List<SseWrapper<ApiResponder<DuelData>>> failedEmitters = new ArrayList<>();

        for (var sseEmitter : emitterSet) {
            try {
                sseEmitter.sendData(getData(partyId));
            } catch (Exception e) {
                log.error("Failed to send SSE data to emitter", e);
                failedEmitters.add(sseEmitter);
            }
        }

        for (var failedEmitter : failedEmitters) {
            emitterSet.remove(failedEmitter);
        }

        if (emitterSet.isEmpty()) {
            partyIdToSseEmitters.remove(partyId);
        }
    }
}

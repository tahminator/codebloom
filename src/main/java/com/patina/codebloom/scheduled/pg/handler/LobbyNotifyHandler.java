package com.patina.codebloom.scheduled.pg.handler;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.patina.codebloom.common.components.DuelData;
import com.patina.codebloom.common.components.DuelManager;

import lombok.extern.slf4j.Slf4j;

@Component
@Profile("!ci")
@Slf4j
public class LobbyNotifyHandler {
    private final ConcurrentHashMap<String, SseEmitter> partyIdToSseEmitter;
    private final DuelManager duelManager;

    public LobbyNotifyHandler(final DuelManager duelManager) {
        this.partyIdToSseEmitter = new ConcurrentHashMap<>();
        this.duelManager = duelManager;
    }

    public void register(final String partyId, final SseEmitter sseEmitter) {
        partyIdToSseEmitter.computeIfAbsent(partyId, _ -> sseEmitter);
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
            DuelData duelData = duelManager.generateDuelData(partyId);
            sseEmitter.send(SseEmitter.event().data(duelData).build());
        }
    }
}

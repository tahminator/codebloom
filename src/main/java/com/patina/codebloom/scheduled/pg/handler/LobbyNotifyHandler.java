package com.patina.codebloom.scheduled.pg.handler;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.patina.codebloom.common.components.DuelData;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@Profile("!ci")
public class LobbyNotifyHandler {
    private final ConcurrentHashMap<String, SseEmitter> partyIdToSseEmitter;

    public LobbyNotifyHandler() {
        this.partyIdToSseEmitter = new ConcurrentHashMap<>();
    }

    public void register(final String partyId, final SseEmitter sseEmitter) {
        partyIdToSseEmitter.computeIfAbsent(partyId, _ -> sseEmitter);
    }

    public void deregister(final String partyId) {
        partyIdToSseEmitter.remove(partyId);
    }

    @Async
    public void handle(final String partyId) throws IOException {
        // TODO: Call method here. Get data, then pass to sse emitter.
        if (!partyIdToSseEmitter.containsKey(partyId)) {
            log.error("Failed to find SSE emitter for the given party");
            return;
        }
        var sseEmitter = partyIdToSseEmitter.get(partyId);
        if (sseEmitter != null) {
            sseEmitter.send(DuelData.DEFAULT);
        }
    }
}

package com.patina.codebloom.scheduled.pg.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.patina.codebloom.common.components.duel.DuelManager;
import com.patina.codebloom.common.dto.ApiResponder;
import com.patina.codebloom.common.dto.lobby.DuelData;
import com.patina.codebloom.common.utils.sse.SseWrapper;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class LobbyNotifyHandlerTest {

    private DuelManager duelManager;
    private SseWrapper<ApiResponder<DuelData>> sseWrapper;
    private LobbyNotifyHandler lobbyNotifyHandler;
    private ConcurrentHashMap<String, Set<SseWrapper<ApiResponder<DuelData>>>> partyIdToSseEmitters;

    @BeforeEach
    void setUp() {
        duelManager = mock(DuelManager.class);
        sseWrapper = mock(SseWrapper.class);
        lobbyNotifyHandler = new LobbyNotifyHandler(duelManager);
        partyIdToSseEmitters = lobbyNotifyHandler.getPartyIdToSseEmitters();
    }

    @Test
    void testRegisterAddsEmitterToSet() throws IOException {
        String partyId = "test-party-id";
        DuelData mockDuelData = DuelData.DEFAULT;
        when(duelManager.generateDuelData(partyId)).thenReturn(mockDuelData);

        lobbyNotifyHandler.register(partyId, sseWrapper);

        assertTrue(partyIdToSseEmitters.containsKey(partyId));
        assertTrue(partyIdToSseEmitters.get(partyId).contains(sseWrapper));
        verify(sseWrapper, times(1)).sendData(any(ApiResponder.class));
    }

    @Test
    void testRegisterSendsInitialData() throws IOException {
        String partyId = "test-party-id";
        DuelData mockDuelData = DuelData.DEFAULT;
        when(duelManager.generateDuelData(partyId)).thenReturn(mockDuelData);

        lobbyNotifyHandler.register(partyId, sseWrapper);

        verify(sseWrapper, times(1)).sendData(any(ApiResponder.class));
        verify(duelManager, times(1)).generateDuelData(partyId);
    }

    @Test
    void testRegisterHandlesExceptionAndRemovesEmitter() throws IOException {
        String partyId = "test-party-id";
        DuelData mockDuelData = DuelData.DEFAULT;
        when(duelManager.generateDuelData(partyId)).thenReturn(mockDuelData);
        doThrow(new IOException("SSE connection failed")).when(sseWrapper).sendData(any(ApiResponder.class));

        lobbyNotifyHandler.register(partyId, sseWrapper);

        verify(sseWrapper, times(1)).completeWithError(any(IOException.class));
        assertFalse(partyIdToSseEmitters.containsKey(partyId));
    }

    @Test
    void testRegisterDoesNotAddDuplicateEmitter() throws IOException {
        String partyId = "test-party-id";
        DuelData mockDuelData = DuelData.DEFAULT;
        when(duelManager.generateDuelData(partyId)).thenReturn(mockDuelData);

        lobbyNotifyHandler.register(partyId, sseWrapper);
        lobbyNotifyHandler.register(partyId, sseWrapper);

        assertEquals(1, partyIdToSseEmitters.get(partyId).size());
        verify(sseWrapper, times(2)).sendData(any(ApiResponder.class));
    }

    @Test
    void testDeregisterRemovesPartyId() throws IOException {
        String partyId = "test-party-id";
        DuelData mockDuelData = DuelData.DEFAULT;
        when(duelManager.generateDuelData(partyId)).thenReturn(mockDuelData);

        lobbyNotifyHandler.register(partyId, sseWrapper);
        assertTrue(partyIdToSseEmitters.containsKey(partyId));

        lobbyNotifyHandler.deregister(partyId);
        assertFalse(partyIdToSseEmitters.containsKey(partyId));
    }

    @Test
    void testHandleWithValidPartyId() throws IOException {
        String partyId = "test-party-id";
        DuelData mockDuelData = DuelData.DEFAULT;
        when(duelManager.generateDuelData(partyId)).thenReturn(mockDuelData);

        lobbyNotifyHandler.register(partyId, sseWrapper);
        lobbyNotifyHandler.handle(partyId);

        verify(sseWrapper, times(2)).sendData(any(ApiResponder.class));
        verify(duelManager, times(2)).generateDuelData(partyId);
    }

    @Test
    void testHandleWithInvalidPartyId() throws IOException {
        String partyId = "non-existent-party";

        lobbyNotifyHandler.handle(partyId);

        verify(sseWrapper, never()).sendData(any(ApiResponder.class));
        verify(duelManager, never()).generateDuelData(anyString());
    }

    @Test
    void testGetDataSuccess() throws IOException {
        String partyId = "test-party-id";
        DuelData mockDuelData = DuelData.DEFAULT;
        when(duelManager.generateDuelData(partyId)).thenReturn(mockDuelData);

        lobbyNotifyHandler.register(partyId, sseWrapper);

        verify(duelManager, times(1)).generateDuelData(partyId);
        verify(sseWrapper, times(1)).sendData(any(ApiResponder.class));
    }

    @Test
    void testGetDataFailure() throws IOException {
        String partyId = "test-party-id";
        when(duelManager.generateDuelData(partyId)).thenThrow(new RuntimeException("Database connection failed"));

        lobbyNotifyHandler.register(partyId, sseWrapper);

        verify(sseWrapper, times(1)).sendData(any(ApiResponder.class));
    }

    @Test
    void testRegisterCleansUpEmptySetAfterException() throws IOException {
        String partyId = "test-party-id";
        DuelData mockDuelData = DuelData.DEFAULT;
        when(duelManager.generateDuelData(partyId)).thenReturn(mockDuelData);
        doThrow(new IOException("SSE connection failed")).when(sseWrapper).sendData(any(ApiResponder.class));

        lobbyNotifyHandler.register(partyId, sseWrapper);

        assertFalse(partyIdToSseEmitters.containsKey(partyId));
    }
}

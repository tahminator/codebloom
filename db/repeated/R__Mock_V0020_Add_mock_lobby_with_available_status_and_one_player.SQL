INSERT INTO "Lobby" 
    (id, "joinCode", status, "createdAt", "expiresAt", "playerCount", "winnerId")
VALUES
    ('83a5169b-a850-4f82-b06f-25b4c23a3be3', 'ABX35Z', 'AVAILABLE'::"LobbyStatusEnum", DEFAULT, NOW() + INTERVAL '30 minutes', 1, NULL);


INSERT INTO "LobbyPlayer"
    (id, "lobbyId", "playerId", points)
VALUES 
    ('fc8668ba-6785-4737-a356-21d0335f07d0', '83a5169b-a850-4f82-b06f-25b4c23a3be3', '0c9b2e77-74cc-4b9e-b7f9-cfe0fd05e50b', 0);




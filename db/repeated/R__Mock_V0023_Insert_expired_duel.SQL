INSERT INTO "Lobby" 
    (id, "joinCode", status, "createdAt", "expiresAt", "playerCount", "winnerId")
VALUES
    ('015843d1-4819-4936-9576-d74bf89a819e', 'AB4109', 'ACTIVE', DEFAULT, NOW() - INTERVAL '30 minutes', 2, NULL);

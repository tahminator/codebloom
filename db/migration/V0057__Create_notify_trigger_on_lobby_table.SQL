CREATE FUNCTION "notifyOnUpsertLobby"()
RETURNS trigger AS $$
BEGIN
    PERFORM pg_notify(
        'upsertLobbyChannel',
        NEW.id::text
    );
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER "lobbyUpsertNotify"
AFTER INSERT OR UPDATE ON public."Lobby"
FOR EACH ROW
EXECUTE FUNCTION "notifyOnUpsertLobby"();

CREATE FUNCTION "notifyOnNewJob"()
RETURNS trigger AS $$
BEGIN
    PERFORM pg_notify(
        'jobInsertChannel',
        json_build_object(
            'id', NEW.id
        )::text
    );
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER "jobInsertNotify"
AFTER INSERT ON public."Job"
FOR EACH ROW
EXECUTE FUNCTION "notifyOnNewJob"();

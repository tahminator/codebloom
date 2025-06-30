DO $$
BEGIN
    
    INSERT INTO "User" 
        (id, "discordId", "discordName", "leetcodeUsername", "nickname", admin)
    VALUES
        ('dba9600f-c762-4378-8b9a-94015c0121e1', '123456789123456789123456789123', 'JohnDoe', 'JohnDoe123', 'John', true);


END $$
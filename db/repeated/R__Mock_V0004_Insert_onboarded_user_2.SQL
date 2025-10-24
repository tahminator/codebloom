DO $$
BEGIN
    
    INSERT INTO "User" 
        (id, "discordId", "discordName", "leetcodeUsername", "nickname", admin)
    VALUES
        ('4013ce85-0d5c-44d6-9975-118266489a96', '123456789123456789123456789124', 'JohnPoe', 'JohnPoe123', 'Johnny', true);


END $$
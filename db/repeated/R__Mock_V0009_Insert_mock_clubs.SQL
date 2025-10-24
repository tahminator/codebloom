DO $$ 
BEGIN
INSERT INTO "Club" 
    (id, name, description, slug, "splashIconUrl", password, tag)
VALUES
    ('a2b2D3d4-e3f6-7820-abcd-ef1234576890','Mock Club','Mock club for testing','mock-club','','mockpassword','Hunter'),
    ('a232D3d4-e3f6-7820-abcd-ef1234512340', 'Mock GWC', 'Mock Gwc club for testing', 'mock-gwc', '/Gwc_logo.png','mockgwcpassword', 'Gwc');
    

END $$
UPDATE
    "Leaderboard"
SET
    "syntaxHighlightingLanguage" = 'java'
WHERE
    name IN (
        'double december = 12.0d;',
        'new Stack("September")',
        '"july".split("")'
    );

UPDATE
    "Leaderboard"
SET
    "syntaxHighlightingLanguage" = 'cpp'
WHERE
    name = 'auto november = nullptr;';
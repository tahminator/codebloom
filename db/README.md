# `db/`

```mermaid
erDiagram
    Achievement {
        timestamp_with_time_zone createdAt "{NOT_NULL}"
        timestamp_with_time_zone deletedAt
        text description
        uuid id PK "{NOT_NULL}"
        boolean isActive
        Tag leaderboard "<Patina,Hunter,Nyu,Baruch,Rpi,Gwc,Sbu,Ccny,Columbia,Cornell,Bmcc,MHCPlusPlus>"
        AchievementPlaceEnum place "<ONE,TWO,THREE>  {NOT_NULL}"
        character_varying title "{NOT_NULL}"
        uuid userId FK "{NOT_NULL}"
    }

    Announcement {
        timestamp_with_time_zone createdAt "{NOT_NULL}"
        timestamp_with_time_zone expiresAt "{NOT_NULL}"
        uuid id PK "{NOT_NULL}"
        text message "{NOT_NULL}"
        boolean showTimer
    }

    ApiKey {
        text apiKeyHash UK "{NOT_NULL}"
        timestamp_with_time_zone createdAt "{NOT_NULL}"
        timestamp_with_time_zone expiresAt "{NOT_NULL}"
        uuid id PK "{NOT_NULL}"
        timestamp_with_time_zone updatedAt "{NOT_NULL}"
        uuid updatedBy FK "{NOT_NULL}"
    }

    ApiKeyAccess {
        ApiKeyAccessEnum access UK "<GWC_READ_BY_USER,TEST_VALUE>  {NOT_NULL}"
        uuid apiKeyId FK,UK "{NOT_NULL}"
        uuid id PK "{NOT_NULL}"
    }

    Auth {
        timestamp_with_time_zone createdAt "{NOT_NULL}"
        text csrf
        uuid id PK "{NOT_NULL}"
        text token "{NOT_NULL}"
    }

    BackgroundTask {
        timestamp_with_time_zone completedAt "{NOT_NULL}"
        uuid id PK "{NOT_NULL}"
        BackgroundTaskEnum task "<LEETCODE_QUESTION_BANK>  {NOT_NULL}"
    }

    Club {
        text description
        uuid id PK "{NOT_NULL}"
        character_varying name "{NOT_NULL}"
        character_varying password "{NOT_NULL}"
        character_varying slug UK "{NOT_NULL}"
        character_varying splashIconUrl
        Tag tag "<Patina,Hunter,Nyu,Baruch,Rpi,Gwc,Sbu,Ccny,Columbia,Cornell,Bmcc,MHCPlusPlus>  {NOT_NULL}"
    }

    DiscordClub {
        timestamp_with_time_zone createdAt "{NOT_NULL}"
        timestamp_with_time_zone deletedAt
        text description
        uuid id PK "{NOT_NULL}"
        text name "{NOT_NULL}"
        Tag tag "<Patina,Hunter,Nyu,Baruch,Rpi,Gwc,Sbu,Ccny,Columbia,Cornell,Bmcc,MHCPlusPlus>  {NOT_NULL}"
    }

    DiscordClubMetadata {
        uuid discordClubId FK "{NOT_NULL}"
        text guildId
        uuid id PK "{NOT_NULL}"
        text leaderboardChannelId
    }

    Job {
        integer attempts "{NOT_NULL}"
        timestamp_with_time_zone completedAt
        timestamp_with_time_zone createdAt "{NOT_NULL}"
        uuid id PK "{NOT_NULL}"
        timestamp_with_time_zone nextAttemptAt "{NOT_NULL}"
        timestamp_with_time_zone processedAt
        character_varying questionId "{NOT_NULL}"
        JobStatus status "<COMPLETE,PROCESSING,INCOMPLETE>  {NOT_NULL}"
    }

    Leaderboard {
        timestamp_with_time_zone createdAt "{NOT_NULL}"
        timestamp_with_time_zone deletedAt
        uuid id PK "{NOT_NULL}"
        character_varying name "{NOT_NULL}"
        timestamp_with_time_zone shouldExpireBy
        text syntaxHighlightingLanguage
    }

    Lobby {
        timestamp_with_time_zone createdAt "{NOT_NULL}"
        timestamp_with_time_zone expiresAt
        uuid id PK "{NOT_NULL}"
        text joinCode "{NOT_NULL}"
        integer playerCount "{NOT_NULL}"
        LobbyStatusEnum status "<CLOSED,AVAILABLE,ACTIVE,COMPLETED>  {NOT_NULL}"
        boolean tie "{NOT_NULL}"
        uuid winnerId FK
    }

    LobbyPlayer {
        uuid id PK "{NOT_NULL}"
        uuid lobbyId FK "{NOT_NULL}"
        uuid playerId FK "{NOT_NULL}"
        integer points "{NOT_NULL}"
    }

    LobbyPlayerQuestion {
        uuid id PK "{NOT_NULL}"
        uuid lobbyPlayerId FK,UK "{NOT_NULL}"
        integer points
        uuid questionId FK,UK
    }

    LobbyQuestion {
        timestamp_with_time_zone createdAt "{NOT_NULL}"
        uuid id PK "{NOT_NULL}"
        uuid lobbyId FK "{NOT_NULL}"
        uuid questionBankId FK "{NOT_NULL}"
        integer userSolvedCount "{NOT_NULL}"
    }

    Metadata {
        timestamp_with_time_zone createdAt "{NOT_NULL}"
        uuid id PK "{NOT_NULL}"
        uuid leaderboardId FK "{NOT_NULL}"
        integer totalScore "{NOT_NULL}"
        uuid userId FK "{NOT_NULL}"
    }

    POTD {
        timestamp_without_time_zone createdAt "{NOT_NULL}"
        uuid id PK "{NOT_NULL}"
        double_precision multiplier "{NOT_NULL}"
        character_varying slug "{NOT_NULL}"
        character_varying title "{NOT_NULL}"
    }

    Question {
        double_precision acceptanceRate "{NOT_NULL}"
        text code
        timestamp_with_time_zone createdAt "{NOT_NULL}"
        text description
        uuid id PK "{NOT_NULL}"
        text language
        character_varying memory
        integer pointsAwarded
        question_difficulty questionDifficulty "<Easy,Medium,Hard>  {NOT_NULL}"
        text questionLink "{NOT_NULL}"
        smallint questionNumber "{NOT_NULL}"
        character_varying questionSlug "{NOT_NULL}"
        text questionTitle "{NOT_NULL}"
        character_varying runtime
        text submissionId UK
        timestamp_with_time_zone submittedAt "{NOT_NULL}"
        uuid userId FK "{NOT_NULL}"
    }

    QuestionBank {
        real acceptanceRate "{NOT_NULL}"
        timestamp_with_time_zone createdAt "{NOT_NULL}"
        text description
        uuid id PK "{NOT_NULL}"
        question_difficulty questionDifficulty "<Easy,Medium,Hard>  {NOT_NULL}"
        text questionLink "{NOT_NULL}"
        smallint questionNumber "{NOT_NULL}"
        character_varying questionSlug "{NOT_NULL}"
        text questionTitle "{NOT_NULL}"
    }

    QuestionTopic {
        timestamp_with_time_zone createdAt "{NOT_NULL}"
        uuid id PK "{NOT_NULL}"
        uuid questionBankId FK,UK "Exactly one of questionBankId or questionId must be non-null. If questionBankId is non-null, this topic refers to a question bank question."
        uuid questionId FK,UK "Exactly one of questionId or questionBankId must be non-null. If questionId is non-null, this topic refers to a question submission."
        LeetcodeTopicEnum topic UK "<stack,data-stream,rejection-sampling,geometry,counting,design,probability-and-statistics,minimum-spanning-tree,line-sweep,number-theory,rolling-hash,segment-tree,biconnected-component,monotonic-stack,iterator,queue,radix-sort,bucket-sort,shell,memoization,string,prefix-sum,concurrency,database,shortest-path,sorting,linked-list,sliding-window,suffix-array,doubly-linked-list,simulation,ordered-set,graph,math,ordered-map,game-theory,dynamic-programming,recursion,monotonic-queue,matrix,reservoir-sampling,merge-sort,combinatorics,interactive,binary-tree,randomized,bitmask,breadth-first-search,string-matching,greedy,brainteaser,backtracking,bit-manipulation,union-find,binary-search-tree,two-pointers,array,depth-first-search,eulerian-circuit,tree,binary-search,strongly-connected-component,enumeration,heap-priority-queue,divide-and-conquer,hash-function,hash-table,trie,topological-sort,quickselect,binary-indexed-tree,counting-sort,unknown>  {NOT_NULL}"
        text topicSlug "{NOT_NULL}"
    }

    Report {
        timestamp_with_time_zone createdAt "{NOT_NULL}"
        text description "{NOT_NULL}"
        character_varying email
        uuid id PK "{NOT_NULL}"
        character_varying title "{NOT_NULL}"
    }

    Session {
        timestamp_with_time_zone expiresAt "{NOT_NULL}"
        character_varying id PK "{NOT_NULL}"
        uuid userId FK "{NOT_NULL}"
    }

    User {
        boolean admin "{NOT_NULL}"
        character_varying discordId UK "{NOT_NULL}"
        character_varying discordName "{NOT_NULL}"
        uuid id PK "{NOT_NULL}"
        character_varying leetcodeUsername UK
        character_varying nickname
        character_varying profileUrl
        character_varying schoolEmail
        text verifyKey
    }

    UserTag {
        timestamp_with_time_zone createdAt "{NOT_NULL}"
        uuid id PK "{NOT_NULL}"
        Tag tag UK "<Patina,Hunter,Nyu,Baruch,Rpi,Gwc,Sbu,Ccny,Columbia,Cornell,Bmcc,MHCPlusPlus>  {NOT_NULL}"
        uuid userId FK,UK "{NOT_NULL}"
    }

    WeeklyMessage {
        timestamp_with_time_zone createdAt "{NOT_NULL}"
        uuid id PK "{NOT_NULL}"
    }

    flyway_schema_history {
        integer checksum
        character_varying description "{NOT_NULL}"
        integer execution_time "{NOT_NULL}"
        character_varying installed_by "{NOT_NULL}"
        timestamp_without_time_zone installed_on "{NOT_NULL}"
        integer installed_rank PK "{NOT_NULL}"
        character_varying script "{NOT_NULL}"
        boolean success "{NOT_NULL}"
        character_varying type "{NOT_NULL}"
        character_varying version
    }

    Achievement }o--|| User : "userId"
    ApiKey }o--|| User : "updatedBy"
    ApiKeyAccess }o--|| ApiKey : "apiKeyId"
    DiscordClubMetadata }o--|| DiscordClub : "discordClubId"
    Metadata }o--|| Leaderboard : "leaderboardId"
    Lobby }o--|| User : "winnerId"
    LobbyPlayer }o--|| Lobby : "lobbyId"
    LobbyQuestion }o--|| Lobby : "lobbyId"
    LobbyPlayer }o--|| User : "playerId"
    LobbyPlayerQuestion }o--|| LobbyPlayer : "lobbyPlayerId"
    LobbyPlayerQuestion }o--|| Question : "questionId"
    LobbyQuestion }o--|| QuestionBank : "questionBankId"
    Metadata }o--|| User : "userId"
    Question }o--|| User : "userId"
    QuestionTopic }o--|| Question : "questionId"
    QuestionTopic }o--|| QuestionBank : "questionBankId"
    Session }o--|| User : "userId"
    UserTag }o--|| User : "userId"
```

<p align="center">
    <i>
        Generated with <code>mermerd -c=postgresql://postgres:passwordnomas@localhost:5432/codebloom-dev-2 -s=public --outputMode=stdout --useAllTables --showDescriptions enumValues,columnComments,notNull</code>
    </i>
</p>
<p align="center">
    <i>
        Also view in <a href="https://mermaid.live/edit#pako:eNrtWm1v2zgS_iuGP1NFk6abTbBYwHF0Ta5uksZOizsUMGiKlrimSC1f4qjd_PcdUpYtxZZst9kUB1zQpBY9M5x5ZjgzJPWtS2REu6ddqs4ZjhVOv4gO_PRIwug9TakwnW_FkPsxLKXa4DQbz5lJxu5x_FUK2iGKYkOjnul86X67uh6Nr-4Gg8cv3R1YI8qpZ63Q0gcD45oolhkmxeoba1nUgX837xsnmkjJKRYdpnvEsHu6-maE4w58FVE1kVhFIOG3G2yYwOjCCkMVusotOsPKkgTdZgy9mxM0nFjUJyJHfcltOmEYPihBOUdnKSHow0X_hlvtfn-vKlHB74ZjQkNh007mPrlZr69CNPp8jUYXt2H4e6ez2RCSYIUJ6DW-xypnIgYMDaeNhntsrKbqMur8azM-j18WWPaEkFaQF3EwfciYorqFdQev-pCACTSO6VbX60TORzC_emp1xt7TvGavE4v98AXWSefu_f72_XRomqTbLNqiWBEyBdlZvj1qPFA9QsAPVRSr4z7ScUHi0fzt3ef--DbsnY_P_jO-G4a3aBQOR-NPvcFdc-x7xQq_-GhGLY7ZAtFKe2uS54p1FzZEq-n-MWzkjIp2Rc8wmcUK1mc0wnq2m8oyzRZp9EdCqT6zd6VxKoAXB2E46l-fh-OPd-C-y-ur8Vnv6v1GBy4N6XM7WVtt35vX19OhwCndgzzDWs-lz_u7smhu47acsIEDkrxOLokUd4rXS4-B32cuOa3onzNNwNw1J_wP1HDP3OrenwzoB2owZE1cBdbbFa1omsvw0sTYMh5dRvtjU2lj-gkWYEwpZantv2XN7QzQianqYGNomhn9HXVulWOeJZp-pLQJAKFXWPI98Zop6QpU3ZT1xfynBXaI3cvmnAEoDw02Vnd08R8EZP_6w80gHIXo5va6Hw6Hl1fv0OVVOdoeY4NKf_ryi_b503Dj7NCjWR6FvgE6y5-Et86FwQ8XLE44_MLyjgdYxBZ6v6dwyckkf7EGdv91-odkog87rEaqcllC2cip6kPpbVbQG1tEmy_NlYgbXA_Dc9T71Lsc9M4GIer1R5efQlTG3Hljo1W2zIZt2VjMGWSZYmuxyQc3Xv-1dNgOkSfhjr09V3q6AqF2wiWaAHtDituk9sfFOv9u9W9WurV1qnX1ngiqJBsvZZO6mxT9ZzPwXi4qTTjDYrabo9x2dSj5PY2aQ38Jwaai-xLWr1LybkYZaTAfQhvwPHv1m-vRebPJ0prntjqSdsLpOFOUMO2iLbXcsIwzsG3P3nl38uazjSUQm6J_TVe39cwMFoTeAhJbdnCQmZ9vP_hDzS6v1bfNEKU0lSpvSie9OQQorfSS5WocR2w6ZQR8mC_HzldDUD5CrHP0gUbMpugCpDRWC69qKWPAxKzRKJ1izkGtJfUVdN57xU_JOGyLo5pCo9bjsfUZFGQccPLT3sNOUqZ1kYnLNNzeyjgOs8tJy24L_mMljVZjHcKQ7xzfPymY_x90zf4cyYyRFy5eayW56E_CB7Cb5x03mZw-pZKq2o2kVpvOBPZcUgTCcv6qc7nGwfTya9QxCTwab6yiU6rcQwcvWToTF9fl06tGlberW1d1oUqruvuqukoGNT0HlBpXPLxHi2MyL6M48ASXkhlynUqgDXgyRYr-QYkTGGjwNoRojGIqU2pUjohrfNwILDkWCwR70wmeMM5MHmARBa7LZ6AN0ShlgqU2DXQGW35gCUA6RSCOBnpOaYaEj_fAJBQKBVLSrYY4SLBOkKaxO-wvWCaMSGjnIfSjwG3rAVdhUCqFNFIwEhQWMFgZ2EiFAA5LkcIRewi0VAZNLJlRU3zWiTtJcbWJfcXORgRGO3ugJk8dA6xvmI1YpaggucdlgjUFRmAHnIMMG9APHhwXaDwDrTiYjDRnkTMA9h6RnCNtp04gVgqDGFf486BGDuDwQgUJpVDBuKYGxQpnCUrdJOVwijMUw7a1RCrKYRMLdgP27g4sdYpAR2GV83wFlwIIEKXYAxBAOr-XTK2cmlIV0wIWgHXChIPPec6Vapdq2L0DX0C2KRyhwMMyZV9pBMMmxXqGJhAxkUmCKVOAjaZYkWQBKehtSOKjB5ijHGixkwxoKjQBlxmYY-a-B2FALFhWAmKFi74pAFnOX0gu1DBzGfguAlYAWuALVeaJEtRyqhgWAWGKWGbQIpYq4pyiUsTgmE0BRiE-XUg5hRKKM8CbAT4Q6AWwEbtnEfVRD-wwppCL3WBqBSmY3BOEDweVFYM_MpNcxoxgXqD-p2VkpimHmUvFwGT6AGp4ZcvFVlBbMRNyLtorj1_ZjfVgmeRvaQYin_N-oVKC96hgNMWM_-CJzg69-JD6rLiTvdtvudZV2KG07dhN3en6uUR54oEjWOZ7aLQ42fVN4f5sV22HZN_lJL6oQc4-fwRX7VU3nNLBwnBkrTckSk4Zp7Wbiw0bO5IAhGE9znzE3kN6mObvab7BBe7A_sVan_J2oKjH__QFwXpENh0CLRH5TOmM5x8W99kvgsty7inP5zgfgxshV4yhAYIalW-6KgACSKY2bQ3vHZJUKQ_SMLF-c-DM2icZCICEcxqNJ_mOO576schKwA5qroiVa1T3WpQFGNtfULDFBfkeKTnP9kEMVqJebt1WN-CVt3oeZRD89VeRHU9BchG7pdTFmxJrROWrAnW6xRsBC-oFr6Mvb_BL8k03aAuu6m2lY61dpJX8T5mqtyWOqXZUVzIVdwRPTSnPs2tUi1PsUrpn9HKLA9Aa7fI0aifquuSlFuWh9gbajfIXYpaz3OzOv3x2zKv9UKtNtZOIKl-x12pyS0NQPZW-hazYLu-mfSvPFu3LVqZdq7KGNVN1UTdWLOqeGgW9bBc6XSiR8Nj1qfVLF_YaUH67PrixmjmWR-CBvdx_pUxLNiVtnHRPp5jDFmmx4Bav5i1JKLS0xV1R9_TgzVsvo3v6rfvQPQ1-ffv61dvD129Ojo6ODk5Ojg5RN--ensDgweuT48PXB8cHh8cHvzyi7lc_6-GrX4-P3xwcHRyfHP3y-u2RE1fMGkYMKsNSFWyNHOaCFFo8_g2cdDko"><code>mermaid.live</code></a>
    </i>
</p>
<p align="center">
    <i>
        Last updated: 02/15/2026
    </i>
</p>

This directory contains the migrations that are applied to our [PostgreSQL](https://www.postgresql.org/) databases.

## Commands

To migrate your local database using your root `.env`, you can simply run:

```bash
just migrate
```

If you need to drop it quickly, you can simply run:

```bash
just drop
```

## Versioned Migrations

Versioned migrations are under `db/migration/`

> [!NOTE]
> Versioned migrations are applied to all databases

### Explanation

- Versioned migrations will only run once.
- You can use these migrations to define tables, schemas, columns (otherwise known as DDL) OR define insertions/updates/deletes of certain columns (otherwise known as DML).
    > If you need to generate mock data **that does not need to be in production**, please look at the docs on [repeatable migrations](#repeatable-migrations).

### Naming Scheme

#### Requirements

`V00{number}__{description}.SQL`

1. Name must be prefixed with a `V`.
1. Version numbers must be sequential and unique.
    - Version numbers must be 4 digits wide. You may pad the left-side with 0s until you reach that goal.
1. Double underscores (\_\_) separate the version from the description
1. Use underscores (\_) instead of spaces in descriptions
1. Files must have `.sql` (or `.SQL`) extension

#### Examples

```txt
V0005__Add_user_table.SQL
V0642__Insert_new_tag_enums.SQL
V9999__Delete_user_table.SQL
```

## Repeatable Migrations

Versioned migrations are under `db/repeated/`

> [!NOTE]
> Repeatable migrations are only applied to local & CI databases. <br />
> Repeatable migrations are **NOT** applied to the production & staging database.

### Explanation

1. Instead of being run just once, repeatable migrations are (re-)applied to a database on [migrate](https://documentation.red-gate.com/fd/migrate-277578887.html) every time their checksum changes.
1. Our main use for repeatable migrations are to generate mock data to use locally and in our CI database, but **is not needed for our production or staging database**.

#### Requirements

1. Name must be prefixed with an `R__Mock`
1. Version numbers must be sequential and unique
1. Use underscores (\_) instead of spaces in descriptions
1. Files must have `.sql` (or `.SQL`) extension

#### Examples

```txt
R__Mock_V0005_Insert_mock_users.SQL
R__Mock_V0011_Add_old_leaderboards.SQL
R__Mock_V9999_Delete_old_mock_users_and_insert_new_users.SQL
```

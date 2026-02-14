package org.patinanetwork.codebloom.api.admin;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import jakarta.servlet.http.HttpServletRequest;
import java.time.OffsetDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.patinanetwork.codebloom.api.admin.body.NewLeaderboardBody;
import org.patinanetwork.codebloom.common.components.DiscordClubManager;
import org.patinanetwork.codebloom.common.components.LeaderboardManager;
import org.patinanetwork.codebloom.common.db.models.discord.DiscordClub;
import org.patinanetwork.codebloom.common.db.models.leaderboard.Leaderboard;
import org.patinanetwork.codebloom.common.db.repos.announcement.AnnouncementRepository;
import org.patinanetwork.codebloom.common.db.repos.discord.club.DiscordClubRepository;
import org.patinanetwork.codebloom.common.db.repos.leaderboard.LeaderboardRepository;
import org.patinanetwork.codebloom.common.db.repos.question.QuestionRepository;
import org.patinanetwork.codebloom.common.db.repos.user.UserRepository;
import org.patinanetwork.codebloom.common.dto.ApiResponder;
import org.patinanetwork.codebloom.common.dto.Empty;
import org.patinanetwork.codebloom.common.security.Protector;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class AdminControllerTest {

    private final UserRepository userRepository = mock(UserRepository.class);
    private final LeaderboardRepository leaderboardRepository = mock(LeaderboardRepository.class);
    private final AnnouncementRepository announcementRepository = mock(AnnouncementRepository.class);
    private final QuestionRepository questionRepository = mock(QuestionRepository.class);
    private final Protector protector = mock(Protector.class);
    private final DiscordClubManager discordClubManager = mock(DiscordClubManager.class);
    private final LeaderboardManager leaderboardManager = mock(LeaderboardManager.class);
    private final DiscordClubRepository discordClubRepository = mock(DiscordClubRepository.class);
    private final HttpServletRequest request = mock(HttpServletRequest.class);

    private final AdminController adminController;

    public AdminControllerTest() {
        adminController = spy(new AdminController(
                leaderboardRepository,
                protector,
                userRepository,
                announcementRepository,
                questionRepository,
                discordClubManager,
                leaderboardManager,
                discordClubRepository));
    }

    @BeforeEach
    void setUp() {
        reset(
                userRepository,
                leaderboardRepository,
                announcementRepository,
                questionRepository,
                protector,
                discordClubManager,
                leaderboardManager,
                request);
    }

    @Test
    void testCreateLeaderboardSuccessNoExistingLeaderboard() {
        NewLeaderboardBody body =
                NewLeaderboardBody.builder().name("Spring 2024 Challenge").build();

        when(leaderboardRepository.getRecentLeaderboardMetadata()).thenReturn(null);

        ResponseEntity<ApiResponder<Empty>> response = adminController.createLeaderboard(request, body);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Leaderboard was created successfully.", response.getBody().getMessage());
        assertEquals(Empty.of(), response.getBody().getPayload());

        verify(protector).validateAdminSession(request);
        verify(leaderboardRepository).getRecentLeaderboardMetadata();
        verify(leaderboardRepository).addNewLeaderboard(any(Leaderboard.class));
        verify(leaderboardRepository).addAllUsersToLeaderboard(any());
        verify(discordClubManager, never()).sendLeaderboardCompletedDiscordMessageToAllClubs();
        verify(leaderboardManager, never()).generateAchievementsForAllWinners();
        verify(leaderboardRepository, never()).disableLeaderboardById(anyString());
    }

    @Test
    void testCreateLeaderboardSuccessWithExistingLeaderboard() {
        NewLeaderboardBody body =
                NewLeaderboardBody.builder().name("Fall 2024 Challenge").build();

        Leaderboard existingLeaderboard =
                Leaderboard.builder().id("existing-id").name("Old Leaderboard").build();

        when(leaderboardRepository.getRecentLeaderboardMetadata()).thenReturn(existingLeaderboard);

        ResponseEntity<ApiResponder<Empty>> response = adminController.createLeaderboard(request, body);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Leaderboard was created successfully.", response.getBody().getMessage());
        assertEquals(Empty.of(), response.getBody().getPayload());

        verify(protector).validateAdminSession(request);
        verify(leaderboardRepository).getRecentLeaderboardMetadata();
        verify(discordClubManager).sendLeaderboardCompletedDiscordMessageToAllClubs();
        verify(leaderboardManager).generateAchievementsForAllWinners();
        verify(leaderboardRepository).disableLeaderboardById("existing-id");
        verify(leaderboardRepository).addNewLeaderboard(any(Leaderboard.class));
        verify(leaderboardRepository).addAllUsersToLeaderboard(any());
    }

    @Test
    void testCreateLeaderboardEmptyName() {
        NewLeaderboardBody body = NewLeaderboardBody.builder().name("").build();

        ResponseEntity<ApiResponder<Empty>> response = adminController.createLeaderboard(request, body);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertEquals(
                "Leaderboard name must be between 1 and 512 characters.",
                response.getBody().getMessage());

        verify(protector).validateAdminSession(request);
        verify(leaderboardRepository, never()).getRecentLeaderboardMetadata();
        verify(leaderboardRepository, never()).addNewLeaderboard(any(Leaderboard.class));
    }

    @Test
    void testCreateLeaderboardWhitespaceOnlyName() {
        NewLeaderboardBody body = NewLeaderboardBody.builder().name("   ").build();

        ResponseEntity<ApiResponder<Empty>> response = adminController.createLeaderboard(request, body);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertEquals(
                "Leaderboard name must be between 1 and 512 characters.",
                response.getBody().getMessage());

        verify(protector).validateAdminSession(request);
        verify(leaderboardRepository, never()).getRecentLeaderboardMetadata();
        verify(leaderboardRepository, never()).addNewLeaderboard(any(Leaderboard.class));
    }

    @Test
    void testCreateLeaderboardNameTooLong() {
        String longName = "a".repeat(513);
        NewLeaderboardBody body = NewLeaderboardBody.builder().name(longName).build();

        ResponseEntity<ApiResponder<Empty>> response = adminController.createLeaderboard(request, body);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertEquals(
                "Leaderboard name must be between 1 and 512 characters.",
                response.getBody().getMessage());

        verify(protector).validateAdminSession(request);
        verify(leaderboardRepository, never()).getRecentLeaderboardMetadata();
        verify(leaderboardRepository, never()).addNewLeaderboard(any(Leaderboard.class));
    }

    @Test
    void testCreateLeaderboardMaxValidName() {
        String maxName = "a".repeat(512);
        NewLeaderboardBody body = NewLeaderboardBody.builder().name(maxName).build();

        when(leaderboardRepository.getRecentLeaderboardMetadata()).thenReturn(null);

        ResponseEntity<ApiResponder<Empty>> response = adminController.createLeaderboard(request, body);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Leaderboard was created successfully.", response.getBody().getMessage());

        verify(protector).validateAdminSession(request);
        verify(leaderboardRepository).getRecentLeaderboardMetadata();
        verify(leaderboardRepository).addNewLeaderboard(any(Leaderboard.class));
        verify(leaderboardRepository).addAllUsersToLeaderboard(any());
    }

    @Test
    void testCreateLeaderboardNameWithLeadingAndTrailingSpaces() {
        NewLeaderboardBody body =
                NewLeaderboardBody.builder().name("  Challenge 2024  ").build();

        when(leaderboardRepository.getRecentLeaderboardMetadata()).thenReturn(null);

        ResponseEntity<ApiResponder<Empty>> response = adminController.createLeaderboard(request, body);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Leaderboard was created successfully.", response.getBody().getMessage());

        verify(protector).validateAdminSession(request);
        verify(leaderboardRepository).getRecentLeaderboardMetadata();
        verify(leaderboardRepository)
                .addNewLeaderboard(argThat(leaderboard -> "Challenge 2024".equals(leaderboard.getName())));
        verify(leaderboardRepository).addAllUsersToLeaderboard(any());
    }

    @Test
    void testCreateLeaderboardWithShouldExpireBy() {
        OffsetDateTime futureDate = OffsetDateTime.now().plusDays(30);

        NewLeaderboardBody body = NewLeaderboardBody.builder()
                .name("Challenge 2024")
                .shouldExpireBy(futureDate)
                .build();

        when(leaderboardRepository.getRecentLeaderboardMetadata()).thenReturn(null);

        ResponseEntity<ApiResponder<Empty>> response = adminController.createLeaderboard(request, body);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Leaderboard was created successfully.", response.getBody().getMessage());

        verify(protector).validateAdminSession(request);
        verify(leaderboardRepository)
                .addNewLeaderboard(argThat(leaderboard -> leaderboard.getShouldExpireBy() != null));
        verify(leaderboardRepository).addAllUsersToLeaderboard(any());
    }

    @Test
    void testCreateLeaderboardWithPastShouldExpireBy() {
        OffsetDateTime pastDate = OffsetDateTime.now().minusDays(30);

        NewLeaderboardBody body = NewLeaderboardBody.builder()
                .name("Challenge 2024")
                .shouldExpireBy(pastDate)
                .build();

        ResponseEntity<ApiResponder<Empty>> response = adminController.createLeaderboard(request, body);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertEquals(
                "The expiration date must be in the future.", response.getBody().getMessage());

        verify(protector).validateAdminSession(request);
        verify(leaderboardRepository, never()).addNewLeaderboard(any(Leaderboard.class));
    }

    @Test
    void testCreateLeaderboardWithCurrentTimeShouldExpireBy() {
        OffsetDateTime current = OffsetDateTime.now();

        NewLeaderboardBody body = NewLeaderboardBody.builder()
                .name("Challenge 2024")
                .shouldExpireBy(current)
                .build();

        ResponseEntity<ApiResponder<Empty>> response = adminController.createLeaderboard(request, body);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertEquals(
                "The expiration date must be in the future.", response.getBody().getMessage());

        verify(protector).validateAdminSession(request);
        verify(leaderboardRepository, never()).addNewLeaderboard(any(Leaderboard.class));
    }

    @Test
    void testCreateLeaderboardWithSyntaxHighlightingLanguage() {
        NewLeaderboardBody body = NewLeaderboardBody.builder()
                .name("Challenge 2024")
                .syntaxHighlightingLanguage("python")
                .build();

        when(leaderboardRepository.getRecentLeaderboardMetadata()).thenReturn(null);

        ResponseEntity<ApiResponder<Empty>> response = adminController.createLeaderboard(request, body);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Leaderboard was created successfully.", response.getBody().getMessage());

        verify(protector).validateAdminSession(request);
        verify(leaderboardRepository).addNewLeaderboard(argThat(leaderboard -> "python"
                .equals(leaderboard.getSyntaxHighlightingLanguage())));
        verify(leaderboardRepository).addAllUsersToLeaderboard(any());
    }

    @Test
    void testCreateLeaderboardWithAllOptionalFields() {
        OffsetDateTime futureDate = OffsetDateTime.now().plusDays(30);

        NewLeaderboardBody body = NewLeaderboardBody.builder()
                .name("Challenge 2024")
                .shouldExpireBy(futureDate)
                .syntaxHighlightingLanguage("python")
                .build();

        when(leaderboardRepository.getRecentLeaderboardMetadata()).thenReturn(null);

        ResponseEntity<ApiResponder<Empty>> response = adminController.createLeaderboard(request, body);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Leaderboard was created successfully.", response.getBody().getMessage());

        verify(protector).validateAdminSession(request);
        verify(leaderboardRepository)
                .addNewLeaderboard(argThat(leaderboard -> leaderboard.getShouldExpireBy() != null
                        && "python".equals(leaderboard.getSyntaxHighlightingLanguage())));
        verify(leaderboardRepository).addAllUsersToLeaderboard(any());
    }

    @Test
    void testCreateLeaderboardWithNullOptionalFields() {
        NewLeaderboardBody body = NewLeaderboardBody.builder()
                .name("Challenge 2024")
                .shouldExpireBy(null)
                .syntaxHighlightingLanguage(null)
                .build();

        when(leaderboardRepository.getRecentLeaderboardMetadata()).thenReturn(null);

        ResponseEntity<ApiResponder<Empty>> response = adminController.createLeaderboard(request, body);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Leaderboard was created successfully.", response.getBody().getMessage());

        verify(protector).validateAdminSession(request);
        verify(leaderboardRepository)
                .addNewLeaderboard(argThat(leaderboard -> leaderboard.getShouldExpireBy() == null
                        && leaderboard.getSyntaxHighlightingLanguage() == null));
        verify(leaderboardRepository).addAllUsersToLeaderboard(any());
    }

    @Test
    void testSendDiscordMessageSuccess() {
        String clubId = "bbf4734a-06b6-11f1-869c-07599d6a11f7";
        DiscordClub club = mock(DiscordClub.class);

        when(discordClubRepository.getDiscordClubById(clubId)).thenReturn(Optional.of(club));

        when(discordClubManager.sendTestEmbedMessageToClub(club)).thenReturn(true);

        ResponseEntity<ApiResponder<Empty>> response = adminController.sendDiscordMessage(clubId, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Message successfully sent!", response.getBody().getMessage());

        verify(protector).validateAdminSession(request);
    }

    @Test
    void testSendDiscordMessageInvalid() {
        DiscordClub club = mock(DiscordClub.class);
        when(discordClubManager.sendTestEmbedMessageToClub(club)).thenReturn(false);

        String clubId = "bbf4734a-06b6-11f1-869c-07599d6a11f7";
        ResponseEntity<ApiResponder<Empty>> response = adminController.sendDiscordMessage(clubId, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Club not found.", response.getBody().getMessage());

        verify(protector).validateAdminSession(request);
    }
}

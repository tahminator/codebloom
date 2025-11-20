package com.patina.codebloom.admin;

// CHECKSTYLE:OFF
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
// CHECKSTYLE:ON

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.patina.codebloom.api.admin.AdminController;
import com.patina.codebloom.api.admin.body.NewLeaderboardBody;
import com.patina.codebloom.common.components.DiscordClubManager;
import com.patina.codebloom.common.components.LeaderboardManager;
import com.patina.codebloom.common.db.models.leaderboard.Leaderboard;
import com.patina.codebloom.common.db.repos.announcement.AnnouncementRepository;
import com.patina.codebloom.common.db.repos.leaderboard.LeaderboardRepository;
import com.patina.codebloom.common.db.repos.question.QuestionRepository;
import com.patina.codebloom.common.db.repos.user.UserRepository;
import com.patina.codebloom.common.dto.ApiResponder;
import com.patina.codebloom.common.dto.Empty;
import com.patina.codebloom.common.security.Protector;

import jakarta.servlet.http.HttpServletRequest;

public class AdminControllerTest {
    private final UserRepository userRepository = mock(UserRepository.class);
    private final LeaderboardRepository leaderboardRepository = mock(LeaderboardRepository.class);
    private final AnnouncementRepository announcementRepository = mock(AnnouncementRepository.class);
    private final QuestionRepository questionRepository = mock(QuestionRepository.class);
    private final Protector protector = mock(Protector.class);
    private final DiscordClubManager discordClubManager = mock(DiscordClubManager.class);
    private final LeaderboardManager leaderboardManager = mock(LeaderboardManager.class);
    private final HttpServletRequest request = mock(HttpServletRequest.class);

    private final AdminController adminController;

    public AdminControllerTest() {
        adminController = spy(
                        new AdminController(
                                        leaderboardRepository,
                                        protector,
                                        userRepository,
                                        announcementRepository,
                                        questionRepository,
                                        discordClubManager,
                                        leaderboardManager));
    }

    @BeforeEach
    void setUp() {
        reset(userRepository, leaderboardRepository, announcementRepository,
                        questionRepository, protector, discordClubManager, leaderboardManager, request);
    }

    @Test
    void testCreateLeaderboardSuccessNoExistingLeaderboard() {
        NewLeaderboardBody body = NewLeaderboardBody.builder()
                        .name("Spring 2024 Challenge")
                        .build();

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
        NewLeaderboardBody body = NewLeaderboardBody.builder()
                        .name("Fall 2024 Challenge")
                        .build();

        Leaderboard existingLeaderboard = Leaderboard.builder()
                        .id("existing-id")
                        .name("Old Leaderboard")
                        .build();

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
        NewLeaderboardBody body = NewLeaderboardBody.builder()
                        .name("")
                        .build();

        ResponseEntity<ApiResponder<Empty>> response = adminController.createLeaderboard(request, body);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertEquals("Leaderboard name must be between 1 and 512 characters.", response.getBody().getMessage());

        verify(protector).validateAdminSession(request);
        verify(leaderboardRepository, never()).getRecentLeaderboardMetadata();
        verify(leaderboardRepository, never()).addNewLeaderboard(any(Leaderboard.class));
    }

    @Test
    void testCreateLeaderboardWhitespaceOnlyName() {
        NewLeaderboardBody body = NewLeaderboardBody.builder()
                        .name("   ")
                        .build();

        ResponseEntity<ApiResponder<Empty>> response = adminController.createLeaderboard(request, body);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertEquals("Leaderboard name must be between 1 and 512 characters.", response.getBody().getMessage());

        verify(protector).validateAdminSession(request);
        verify(leaderboardRepository, never()).getRecentLeaderboardMetadata();
        verify(leaderboardRepository, never()).addNewLeaderboard(any(Leaderboard.class));
    }

    @Test
    void testCreateLeaderboardNameTooLong() {
        String longName = "a".repeat(513);
        NewLeaderboardBody body = NewLeaderboardBody.builder()
                        .name(longName)
                        .build();

        ResponseEntity<ApiResponder<Empty>> response = adminController.createLeaderboard(request, body);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertEquals("Leaderboard name must be between 1 and 512 characters.", response.getBody().getMessage());

        verify(protector).validateAdminSession(request);
        verify(leaderboardRepository, never()).getRecentLeaderboardMetadata();
        verify(leaderboardRepository, never()).addNewLeaderboard(any(Leaderboard.class));
    }

    @Test
    void testCreateLeaderboardMaxValidName() {
        String maxName = "a".repeat(512);
        NewLeaderboardBody body = NewLeaderboardBody.builder()
                        .name(maxName)
                        .build();

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
        NewLeaderboardBody body = NewLeaderboardBody.builder()
                        .name("  Challenge 2024  ")
                        .build();

        when(leaderboardRepository.getRecentLeaderboardMetadata()).thenReturn(null);

        ResponseEntity<ApiResponder<Empty>> response = adminController.createLeaderboard(request, body);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Leaderboard was created successfully.", response.getBody().getMessage());

        verify(protector).validateAdminSession(request);
        verify(leaderboardRepository).getRecentLeaderboardMetadata();
        verify(leaderboardRepository).addNewLeaderboard(argThat(leaderboard -> "Challenge 2024".equals(leaderboard.getName())));
        verify(leaderboardRepository).addAllUsersToLeaderboard(any());
    }
}

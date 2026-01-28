package org.patinanetwork.codebloom.api.leaderboard;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.patinanetwork.codebloom.common.components.LeaderboardManager;
import org.patinanetwork.codebloom.common.db.models.leaderboard.Leaderboard;
import org.patinanetwork.codebloom.common.db.repos.leaderboard.LeaderboardRepository;
import org.patinanetwork.codebloom.common.db.repos.leaderboard.options.LeaderboardFilterOptions;
import org.patinanetwork.codebloom.common.db.repos.user.UserRepository;
import org.patinanetwork.codebloom.common.security.Protector;

public class LeaderboardControllerTest {
    private static final int PAGE_SIZE = 20;
    private static final int PAGE = 1;
    private static final String LEADERBOARD_ID = UUID.randomUUID().toString();

    private final LeaderboardRepository leaderboardRepository = mock(LeaderboardRepository.class);
    private final UserRepository userRepository = mock(UserRepository.class);
    private final Protector protector = mock(Protector.class);
    private final LeaderboardManager leaderboardManager = mock(LeaderboardManager.class);

    private final LeaderboardController leaderboardController;

    public LeaderboardControllerTest() {
        leaderboardController =
                new LeaderboardController(leaderboardRepository, userRepository, protector, leaderboardManager);
    }

    @BeforeEach
    void setup() {
        when(leaderboardManager.getLeaderboardUsers(eq(LEADERBOARD_ID), any(), anyBoolean()))
                .thenReturn(null);
    }

    @Test
    void testGetLeaderboardUsersByIdMhcPlusPlus() {
        leaderboardController.getLeaderboardUsersById(
                LEADERBOARD_ID,
                PAGE,
                PAGE_SIZE,
                "",
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                true,
                false,
                null);

        ArgumentCaptor<LeaderboardFilterOptions> optsCaptor = ArgumentCaptor.forClass(LeaderboardFilterOptions.class);
        verify(leaderboardManager, times(1)).getLeaderboardUsers(eq(LEADERBOARD_ID), optsCaptor.capture(), eq(false));
        var opts = optsCaptor.getValue();

        assertNotNull(opts);
        assertTrue(opts.isMhcplusplus());
        assertEquals(PAGE_SIZE, opts.getPageSize());
        assertEquals(PAGE, opts.getPage());
    }

    @Test
    void testCurrentLeaderboardUsersMhcPlusPlus() {
        when(leaderboardRepository.getRecentLeaderboardMetadata())
                .thenReturn(Leaderboard.builder().id(LEADERBOARD_ID).build());

        leaderboardController.getCurrentLeaderboardUsers(
                null, PAGE, PAGE_SIZE, "", false, false, false, false, false, false, false, false, false, false, false,
                true, false);

        ArgumentCaptor<LeaderboardFilterOptions> optsCaptor = ArgumentCaptor.forClass(LeaderboardFilterOptions.class);
        verify(leaderboardManager, times(1)).getLeaderboardUsers(eq(LEADERBOARD_ID), optsCaptor.capture(), eq(false));
        var opts = optsCaptor.getValue();

        assertNotNull(opts);
        assertTrue(opts.isMhcplusplus());
        assertEquals(PAGE_SIZE, opts.getPageSize());
        assertEquals(PAGE, opts.getPage());
    }
}

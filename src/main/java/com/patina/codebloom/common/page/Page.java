package com.patina.codebloom.common.page;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

/**
 * Page object to build pagination.
 *
 * @see com.patina.codebloom.api.leaderboard.LeaderboardController pagination
 * examples in controller
 */
@Builder
@Getter
@Jacksonized
public class Page<T> {
    private boolean hasNextPage;
    private T items;
    private int pages;
    private int pageSize;
}

package com.patina.codebloom.common.db.repos.club;

import com.patina.codebloom.common.db.models.club.Club;

public interface ClubRepository {
    /**
     * Creates a new club in the database.
     *
     * @note - The provided object's methods will be overridden with any returned
     * data from the database.
     *
     * @param club - required fields:
     * <ul>
     * <li>name</li>
     * <li>slug</li>
     * <li>password</li>
     * <li>tag</li>
     * </ul>
     */
    void createClub(Club club);

    /**
     * Updates an existing club by its ID.
     *
     * @note - The provided object's methods will be overridden with any returned
     * data from the database.
     *
     * @param club - overridden fields:
     * <ul>
     * <li>name</li>
     * <li>description</li>
     * <li>splashIconUrl</li>
     * <li>password</li>
     * <li>tag</li>
     * </ul>
     * @return updated club if successful
     */
    Club updateClub(Club club);

    Club getClubById(String id);

    Club getClubBySlug(String slug);

    boolean deleteClubBySlug(String slug);

    boolean deleteClubById(String id);
}

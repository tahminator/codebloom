package com.patina.codebloom.common.db.repos.club;

import com.patina.codebloom.common.db.models.usertag.Tag;
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
     */
    void createClub (final Club Club);

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

    Club updateClub (final Club Club);

    Club getClubById(final String id);

    Club getClubBySlug(final String slug);

    boolean deleteClubBySlug(final String slug);

    boolean deleteClubById(final String id);
}


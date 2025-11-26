package com.patina.codebloom.common.db.repos.announcement;

import com.patina.codebloom.common.db.models.announcement.Announcement;
import java.util.List;

public interface AnnouncementRepository {
    List<Announcement> getAllAnnouncements();

    Announcement getAnnouncementById(String id);

    Announcement getRecentAnnouncement();

    /**
     * @note The id property of the object will be overriden.
     * @note the returned boolean indicates if the operation was a success or not.
     *
     */
    boolean createAnnouncement(Announcement announcement);

    boolean deleteAnnouncementById(String id);

    boolean updateAnnouncement(Announcement announcement);
}

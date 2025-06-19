package com.patina.codebloom.common.db.repos.announcement;

import java.sql.ResultSet;
import java.util.List;

import com.patina.codebloom.common.db.models.announcement.Announcement;

public interface AnnouncementRepository {
    public List<Announcement> getAllAnnouncements();

    public Announcement getAnnouncementById(String id);

    public Announcement getRecentAnnouncement();

    /**
     * @note The id property of the object will be overriden.
     * @note the returned boolean indicates if the operation was a success or not.
     * 
     */
    public boolean createAnnouncement(Announcement announcement);

    public boolean deleteAnnouncementById(String id);
}

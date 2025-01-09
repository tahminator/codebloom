package com.patina.codebloom.common.db.repos.points;

import java.util.ArrayList;

import com.patina.codebloom.common.db.models.Points;

public interface PointsRepository {
    Points createPoints(Points points);

    Points getMostRecentPointsById(String id);

    ArrayList<Points> getAllPointsById(String userId);
}

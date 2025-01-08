package com.patina.codebloom.common.db.repos.points;

import com.patina.codebloom.common.db.models.Points;

public interface PointsRepository {
    Points createPoints(Points points);

    Points getPointsById(String id);
}

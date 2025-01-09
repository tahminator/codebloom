package com.patina.codebloom.common.db.repos.points;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.patina.codebloom.common.db.DbConnection;
import com.patina.codebloom.common.db.models.Points;

@Component
public class PointsSqlRepository implements PointsRepository {
    DbConnection dbConnection;
    Connection conn;

    public PointsSqlRepository(DbConnection dbConnection) {
        this.dbConnection = dbConnection;
        this.conn = dbConnection.getConn();
    }

    @Override
    public Points createPoints(Points points) {
        String sql = "INSERT INTO \"Points\" (id, \"userId\", \"totalScore\", \"createdAt\") VALUES (?, ?, ?, ?)";

        points.setId(UUID.randomUUID().toString());

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, UUID.fromString(points.getId()));
            stmt.setString(2, points.getUserId());
            stmt.setInt(3, points.getTotalScore());
            stmt.setObject(4, points.getCreatedAt());

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                return getMostRecentPointsById(points.getId());
            } else {
                throw new RuntimeException("Something went wrong.");
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to create points", e);
        }

    }

    @Override
    public Points getMostRecentPointsById(String id) {
        Points points = null;
        String sql = """
                    SELECT id, "userId", "totalScore", "createdAt"
                    FROM "Points"
                    WHERE "userId" = ? AND "deletedAt" IS NULL
                    ORDER BY "createdAt" DESC
                    LIMIT 1
                """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    var pointsId = rs.getString("id");
                    var userId = rs.getString("userId");
                    var totalScore = rs.getInt("totalScore");
                    var createdAt = rs.getTimestamp("createdAt").toLocalDateTime();
                    points = new Points(pointsId, userId, totalScore, createdAt);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving most recent points.", e);
        }
        return points;
    }

    @Override
    public ArrayList<Points> getAllPointsById(String userId) {
        ArrayList<Points> pointsList = new ArrayList<>();
        String sql = "SELECT id, \"userId\", \"totalScore\", \"createdAt\" FROM \"Points\" WHERE \"userId\" = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    var pointsId = rs.getString("id");
                    var totalScore = rs.getInt("totalScore");
                    var createdAt = rs.getTimestamp("createdAt").toLocalDateTime();

                    Points points = new Points(pointsId, userId, createdAt);
                    points.setTotalScore(totalScore);
                    pointsList.add(points);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving all the points for user.", e);
        }

        return pointsList;
    }

}

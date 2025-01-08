package com.patina.codebloom.common.db.repos.points;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

        points.setId(UUID.randomUUID().toString().replace("-", ""));

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, points.getId());
            stmt.setString(2, points.getUserId());
            stmt.setInt(3, points.getTotalScore());
            stmt.setObject(4, points.getCreatedAt());

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                return getPointsById(points.getId());
            } else {
                throw new RuntimeException("Something went wrong.");
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to create points", e);
        }

    }

    @Override
    public Points getPointsById(String id) {
        Points points = null;
        String sql = "SELECT id, \"userId\", \"totalScore\", \"createdAt\" FROM \"Points\" WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    var pointsId = rs.getString("id");
                    var userId = rs.getString("userId");
                    var totalScore = rs.getInt("totalScore");
                    var createdAt = rs.getTimestamp("createdAt").toLocalDateTime();
                    points = new Points(pointsId, userId, totalScore, createdAt);
                    return points;
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving points.", e);
        }
        return points;
    }
}

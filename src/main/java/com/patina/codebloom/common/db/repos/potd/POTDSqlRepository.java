package com.patina.codebloom.common.db.repos.potd;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.patina.codebloom.common.db.DbConnection;
import com.patina.codebloom.common.db.models.potd.POTD;

@Component
public class POTDSqlRepository implements POTDRepository {

    private Connection conn;

    public POTDSqlRepository(final DbConnection dbConnection) {
        this.conn = dbConnection.getConn();
    }

    private POTD mapRowToPOTD(final ResultSet rs) throws SQLException {
        String id = rs.getString("id");
        String title = rs.getString("title");
        String slug = rs.getString("slug");
        float multiplier = rs.getFloat("multiplier");
        LocalDateTime createdAt = rs.getTimestamp("createdAt").toLocalDateTime();
        return new POTD(id, title, slug, multiplier, createdAt);
    }

    @Override
    public POTD createPOTD(final POTD potd) {
        String sql = """
                INSERT INTO "POTD"
                    ("id", "title", "slug", "multiplier", "createdAt")
                VALUES
                    (?, ?, ?, ?, ?)
                """;

        potd.setId(UUID.randomUUID().toString());

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, UUID.fromString(potd.getId()));
            stmt.setString(2, potd.getTitle());
            stmt.setString(3, potd.getSlug());
            stmt.setFloat(4, potd.getMultiplier());
            stmt.setObject(5, potd.getCreatedAt());

            stmt.executeUpdate();
            return potd;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create POTD", e);
        }
    }

    @Override
    public POTD getPOTDById(final String id) {
        String sql = "SELECT id, \"title\", \"slug\", \"multiplier\", \"createdAt\" FROM \"POTD\" WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToPOTD(rs);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to get POTD by id", e);
        }

        return null;
    }

    @Override
    public ArrayList<POTD> getAllPOTDS() {
        String sql = "SELECT id, \"title\", \"slug\", \"multiplier\", \"createdAt\" FROM \"POTD\"";
        ArrayList<POTD> potdList = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                potdList.add(mapRowToPOTD(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get all POTDs", e);
        }
        return potdList;
    }

    @Override
    public void updatePOTD(final POTD potd) {
        String sql = "UPDATE \"POTD\" SET title = ?, slug = ?, multiplier = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, UUID.fromString(potd.getId()));
            stmt.setString(2, potd.getSlug());
            stmt.setFloat(3, potd.getMultiplier());
            stmt.setString(4, potd.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update POTD", e);
        }
    }

    @Override
    public void deletePOTD(final String id) {
        String sql = "DELETE FROM \"POTD\" WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete POTD", e);
        }
    }

    @Override
    public POTD getCurrentPOTD() {
        String sql = """
                SELECT "id", "title", "slug", "multiplier", "createdAt"
                FROM "POTD"
                ORDER BY "createdAt" DESC
                LIMIT 1
                """;

        try (PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return mapRowToPOTD(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get current POTD", e);
        }
        return null;
    }

}

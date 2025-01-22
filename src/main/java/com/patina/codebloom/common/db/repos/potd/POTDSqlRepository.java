package com.patina.codebloom.common.db.repos.potd;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

import com.patina.codebloom.common.db.DbConnection;
import com.patina.codebloom.common.db.models.potd.POTD;

public class POTDSqlRepository implements POTDRepository {

    DbConnection dbConnection;
    Connection conn;

    public POTDSqlRepository(DbConnection dbConnection) {
        this.dbConnection = dbConnection;
        this.conn = dbConnection.getConn();
    }

    private POTD mapRowToPOTD(ResultSet rs) throws SQLException {
        String id = rs.getString("id");
        String title = rs.getString("title");
        String slug = rs.getString("slug");
        int multiplier = rs.getInt("multiplier");
        LocalDateTime createdAt = rs.getTimestamp("createdAt").toLocalDateTime();
        return new POTD(id, title, slug, multiplier, createdAt);
    }

    @Override
    public POTD createPOTD(POTD potd) {
        String sql = "INSERT INTO potd (id, title, slug, multiplier, createdAt) VALUES (?, ?, ?, ?, ?)";

        potd.setId(UUID.randomUUID().toString());

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, potd.getId());
            stmt.setString(2, potd.getTitle());
            stmt.setString(3, potd.getSlug());
            stmt.setInt(4, potd.getMultiplier());
            stmt.setObject(5, potd.getCreatedAt());

            stmt.executeUpdate();
            return potd;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create POTD");
        }
    }

    @Override
    public POTD getPOTDById(String id) {
        String sql = "SELECT id, \"title\", \"slug\", \"multiplier\", \"createdAt\" FROM potd WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToPOTD(rs);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to get POTD by id");
        }

        return null;
    }

    @Override
    public ArrayList<POTD> getAllPOTDS() {
        String sql = "SELECT id, \"title\", \"slug\", \"multiplier\", \"createdAt\" FROM potd";
        ArrayList<POTD> potdList = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                potdList.add(mapRowToPOTD(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return potdList;
    }

    @Override
    public void updatePOTD(POTD potd) {
        String sql = "UPDATE potd SET title = ?, slug = ?, multiplier = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, potd.getTitle());
            stmt.setString(2, potd.getSlug());
            stmt.setInt(3, potd.getMultiplier());
            stmt.setString(4, potd.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deletePOTD(String id) {
        String sql = "DELETE FROM potd WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public POTD getCurrentPOTD() {
        String sql = "SELECT \"id\", \"title\", \"slug\", \"multiplier\", \"createdAt\" " +
                "FROM potd " +
                "ORDER BY createdAt DESC " +
                "LIMIT 1";

        try (PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return mapRowToPOTD(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}

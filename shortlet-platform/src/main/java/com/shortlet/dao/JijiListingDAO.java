package com.shortlet.dao;

import com.shortlet.model.JijiListing;
import com.shortlet.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JijiListingDAO {

    public void saveOrUpdate(JijiListing listing) {
        String sql = """
            MERGE INTO jiji_listings(jiji_url, title, price_numeric, price_text, location, description, image_url, is_active)
            KEY(jiji_url)
            VALUES(?, ?, ?, ?, ?, ?, ?, TRUE)
            """;
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, listing.getJijiUrl());
            stmt.setString(2, listing.getTitle());
            stmt.setBigDecimal(3, listing.getPriceNumeric());
            stmt.setString(4, listing.getPriceText());
            stmt.setString(5, listing.getLocation());
            stmt.setString(6, listing.getDescription());
            stmt.setString(7, listing.getImageUrl());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<JijiListing> getActiveListings(int limit, int offset) {
        List<JijiListing> listings = new ArrayList<>();
        String sql = "SELECT * FROM jiji_listings WHERE is_active = TRUE ORDER BY scraped_at DESC LIMIT ? OFFSET ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, limit);
            stmt.setInt(2, offset);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    listings.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return listings;
    }

    public List<JijiListing> searchActiveListings(String query, int limit, int offset) {
        if (query == null || query.isBlank()) {
            return getActiveListings(limit, offset);
        }
        List<JijiListing> listings = new ArrayList<>();
        String sql = """
            SELECT * FROM jiji_listings 
            WHERE is_active = TRUE 
              AND (LOWER(title) LIKE ? OR LOWER(location) LIKE ?)
            ORDER BY scraped_at DESC LIMIT ? OFFSET ?
            """;
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            String match = "%" + query.toLowerCase() + "%";
            stmt.setString(1, match);
            stmt.setString(2, match);
            stmt.setInt(3, limit);
            stmt.setInt(4, offset);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    listings.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return listings;
    }

    public JijiListing getListingById(long id) {
        String sql = "SELECT * FROM jiji_listings WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void saveInquiry(long userId, long listingId) {
        String sql = "INSERT INTO jiji_inquiries (user_id, jiji_listing_id) VALUES (?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, userId);
            stmt.setLong(2, listingId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean hasInquired(long userId, long listingId) {
        String sql = "SELECT COUNT(*) FROM jiji_inquiries WHERE user_id = ? AND jiji_listing_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, userId);
            stmt.setLong(2, listingId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private JijiListing mapRow(ResultSet rs) throws SQLException {
        JijiListing listing = new JijiListing();
        listing.setId(rs.getLong("id"));
        listing.setJijiUrl(rs.getString("jiji_url"));
        listing.setTitle(rs.getString("title"));
        listing.setPriceNumeric(rs.getBigDecimal("price_numeric"));
        listing.setPriceText(rs.getString("price_text"));
        listing.setLocation(rs.getString("location"));
        listing.setDescription(rs.getString("description"));
        listing.setImageUrl(rs.getString("image_url"));
        listing.setScrapedAt(rs.getTimestamp("scraped_at"));
        listing.setActive(rs.getBoolean("is_active"));
        return listing;
    }
}

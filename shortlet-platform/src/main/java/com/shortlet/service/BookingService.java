package com.shortlet.service;

import com.shortlet.model.Booking;
import com.shortlet.model.Property;
import com.shortlet.util.DatabaseUtil;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BookingService {
    public List<Booking> findByUser(long userId) throws SQLException {
        return queryBookings("""
                SELECT b.id, b.user_id, u.name AS user_name, u.email AS user_email, u.password_hash,
                       p.title AS apartment_title, p.city, p.nightly_rate,
                       b.check_in, b.check_out, b.nights, b.total_amount, b.payment_method, b.payment_status,
                       b.status, b.created_at
                FROM bookings b
                JOIN users u ON u.id = b.user_id
                JOIN properties p ON p.id = b.property_id
                WHERE b.user_id = ?
                ORDER BY b.check_in
                """, userId);
    }

    public List<Booking> findAllWithUsers() throws SQLException {
        return queryBookings("""
                SELECT b.id, u.id AS user_id, u.name AS user_name, u.email AS user_email, u.password_hash,
                       p.title AS apartment_title, p.city, p.nightly_rate,
                       b.check_in, b.check_out, b.nights, b.total_amount, b.payment_method, b.payment_status,
                       COALESCE(b.status, 'NO_BOOKING') AS status, b.created_at
                FROM users u
                LEFT JOIN bookings b ON b.user_id = u.id
                LEFT JOIN properties p ON p.id = b.property_id
                ORDER BY u.id, b.created_at DESC
                """);
    }

    public List<Property> searchProperties(String city) throws SQLException {
        List<Property> properties = new ArrayList<>();
        String sql = "SELECT * FROM properties WHERE (? IS NULL OR LOWER(city) LIKE ?) ORDER BY nightly_rate";
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            String filter = city == null || city.isBlank() ? null : "%" + city.toLowerCase() + "%";
            statement.setString(1, filter);
            statement.setString(2, filter);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    Property property = new Property();
                    property.setId(rs.getLong("id"));
                    property.setTitle(rs.getString("title"));
                    property.setCity(rs.getString("city"));
                    property.setAddress(rs.getString("address"));
                    property.setNightlyRate(rs.getBigDecimal("nightly_rate"));
                    property.setImageUrl(rs.getString("image_url"));
                    property.setSourceUrl(rs.getString("source_url"));
                    properties.add(property);
                    properties.add(mapProperty(rs));
                }
            }
        }
        return properties;
    }

    public List<Property> getFeaturedProperties(int limit) throws SQLException {
        List<Property> properties = new ArrayList<>();
        String sql = "SELECT * FROM properties WHERE featured = TRUE OR rating >= 4.7 ORDER BY rating DESC LIMIT ?";
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, limit);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    properties.add(mapProperty(rs));
                }
            }
        }
        return properties;
    }

    public Optional<Property> findPropertyById(long propertyId) throws SQLException {
        String sql = "SELECT * FROM properties WHERE id = ?";
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, propertyId);
            try (ResultSet rs = statement.executeQuery()) {
                if (!rs.next()) {
                    return Optional.empty();
                }
                Property property = new Property();
                property.setId(rs.getLong("id"));
                property.setTitle(rs.getString("title"));
                property.setCity(rs.getString("city"));
                property.setAddress(rs.getString("address"));
                property.setNightlyRate(rs.getBigDecimal("nightly_rate"));
                property.setImageUrl(rs.getString("image_url"));
                property.setSourceUrl(rs.getString("source_url"));
                return Optional.of(property);
            }
        }
    }

    public Booking createBooking(long userId, long propertyId, LocalDate checkIn, LocalDate checkOut, String paymentMethod) throws SQLException {
        if (!checkOut.isAfter(checkIn)) {
            throw new IllegalArgumentException("Check-out date must be after check-in date.");
        }
        Property property = findPropertyById(propertyId)
                .orElseThrow(() -> new IllegalArgumentException("Selected apartment was not found."));
        long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
        BigDecimal total = property.getNightlyRate().multiply(BigDecimal.valueOf(nights));
        String normalizedPaymentMethod = normalizePaymentMethod(paymentMethod);
        String status = "PAY_ON_ARRIVAL".equals(normalizedPaymentMethod) ? "RESERVED" : "CONFIRMED";
        String paymentStatus = "PAY_ON_ARRIVAL".equals(normalizedPaymentMethod) ? "PENDING" : "PAYMENT_METHOD_SELECTED";

        String sql = """
                INSERT INTO bookings(user_id, property_id, check_in, check_out, nights, total_amount, payment_method, payment_status, status)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1, userId);
            statement.setLong(2, propertyId);
            statement.setDate(3, java.sql.Date.valueOf(checkIn));
            statement.setDate(4, java.sql.Date.valueOf(checkOut));
            statement.setLong(5, nights);
            statement.setBigDecimal(6, total);
            statement.setString(7, normalizedPaymentMethod);
            statement.setString(8, paymentStatus);
            statement.setString(9, status);
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                keys.next();
                long bookingId = keys.getLong(1);
                List<Booking> results = queryBookings("""
                        SELECT b.id, b.user_id, u.name AS user_name, u.email AS user_email, u.password_hash,
                               p.title AS apartment_title, p.city, p.nightly_rate,
                               b.check_in, b.check_out, b.nights, b.total_amount, b.payment_method, b.payment_status,
                               b.status, b.created_at
                        FROM bookings b
                        JOIN users u ON u.id = b.user_id
                        JOIN properties p ON p.id = b.property_id
                        WHERE b.id = ?
                        """, bookingId);
                if (results.isEmpty()) {
                    throw new SQLException("Failed to retrieve created booking.");
                }
                return results.get(0);
            }
        }
    }

    private String normalizePaymentMethod(String paymentMethod) {
        if (paymentMethod == null) {
            return "PAY_ON_ARRIVAL";
        }
        return switch (paymentMethod) {
            case "CARD", "BANK_TRANSFER", "USSD", "PAY_ON_ARRIVAL" -> paymentMethod;
            default -> "PAY_ON_ARRIVAL";
        };
    }

    private List<Booking> queryBookings(String sql, Object... params) throws SQLException {
        List<Booking> bookings = new ArrayList<>();
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                statement.setObject(i + 1, params[i]);
            }
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    Booking booking = new Booking();
                    booking.setId(rs.getLong("id"));
                    booking.setUserId(rs.getLong("user_id"));
                    booking.setUserName(rs.getString("user_name"));
                    booking.setUserEmail(rs.getString("user_email"));
                    booking.setPasswordHash(rs.getString("password_hash"));
                    booking.setApartmentTitle(rs.getString("apartment_title"));
                    booking.setCity(rs.getString("city"));
                    booking.setNightlyRate(rs.getBigDecimal("nightly_rate"));
                    booking.setCheckIn(rs.getDate("check_in") == null ? null : rs.getDate("check_in").toLocalDate());
                    booking.setCheckOut(rs.getDate("check_out") == null ? null : rs.getDate("check_out").toLocalDate());
                    booking.setNights(rs.getLong("nights"));
                    booking.setTotalAmount(rs.getBigDecimal("total_amount"));
                    booking.setPaymentMethod(rs.getString("payment_method"));
                    booking.setPaymentStatus(rs.getString("payment_status"));
                    booking.setStatus(rs.getString("status"));
                    booking.setCreatedAt(rs.getTimestamp("created_at") == null ? null : rs.getTimestamp("created_at").toLocalDateTime());
                    bookings.add(booking);
                }
            }
        }
        return bookings;
    }

    public List<Property> searchPropertiesWithFilters(String city, Double maxPrice, Boolean wifi, Boolean selfCheckIn, Boolean nearAirport, String sortBy, Long currentUserId) throws SQLException {
        List<Property> properties = new ArrayList<>();
        String dbType = "";
        try (Connection conn = DatabaseUtil.getConnection()) {
            dbType = conn.getMetaData().getDatabaseProductName().toLowerCase();
        }

        String dateFunc = dbType.contains("sqlite") 
            ? "datetime('now', '-30 minutes')" 
            : "DATEADD(MINUTE, -30, CURRENT_TIMESTAMP)";

        StringBuilder sql = new StringBuilder("SELECT p.*, ");
        sql.append(" (SELECT COUNT(*) FROM wishlist w WHERE w.property_id = p.id AND w.user_id = ?) AS is_wishlisted, ");
        sql.append(" (SELECT COUNT(*) FROM property_views pv WHERE pv.property_id = p.id AND pv.viewed_at >= ").append(dateFunc).append(") AS view_count ");
        sql.append(" FROM properties p WHERE 1=1 ");
        
        List<Object> params = new ArrayList<>();
        params.add(currentUserId);
        
        if (city != null && !city.isBlank()) {
            sql.append(" AND LOWER(p.city) LIKE ? ");
            params.add("%" + city.toLowerCase() + "%");
        }
        if (maxPrice != null) {
            sql.append(" AND p.nightly_rate <= ? ");
            params.add(BigDecimal.valueOf(maxPrice));
        }
        if (wifi != null && wifi) {
            sql.append(" AND p.wifi = TRUE ");
        }
        if (selfCheckIn != null && selfCheckIn) {
            sql.append(" AND p.self_check_in = TRUE ");
        }
        if (nearAirport != null && nearAirport) {
            sql.append(" AND p.near_airport = TRUE ");
        }
        
        if (sortBy != null) {
            switch (sortBy) {
                case "price_asc" -> sql.append(" ORDER BY p.nightly_rate ASC ");
                case "price_desc" -> sql.append(" ORDER BY p.nightly_rate DESC ");
                case "rating" -> sql.append(" ORDER BY p.rating DESC ");
                default -> sql.append(" ORDER BY p.nightly_rate ASC ");
            }
        } else {
            sql.append(" ORDER BY p.nightly_rate ASC ");
        }
        
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                statement.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    Property property = mapProperty(rs);
                    property.setIsWishlisted(rs.getInt("is_wishlisted") > 0);
                    property.setUrgencyViews(rs.getInt("view_count"));
                    property.setImages(getPropertyImages(property.getId()));
                    properties.add(property);
                }
            }
        }
        return properties;
    }

    private Property mapProperty(ResultSet rs) throws SQLException {
        Property property = new Property();
        property.setId(rs.getLong("id"));
        property.setTitle(rs.getString("title"));
        property.setCity(rs.getString("city"));
        property.setAddress(rs.getString("address"));
        property.setNightlyRate(rs.getBigDecimal("nightly_rate"));
        property.setImageUrl(rs.getString("image_url"));
        property.setSourceUrl(rs.getString("source_url"));
        property.setLatitude(rs.getDouble("latitude"));
        property.setLongitude(rs.getDouble("longitude"));
        property.setWifi(rs.getBoolean("wifi"));
        property.setSelfCheckIn(rs.getBoolean("self_check_in"));
        property.setNearAirport(rs.getBoolean("near_airport"));
        property.setReplyTimeMinutes(rs.getInt("reply_time_minutes"));
        property.setRating(rs.getDouble("rating"));
        return property;
    }

    public List<String> getPropertyImages(long propertyId) throws SQLException {
        List<String> images = new ArrayList<>();
        String sql = "SELECT image_url FROM property_images WHERE property_id = ?";
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, propertyId);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    images.add(rs.getString("image_url"));
                }
            }
        }
        return images;
    }

    public List<Property> getRecommendations(long userId) throws SQLException {
        String city = "Lagos"; // fallback city
        BigDecimal avgPrice = BigDecimal.valueOf(150000.00); // fallback price range center
        
        // Query search history for the user's latest search
        String historySql = "SELECT city FROM search_history WHERE user_id = ? ORDER BY searched_at DESC LIMIT 1";
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(historySql)) {
            statement.setLong(1, userId);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    city = rs.getString("city");
                }
            }
        }
        
        // Get user's average booked price or current properties in that city
        String avgPriceSql = """
            SELECT AVG(p.nightly_rate) AS avg_rate 
            FROM bookings b 
            JOIN properties p ON p.id = b.property_id 
            WHERE b.user_id = ?
            """;
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(avgPriceSql)) {
            statement.setLong(1, userId);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next() && rs.getObject("avg_rate") != null) {
                    Object val = rs.getObject("avg_rate");
                    avgPrice = new java.math.BigDecimal(val.toString());
                }
            }
        }
        
        // Now search properties in the latest city with price range within ±20%
        BigDecimal minPrice = avgPrice.multiply(BigDecimal.valueOf(0.8));
        BigDecimal maxPrice = avgPrice.multiply(BigDecimal.valueOf(1.2));
        
        List<Property> recommended = new ArrayList<>();
        String recSql = """
            SELECT p.*,
                   (SELECT COUNT(*) FROM wishlist w WHERE w.property_id = p.id AND w.user_id = ?) AS is_wishlisted
            FROM properties p
            WHERE LOWER(p.city) = LOWER(?) AND p.nightly_rate BETWEEN ? AND ?
            LIMIT 3
            """;
        
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(recSql)) {
            statement.setLong(1, userId);
            statement.setString(2, city);
            statement.setBigDecimal(3, minPrice);
            statement.setBigDecimal(4, maxPrice);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    Property property = mapProperty(rs);
                    property.setIsWishlisted(rs.getInt("is_wishlisted") > 0);
                    property.setImages(getPropertyImages(property.getId()));
                    recommended.add(property);
                }
            }
        }
        
        // If we found fewer than 3, fill it up with properties from other categories or other cities within matching price
        if (recommended.size() < 3) {
            StringBuilder fallbackSql = new StringBuilder();
            fallbackSql.append("SELECT p.*, ");
            fallbackSql.append("       (SELECT COUNT(*) FROM wishlist w WHERE w.property_id = p.id AND w.user_id = ?) AS is_wishlisted ");
            fallbackSql.append("FROM properties p ");
            fallbackSql.append("WHERE p.id NOT IN (-1");
            for (Property r : recommended) {
                fallbackSql.append(", ").append(r.getId());
            }
            fallbackSql.append(") AND (p.featured = TRUE OR p.rating >= 4.5) LIMIT ?");

            try (Connection connection = DatabaseUtil.getConnection();
                 PreparedStatement statement = connection.prepareStatement(fallbackSql.toString())) {
                statement.setLong(1, userId);
                statement.setInt(2, 3 - recommended.size());
                try (ResultSet rs = statement.executeQuery()) {
                    while (rs.next()) {
                        Property property = mapProperty(rs);
                        property.setIsWishlisted(rs.getInt("is_wishlisted") > 0);
                        property.setImages(getPropertyImages(property.getId()));
                        recommended.add(property);
                    }
                }
            }
        }
        
        return recommended;
    }

    public boolean toggleWishlist(long userId, long propertyId) throws SQLException {
        boolean exists = false;
        String checkSql = "SELECT COUNT(*) FROM wishlist WHERE user_id = ? AND property_id = ?";
        
        try (Connection connection = DatabaseUtil.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(checkSql)) {
                statement.setLong(1, userId);
                statement.setLong(2, propertyId);
                try (ResultSet rs = statement.executeQuery()) {
                    if (rs.next()) {
                        exists = rs.getInt(1) > 0;
                    }
                }
            }

            if (exists) {
                String deleteSql = "DELETE FROM wishlist WHERE user_id = ? AND property_id = ?";
                try (PreparedStatement statement = connection.prepareStatement(deleteSql)) {
                    statement.setLong(1, userId);
                    statement.setLong(2, propertyId);
                    statement.executeUpdate();
                }
                return false;
            } else {
                String insertSql = "INSERT INTO wishlist(user_id, property_id) VALUES (?, ?)";
                try (PreparedStatement statement = connection.prepareStatement(insertSql)) {
                    statement.setLong(1, userId);
                    statement.setLong(2, propertyId);
                    statement.executeUpdate();
                }
                return true;
            }
        }
    }

    public List<Property> getWishlistedProperties(long userId) throws SQLException {
        List<Property> properties = new ArrayList<>();
        String sql = """
            SELECT p.*,
                   (SELECT COUNT(*) FROM wishlist w WHERE w.property_id = p.id AND w.user_id = ?) AS is_wishlisted
            FROM wishlist w
            JOIN properties p ON p.id = w.property_id
            WHERE w.user_id = ?
            """;
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, userId);
            statement.setLong(2, userId);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    Property property = mapProperty(rs);
                    property.setIsWishlisted(true);
                    property.setImages(getPropertyImages(property.getId()));
                    properties.add(property);
                }
            }
        }
        return properties;
    }

    public void recordPropertyView(long propertyId) throws SQLException {
        String sql = "INSERT INTO property_views(property_id) VALUES (?)";
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, propertyId);
            statement.executeUpdate();
        }
    }

    public List<java.util.Map<String, Object>> getAllTeamMembers() throws SQLException {
        List<java.util.Map<String, Object>> team = new ArrayList<>();
        String sql = "SELECT * FROM team_members ORDER BY id";
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                java.util.Map<String, Object> member = new java.util.HashMap<>();
                member.put("name", rs.getString("name"));
                member.put("role", rs.getString("role"));
                member.put("bio", rs.getString("bio"));
                member.put("imageUrl", rs.getString("image_url"));
                team.add(member);
            }
        }
        return team;
    }

    public List<java.util.Map<String, Object>> getRecentTestimonials(int limit) throws SQLException {
        List<java.util.Map<String, Object>> testimonials = new ArrayList<>();
        String sql = """
            SELECT t.*, p.id AS p_id, p.title AS property_title, p.image_url AS property_image_url
            FROM testimonials t
            LEFT JOIN properties p ON t.property_id = p.id
            ORDER BY t.id DESC LIMIT ?
            """;
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, limit);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    java.util.Map<String, Object> t = new java.util.HashMap<>();
                    t.put("guestName", rs.getString("guest_name"));
                    t.put("location", rs.getString("location"));
                    t.put("rating", rs.getInt("rating"));
                    t.put("quote", rs.getString("quote"));
                    t.put("propertyId", rs.getObject("property_id") != null ? rs.getLong("p_id") : null);
                    t.put("propertyTitle", rs.getString("property_title"));
                    t.put("propertyImageUrl", rs.getString("property_image_url"));
                    testimonials.add(t);
                }
            }
        }
        return testimonials;
    }

    public void recordPropertyViews(List<Long> propertyIds) throws SQLException {
        if (propertyIds == null || propertyIds.isEmpty()) return;
        String sql = "INSERT INTO property_views(property_id) VALUES (?)";
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            for (long id : propertyIds) {
                statement.setLong(1, id);
                statement.addBatch();
            }
            statement.executeBatch();
        }
    }

    public List<String> getSearchSuggestions(String query) throws SQLException {
        List<String> suggestions = new ArrayList<>();
        if (query == null || query.isBlank()) {
            return suggestions;
        }
        String sql = """
            SELECT DISTINCT city AS suggestion FROM properties WHERE LOWER(city) LIKE ?
            UNION
            SELECT DISTINCT address AS suggestion FROM properties WHERE LOWER(address) LIKE ?
            LIMIT 5
            """;
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            String filter = "%" + query.toLowerCase() + "%";
            statement.setString(1, filter);
            statement.setString(2, filter);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    suggestions.add(rs.getString("suggestion"));
                }
            }
        }
        return suggestions;
    }

    public void recordSearchHistory(long userId, String city) throws SQLException {
        if (city == null || city.isBlank()) return;
        String sql = "INSERT INTO search_history(user_id, city) VALUES (?, ?)";
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, userId);
            statement.setString(2, city);
            statement.executeUpdate();
        }
    }
}

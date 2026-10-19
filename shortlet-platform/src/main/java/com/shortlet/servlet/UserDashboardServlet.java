package com.shortlet.servlet;

import com.shortlet.model.Property;
import com.shortlet.model.User;
import com.shortlet.service.BookingService;
import com.shortlet.util.ServletUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.time.format.DateTimeParseException;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class UserDashboardServlet extends HttpServlet {
    private final BookingService bookingService = new BookingService();

    @Override
    @SuppressWarnings("unchecked")
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = ServletUtil.currentUser(request);
        
        String city = request.getParameter("city");
        String maxPriceStr = request.getParameter("maxPrice");
        String wifiStr = request.getParameter("wifi");
        String selfCheckInStr = request.getParameter("selfCheckIn");
        String nearAirportStr = request.getParameter("nearAirport");
        String sortBy = request.getParameter("sortBy");
        String tab = request.getParameter("tab");
        if (tab == null) {
            tab = "all";
        }
        
        Double maxPrice = null;
        if (maxPriceStr != null && !maxPriceStr.isBlank()) {
            try {
                maxPrice = Double.parseDouble(maxPriceStr);
            } catch (NumberFormatException ignored) {}
        }
        
        boolean wifi = "true".equals(wifiStr);
        boolean selfCheckIn = "true".equals(selfCheckInStr);
        boolean nearAirport = "true".equals(nearAirportStr);
        
        // Handle search history in session
        HttpSession session = request.getSession();
        List<String> recentSearches = (List<String>) session.getAttribute("recentSearches");
        if (recentSearches == null) {
            recentSearches = new ArrayList<>();
        }
        if (city != null && !city.isBlank()) {
            recentSearches.remove(city);
            recentSearches.add(0, city);
            if (recentSearches.size() > 3) {
                recentSearches = recentSearches.subList(0, 3);
            }
            session.setAttribute("recentSearches", recentSearches);
            
            try {
                bookingService.recordSearchHistory(user.getId(), city);
            } catch (SQLException ignored) {}
        }

        try {
            List<Property> properties;
            if ("favourites".equals(tab)) {
                properties = bookingService.getWishlistedProperties(user.getId());
            } else {
                properties = bookingService.searchPropertiesWithFilters(city, maxPrice, wifi, selfCheckIn, nearAirport, sortBy, user.getId());
            }
            
            // Record view for loaded properties to simulate real-time engagement!
            bookingService.recordPropertyViews(properties.stream().map(Property::getId).toList());
            
            // Fetch Recommendations
            List<Property> recommendations = bookingService.getRecommendations(user.getId());
            
            // Fetch Jiji Listings based on active city filter
            com.shortlet.dao.JijiListingDAO jijiDAO = new com.shortlet.dao.JijiListingDAO();
            List<com.shortlet.model.JijiListing> jijiListings;
            if (city != null && !city.isBlank()) {
                jijiListings = jijiDAO.searchActiveListings(city, 12, 0);
            } else {
                jijiListings = jijiDAO.getActiveListings(12, 0);
            }
            
            request.setAttribute("bookings", bookingService.findByUser(user.getId()));
            request.setAttribute("properties", properties);
            request.setAttribute("recommendations", recommendations);
            request.setAttribute("jijiListings", jijiListings);
            request.setAttribute("city", city == null ? "" : city);
            request.setAttribute("maxPrice", maxPriceStr == null ? "" : maxPriceStr);
            request.setAttribute("wifi", wifi);
            request.setAttribute("selfCheckIn", selfCheckIn);
            request.setAttribute("nearAirport", nearAirport);
            request.setAttribute("sortBy", sortBy == null ? "" : sortBy);
            request.setAttribute("tab", tab);
            request.setAttribute("recentSearches", recentSearches);
            
            request.getRequestDispatcher("/WEB-INF/views/user-dashboard.jsp").forward(request, response);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = ServletUtil.currentUser(request);
        try {
            long propertyId = Long.parseLong(request.getParameter("propertyId"));
            LocalDate checkIn = LocalDate.parse(request.getParameter("checkIn"));
            LocalDate checkOut = LocalDate.parse(request.getParameter("checkOut"));
            String paymentMethod = request.getParameter("paymentMethod");
            bookingService.createBooking(user.getId(), propertyId, checkIn, checkOut, paymentMethod);
            response.sendRedirect("/dashboard?booked=true");
        } catch (IllegalArgumentException | DateTimeParseException e) {
            request.setAttribute("error", e.getMessage());
            doGet(request, response);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}

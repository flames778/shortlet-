package com.shortlet.servlet;

import com.google.gson.Gson;
import com.shortlet.model.Property;
import com.shortlet.service.BookingService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PropertyMarkersServlet extends HttpServlet {
    private final BookingService bookingService = new BookingService();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        try {
            List<Property> properties = bookingService.searchPropertiesWithFilters(null, null, false, false, false, null, 0L);
            List<Map<String, Object>> markers = new ArrayList<>();
            for (Property p : properties) {
                Map<String, Object> marker = new HashMap<>();
                marker.put("id", p.getId());
                marker.put("title", p.getTitle());
                marker.put("price", p.getNightlyRate());
                marker.put("latitude", p.getLatitude());
                marker.put("longitude", p.getLongitude());
                markers.add(marker);
            }
            response.getWriter().write(gson.toJson(markers));
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
}

package com.shortlet.servlet;

import com.shortlet.model.Property;
import com.shortlet.service.BookingService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class SearchServlet extends HttpServlet {
    private final BookingService bookingService = new BookingService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String city = request.getParameter("city");
        try {
            List<Property> results = bookingService.searchProperties(city);
            request.setAttribute("searchResults", results);
            request.getRequestDispatcher("/WEB-INF/views/searchResults.jsp").forward(request, response);
        } catch (SQLException e) {
            throw new ServletException("Search operation failed", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}
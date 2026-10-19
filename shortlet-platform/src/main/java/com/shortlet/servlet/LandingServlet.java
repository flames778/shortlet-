package com.shortlet.servlet;

import com.shortlet.service.BookingService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

public class LandingServlet extends HttpServlet {
    private final BookingService bookingService = new BookingService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            request.setAttribute("featuredProperties", bookingService.getFeaturedProperties(3));
            request.setAttribute("team", bookingService.getAllTeamMembers());
            request.setAttribute("testimonials", bookingService.getRecentTestimonials(3));
            request.getRequestDispatcher("/WEB-INF/views/landing.jsp").forward(request, response);
        } catch (SQLException e) {
            throw new ServletException("Failed to load landing page data", e);
        }
    }
}

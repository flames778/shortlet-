package com.shortlet.servlet;

import com.google.gson.Gson;
import com.shortlet.dao.JijiListingDAO;
import com.shortlet.model.User;
import com.shortlet.util.ServletUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class JijiInquiryServlet extends HttpServlet {
    private final JijiListingDAO jijiListingDAO = new JijiListingDAO();
    private final Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        User user = ServletUtil.currentUser(request);
        if (user == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\": \"Unauthorized\"}");
            return;
        }

        String listingIdStr = request.getParameter("listingId");
        if (listingIdStr == null || listingIdStr.isBlank()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"Missing listingId parameter\"}");
            return;
        }

        try {
            long listingId = Long.parseLong(listingIdStr);
            
            // Check if user has already inquired about this listing
            boolean alreadyInquired = jijiListingDAO.hasInquired(user.getId(), listingId);
            Map<String, Object> result = new HashMap<>();
            
            if (!alreadyInquired) {
                jijiListingDAO.saveInquiry(user.getId(), listingId);
                result.put("success", true);
                result.put("message", "Inquiry recorded successfully! Our team will reach out to you within 2 hours.");
            } else {
                result.put("success", true);
                result.put("message", "You have already inquired about this property. Our support team is already working on your request!");
            }
            
            response.getWriter().write(gson.toJson(result));
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"Invalid listingId format\"}");
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
}

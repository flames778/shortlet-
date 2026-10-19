package com.shortlet.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class ErrorServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String code = request.getParameter("code");
        if (code == null) {
            Object status = request.getAttribute("jakarta.servlet.error.status_code");
            code = status == null ? "500" : status.toString();
        }
        request.setAttribute("code", code);
        request.setAttribute("message", switch (code) {
            case "403" -> "You do not have permission to access this page.";
            case "404" -> "The page you requested could not be found.";
            default -> "Something unexpected happened. Please try again.";
        });
        request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
    }
}

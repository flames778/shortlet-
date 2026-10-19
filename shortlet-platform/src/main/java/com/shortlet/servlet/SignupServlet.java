package com.shortlet.servlet;

import com.shortlet.service.UserService;
import com.shortlet.util.PasswordUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/signup")
public class SignupServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/signup.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String fullName = req.getParameter("fullName");
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        String confirmPassword = req.getParameter("confirmPassword");
        String terms = req.getParameter("terms");

        if (fullName == null || fullName.isEmpty() || email == null || email.isEmpty() || password == null || password.isEmpty() || terms == null) {
            req.setAttribute("error", "All fields are required and terms must be accepted.");
            req.getRequestDispatcher("/WEB-INF/views/signup.jsp").forward(req, resp);
            return;
        }

        if (!password.equals(confirmPassword)) {
            req.setAttribute("error", "Passwords do not match.");
            req.getRequestDispatcher("/WEB-INF/views/signup.jsp").forward(req, resp);
            return;
        }

        if (password.length() < 8) {
            req.setAttribute("error", "Password must be at least 8 characters long.");
            req.getRequestDispatcher("/WEB-INF/views/signup.jsp").forward(req, resp);
            return;
        }

        try {
            UserService userService = new UserService();
            if (userService.findByEmail(email).isPresent()) {
                req.setAttribute("error", "Email is already registered.");
                req.getRequestDispatcher("/WEB-INF/views/signup.jsp").forward(req, resp);
                return;
            }

            userService.register(fullName, email, password);
            resp.sendRedirect("/login?success=Account created successfully. Please log in.");
        } catch (SQLException e) {
            throw new ServletException("Error during signup", e);
        }
    }
}
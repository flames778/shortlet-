package com.shortlet.servlet;

import com.shortlet.model.User;
import com.shortlet.service.UserService;
import com.shortlet.util.PasswordUtil;
import com.shortlet.util.ServletUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (ServletUtil.currentUser(req) != null) {
            resp.sendRedirect("/");
            return;
        }
        req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = req.getParameter("email");
        String password = req.getParameter("password");

        try {
            UserService userService = new UserService();
            User user = userService.findByEmail(email).orElse(null);

            if (user != null && PasswordUtil.verify(password, user.getPasswordHash())) {
                ServletUtil.login(req, user);
                resp.sendRedirect("ADMIN".equals(user.getRole()) ? "/admin" : "/dashboard");
            } else {
                req.setAttribute("error", "Invalid email or password");
                req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, resp);
            }
        } catch (SQLException e) {
            throw new ServletException("Error during login", e);
        }
    }
}
package com.shortlet.util;

import com.shortlet.model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

public final class ServletUtil {
    private ServletUtil() {
    }

    public static User currentUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return session == null ? null : (User) session.getAttribute("user");
    }

    public static void login(HttpServletRequest request, User user) {
        request.getSession(true);
        request.changeSessionId();
        request.getSession(false).setAttribute("user", user);
    }

    public static boolean isAdmin(HttpServletRequest request) {
        User user = currentUser(request);
        return user != null && "ADMIN".equals(user.getRole());
    }
}

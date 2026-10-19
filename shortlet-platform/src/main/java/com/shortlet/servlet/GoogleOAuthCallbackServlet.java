package com.shortlet.servlet;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.shortlet.model.User;
import com.shortlet.service.UserService;
import com.shortlet.util.AppConfig;
import com.shortlet.util.ServletUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class GoogleOAuthCallbackServlet extends HttpServlet {
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final Gson gson = new Gson();
    private final UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String expectedState = (String) request.getSession(true).getAttribute("oauth_state");
        String actualState = request.getParameter("state");
        String code = request.getParameter("code");
        if (expectedState == null || !expectedState.equals(actualState) || code == null) {
            response.sendRedirect("/login?error=oauth_state");
            return;
        }
        request.getSession().removeAttribute("oauth_state");
        try {
            String accessToken = exchangeCode(code);
            JsonObject profile = fetchProfile(accessToken);
            String email = profile.get("email").getAsString();
            String name = profile.has("name") ? profile.get("name").getAsString() : email;
            User user = userService.findOrCreateGoogleUser(name, email);
            ServletUtil.login(request, user);
            response.sendRedirect("ADMIN".equals(user.getRole()) ? "/admin" : "/dashboard");
        } catch (Exception e) {
            throw new ServletException("Google OAuth login failed", e);
        }
    }

    private String exchangeCode(String code) throws IOException, InterruptedException {
        String form = "code=" + enc(code)
                + "&client_id=" + enc(AppConfig.get("google.clientId"))
                + "&client_secret=" + enc(AppConfig.get("google.clientSecret"))
                + "&redirect_uri=" + enc(AppConfig.get("google.redirectUri"))
                + "&grant_type=authorization_code";
        HttpRequest request = HttpRequest.newBuilder(URI.create("https://oauth2.googleapis.com/token"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(form))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() >= 300) {
            throw new IOException("Google token endpoint returned " + response.statusCode());
        }
        return gson.fromJson(response.body(), JsonObject.class).get("access_token").getAsString();
    }

    private JsonObject fetchProfile(String accessToken) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder(URI.create("https://openidconnect.googleapis.com/v1/userinfo"))
                .header("Authorization", "Bearer " + accessToken)
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() >= 300) {
            throw new IOException("Google userinfo endpoint returned " + response.statusCode());
        }
        return gson.fromJson(response.body(), JsonObject.class);
    }

    private String enc(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}

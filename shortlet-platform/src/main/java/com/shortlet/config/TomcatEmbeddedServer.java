package com.shortlet.config;

import com.shortlet.filter.AuthenticationFilter;
import com.shortlet.servlet.AdminDashboardServlet;
import com.shortlet.servlet.ErrorServlet;
import com.shortlet.servlet.ExportExcelServlet;
import com.shortlet.servlet.ForgotPasswordServlet;
import com.shortlet.servlet.GoogleOAuthCallbackServlet;
import com.shortlet.servlet.LandingServlet;
import com.shortlet.servlet.LoginServlet;
import com.shortlet.servlet.SearchServlet;
import com.shortlet.servlet.LogoutServlet;
import com.shortlet.servlet.SignupServlet;
import com.shortlet.servlet.UserDashboardServlet;
import com.shortlet.servlet.PropertyMarkersServlet;
import com.shortlet.servlet.SearchSuggestionServlet;
import com.shortlet.servlet.WishlistToggleServlet;
import com.shortlet.servlet.JijiInquiryServlet;
import org.apache.catalina.Context;
import org.apache.catalina.Wrapper;
import org.apache.catalina.startup.Tomcat;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.CodeSource;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class TomcatEmbeddedServer {
    public static void main(String[] args) throws Exception {
        int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "8080"));

        // Development logic: Use src/main/webapp directly if it exists for instant JSP updates
        // We check the current directory and the 'shortlet-platform' subdirectory for IDE flexibility
        Path webappPath = Path.of("src/main/webapp").toAbsolutePath();
        if (!Files.exists(webappPath)) {
            webappPath = Path.of("shortlet-platform", "src", "main", "webapp").toAbsolutePath();
        }
        if (!Files.exists(webappPath)) {
            webappPath = extractWebapp();
        }
        System.out.println("Configuring webapp with docBase: " + webappPath);

        Tomcat tomcat = new Tomcat();
        tomcat.setPort(port);
        tomcat.getConnector();

        Context context = tomcat.addWebapp("", webappPath.toString());
        context.setParentClassLoader(TomcatEmbeddedServer.class.getClassLoader());
        
        // Map target/classes to WEB-INF/classes so Tomcat can load application listeners and classes
        org.apache.catalina.WebResourceRoot resources = new org.apache.catalina.webresources.StandardRoot(context);
        resources.addPreResources(new org.apache.catalina.webresources.DirResourceSet(
                resources, "/WEB-INF/classes", new java.io.File("target/classes").getAbsolutePath(), "/"));
        context.setResources(resources);

        context.addApplicationListener(AppStartupListener.class.getName());
        context.setSessionCookieName("SHORTLET_SESSION");
        context.setSessionCookiePath("/");
        context.setUseHttpOnly(true);

        // Using "/" as the mapping for the landing servlet ensures it handles the root URL correctly
        addServlet(context, "landing", new LandingServlet(), "/");
        context.addServletMappingDecoded("/home", "landing");
        context.addServletMappingDecoded("/index", "landing");
        addServlet(context, "search", new SearchServlet(), "/search/*");
        addServlet(context, "login", new LoginServlet(), "/login");
        addServlet(context, "signup", new SignupServlet(), "/signup");
        addServlet(context, "googleCallback", new GoogleOAuthCallbackServlet(), "/oauth2/google/callback");
        addServlet(context, "userDashboard", new UserDashboardServlet(), "/dashboard");
        addServlet(context, "adminDashboard", new AdminDashboardServlet(), "/admin");
        context.addServletMappingDecoded("/admin/*", "adminDashboard");
        addServlet(context, "exportExcel", new ExportExcelServlet(), "/admin/export.xlsx");
        addServlet(context, "logout", new LogoutServlet(), "/logout");
        addServlet(context, "error", new ErrorServlet(), "/error");
        addServlet(context, "propertyMarkers", new PropertyMarkersServlet(), "/api/properties/markers");
        addServlet(context, "searchSuggestion", new SearchSuggestionServlet(), "/api/search/suggest");
        addServlet(context, "wishlistToggle", new WishlistToggleServlet(), "/wishlist/toggle");
        addServlet(context, "jijiInquiry", new JijiInquiryServlet(), "/inquire");
        addServlet(context, "forgotPassword", new ForgotPasswordServlet(), "/forgot-password");

        context.addFilterDef(AuthenticationFilter.definition());
        context.addFilterMap(AuthenticationFilter.map("/dashboard*"));
        context.addFilterMap(AuthenticationFilter.map("/wishlist/*"));
        context.addFilterMap(AuthenticationFilter.map("/inquire*"));
        context.addFilterMap(AuthenticationFilter.map("/admin*"));
        context.addFilterMap(AuthenticationFilter.map("/admin/*"));

        context.addErrorPage(errorPage(404, "/error?code=404"));
        context.addErrorPage(errorPage(403, "/error?code=403"));
        context.addErrorPage(errorPage(500, "/error?code=500"));

        tomcat.start();
        System.out.println("Shortlet running at http://localhost:" + port);
        tomcat.getServer().await();
    }

    private static void addServlet(Context context, String name, jakarta.servlet.Servlet servlet, String mapping) {
        Wrapper wrapper = Tomcat.addServlet(context, name, servlet);
        wrapper.setLoadOnStartup(1);
        context.addServletMappingDecoded(mapping, name);
    }

    private static org.apache.tomcat.util.descriptor.web.ErrorPage errorPage(int code, String location) {
        org.apache.tomcat.util.descriptor.web.ErrorPage errorPage = new org.apache.tomcat.util.descriptor.web.ErrorPage();
        errorPage.setErrorCode(code);
        errorPage.setLocation(location);
        return errorPage;
    }

    private static Path extractWebapp() throws IOException, URISyntaxException {
        Path target = Files.createTempDirectory("shortlet-webapp-");
        CodeSource codeSource = TomcatEmbeddedServer.class.getProtectionDomain().getCodeSource();
        if (codeSource == null) {
            return target;
        }
        URI location = codeSource.getLocation().toURI();
        Path source = Path.of(location);
        if (Files.isDirectory(source)) {
            Path sourceWebapp = source.resolve("webapp");
            if (Files.exists(sourceWebapp)) {
                copyDirectory(sourceWebapp, target);
            }
        } else {
            try (JarFile jarFile = new JarFile(source.toFile())) {
                var entries = jarFile.entries();
                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    if (!entry.getName().startsWith("webapp/") || entry.isDirectory()) {
                        continue;
                    }
                    Path output = target.resolve(entry.getName().substring("webapp/".length()));
                    Files.createDirectories(output.getParent());
                    try (var input = jarFile.getInputStream(entry)) {
                        Files.copy(input, output);
                    }
                }
            }
        }
        return target;
    }

    private static void copyDirectory(Path source, Path target) throws IOException {
        try (var paths = Files.walk(source)) {
            for (Path path : paths.toList()) {
                Path destination = target.resolve(source.relativize(path).toString());
                if (Files.isDirectory(path)) {
                    Files.createDirectories(destination);
                } else {
                    Files.createDirectories(destination.getParent());
                    Files.copy(path, destination);
                }
            }
        }
    }
}

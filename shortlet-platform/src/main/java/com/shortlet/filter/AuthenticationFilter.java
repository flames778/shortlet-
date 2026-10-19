package com.shortlet.filter;

import com.shortlet.model.User;
import com.shortlet.util.ServletUtil;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;

import java.io.IOException;

public class AuthenticationFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        User user = ServletUtil.currentUser(request);

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login?next=" + request.getRequestURI());
            return;
        }
        if (request.getRequestURI().startsWith(request.getContextPath() + "/admin") && !"ADMIN".equals(user.getRole())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        chain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {
    }

    public static FilterDef definition() {
        FilterDef filterDef = new FilterDef();
        filterDef.setFilterName("authenticationFilter");
        filterDef.setFilterClass(AuthenticationFilter.class.getName());
        return filterDef;
    }

    public static FilterMap map(String pattern) {
        FilterMap filterMap = new FilterMap();
        filterMap.setFilterName("authenticationFilter");
        filterMap.addURLPattern(pattern);
        return filterMap;
    }
}

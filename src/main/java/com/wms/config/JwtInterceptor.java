package com.wms.config;

import com.wms.common.JwtUtils;
import com.wms.pojo.User;
import com.wms.security.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Value("${wms.auth.disabled:false}")
    private boolean authDisabled;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // Allow CORS preflight
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String uri = request.getRequestURI();
        // Login endpoint does not require token
        if (uri.contains("/login")) {
            return true;
        }

        // For local debugging: set wms.auth.disabled=true
        if (authDisabled) {
            return true;
        }

        String token = request.getHeader("Authorization");
        if (token == null || token.isEmpty()) {
            response.setStatus(401);
            response.getWriter().write("{\"code\":401,\"msg\":\"missing token\",\"data\":null}");
            return false;
        }

        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        if (!JwtUtils.verifyToken(token)) {
            response.setStatus(401);
            response.getWriter().write("{\"code\":401,\"msg\":\"invalid or expired token\",\"data\":null}");
            return false;
        }

        User user = JwtUtils.getUserFromToken(token);
        request.setAttribute("currentUser", user);
        UserContext.set(user);

        // RBAC: only ADMIN can modify (POST/PUT/DELETE)
        String method = request.getMethod();
        if ("POST".equalsIgnoreCase(method) || "PUT".equalsIgnoreCase(method) || "DELETE".equalsIgnoreCase(method)) {
            if (user == null || user.getRole() == null || !"ADMIN".equalsIgnoreCase(user.getRole())) {
                response.setStatus(403);
                response.getWriter().write("{\"code\":403,\"msg\":\"forbidden\",\"data\":null}");
                return false;
            }
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserContext.clear();
    }
}

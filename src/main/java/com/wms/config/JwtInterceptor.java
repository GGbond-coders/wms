package com.wms.config;

import com.wms.common.JwtUtils;
import com.wms.pojo.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 允许跨域请求的预检请求通过
        if ("OPTIONS".equals(request.getMethod())) {
            return true;
        }

        // 登录接口不需要验证token
        String uri = request.getRequestURI();
        if (uri.contains("/login")) {
            return true;
        }

        // 从请求头获取token
        String token = request.getHeader("Authorization");
        if (token == null || token.isEmpty()) {
            response.setStatus(401);
            response.getWriter().write("{\"code\":401,\"msg\":\"未提供token\",\"data\":null}");
            return false;
        }

        // 去掉Bearer前缀（如果有）
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        // 验证token
        if (!JwtUtils.verifyToken(token)) {
            response.setStatus(401);
            response.getWriter().write("{\"code\":401,\"msg\":\"token无效或已过期\",\"data\":null}");
            return false;
        }

        // 将用户信息存入request中，方便后续使用
        User user = JwtUtils.getUserFromToken(token);
        request.setAttribute("currentUser", user);

        return true;
    }
}
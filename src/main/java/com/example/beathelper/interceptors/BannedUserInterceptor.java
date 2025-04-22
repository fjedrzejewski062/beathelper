package com.example.beathelper.interceptors;

import com.example.beathelper.entities.User;
import com.example.beathelper.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class BannedUserInterceptor implements HandlerInterceptor {
    private final UserService userService;

    public BannedUserInterceptor(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();

        if(uri.startsWith("/admin") || uri.startsWith("/banned") ||
                uri.startsWith("/css") || uri.startsWith("/img") || uri.startsWith("/js")){
            return true;
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())){
            String email = auth.getName();
            User user = userService.findByEmail(email).orElse(null);

            if(user != null && user.isBanned()){
                response.sendRedirect(request.getContextPath() + "/banned");
                return false;
            }
        }
        return true;
    }
}

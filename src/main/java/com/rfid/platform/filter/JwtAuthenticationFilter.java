package com.rfid.platform.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rfid.platform.common.AccountContext;
import com.rfid.platform.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String token = getTokenFromRequest(request);
        
        if (StringUtils.isNotBlank(token)) {
            try {
                String username = jwtUtil.getUsernameFromToken(token);
                Long userId = jwtUtil.getUserIdFromToken(token);
                
                if (StringUtils.isNotBlank(username) && !jwtUtil.isTokenExpired(token)) {
                    // 验证token是否在Redis中存在
                    String tokenKey = "token:" + token;
                    Object cachedUserId = redisTemplate.opsForValue().get(tokenKey);
                    
                    if (cachedUserId != null && userId.equals(Long.valueOf(cachedUserId.toString()))) {
                        // 设置用户上下文
                        AccountContext.setAccountId(userId);
                        
                        // 设置Spring Security上下文
                        UsernamePasswordAuthenticationToken authentication = 
                            new UsernamePasswordAuthenticationToken(
                                username, null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
                            );
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        
                        filterChain.doFilter(request, response);
                        return;
                    } else {
                        // Token在Redis中不存在或用户ID不匹配
                        sendAuthenticationError(response, "Token已失效或无效");
                        return;
                    }
                } else {
                    // Token过期或用户名为空
                    sendAuthenticationError(response, "Token已过期或无效");
                    return;
                }
            } catch (Exception e) {
                logger.error("JWT token validation failed", e);
                sendAuthenticationError(response, "Token验证失败");
                return;
            }
        }
        
        filterChain.doFilter(request, response);
    }
    
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.isNotBlank(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
    
    private void sendAuthenticationError(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("code", 401);
        errorResponse.put("message", message);
        errorResponse.put("success", false);
        errorResponse.put("timestamp", System.currentTimeMillis());
        
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonResponse = objectMapper.writeValueAsString(errorResponse);
        
        response.getWriter().write(jsonResponse);
        response.getWriter().flush();
    }
}
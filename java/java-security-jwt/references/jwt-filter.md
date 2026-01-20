# JWT 认证过滤器实现指南

## 完整实现

```java
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private final JwtTokenService jwtTokenService;
    private final UserDetailsService userDetailsService;
    
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        
        try {
            String token = extractTokenFromRequest(request);
            
            if (token != null && jwtTokenService.validateToken(token)) {
                String username = jwtTokenService.getUsernameFromToken(token);
                
                // 检查 SecurityContext 中是否已有认证信息
                if (SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    
                    // 从 Token 中获取权限
                    List<GrantedAuthority> authorities = jwtTokenService.getAuthoritiesFromToken(token);
                    
                    UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            authorities
                        );
                    
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (Exception e) {
            log.error("Cannot set user authentication: {}", e.getMessage());
            // 不抛出异常，继续过滤器链，让后续的 Security 配置处理未认证请求
        }
        
        filterChain.doFilter(request, response);
    }
    
    /**
     * 从请求中提取 Token
     */
    private String extractTokenFromRequest(HttpServletRequest request) {
        // 1. 从 Authorization Header 中提取
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        
        // 2. 从 Cookie 中提取（可选）
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("accessToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        
        return null;
    }
}
```

## 异常处理增强版

```java
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private final JwtTokenService jwtTokenService;
    private final UserDetailsService userDetailsService;
    private final ObjectMapper objectMapper;
    
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        
        try {
            String token = extractTokenFromRequest(request);
            
            if (token != null) {
                if (!jwtTokenService.validateToken(token)) {
                    handleAuthenticationError(response, "Invalid token");
                    return;
                }
                
                if (jwtTokenService.isTokenExpired(token)) {
                    handleAuthenticationError(response, "Token expired");
                    return;
                }
                
                String username = jwtTokenService.getUsernameFromToken(token);
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                
                List<GrantedAuthority> authorities = jwtTokenService.getAuthoritiesFromToken(token);
                
                UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        authorities
                    );
                
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (UsernameNotFoundException e) {
            handleAuthenticationError(response, "User not found");
            return;
        } catch (Exception e) {
            log.error("JWT authentication failed", e);
            handleAuthenticationError(response, "Authentication failed");
            return;
        }
        
        filterChain.doFilter(request, response);
    }
    
    private void handleAuthenticationError(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        
        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.UNAUTHORIZED.value(),
            "Unauthorized",
            message,
            Instant.now()
        );
        
        objectMapper.writeValue(response.getWriter(), errorResponse);
    }
    
    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
```

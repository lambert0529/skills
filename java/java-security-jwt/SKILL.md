---
name: java-security-jwt
description: 为 Spring Boot 应用实现 JWT 认证和授权，包括 Token 生成、验证、刷新机制、基于角色的访问控制（RBAC）和权限控制。使用场景：(1) 实现无状态 REST API 认证，(2) 构建 SPA 后端，(3) 微服务间认证，(4) 实现角色和权限控制，(5) 集成 OAuth2 提供商
---

# Java JWT 安全认证规范

## 快速开始

实现 JWT 认证的基本步骤：

1. 添加依赖（Spring Security + JJWT）
2. 配置 JWT 工具类（生成、验证 Token）
3. 实现 JWT 认证过滤器
4. 配置 Spring Security（SecurityFilterChain）
5. 实现用户认证服务（UserDetailsService）
6. 创建认证 Controller（登录、注册、刷新 Token）

## 核心组件

### 1. JWT 工具类
- Token 生成：包含用户信息、角色、过期时间
- Token 验证：验证签名、过期时间
- Token 解析：提取用户信息

### 2. 认证过滤器
- 拦截请求，提取 Token
- 验证 Token 有效性
- 设置 SecurityContext

### 3. Spring Security 配置
- 配置 SecurityFilterChain
- 设置认证和授权规则
- 配置 CORS、CSRF

### 4. 用户认证服务
- 实现 UserDetailsService
- 密码加密（BCrypt）
- 用户信息加载

## 依赖配置

### Maven 依赖

```xml
<dependencies>
    <!-- Spring Security -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>
    
    <!-- JWT Library -->
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-api</artifactId>
        <version>0.12.6</version>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-impl</artifactId>
        <version>0.12.6</version>
        <scope>runtime</scope>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-jackson</artifactId>
        <version>0.12.6</version>
        <scope>runtime</scope>
    </dependency>
</dependencies>
```

## 实现模式

### JWT 工具类

```java
@Service
@RequiredArgsConstructor
public class JwtTokenService {
    
    private final JwtProperties jwtProperties;
    private final SecretKey secretKey;
    
    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
            .subject(userDetails.getUsername())
            .claim("authorities", getAuthorities(userDetails))
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + jwtProperties.getExpiration()))
            .signWith(secretKey)
            .compact();
    }
    
    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
    
    public String getUsernameFromToken(String token) {
        return Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .getPayload()
            .getSubject();
    }
}
```

### JWT 认证过滤器

```java
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private final JwtTokenService jwtTokenService;
    private final UserDetailsService userDetailsService;
    
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        
        String token = extractTokenFromRequest(request);
        
        if (token != null && jwtTokenService.validateToken(token)) {
            String username = jwtTokenService.getUsernameFromToken(token);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            
            UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
                );
            
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        
        filterChain.doFilter(request, response);
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

### Spring Security 配置

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/v1/auth/**").permitAll()
                .requestMatchers("/api/v1/public/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/products/**").hasAnyRole("USER", "ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/v1/products/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authenticationProvider(authenticationProvider)
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
}
```

### 认证 Controller

```java
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthService authService;
    
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        AuthResponse response = authService.refreshToken(request.refreshToken());
        return ResponseEntity.ok(response);
    }
}
```

## 最佳实践

### 1. Token 过期时间
- Access Token：15-60 分钟（短期）
- Refresh Token：7-30 天（长期）

### 2. Token 存储
- Access Token：存储在内存或 localStorage（前端）
- Refresh Token：存储在 HttpOnly Cookie 或数据库（更安全）

### 3. 密码加密
- 使用 BCrypt 加密密码
- 不要存储明文密码

### 4. 安全配置
- 使用 HTTPS 传输 Token
- 配置 CORS 策略
- 禁用 CSRF（无状态 API）

### 5. 异常处理
- Token 过期：返回 401 Unauthorized
- Token 无效：返回 401 Unauthorized
- 权限不足：返回 403 Forbidden

## 约束和警告

### 1. 永远不要在前端存储敏感信息
Token 中不要包含密码等敏感信息。

### 2. 使用 HTTPS
在生产环境中必须使用 HTTPS 传输 Token。

### 3. 实现 Token 刷新机制
Access Token 过期后，使用 Refresh Token 刷新，而不是重新登录。

### 4. 实现 Token 黑名单
对于需要立即失效的场景（如登出），实现 Token 黑名单机制。

### 5. 限制 Token 长度
避免在 Token 中存储过多信息，保持 Token 简洁。

## 详细参考

- **JWT 工具类实现**：见 [references/jwt-token-service.md](references/jwt-token-service.md)
- **认证过滤器**：见 [references/jwt-filter.md](references/jwt-filter.md)
- **Spring Security 配置**：见 [references/security-config.md](references/security-config.md)
- **角色和权限控制**：见 [references/rbac-patterns.md](references/rbac-patterns.md)
- **OAuth2 集成**：见 [references/oauth2-integration.md](references/oauth2-integration.md)
- **代码模板**：见 `assets/` 目录

## 注意事项

1. **Token 安全**：Token 签名密钥要足够复杂，不要硬编码
2. **过期时间**：合理设置 Token 过期时间，平衡安全性和用户体验
3. **刷新机制**：实现 Refresh Token 机制，避免频繁登录
4. **日志记录**：记录认证失败和异常情况，便于排查问题
5. **性能优化**：Token 验证要高效，避免影响 API 性能

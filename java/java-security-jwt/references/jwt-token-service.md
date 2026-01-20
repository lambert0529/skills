# JWT Token Service 实现指南

## 完整实现

```java
@Service
@RequiredArgsConstructor
@Slf4j
public class JwtTokenService {
    
    private final JwtProperties jwtProperties;
    private final SecretKey secretKey;
    
    /**
     * 生成 Access Token
     */
    public String generateAccessToken(UserDetails userDetails) {
        Date expiryDate = new Date(System.currentTimeMillis() + jwtProperties.getAccessTokenExpiration());
        
        return Jwts.builder()
            .subject(userDetails.getUsername())
            .claim("authorities", getAuthorities(userDetails))
            .claim("userId", getUserId(userDetails))
            .issuedAt(new Date())
            .expiration(expiryDate)
            .signWith(secretKey)
            .compact();
    }
    
    /**
     * 生成 Refresh Token
     */
    public String generateRefreshToken(UserDetails userDetails) {
        Date expiryDate = new Date(System.currentTimeMillis() + jwtProperties.getRefreshTokenExpiration());
        
        return Jwts.builder()
            .subject(userDetails.getUsername())
            .claim("type", "refresh")
            .issuedAt(new Date())
            .expiration(expiryDate)
            .signWith(secretKey)
            .compact();
    }
    
    /**
     * 验证 Token
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * 从 Token 中提取用户名
     */
    public String getUsernameFromToken(String token) {
        return Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .getPayload()
            .getSubject();
    }
    
    /**
     * 从 Token 中提取权限
     */
    @SuppressWarnings("unchecked")
    public List<GrantedAuthority> getAuthoritiesFromToken(String token) {
        Claims claims = Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .getPayload();
        
        List<String> authorities = (List<String>) claims.get("authorities");
        return authorities.stream()
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toList());
    }
    
    /**
     * 检查 Token 是否过期
     */
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
            
            return claims.getExpiration().before(new Date());
        } catch (JwtException e) {
            return true;
        }
    }
    
    private List<String> getAuthorities(UserDetails userDetails) {
        return userDetails.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toList());
    }
    
    private Long getUserId(UserDetails userDetails) {
        // 如果 UserDetails 是自定义实现，可以获取 userId
        if (userDetails instanceof CustomUserDetails) {
            return ((CustomUserDetails) userDetails).getUserId();
        }
        return null;
    }
}
```

## JWT 配置属性

```java
@ConfigurationProperties(prefix = "jwt")
@Data
public class JwtProperties {
    private String secret;
    private Long accessTokenExpiration = 3600000L; // 1小时
    private Long refreshTokenExpiration = 604800000L; // 7天
}
```

## SecretKey 配置

```java
@Configuration
public class JwtConfig {
    
    @Value("${jwt.secret}")
    private String secret;
    
    @Bean
    public SecretKey secretKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
}
```

## application.yml 配置

```yaml
jwt:
  secret: your-secret-key-must-be-at-least-256-bits-long
  access-token-expiration: 3600000  # 1小时（毫秒）
  refresh-token-expiration: 604800000  # 7天（毫秒）
```

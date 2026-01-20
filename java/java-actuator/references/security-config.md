# Actuator 安全管理配置

## Spring Security 配置

```java
@Configuration
@EnableWebSecurity
public class ActuatorSecurityConfig {
    
    @Bean
    public SecurityFilterChain actuatorSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher("/actuator/**")
            .authorizeHttpRequests(auth -> auth
                // 健康检查端点公开
                .requestMatchers("/actuator/health/**").permitAll()
                // 其他端点需要 ACTUATOR 角色
                .requestMatchers(EndpointRequest.toAnyEndpoint()).hasRole("ACTUATOR")
                .anyRequest().denyAll()
            )
            .httpBasic();
        
        return http.build();
    }
}
```

## 独立管理端口

```yaml
management:
  server:
    port: 9090
    address: 127.0.0.1  # 只允许本地访问
```

## 使用独立端口的 Security 配置

```java
@Configuration
@Order(1)
@EnableWebSecurity
public class ManagementSecurityConfig {
    
    @Bean
    public SecurityFilterChain managementSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher("/actuator/**")
            .authorizeHttpRequests(auth -> auth
                .anyRequest().hasRole("ACTUATOR")
            )
            .httpBasic();
        
        return http.build();
    }
}
```

## 用户配置

```yaml
spring:
  security:
    user:
      name: actuator
      password: ${ACTUATOR_PASSWORD:changeme}
      roles: ACTUATOR
```

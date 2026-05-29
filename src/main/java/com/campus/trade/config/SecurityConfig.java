package com.campus.trade.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security 配置类
 * 项目使用自定义 JWT 拦截器进行认证，此处仅禁用默认的表单登录和 CSRF，
 * 并提供 BCryptPasswordEncoder Bean 供全局使用。
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 禁用 CSRF（前后端分离项目使用 JWT，不需要 CSRF 保护）
            .csrf(csrf -> csrf.disable())
            // 禁用 Session（使用 JWT 无状态认证）
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // 禁用默认的表单登录和 HTTP Basic 认证
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable())
            // 放行所有请求（认证由自定义 JwtInterceptor 处理）
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

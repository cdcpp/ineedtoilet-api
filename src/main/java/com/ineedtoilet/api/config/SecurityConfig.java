package com.ineedtoilet.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CorsConfigurationSource corsConfigurationSource;

    public SecurityConfig(CorsConfigurationSource corsConfigurationSource) {
        this.corsConfigurationSource = corsConfigurationSource;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. CORS 설정을 Spring Security에 통합
                .cors(cors -> cors.configurationSource(corsConfigurationSource))

                // 2. CSRF 보호 비활성화 (Stateless API 서버의 경우 일반적으로 비활성화)
                .csrf(csrf -> csrf.disable())

                // 3. 세션 관리 정책을 STATELESS로 설정 (JWT 등 토큰 기반 인증 시 사용)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 4. HTTP 요청에 대한 접근 권한 설정
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/**").permitAll() // 모든 요청을 허용 (추후 필요에 따라 경로별 권한 설정)
                        .anyRequest().authenticated()
                );

        return http.build();
    }
}

package kr.co.solpick.auth.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JWTUtil jwtUtil;

    public SecurityConfig(JWTUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/login").permitAll() // 로그인은 인증 없이 접근 가능
                                .requestMatchers("/solpick/refrigerator/**").permitAll() // 식재료 관련
                                .requestMatchers("/solpick/noti/**").permitAll() // 알림 관련
                                .requestMatchers("/solpick/api/points").permitAll()
                                .requestMatchers("/solpick/api/points/update").permitAll()
                                .requestMatchers("/solpick/api/payment/verify-card").permitAll()
                                .requestMatchers("/solpick/api/card/**").permitAll()
                                .requestMatchers("/solpick/api/card-design/**").permitAll()
                                .requestMatchers("/solpick/api/card-design/card-info/**").permitAll()
                                .requestMatchers("/solpick/api/game/**").permitAll()
                                .requestMatchers("/solpick/api/game/recipe/**").permitAll()
                                .requestMatchers("/api/refrigerator/ingredients/**").permitAll()
                                .requestMatchers("/api/refrigerator/recommend/**").permitAll()
                                .requestMatchers("/api/allergy-management/**").permitAll()
                                .requestMatchers("/api/user-allergy/**").permitAll()
                                .requestMatchers("/api/meal-plan/**").permitAll()
//                        .requestMatchers("/member/**").permitAll() //순서가 중요 아래 코드보다 위에 있어야함
                        .anyRequest().authenticated() // 다른 모든 요청은 인증 필요
                );

        // 토큰 체크 필터 추가
        TokenCheckFilter tokenCheckFilter = new TokenCheckFilter(jwtUtil);
        http.addFilterBefore(tokenCheckFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
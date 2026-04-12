package com.example.student_management.config;

import com.example.student_management.security.CustomUserDetailsService;
import com.example.student_management.security.JwtAuthenticationFilter;
import com.example.student_management.security.RestAccessDeniedHandler;
import com.example.student_management.security.RestAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomUserDetailsService customUserDetailsService;
    private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;
    private final RestAccessDeniedHandler restAccessDeniedHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(restAuthenticationEntryPoint)
                        .accessDeniedHandler(restAccessDeniedHandler)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/login").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/files/**").permitAll()
                        .requestMatchers(RegexRequestMatcher.regexMatcher(HttpMethod.GET, "/students/\\d+")).hasAnyRole("ADMIN", "STUDENT")
                        .requestMatchers(RegexRequestMatcher.regexMatcher(HttpMethod.GET, "/students/summary/\\d+")).hasAnyRole("ADMIN", "STUDENT")
                        .requestMatchers(RegexRequestMatcher.regexMatcher(HttpMethod.POST, "/students/\\d+/documents")).hasAnyRole("ADMIN", "STUDENT")
                        .requestMatchers(RegexRequestMatcher.regexMatcher(HttpMethod.POST, "/students/\\d+/profile-image")).hasAnyRole("ADMIN", "STUDENT")
                        .requestMatchers(RegexRequestMatcher.regexMatcher(HttpMethod.POST, "/students/\\d+/request-id-card")).hasAnyRole("ADMIN", "STUDENT")
                        .requestMatchers(RegexRequestMatcher.regexMatcher(HttpMethod.POST, "/students/\\d+/id-card/request")).hasAnyRole("ADMIN", "STUDENT")
                        .requestMatchers(RegexRequestMatcher.regexMatcher(HttpMethod.GET, "/students/\\d+/id-card-status")).hasAnyRole("ADMIN", "STUDENT")
                        .requestMatchers(RegexRequestMatcher.regexMatcher(HttpMethod.GET, "/students/\\d+/id-card/status")).hasAnyRole("ADMIN", "STUDENT")
                        .requestMatchers(RegexRequestMatcher.regexMatcher(HttpMethod.PUT, "/students/\\d+/id-card/approve")).hasAnyRole("ADMIN")
                        .requestMatchers(RegexRequestMatcher.regexMatcher(HttpMethod.PUT, "/students/\\d+/id-card/reject")).hasAnyRole("ADMIN")
                        .anyRequest().hasRole("ADMIN")
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(customUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}



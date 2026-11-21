package com.driveease.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.driveease.security.CustomUserDetailsService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(userDetailsService);

        provider.setPasswordEncoder(passwordEncoder());

        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http
            .csrf(csrf -> csrf.disable())

            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
            )

            .authenticationProvider(authenticationProvider())

            .authorizeHttpRequests(auth -> auth

                // Protect current user endpoint
                .requestMatchers(HttpMethod.GET, "/api/auth/me")
                .authenticated()

                // Public pages and resources
                .requestMatchers(
                    "/",
                    "/index.html",
                    "/login.html",
                    "/register.html",
                    "/browse-vehicles.html",
                    "/admin-reports.html",
                    "/favicon.ico",
                    "/api/auth/**",
                    "/swagger-ui/**",
                    "/v3/api-docs/**",
                    "/css/**",
                    "/js/**",
                    "/images/**"
                ).permitAll()
                
                // View all reviews - Admin only
                .requestMatchers(HttpMethod.GET, "/api/reviews")
                .hasRole("ADMIN")

                // Vehicle permissions
                .requestMatchers(HttpMethod.GET, "/api/vehicles/**")
                .permitAll()

                .requestMatchers(HttpMethod.POST, "/api/vehicles/**")
                .hasRole("ADMIN")

                .requestMatchers(HttpMethod.PUT, "/api/vehicles/**")
                .hasRole("ADMIN")

                .requestMatchers(HttpMethod.DELETE, "/api/vehicles/**")
                .hasRole("ADMIN")

                // Booking permissions
                .requestMatchers(HttpMethod.GET, "/api/bookings")
                .hasRole("ADMIN")

                .requestMatchers(HttpMethod.GET, "/api/bookings/vehicle/**")
                .hasRole("ADMIN")

                .requestMatchers(HttpMethod.PUT, "/api/bookings/**")
                .hasRole("ADMIN")

                .requestMatchers(HttpMethod.POST, "/api/bookings/**")
                .hasAnyRole("ADMIN", "CUSTOMER")

                .requestMatchers(HttpMethod.GET, "/api/bookings/**")
                .hasAnyRole("ADMIN", "CUSTOMER")

                .requestMatchers(HttpMethod.DELETE, "/api/bookings/**")
                .hasAnyRole("ADMIN", "CUSTOMER")

                // Payment permissions
                .requestMatchers(HttpMethod.POST, "/api/payments/**")
                .hasAnyRole("ADMIN", "CUSTOMER")

                .requestMatchers(HttpMethod.GET, "/api/payments")
                .hasRole("ADMIN")

                .requestMatchers(HttpMethod.GET, "/api/payments/**")
                .hasAnyRole("ADMIN", "CUSTOMER")

                // Admin customer management
                .requestMatchers(HttpMethod.GET, "/api/admin/customers/**")
                .hasRole("ADMIN")

                .requestMatchers(HttpMethod.PUT, "/api/admin/customers/**")
                .hasRole("ADMIN")

                // Review permissions
                .requestMatchers(HttpMethod.GET, "/api/reviews/vehicle/**")
                .permitAll()

                .requestMatchers(HttpMethod.GET, "/api/reviews/**")
                .hasAnyRole("ADMIN", "CUSTOMER")

                .requestMatchers(HttpMethod.POST, "/api/reviews/**")
                .hasAnyRole("ADMIN", "CUSTOMER")

                .requestMatchers(HttpMethod.DELETE, "/api/reviews/**")
                .hasAnyRole("ADMIN", "CUSTOMER")

                .requestMatchers("/api/admin/reports/**").hasRole("ADMIN")
                
                // All other endpoints require authentication
                .anyRequest().authenticated()
            );

        return http.build();
    }
}

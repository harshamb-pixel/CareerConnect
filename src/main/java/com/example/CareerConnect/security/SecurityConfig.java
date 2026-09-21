package com.example.CareerConnect.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /*
     * Spring Security auto-detects these two beans and wires them into a
     * DaoAuthenticationProvider internally — no manual provider setup needed.
     *
     * CustomUserDetailsService is annotated @Service so it is already a bean
     * that implements UserDetailsService. Spring Security picks it up automatically.
     */

    private final CustomOAuth2UserService customOAuth2UserService;

    public SecurityConfig(CustomOAuth2UserService customOAuth2UserService) {
        this.customOAuth2UserService = customOAuth2UserService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())

            .authorizeHttpRequests(auth -> auth

                .requestMatchers(
                    "/",
                    "/register",
                    "/login",
                    "/forgot-password",
                    "/reset-password",
                    "/verify-otp",
                    "/verify-reset-otp",
                    "/resend-reset-otp",
                    "/no-role",
                    "/css/**",
                    "/js/**",
                    "/images/**",
                    "/uploads/**"
                ).permitAll()

                // Dashboard — includes the read-only Demo Admin
                .requestMatchers("/dashboard")
                    .hasAnyRole(
                        "STUDENT",
                        "EMPLOYER",
                        "ADMIN",
                        "DEMO_ADMIN"
                    )

                // Job listing pages
                .requestMatchers("/jobs", "/jobs/**")
                    .hasAnyRole(
                        "STUDENT",
                        "EMPLOYER",
                        "ADMIN",
                        "DEMO_ADMIN"
                    )

                // Employer pages
                .requestMatchers("/employer/**")
                    .hasRole("EMPLOYER")

                // Application APIs
                .requestMatchers("/api/applications/**")
                    .hasAnyRole("EMPLOYER", "ADMIN")

                // Read-only Demo Admin pages
                .requestMatchers("/demo-admin/**")
                    .hasRole("DEMO_ADMIN")

                // Real Admin pages — REAL ADMIN ONLY
                .requestMatchers("/admin/**")
                    .hasRole("ADMIN")

                // Student pages
                .requestMatchers("/student/**")
                    .hasRole("STUDENT")

                // Everything else requires authentication
                .anyRequest().authenticated()
            )

            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/dashboard", true)
                .permitAll()
            )

            .oauth2Login(oauth2 -> oauth2
                .loginPage("/login")
                .defaultSuccessUrl("/dashboard", true)
                .userInfoEndpoint(userInfo -> userInfo
                    .userService(customOAuth2UserService)
                )
            )

            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            );

        return http.build();
    }
}
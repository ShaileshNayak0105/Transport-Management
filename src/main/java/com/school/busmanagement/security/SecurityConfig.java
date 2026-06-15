package com.school.busmanagement.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Static assets and the login page must stay public so users can reach
                // the sign-in screen before authentication.
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/css/**", "/js/**", "/images/**").permitAll()
                        // Only ADMIN users can access admin management pages.
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        // Only PARENT users can access parent-facing pages.
                        .requestMatchers("/parent/**").hasRole("PARENT")
                        // Any other endpoint requires a logged-in session.
                        .anyRequest().authenticated()
                )
                // Form login fits this project because Thymeleaf pages are rendered
                // on the server and authentication is session-based, not token-based.
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .defaultSuccessUrl("/dashboard", true)
                        .failureUrl("/login?error")
                        .permitAll()
                )
                // Logout clears the session and cookie so the browser can no longer
                // use the previous authenticated state.
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )
                // Session settings protect against session fixation and limit each
                // account to one active login at a time.
                .sessionManagement(session -> session
                        .sessionFixation(sessionFixation -> sessionFixation.changeSessionId())
                        .maximumSessions(1)
                )
                .authenticationProvider(authenticationProvider());

        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        // Delegate user lookup to our custom service so login uses database users.
        provider.setUserDetailsService(customUserDetailsService);
        // BCrypt compares the raw login password against the stored hash safely.
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // BCrypt is a one-way password hash, which means stored passwords cannot
        // be reversed back into plain text if the database is exposed.
        return new BCryptPasswordEncoder();
    }
}

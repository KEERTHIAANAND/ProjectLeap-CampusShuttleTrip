package com.campustracker.config;  // replace with your package

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable() // disable CSRF for testing
            .authorizeHttpRequests()
            .anyRequest().permitAll(); // allow all requests without auth
        return http.build();
    }
}

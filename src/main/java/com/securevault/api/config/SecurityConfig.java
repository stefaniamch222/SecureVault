package com.securevault.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Disabilitiamo CSRF per facilitare i test con le API REST
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().authenticated() // Richiede l'autenticazione per qualsiasi rotta
                )
                .httpBasic(Customizer.withDefaults()); // Abilita l'autenticazione Basic (user/password)

        return http.build();
    }

    // Creiamo un utente di test fisso: username "admin", password "password123"
    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails admin = User.builder()
                .username("admin")
                .password("{noop}password123") // {noop} significa password in chiaro senza cifratura per ora
                .roles("USER")
                .build();

        return new InMemoryUserDetailsManager(admin);
    }
}
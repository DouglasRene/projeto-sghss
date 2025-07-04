package com.sghss.production.config;

import com.sghss.production.security.JwtAuthFilter;
import com.sghss.production.security.UserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true) // Habilita @PreAuthorize
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final UserDetailsServiceImpl userDetailsService;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter, UserDetailsServiceImpl userDetailsService) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Desabilita CSRF para APIs REST
            .authorizeHttpRequests(authorize -> authorize
                // Permite acesso público ao endpoint de login e documentação
                .requestMatchers("/api/auth/login", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                // NOVO: Permite acesso ao registro APENAS para ADMINS
                .requestMatchers("/api/auth/register").hasRole("ADMIN") // <<< Adicione esta linha
                // NOVO: Permite acesso a /api/pacientes (POST) APENAS para ADMINS
                .requestMatchers("/api/pacientes").hasRole("ADMIN") // <<< Exemplo para RF01
                // NOVO: Permite acesso a /api/pacientes/{id} (GET) para MEDICO ou ADMIN
                .requestMatchers("/api/pacientes/{id}").hasAnyRole("MEDICO", "ADMIN") // <<< Exemplo para RF01
                // Todas as outras requisições exigem autenticação (usuário logado)
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Sem estado para JWT
            )
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class); // Adiciona o filtro JWT

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Criptografia de senha
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
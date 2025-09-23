package com.roarmot.roarmot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // IGNORAR CSRF para las rutas POST de registro.
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/enviar-codigo", "/validar-codigo", "/guardar-usuario") // AÑADIDO: /guardar-usuario
            )
            .authorizeHttpRequests(authorize -> authorize
                // 1. Recursos Estáticos (GET)
                .requestMatchers("/css/**", "/js/**", "/imagenes/**", "/favicon.ico").permitAll()
                
                // 2. Rutas del Flujo de Registro que son GET (mostrar la página)
                .requestMatchers("/", "/login", "/RegistroPaso1", "/RegistroPaso2", "/RegistroPaso3").permitAll()
                
                // 3. RUTAS DE PROCESAMIENTO DEL REGISTRO QUE SON POST
                .requestMatchers(HttpMethod.POST, "/enviar-codigo", "/validar-codigo", "/guardar-usuario").permitAll() // AÑADIDO: /guardar-usuario
                
                // 4. Cualquier otra solicitud DEBE estar autenticada
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login") 
                .loginProcessingUrl("/login") 
                .defaultSuccessUrl("/dashboard", true)
                .failureUrl("/login?error")
                .permitAll()
            )
            .logout(logout -> logout
                .permitAll()
            );
                
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
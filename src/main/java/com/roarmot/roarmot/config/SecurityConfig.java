package com.roarmot.roarmot.config;

import com.roarmot.roarmot.Services.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 1. CSRF: Ignorar para todos los POST que manejan el registro y el producto.
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/enviar-codigo", "/validar-codigo", "/guardar-usuario", "/panel/ProductProcess")
            )
            .authorizeHttpRequests(authorize -> authorize
                
                // 2. Rutas PÚBLICAS (GET)
                .requestMatchers("/", "/login", "/RegistroPaso1", "/RegistroPaso2", "/RegistroPaso3").permitAll()
                
                // 3. Rutas PÚBLICAS (POST)
                .requestMatchers(HttpMethod.POST, "/enviar-codigo", "/validar-codigo", "/guardar-usuario").permitAll() 
                
                // 4. [PROTECCIÓN VENDEDOR]
                // TODAS las rutas bajo /panel (GETs y POSTs) requieren el rol Vendedor.
                // Esta línea DEBE estar DESCOMENTADA para que funcione la seguridad.
                .requestMatchers("/panel/**").hasAuthority("ROLE_Vendedor")
                
                // 5. Cualquier otra solicitud DEBE estar autenticada
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login") 
                .loginProcessingUrl("/login") 
                .defaultSuccessUrl("/dashboard", true)
                .failureUrl("/login?error")
                .permitAll()
            )
            // *** INICIO: CONFIGURACIÓN DETALLADA DEL LOGOUT ***
            .logout(logout -> logout
                // URL que dispara el proceso de cierre de sesión (POST recomendado)
                .logoutUrl("/logout") 
                // URL a la que se redirige al usuario después de un cierre de sesión exitoso
                .logoutSuccessUrl("/login?logout") 
                // Invalida la sesión HTTP actual
                .invalidateHttpSession(true)
                // Borra la cookie de sesión si existe
                .deleteCookies("JSESSIONID")
                // Permite a todos acceder a la URL de logout
                .permitAll()
            );
            // *** FIN: CONFIGURACIÓN DETALLADA DEL LOGOUT ***
                
        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers(
            "/css/**", 
            "/js/**", 
            "/imagenes/**", 
            "/favicon.ico", 
            "/uploads/**",
            // ¡Ajuste! La ruta web para mostrar imágenes de productos
            "/products/**",

            "/favicon.icon"
        );
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

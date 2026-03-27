package com.sena.springpoo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuración central de Spring Security.
 * Define qué rutas son públicas, cuáles requieren autenticación y el formulario de login.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomAuthProvider customAuthProvider;

    public SecurityConfig(CustomAuthProvider customAuthProvider) {
        this.customAuthProvider = customAuthProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            // ── Registrar nuestro proveedor de autenticación personalizado ──
            .authenticationProvider(customAuthProvider)

            // ── Reglas de acceso por ruta ──
            .authorizeHttpRequests(auth -> auth
                // Rutas públicas
                .requestMatchers("/", "/login", "/js/**", "/css/**").permitAll()
                // Solo ADMIN puede acceder al panel CRUD y a la API
                .requestMatchers("/crud/**", "/usuariosAPI/**",
                                 "/guardarUsuario", "/eliminarUsuario/**").hasRole("ADMIN")
                // Cualquier otra ruta requiere estar autenticado
                .anyRequest().authenticated()
            )

            // ── Formulario de login personalizado ──
            .formLogin(form -> form
                .loginPage("/login")                    // Vista Thymeleaf en templates/login.html
                .usernameParameter("documento")         // El campo "username" de Spring = documento
                .passwordParameter("contrasena")        // El campo "password" de Spring = contrasena
                .defaultSuccessUrl("/crud", true)        // Redirige al CRUD tras login exitoso
                .failureUrl("/login?error")             // Redirige de vuelta con parámetro de error
                .permitAll()
            )

            // ── Logout ──
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            );

        return http.build();
    }

    /**
     * Bean de BCrypt disponible para toda la app (inyectable donde se necesite).
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

package com.devsv.cinemark_api.config;

import com.devsv.cinemark_api.security.CustomUserDetailsService;
import com.devsv.cinemark_api.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomUserDetailsService customUserDetailsService;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
            CustomUserDetailsService customUserDetailsService) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.customUserDetailsService = customUserDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(customUserDetailsService);

        provider.setPasswordEncoder(passwordEncoder);

        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(List.of("http://localhost:5173", "http://127.0.0.1:5173", "http://localhost:5174", "http://127.0.0.1:5174"));

        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));

        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept", "Origin", "X-Requested-With"));

        configuration.setExposedHeaders(List.of("Authorization"));

        configuration.setAllowCredentials(false);

        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
            DaoAuthenticationProvider authenticationProvider) throws Exception {

        http.cors(Customizer.withDefaults()).csrf(AbstractHttpConfigurer::disable)

                // DANIEL - SEGURIDAD: sesión stateless para usar JWT sin JSESSIONID
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authenticationProvider(authenticationProvider)

                // DANIEL - SEGURIDAD: configuración de autorización por roles (ADMIN/CLIENTE) y endpoints públicos
                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // DANIEL - SEGURIDAD: login y registro públicos sin autenticación
                        .requestMatchers("/api/auth/register", "/api/auth/login", "/error").permitAll()

                        .requestMatchers(HttpMethod.GET, "/uploads/**").permitAll()

                        // DANIEL - SEGURIDAD: consulta de catálogo público (películas, funciones, géneros, productos)
                        .requestMatchers(HttpMethod.GET, "/api/peliculas/**", "/api/funciones/**", "/api/generos/**", "/api/productos/**")
                        .permitAll()

                        .requestMatchers(HttpMethod.GET, "/api/promociones/vigentes").permitAll()

                        // DANIEL - SEGURIDAD: administración de catálogo solo ADMIN
                        .requestMatchers("/api/peliculas/**", "/api/funciones/**", "/api/generos/**", "/api/productos/**")
                        .hasRole("ADMIN")

                        // DANIEL - SEGURIDAD: disponibilidad de asientos para ADMIN y CLIENTE
                        .requestMatchers(HttpMethod.GET, "/api/asientos/funcion/*/disponibilidad")
                        .hasAnyRole("ADMIN", "CLIENTE")

                        // DANIEL - SEGURIDAD: compras - listar todas solo ADMIN, mis compras y crear para ambos roles
                        .requestMatchers(HttpMethod.GET, "/api/compras").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.GET, "/api/compras/mis-compras").hasAnyRole("ADMIN", "CLIENTE")

                        .requestMatchers(HttpMethod.GET, "/api/compras/*").hasAnyRole("ADMIN", "CLIENTE")

                        .requestMatchers(HttpMethod.POST, "/api/compras").hasAnyRole("ADMIN", "CLIENTE")

                        .requestMatchers(HttpMethod.PUT, "/api/compras/*/productos").hasAnyRole("ADMIN", "CLIENTE")

                        .requestMatchers("/api/compras/**").hasRole("ADMIN")

                        // DANIEL - SEGURIDAD: pagos - consultas y creación para ambos, administración solo ADMIN
                        .requestMatchers(HttpMethod.GET, "/api/pagos").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.GET, "/api/pagos/compra/*").hasAnyRole("ADMIN", "CLIENTE")

                        .requestMatchers(HttpMethod.GET, "/api/pagos/*").hasAnyRole("ADMIN", "CLIENTE")

                        .requestMatchers(HttpMethod.POST, "/api/pagos").hasAnyRole("ADMIN", "CLIENTE")

                        .requestMatchers("/api/pagos/**").hasRole("ADMIN")

                        // DANIEL - SEGURIDAD: boletos - reserva para ambos, gestión completa solo ADMIN
                        .requestMatchers(HttpMethod.GET, "/api/boletos/funcion/*").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.GET, "/api/boletos/compra/*").hasAnyRole("ADMIN", "CLIENTE")

                        .requestMatchers(HttpMethod.GET, "/api/boletos/*").hasAnyRole("ADMIN", "CLIENTE")

                        .requestMatchers(HttpMethod.POST, "/api/boletos/reservar").hasAnyRole("ADMIN", "CLIENTE")

                        .requestMatchers("/api/boletos/**").hasRole("ADMIN")

                        // DANIEL - SEGURIDAD: perfil de cliente solo rol CLIENTE
                        .requestMatchers(HttpMethod.GET, "/api/usuarios/perfil").hasRole("CLIENTE")

                        .requestMatchers(HttpMethod.PUT, "/api/usuarios/perfil").hasRole("CLIENTE")

                        .requestMatchers("/api/comprobantes/**", "/api/perfil/**", "/api/historial/**")
                        .hasAnyRole("ADMIN", "CLIENTE")

                        // DANIEL - SEGURIDAD: endpoints administrativos completos solo ADMIN
                        .requestMatchers("/api/roles/**", "/api/usuarios/**", "/api/inventarios/**", "/api/movimientos-inventario/**", "/api/categorias-productos/**", "/api/salas/**", "/api/asientos/**", "/api/dashboard/admin/**", "/api/reportes/**", "/api/incidencias/**", "/api/limpieza-salas/**", "/api/promociones/**")
                        .hasRole("ADMIN")

                        .anyRequest().authenticated())

                // DANIEL - SEGURIDAD: filtro JWT antes del filtro de usuario/contraseña estándar
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
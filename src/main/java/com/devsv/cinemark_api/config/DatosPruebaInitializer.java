package com.devsv.cinemark_api.config;

import com.devsv.cinemark_api.service.DatosPruebaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

// Componente de inicialización que delega en el servicio transaccional
@Slf4j
@Configuration
@Profile("dev")
@ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true")
@RequiredArgsConstructor
public class DatosPruebaInitializer {

    private final DatosPruebaService datosPruebaService;

    @Bean
    CommandLineRunner ejecutarSeederDatosPrueba() {
        return args -> {
            datosPruebaService.inicializarDatos();
        };
    }
}

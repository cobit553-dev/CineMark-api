package com.devsv.cinemark_api.config;

import com.devsv.cinemark_api.repository.GeneroRepository;
import com.devsv.cinemark_api.repository.PeliculaRepository;
import com.devsv.cinemark_api.service.DatosPruebaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("dev")
class DatosPruebaSeederEnabledTest {

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> "jdbc:h2:mem:enabledtestdb;DB_CLOSE_DELAY=-1;MODE=PostgreSQL");
        registry.add("spring.datasource.driver-class-name", () -> "org.h2.Driver");
        registry.add("spring.datasource.username", () -> "sa");
        registry.add("spring.datasource.password", () -> "");
        registry.add("app.seed.enabled", () -> "true");
    }

    @Autowired
    private GeneroRepository generoRepository;

    @Autowired
    private PeliculaRepository peliculaRepository;

    @Autowired
    private DatosPruebaService datosPruebaService;

    @Test
    void testSeederIsIdempotentAndRunsTwice() {
        long countGeneros1 = generoRepository.count();
        long countPeliculas1 = peliculaRepository.count();

        assertTrue(countGeneros1 >= 8, "Debe haber al menos 8 géneros");
        assertTrue(countPeliculas1 >= 20, "Debe haber al menos 20 películas");

        // Ejecutar el seeder una segunda vez explícitamente para comprobar idempotencia y ausencia de duplicados
        datosPruebaService.inicializarDatos();

        long countGeneros2 = generoRepository.count();
        long countPeliculas2 = peliculaRepository.count();

        assertEquals(countGeneros1, countGeneros2, "El seeder no debe generar duplicados de géneros al ejecutarse de nuevo");
        assertEquals(countPeliculas1, countPeliculas2, "El seeder no debe generar duplicados de películas al ejecutarse de nuevo");
    }
}

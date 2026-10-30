package com.devsv.cinemark_api.config;

import com.devsv.cinemark_api.repository.PeliculaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("dev")
class DatosPruebaSeederDisabledTest {

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> "jdbc:h2:mem:disabletestdb;DB_CLOSE_DELAY=-1;MODE=PostgreSQL");
        registry.add("spring.datasource.driver-class-name", () -> "org.h2.Driver");
        registry.add("spring.datasource.username", () -> "sa");
        registry.add("spring.datasource.password", () -> "");
        registry.add("app.seed.enabled", () -> "false");
    }

    @Autowired(required = false)
    private PeliculaRepository peliculaRepository;

    @Test
    void testSeederDisabledDoesNotInsertMovies() {
        // Con app.seed.enabled=false, el seeder no debe ejecutarse y no deben insertarse películas automáticamente
        long countPeliculas = peliculaRepository != null ? peliculaRepository.count() : 0;
        assertEquals(0, countPeliculas, "Con el seeder deshabilitado no deben insertarse películas automáticamente");
    }
}

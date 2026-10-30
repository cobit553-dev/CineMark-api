package com.devsv.cinemark_api.config;

import com.devsv.cinemark_api.repository.GeneroRepository;
import com.devsv.cinemark_api.repository.PeliculaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("dev")
@TestPropertySource(locations = "classpath:application-test.properties")
class DatosPruebaInitializerTest {

    @Autowired
    private GeneroRepository generoRepository;

    @Autowired
    private PeliculaRepository peliculaRepository;

    @Test
    void testSeederIsIdempotent() {
        long countGeneros = generoRepository.count();
        long countPeliculas = peliculaRepository.count();

        assertTrue(countGeneros >= 8, "Debe haber al menos 8 géneros");
        assertTrue(countPeliculas >= 20, "Debe haber al menos 20 películas");

        // Segunda verificación de idempotencia (al reejecutar o verificar estado)
        long countGeneros2 = generoRepository.count();
        long countPeliculas2 = peliculaRepository.count();

        assertTrue(countGeneros2 == countGeneros, "El seeder no debe duplicar géneros al reejecutarse");
        assertTrue(countPeliculas2 == countPeliculas, "El seeder no debe duplicar películas al reejecutarse");
    }
}

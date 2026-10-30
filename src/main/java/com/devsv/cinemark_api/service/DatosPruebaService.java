package com.devsv.cinemark_api.service;

import com.devsv.cinemark_api.entities.Genero;
import com.devsv.cinemark_api.entities.Pelicula;
import com.devsv.cinemark_api.entities.PeliculaGenero;
import com.devsv.cinemark_api.repository.GeneroRepository;
import com.devsv.cinemark_api.repository.PeliculaGeneroRepository;
import com.devsv.cinemark_api.repository.PeliculaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

// Servicio transaccional encargado de inicializar los datos de prueba de forma idempotente
@Slf4j
@Service
@RequiredArgsConstructor
public class DatosPruebaService {

    private final GeneroRepository generoRepository;
    private final PeliculaRepository peliculaRepository;
    private final PeliculaGeneroRepository peliculaGeneroRepository;

    @Transactional
    public void inicializarDatos() {
        log.info("=== Iniciando seeder de datos de prueba (Transaccional) ===");

        // 1. Sembrar 8 Géneros cinematográficos de manera idempotente
        List<String> nombresGeneros = List.of(
            "Acción", "Comedia", "Drama", "Ciencia Ficción", 
            "Terror", "Animación", "Romance", "Suspenso"
        );

        int generosInsertados = 0;
        int generosOmitidos = 0;

        for (String nombre : nombresGeneros) {
            if (!generoRepository.existsByNombreIgnoreCase(nombre)) {
                generoRepository.save(Genero.builder().nombre(nombre).build());
                generosInsertados++;
            } else {
                generosOmitidos++;
            }
        }
        log.info("Géneros procesados -> Insertados: {}, Omitidos (ya existían): {}", generosInsertados, generosOmitidos);

        // 2. Sembrar 20 Películas de demostración (imagen como null al no estar comprobada su correspondencia exacta)
        LocalDate hoy = LocalDate.now();

        record PeliculaSeedInfo(
            String titulo, String clasificacion, Integer duracion, String sinopsis,
            String trailerUrl, LocalDate estreno, LocalDate fin, Boolean estado, List<String> generos
        ) {}

        List<PeliculaSeedInfo> peliculasInfo = List.of(
            new PeliculaSeedInfo(
                "Inception: El Origen", "PG-13", 148,
                "Un ladrón que roba secretos corporativos a través de los sueños recibe la tarea inversa de implantar una idea.",
                "https://www.youtube.com/watch?v=YoHD9XEInc0",
                hoy.minusDays(15), hoy.plusDays(45), true, List.of("Ciencia Ficción", "Acción", "Suspenso")
            ),
            new PeliculaSeedInfo(
                "Interstellar", "PG-13", 169,
                "Un grupo de exploradores viaja a través de un agujero de gusano en el espacio en un intento por asegurar la supervivencia de la humanidad.",
                "https://www.youtube.com/watch?v=zSWdZVtXT7E",
                hoy.minusDays(30), hoy.plusDays(60), true, List.of("Ciencia Ficción", "Drama")
            ),
            new PeliculaSeedInfo(
                "The Dark Knight", "PG-13", 152,
                "Batman se enfrenta al Guasón, un criminal psicópata que siembra el caos y el pánico en Ciudad Gótica.",
                "https://www.youtube.com/watch?v=EXeTwQWrcwY",
                hoy.minusDays(60), hoy.plusDays(10), true, List.of("Acción", "Suspenso")
            ),
            new PeliculaSeedInfo(
                "El Padrino", "R", 175,
                "El envejecido patriarca de una dinastía del crimen organizado transfiere el control de su imperio clandestino a su reluctante hijo.",
                "https://www.youtube.com/watch?v=sY1S34973zA",
                hoy.minusDays(90), hoy.minusDays(10), false, List.of("Drama")
            ),
            new PeliculaSeedInfo(
                "Toy Story", "G", 81,
                "Los juguetes de Andy cobran vida cuando los humanos no están presentes, compitiendo por el afecto de su dueño.",
                "https://www.youtube.com/watch?v=KYz2wyBy3kc",
                hoy.minusDays(20), hoy.plusDays(70), true, List.of("Animación", "Comedia")
            ),
            new PeliculaSeedInfo(
                "El Conjunto del Terror", "R", 112,
                "Una familia experimenta extraños fenómenos paranormales en su nueva casa de campo.",
                "https://www.youtube.com/watch?v=k10ETkpYFcc",
                hoy.minusDays(5), hoy.plusDays(25), true, List.of("Terror", "Suspenso")
            ),
            new PeliculaSeedInfo(
                "Superbad", "R", 113,
                "Dos amigos de secundaria inseparables se enfrentan a la difícil tarea de conseguir alcohol para una fiesta antes de graduarse.",
                "https://www.youtube.com/watch?v=4eaTcoebX50",
                hoy.minusDays(100), hoy.minusDays(40), false, List.of("Comedia")
            ),
            new PeliculaSeedInfo(
                "Titanic", "PG-13", 195,
                "Un artista pobre y una joven rica se enamoran a bordo del ill-fated viaje inaugural del R.M.S. Titanic.",
                "https://www.youtube.com/watch?v=kVrqfYjkTdQ",
                hoy.minusDays(45), hoy.plusDays(15), true, List.of("Drama", "Romance")
            ),
            new PeliculaSeedInfo(
                "Matrix", "R", 136,
                "Un hacker descubre que la realidad cotidiana es una simulación creada por máquinas inteligentes.",
                "https://www.youtube.com/watch?v=vKQi3bBA1y8",
                hoy.minusDays(80), hoy.plusDays(30), true, List.of("Ciencia Ficción", "Acción")
            ),
            new PeliculaSeedInfo(
                "Forrest Gump", "PG-13", 142,
                "Las presidencias de Kennedy y Johnson, Vietnam y otros eventos históricos se despliegan a través de la perspectiva de un hombre de Alabama con bajo coeficiente intelectual.",
                "https://www.youtube.com/watch?v=XHjIqQBsPjk",
                hoy.minusDays(10), hoy.plusDays(50), true, List.of("Drama", "Comedia", "Romance")
            ),
            new PeliculaSeedInfo(
                "Gladiador", "R", 155,
                "Un ex general romano busca venganza contra el corrupto emperador que asesinó a su familia.",
                "https://www.youtube.com/watch?v=owK1qxDselE",
                hoy.minusDays(40), hoy.plusDays(20), true, List.of("Acción", "Drama")
            ),
            new PeliculaSeedInfo(
                "Jurassic Park", "PG-13", 127,
                "Científicos visitan un parque temático poblado por dinosaurios clonados, donde las criaturas escapan de su control.",
                "https://www.youtube.com/watch?v=lc0UehYemQA",
                hoy.minusDays(70), hoy.plusDays(5), true, List.of("Ciencia Ficción", "Suspenso", "Acción")
            ),
            new PeliculaSeedInfo(
                "La La Land", "PG-13", 128,
                "Un pianista de jazz y una aspirante a actriz se enamoran en Los Ángeles mientras persiguen sus sueños profesionales.",
                "https://www.youtube.com/watch?v=0pdqf4P9MB8",
                hoy.minusDays(150), hoy.minusDays(80), false, List.of("Romance", "Drama")
            ),
            new PeliculaSeedInfo(
                "El Rey León", "G", 88,
                "Un joven león príncipe huye de su reino tras la trágica muerte de su padre, debiendo reclamar su destino.",
                "https://www.youtube.com/watch?v=7TavVZMewpY",
                hoy.minusDays(12), hoy.plusDays(40), true, List.of("Animación", "Drama")
            ),
            new PeliculaSeedInfo(
                "Seven", "R", 127,
                "Dos detectives persiguen a un asesino en serie que basa sus crímenes en los siete pecados capitales.",
                "https://www.youtube.com/watch?v=znmZoVkCjpI",
                hoy.minusDays(65), hoy.plusDays(10), true, List.of("Suspenso", "Drama")
            ),
            new PeliculaSeedInfo(
                "Buscando a Nemo", "G", 100,
                "Un pez payaso sobreprotector se embarca en un largo viaje a través del océano para rescatar a su hijo.",
                "https://www.youtube.com/watch?v=wZni9gFXw_U",
                hoy.minusDays(25), hoy.plusDays(35), true, List.of("Animación", "Comedia")
            ),
            new PeliculaSeedInfo(
                "El Orfanato", "PG-13", 105,
                "Una mujer regresa al orfanato donde creció con la intención de abrir una residencia para niños discapacitados.",
                "https://www.youtube.com/watch?v=n5h7cQ1iS30",
                hoy.minusDays(140), hoy.minusDays(70), false, List.of("Terror", "Suspenso")
            ),
            new PeliculaSeedInfo(
                "Whiplash", "R", 106,
                "Un prometedor baterista de jazz se inscribe en un conservatorio musical riguroso bajo la tutela de un instructor implacable.",
                "https://www.youtube.com/watch?v=7d_jQycdQGo",
                hoy.minusDays(50), hoy.plusDays(10), true, List.of("Drama")
            ),
            new PeliculaSeedInfo(
                "Sherlock Holmes", "PG-13", 128,
                "El excéntrico detective y su socio deben usar sus habilidades de combate y deducción para derrotar a un complot oscuro.",
                "https://www.youtube.com/watch?v=J79bUfbpQz0",
                hoy.minusDays(110), hoy.minusDays(20), false, List.of("Acción", "Suspenso")
            ),
            new PeliculaSeedInfo(
                "Amélie", "R", 122,
                "Una joven ingenua e inocente en Montmartre decide cambiar discretamente las vidas de las personas que la rodean.",
                "https://www.youtube.com/watch?v=HUEvRyemksg",
                hoy.minusDays(18), hoy.plusDays(50), true, List.of("Comedia", "Romance")
            )
        );

        int peliculasInsertadas = 0;
        int peliculasOmitidas = 0;

        for (PeliculaSeedInfo info : peliculasInfo) {
            if (peliculaRepository.existsByTituloIgnoreCase(info.titulo())) {
                peliculasOmitidas++;
                continue;
            }

            Pelicula pelicula = Pelicula.builder()
                    .titulo(info.titulo())
                    .clasificacion(info.clasificacion())
                    .duracion(info.duracion())
                    .sinopsis(info.sinopsis())
                    .trailerUrl(info.trailerUrl())
                    .fechaEstreno(info.estreno())
                    .fechaFinExhibicion(info.fin())
                    .estado(info.estado())
                    .imagen(null) // Usar null ya que no está comprobada la correspondencia exacta de imagen externa
                    .build();

            Pelicula guardada = peliculaRepository.save(pelicula);

            for (String nombreGenero : info.generos()) {
                generoRepository.findByNombreIgnoreCase(nombreGenero).ifPresent(genero -> {
                    PeliculaGenero rel = PeliculaGenero.builder()
                            .pelicula(guardada)
                            .genero(genero)
                            .build();
                    peliculaGeneroRepository.save(rel);
                });
            }

            peliculasInsertadas++;
        }

        log.info("Películas procesadas -> Insertadas: {}, Omitidas (ya existían): {}", peliculasInsertadas, peliculasOmitidas);
        log.info("=== Seeder de datos de prueba finalizado con éxito ===");
    }
}

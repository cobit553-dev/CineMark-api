package com.devsv.cinemark_api.services;

import com.devsv.cinemark_api.dto.PeliculaDTO;
import com.devsv.cinemark_api.dto.PeliculaRequestDTO;
import com.devsv.cinemark_api.entities.Genero;
import com.devsv.cinemark_api.entities.Pelicula;
import com.devsv.cinemark_api.entities.PeliculaGenero;
import com.devsv.cinemark_api.exceptions.BadRequestException;
import com.devsv.cinemark_api.exceptions.ConflictException;
import com.devsv.cinemark_api.exceptions.ResourceNotFoundException;
import com.devsv.cinemark_api.interfaces.IPeliculaService;
import com.devsv.cinemark_api.mappers.PeliculaMapper;
import com.devsv.cinemark_api.repository.GeneroRepository;
import com.devsv.cinemark_api.repository.PeliculaGeneroRepository;
import com.devsv.cinemark_api.repository.PeliculaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PeliculaService implements IPeliculaService {

    private static final Path DIRECTORIO_IMAGENES = Paths.get("uploads", "peliculas").toAbsolutePath().normalize();

    private static final Set<String> TIPOS_IMAGEN_PERMITIDOS = Set.of("image/jpeg", "image/png", "image/webp");

    private final PeliculaRepository peliculaRepository;
    private final GeneroRepository generoRepository;
    private final PeliculaGeneroRepository peliculaGeneroRepository;
    private final PeliculaMapper peliculaMapper;

    @Override
    @Transactional(readOnly = true)
    public List<PeliculaDTO> findAll() {
        return peliculaRepository.findAll().stream().map(this::convertirConGeneros).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PeliculaDTO findById(Long id) {
        return convertirConGeneros(obtenerPelicula(id));
    }

    @Override
    @Transactional
    public PeliculaDTO create(PeliculaRequestDTO dto) {
        validarFechas(dto);

        String titulo = dto.getTitulo().trim();
        validarTituloDuplicado(titulo);

        Pelicula pelicula = new Pelicula();
        pelicula.setTitulo(titulo);
        aplicarDatos(pelicula, dto);

        if (tieneImagen(dto.getImagen())) {
            pelicula.setImagen(guardarImagen(dto.getImagen()));
        }

        Pelicula guardada = peliculaRepository.save(pelicula);
        guardarGeneros(guardada, dto.getGenerosIds());

        return convertirConGeneros(guardada);
    }

    @Override
    @Transactional
    public PeliculaDTO update(Long id, PeliculaRequestDTO dto) {
        validarFechas(dto);

        Pelicula pelicula = obtenerPelicula(id);
        String titulo = dto.getTitulo().trim();

        validarTituloDuplicadoEnEdicion(id, titulo);

        pelicula.setTitulo(titulo);
        aplicarDatos(pelicula, dto);

        if (tieneImagen(dto.getImagen())) {
            actualizarImagen(pelicula, dto.getImagen());
        }

        Pelicula actualizada = peliculaRepository.save(pelicula);
        actualizarGeneros(actualizada, dto.getGenerosIds());

        return convertirConGeneros(actualizada);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Pelicula pelicula = obtenerPelicula(id);

        eliminarRelacionesGeneros(id);

        String imagen = pelicula.getImagen();

        peliculaRepository.delete(pelicula);
        eliminarImagen(imagen);
    }

    private void aplicarDatos(Pelicula pelicula, PeliculaRequestDTO dto) {
        pelicula.setClasificacion(dto.getClasificacion().trim());
        pelicula.setDuracion(dto.getDuracion());
        pelicula.setSinopsis(limpiarTextoOpcional(dto.getSinopsis()));
        pelicula.setTrailerUrl(limpiarTextoOpcional(dto.getTrailerUrl()));
        pelicula.setFechaEstreno(dto.getFechaEstreno());
        pelicula.setFechaFinExhibicion(dto.getFechaFinExhibicion());

        if (pelicula.getId() == null) {
            pelicula.setEstado(dto.getEstado() != null ? dto.getEstado() : true);
        } else if (dto.getEstado() != null) {
            pelicula.setEstado(dto.getEstado());
        }
    }

    private Pelicula obtenerPelicula(Long id) {
        return peliculaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Película no encontrada con id: " + id));
    }

    private Genero obtenerGenero(Long id) {
        return generoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Género no encontrado con id: " + id));
    }

    private void validarTituloDuplicado(String titulo) {
        if (peliculaRepository.existsByTituloIgnoreCase(titulo)) {
            throw new ConflictException("Ya existe una película con ese título");
        }
    }

    private void validarTituloDuplicadoEnEdicion(Long id, String titulo) {
        peliculaRepository.findByTituloIgnoreCase(titulo).filter(encontrada -> !encontrada.getId().equals(id))
                .ifPresent(encontrada -> {
                    throw new ConflictException("Ya existe una película con ese título");
                });
    }

    private void validarFechas(PeliculaRequestDTO dto) {
        if (dto.getFechaEstreno() == null || dto.getFechaFinExhibicion() == null) {
            return;
        }

        if (dto.getFechaFinExhibicion().isBefore(dto.getFechaEstreno())) {
            throw new BadRequestException("La fecha final de exhibición no puede ser anterior a la fecha de estreno");
        }
    }

    private void actualizarGeneros(Pelicula pelicula, List<Long> generosIds) {
        eliminarRelacionesGeneros(pelicula.getId());
        guardarGeneros(pelicula, generosIds);
    }

    private void eliminarRelacionesGeneros(Long peliculaId) {
        List<PeliculaGenero> relaciones = peliculaGeneroRepository.findByPeliculaId(peliculaId);

        if (relaciones.isEmpty()) {
            return;
        }

        peliculaGeneroRepository.deleteAll(relaciones);
        peliculaGeneroRepository.flush();
    }

    private void guardarGeneros(Pelicula pelicula, List<Long> generosIds) {
        if (generosIds == null || generosIds.isEmpty()) {
            return;
        }

        generosIds.stream().distinct().forEach(generoId -> {
            Genero genero = obtenerGenero(generoId);

            PeliculaGenero relacion = PeliculaGenero.builder().pelicula(pelicula).genero(genero).build();

            peliculaGeneroRepository.save(relacion);
        });
    }

    private PeliculaDTO convertirConGeneros(Pelicula pelicula) {
        PeliculaDTO dto = peliculaMapper.toDTO(pelicula);

        List<PeliculaGenero> relaciones = peliculaGeneroRepository.findByPeliculaId(pelicula.getId());

        dto.setGenerosIds(relaciones.stream().map(relacion -> relacion.getGenero().getId()).toList());

        dto.setGenerosNombres(relaciones.stream().map(relacion -> relacion.getGenero().getNombre()).toList());

        return dto;
    }

    private boolean tieneImagen(MultipartFile imagen) {
        return imagen != null && !imagen.isEmpty();
    }

    private void actualizarImagen(Pelicula pelicula, MultipartFile nuevaImagen) {
        String imagenAnterior = pelicula.getImagen();

        pelicula.setImagen(guardarImagen(nuevaImagen));
        eliminarImagen(imagenAnterior);
    }

    private String guardarImagen(MultipartFile archivo) {
        validarImagen(archivo);

        try {
            Files.createDirectories(DIRECTORIO_IMAGENES);

            String extension = obtenerExtension(archivo.getOriginalFilename());
            String nombreArchivo = UUID.randomUUID() + extension;

            Path destino = DIRECTORIO_IMAGENES.resolve(nombreArchivo).normalize();

            if (!destino.startsWith(DIRECTORIO_IMAGENES)) {
                throw new BadRequestException("Nombre de archivo no válido");
            }

            Files.copy(archivo.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);

            return "uploads/peliculas/" + nombreArchivo;

        } catch (IOException exception) {
            throw new BadRequestException("No se pudo guardar la imagen de la película");
        }
    }

    private void validarImagen(MultipartFile archivo) {
        if (!tieneImagen(archivo)) {
            return;
        }

        String tipoContenido = archivo.getContentType();

        if (tipoContenido == null || !TIPOS_IMAGEN_PERMITIDOS.contains(tipoContenido)) {
            throw new BadRequestException("La imagen debe ser JPG, PNG o WEBP");
        }
    }

    private String obtenerExtension(String nombreOriginal) {
        if (nombreOriginal == null || !nombreOriginal.contains(".")) {
            return "";
        }

        return nombreOriginal.substring(nombreOriginal.lastIndexOf(".")).toLowerCase();
    }

    private void eliminarImagen(String ruta) {
        if (ruta == null || ruta.isBlank() || !ruta.startsWith("uploads/peliculas/")) {
            return;
        }

        try {
            String nombreArchivo = ruta.substring("uploads/peliculas/".length());
            Path archivo = DIRECTORIO_IMAGENES.resolve(nombreArchivo).normalize();

            if (archivo.startsWith(DIRECTORIO_IMAGENES)) {
                Files.deleteIfExists(archivo);
            }

        } catch (IOException ignored) {
        }
    }

    private String limpiarTextoOpcional(String valor) {
        return valor == null || valor.isBlank() ? null : valor.trim();
    }
}
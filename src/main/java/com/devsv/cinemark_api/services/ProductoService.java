package com.devsv.cinemark_api.services;

import com.devsv.cinemark_api.dto.ProductoDTO;
import com.devsv.cinemark_api.dto.ProductoRequestDTO;
import com.devsv.cinemark_api.entities.CategoriaProducto;
import com.devsv.cinemark_api.entities.Producto;
import com.devsv.cinemark_api.exceptions.BadRequestException;
import com.devsv.cinemark_api.exceptions.ConflictException;
import com.devsv.cinemark_api.exceptions.ResourceNotFoundException;
import com.devsv.cinemark_api.interfaces.IProductoService;
import com.devsv.cinemark_api.mappers.ProductoMapper;
import com.devsv.cinemark_api.repository.CategoriaProductoRepository;
import com.devsv.cinemark_api.repository.ProductoRepository;
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
public class ProductoService implements IProductoService {

    private static final Path DIRECTORIO_IMAGENES = Paths.get("uploads", "productos").toAbsolutePath().normalize();

    private static final Set<String> TIPOS_IMAGEN_PERMITIDOS = Set.of("image/jpeg", "image/png", "image/webp");

    private final ProductoRepository productoRepository;
    private final CategoriaProductoRepository categoriaRepository;
    private final ProductoMapper productoMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ProductoDTO> findAll() {
        return productoMapper.toDtoList(productoRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public ProductoDTO findById(Long id) {
        return productoMapper.toDTO(obtenerProducto(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoDTO> findByCategoria(Long categoriaId) {
        if (!categoriaRepository.existsById(categoriaId)) {
            throw new ResourceNotFoundException("Categoría no encontrada con id: " + categoriaId);
        }

        return productoMapper.toDtoList(productoRepository.findByCategoriaId(categoriaId));
    }

    @Override
    @Transactional
    public ProductoDTO create(ProductoRequestDTO dto) {
        String nombre = dto.getNombre().trim();

        validarNombreDuplicado(nombre);

        CategoriaProducto categoria = obtenerCategoria(dto.getCategoriaId());

        Producto producto = new Producto();
        producto.setCategoria(categoria);
        producto.setNombre(nombre);
        producto.setDescripcion(limpiarTextoOpcional(dto.getDescripcion()));
        producto.setPrecio(dto.getPrecio());
        producto.setEstado(dto.getEstado() != null ? dto.getEstado() : true);

        if (tieneImagen(dto.getImagen())) {
            producto.setImagen(guardarImagen(dto.getImagen()));
        }

        return productoMapper.toDTO(productoRepository.save(producto));
    }

    @Override
    @Transactional
    public ProductoDTO update(Long id, ProductoRequestDTO dto) {
        Producto producto = obtenerProducto(id);
        String nombre = dto.getNombre().trim();

        validarNombreDuplicadoEnEdicion(id, nombre);

        CategoriaProducto categoria = obtenerCategoria(dto.getCategoriaId());

        producto.setCategoria(categoria);
        producto.setNombre(nombre);
        producto.setDescripcion(limpiarTextoOpcional(dto.getDescripcion()));
        producto.setPrecio(dto.getPrecio());

        if (dto.getEstado() != null) {
            producto.setEstado(dto.getEstado());
        }

        if (tieneImagen(dto.getImagen())) {
            actualizarImagen(producto, dto.getImagen());
        }

        return productoMapper.toDTO(productoRepository.save(producto));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Producto producto = obtenerProducto(id);
        String imagen = producto.getImagen();

        productoRepository.delete(producto);
        eliminarImagen(imagen);
    }

    private Producto obtenerProducto(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + id));
    }

    private CategoriaProducto obtenerCategoria(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con id: " + id));
    }

    private void validarNombreDuplicado(String nombre) {
        if (productoRepository.existsByNombreIgnoreCase(nombre)) {
            throw new ConflictException("Ya existe un producto con ese nombre");
        }
    }

    private void validarNombreDuplicadoEnEdicion(Long id, String nombre) {
        productoRepository.findByNombreIgnoreCase(nombre).filter(encontrado -> !encontrado.getId().equals(id))
                .ifPresent(encontrado -> {
                    throw new ConflictException("Ya existe un producto con ese nombre");
                });
    }

    private boolean tieneImagen(MultipartFile imagen) {
        return imagen != null && !imagen.isEmpty();
    }

    private void actualizarImagen(Producto producto, MultipartFile nuevaImagen) {
        String imagenAnterior = producto.getImagen();

        producto.setImagen(guardarImagen(nuevaImagen));
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

            return "uploads/productos/" + nombreArchivo;

        } catch (IOException exception) {
            throw new BadRequestException("No se pudo guardar la imagen del producto");
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
        if (ruta == null || ruta.isBlank() || !ruta.startsWith("uploads/productos/")) {
            return;
        }

        try {
            String nombreArchivo = ruta.substring("uploads/productos/".length());
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
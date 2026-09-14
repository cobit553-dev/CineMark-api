package com.devsv.cinemark_api.services;

import com.devsv.cinemark_api.dto.*;
import com.devsv.cinemark_api.entities.*;
import com.devsv.cinemark_api.enums.*;
import com.devsv.cinemark_api.exceptions.BadRequestException;
import com.devsv.cinemark_api.exceptions.ConflictException;
import com.devsv.cinemark_api.exceptions.ForbiddenException;
import com.devsv.cinemark_api.exceptions.ResourceNotFoundException;
import com.devsv.cinemark_api.interfaces.ICompraService;
import com.devsv.cinemark_api.interfaces.IMovimientoInventarioService;
import com.devsv.cinemark_api.mappers.CompraMapper;
import com.devsv.cinemark_api.repository.*;
import com.devsv.cinemark_api.utils.CodigoCompraUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class CompraService implements ICompraService {

    private static final long MINUTOS_EXPIRACION_COMPRA = 10;

    private final CompraRepository compraRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;
    private final InventarioRepository inventarioRepository;
    private final PromocionRepository promocionRepository;
    private final BoletoRepository boletoRepository;
    private final IMovimientoInventarioService movimientoInventarioService;
    private final CompraMapper compraMapper;

    @Override
    @Transactional(readOnly = true)
    public List<CompraDTO> findAll() {
        return compraMapper.toDtoList(compraRepository.findAllByOrderByFechaDesc());
    }

    @Override
    @Transactional(readOnly = true)
    public CompraDTO findById(Long id, String emailUsuario) {
        Compra compra = compraRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Compra no encontrada con id: " + id));

        Usuario usuario = obtenerUsuarioAutenticado(emailUsuario);
        validarPropiedadCompra(compra, usuario);

        return compraMapper.toDTO(compra);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CompraDTO> findMisCompras(String email) {
        Usuario usuario = obtenerUsuarioAutenticado(email);

        return compraMapper.toDtoList(compraRepository.findByUsuarioIdOrderByFechaDesc(usuario.getId()));
    }

    @Override
    @Transactional
    public CompraDTO create(CompraRequestDTO dto, String emailUsuario) {
        if (dto == null) {
            throw new BadRequestException("Los datos de la compra son obligatorios");
        }

        Usuario usuario = obtenerUsuarioAutenticado(emailUsuario);
        LocalDateTime ahora = LocalDateTime.now();

        Promocion promocion = obtenerPromocionValida(dto.getPromocionId(), ahora.toLocalDate());

        Compra compra = Compra.builder().usuario(usuario).fecha(ahora)
                .fechaExpiracion(ahora.plusMinutes(MINUTOS_EXPIRACION_COMPRA)).subtotal(montoCero())
                .descuento(montoCero()).total(montoCero()).codigoCompra(CodigoCompraUtil.generar()).promocion(promocion)
                .estado(EstadoCompra.PENDIENTE).detalles(new ArrayList<>()).build();

        BigDecimal subtotalProductos = BigDecimal.ZERO;

        if (dto.getDetalles() != null) {
            validarProductosDuplicados(dto.getDetalles());

            for (DetalleCompraDTO detalleDTO : dto.getDetalles()) {
                DetalleCompra detalle = crearDetalleNuevo(compra, detalleDTO, emailUsuario);

                compra.getDetalles().add(detalle);
                subtotalProductos = subtotalProductos.add(detalle.getSubtotal());
            }
        }

        subtotalProductos = escalar(subtotalProductos);
        BigDecimal subtotalBoletos = montoCero();

        if (promocion == null) {
            promocion = obtenerMejorPromocionAutomatica(subtotalProductos, subtotalBoletos, ahora.toLocalDate());

            compra.setPromocion(promocion);
        }

        BigDecimal descuento = calcularDescuento(subtotalProductos, subtotalBoletos, promocion);

        compra.setSubtotal(subtotalProductos);
        compra.setDescuento(descuento);
        compra.setTotal(escalar(subtotalProductos.subtract(descuento).max(BigDecimal.ZERO)));

        return compraMapper.toDTO(compraRepository.save(compra));
    }

    @Override
    @Transactional
    public CompraDTO actualizarProductos(Long id, CompraProductosRequestDTO dto, String emailUsuario) {

        if (dto == null || dto.getDetalles() == null) {
            throw new BadRequestException("La lista de productos es obligatoria");
        }

        Usuario usuario = obtenerUsuarioAutenticado(emailUsuario);

        Compra compra = compraRepository.findWithLockById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Compra no encontrada con id: " + id));

        validarPropiedadCompra(compra, usuario);
        validarCompraEditable(compra);
        validarProductosDuplicados(dto.getDetalles());

        Map<Long, Integer> cantidadesAnteriores = obtenerCantidadesActuales(compra);

        Map<Long, Integer> cantidadesNuevas = obtenerCantidadesNuevas(dto.getDetalles());

        actualizarStockProductos(compra, cantidadesAnteriores, cantidadesNuevas, emailUsuario);

        reconstruirDetalles(compra, dto.getDetalles());
        recalcularTotales(compra);

        return compraMapper.toDTO(compraRepository.save(compra));
    }

    private Map<Long, Integer> obtenerCantidadesActuales(Compra compra) {
        Map<Long, Integer> cantidades = new HashMap<>();

        for (DetalleCompra detalle : compra.getDetalles()) {
            Long productoId = detalle.getProducto().getId();

            cantidades.merge(productoId, detalle.getCantidad(), Integer::sum);
        }

        return cantidades;
    }

    private Map<Long, Integer> obtenerCantidadesNuevas(List<DetalleCompraDTO> detalles) {

        Map<Long, Integer> cantidades = new HashMap<>();

        for (DetalleCompraDTO detalle : detalles) {
            validarDetalleProducto(detalle);

            cantidades.put(detalle.getProductoId(), detalle.getCantidad());
        }

        return cantidades;
    }

    private void actualizarStockProductos(Compra compra, Map<Long, Integer> cantidadesAnteriores,
            Map<Long, Integer> cantidadesNuevas, String emailUsuario) {

        Set<Long> productosAProcesar = new HashSet<>();

        productosAProcesar.addAll(cantidadesAnteriores.keySet());
        productosAProcesar.addAll(cantidadesNuevas.keySet());

        for (Long productoId : productosAProcesar) {
            int anterior = cantidadesAnteriores.getOrDefault(productoId, 0);
            int nueva = cantidadesNuevas.getOrDefault(productoId, 0);
            int diferencia = nueva - anterior;

            if (diferencia == 0) {
                continue;
            }

            Producto producto = productoRepository.findById(productoId)
                    .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + productoId));

            Inventario inventario = inventarioRepository.findWithLockByProductoId(productoId)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "El producto " + producto.getNombre() + " no tiene inventario"));

            if (diferencia > 0) {
                reservarStockAdicional(compra, producto, inventario, diferencia, emailUsuario);
            } else {
                devolverStock(compra, inventario, diferencia, emailUsuario);
            }
        }
    }

    private void reservarStockAdicional(Compra compra, Producto producto, Inventario inventario, int diferencia,
            String emailUsuario) {

        if (!Boolean.TRUE.equals(producto.getEstado())) {
            throw new ConflictException("El producto " + producto.getNombre() + " no está disponible");
        }

        int stockActual = inventario.getStockActual() != null ? inventario.getStockActual() : 0;

        if (stockActual < diferencia) {
            throw new ConflictException(
                    "Stock insuficiente para el producto " + producto.getNombre() + ". Disponible: " + stockActual);
        }

        registrarMovimientoProducto(inventario.getId(), TipoMovimientoInventario.SALIDA, diferencia,
                "Reserva de stock para compra " + compra.getCodigoCompra(), emailUsuario);
    }

    private void devolverStock(Compra compra, Inventario inventario, int diferencia, String emailUsuario) {

        registrarMovimientoProducto(inventario.getId(), TipoMovimientoInventario.ENTRADA, Math.abs(diferencia),
                "Devolución de stock por ajuste de compra " + compra.getCodigoCompra(), emailUsuario);
    }

    private void reconstruirDetalles(Compra compra, List<DetalleCompraDTO> detalles) {

        compra.getDetalles().clear();

        for (DetalleCompraDTO detalleDTO : detalles) {
            Producto producto = productoRepository.findById(detalleDTO.getProductoId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Producto no encontrado con id: " + detalleDTO.getProductoId()));

            validarProductoDisponible(producto);

            BigDecimal precioUnitario = producto.getPrecio();

            validarPrecioProducto(producto, precioUnitario);

            BigDecimal subtotalDetalle = escalar(precioUnitario.multiply(BigDecimal.valueOf(detalleDTO.getCantidad())));

            compra.getDetalles()
                    .add(DetalleCompra.builder().compra(compra).producto(producto).cantidad(detalleDTO.getCantidad())
                            .precioUnitario(precioUnitario).subtotal(subtotalDetalle).build());
        }
    }

    private DetalleCompra crearDetalleNuevo(Compra compra, DetalleCompraDTO detalleDTO, String emailUsuario) {

        validarDetalleProducto(detalleDTO);

        Producto producto = productoRepository.findById(detalleDTO.getProductoId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Producto no encontrado con id: " + detalleDTO.getProductoId()));

        validarProductoDisponible(producto);

        Inventario inventario = inventarioRepository.findWithLockByProductoId(producto.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "El producto " + producto.getNombre() + " no tiene inventario"));

        int stockActual = inventario.getStockActual() != null ? inventario.getStockActual() : 0;

        if (stockActual < detalleDTO.getCantidad()) {
            throw new ConflictException(
                    "Stock insuficiente para el producto " + producto.getNombre() + ". Disponible: " + stockActual);
        }

        BigDecimal precioUnitario = producto.getPrecio();

        validarPrecioProducto(producto, precioUnitario);

        BigDecimal subtotalDetalle = escalar(precioUnitario.multiply(BigDecimal.valueOf(detalleDTO.getCantidad())));

        registrarMovimientoProducto(inventario.getId(), TipoMovimientoInventario.SALIDA, detalleDTO.getCantidad(),
                "Reserva de stock para compra " + compra.getCodigoCompra(), emailUsuario);

        return DetalleCompra.builder().compra(compra).producto(producto).cantidad(detalleDTO.getCantidad())
                .precioUnitario(precioUnitario).subtotal(subtotalDetalle).build();
    }

    private void validarProductoDisponible(Producto producto) {
        if (!Boolean.TRUE.equals(producto.getEstado())) {
            throw new ConflictException("El producto " + producto.getNombre() + " no está disponible");
        }
    }

    private void validarPrecioProducto(Producto producto, BigDecimal precioUnitario) {

        if (precioUnitario == null || precioUnitario.compareTo(BigDecimal.ZERO) < 0) {

            throw new ConflictException("El producto " + producto.getNombre() + " no tiene un precio válido");
        }
    }

    private void recalcularTotales(Compra compra) {
        BigDecimal subtotalProductos = compra.getDetalles().stream().map(DetalleCompra::getSubtotal)
                .filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);

        List<Boleto> boletos = boletoRepository.findByCompraId(compra.getId());

        BigDecimal subtotalBoletos = boletos.stream().filter(boleto -> boleto.getEstado() == EstadoBoleto.RESERVADO ||
                        boleto.getEstado() == EstadoBoleto.VENDIDO).map(Boleto::getPrecio).filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        subtotalProductos = escalar(subtotalProductos);
        subtotalBoletos = escalar(subtotalBoletos);

        Promocion promocion = compra.getPromocion();

        if (promocion == null) {
            promocion = obtenerMejorPromocionAutomatica(subtotalProductos, subtotalBoletos, LocalDate.now());

            compra.setPromocion(promocion);
        }

        BigDecimal descuento = calcularDescuento(subtotalProductos, subtotalBoletos, promocion);

        BigDecimal subtotal = escalar(subtotalProductos.add(subtotalBoletos));

        BigDecimal total = escalar(subtotal.subtract(descuento).max(BigDecimal.ZERO));

        compra.setSubtotal(subtotal);
        compra.setDescuento(descuento);
        compra.setTotal(total);
    }

    private void validarCompraEditable(Compra compra) {
        if (compra.getEstado() != EstadoCompra.PENDIENTE) {
            throw new ConflictException("Solo se pueden modificar productos de una compra pendiente");
        }

        if (compra.getFechaExpiracion() != null && LocalDateTime.now().isAfter(compra.getFechaExpiracion())) {

            throw new ConflictException("La compra ha expirado");
        }
    }

    private void validarProductosDuplicados(List<DetalleCompraDTO> detalles) {

        Set<Long> ids = new HashSet<>();

        for (DetalleCompraDTO detalle : detalles) {
            validarDetalleProducto(detalle);

            if (!ids.add(detalle.getProductoId())) {
                throw new BadRequestException("No se puede repetir el mismo producto en la compra");
            }
        }
    }

    private void validarDetalleProducto(DetalleCompraDTO detalleDTO) {

        if (detalleDTO == null) {
            throw new BadRequestException("El detalle de compra no puede ser nulo");
        }

        if (detalleDTO.getProductoId() == null) {
            throw new BadRequestException("El producto es obligatorio");
        }

        if (detalleDTO.getCantidad() == null || detalleDTO.getCantidad() <= 0) {

            throw new BadRequestException("La cantidad del producto debe ser mayor que cero");
        }
    }

    private void registrarMovimientoProducto(Long inventarioId, TipoMovimientoInventario tipo, Integer cantidad,
            String motivo, String emailUsuario) {

        MovimientoInventarioDTO movimiento = MovimientoInventarioDTO.builder().inventarioId(inventarioId).tipo(tipo)
                .cantidad(cantidad).motivo(motivo).build();

        movimientoInventarioService.create(movimiento, emailUsuario);
    }

    private void validarPropiedadCompra(Compra compra, Usuario usuario) {

        if (!esAdministrador(usuario) && !compra.getUsuario().getId().equals(usuario.getId())) {

            throw new ForbiddenException("No tiene permiso para consultar o modificar esta compra");
        }
    }

    private Usuario obtenerUsuarioAutenticado(String emailUsuario) {

        if (emailUsuario == null || emailUsuario.isBlank()) {
            throw new ForbiddenException("Usuario no autenticado");
        }

        return usuarioRepository.findByEmailIgnoreCase(emailUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario autenticado no encontrado"));
    }

    private boolean esAdministrador(Usuario usuario) {
        return usuario.getRol() != null && usuario.getRol().getNombre() == NombreRol.ADMIN;
    }

    private Promocion obtenerPromocionValida(Long promocionId, LocalDate fechaActual) {

        if (promocionId == null) {
            return null;
        }

        Promocion promocion = promocionRepository.findById(promocionId)
                .orElseThrow(() -> new ResourceNotFoundException("Promoción no encontrada con id: " + promocionId));

        validarPromocion(promocion, fechaActual);

        return promocion;
    }

    private Promocion obtenerMejorPromocionAutomatica(BigDecimal subtotalProductos, BigDecimal subtotalBoletos,
            LocalDate fechaActual) {

        Promocion mejorPromocion = null;
        BigDecimal mejorDescuento = BigDecimal.ZERO;

        for (Promocion promocion : promocionRepository.findAll()) {
            if (!esPromocionVigente(promocion, fechaActual)) {
                continue;
            }

            BigDecimal baseDescuento = obtenerBaseDescuento(subtotalProductos, subtotalBoletos, promocion.getTipoAplicacion());

            if (baseDescuento.compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }

            BigDecimal descuento = escalar(baseDescuento.multiply(promocion.getPorcentajeDescuento())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP));

            if (descuento.compareTo(mejorDescuento) > 0) {
                mejorDescuento = descuento;
                mejorPromocion = promocion;
            }
        }

        return mejorPromocion;
    }

    private boolean esPromocionVigente(Promocion promocion, LocalDate fechaActual) {

        if (promocion == null || promocion.getEstado() != EstadoPromocion.ACTIVA) {
            return false;
        }

        if (promocion.getFechaInicio() == null || promocion.getFechaFin() == null) {
            return false;
        }

        if (fechaActual.isBefore(promocion.getFechaInicio()) || fechaActual.isAfter(promocion.getFechaFin())) {
            return false;
        }

        if (promocion.getDiaSemana() != null && promocion.getDiaSemana() != fechaActual.getDayOfWeek()) {
            return false;
        }

        if (promocion.getTipoAplicacion() == null) {
            return false;
        }

        BigDecimal porcentaje = promocion.getPorcentajeDescuento();

        return porcentaje != null && porcentaje.compareTo(BigDecimal.ZERO) > 0 &&
                porcentaje.compareTo(BigDecimal.valueOf(100)) <= 0;
    }

    private void validarPromocion(Promocion promocion, LocalDate fechaActual) {

        if (promocion.getEstado() != EstadoPromocion.ACTIVA) {
            throw new ConflictException("La promoción seleccionada no está activa");
        }

        if (promocion.getFechaInicio() == null || promocion.getFechaFin() == null) {

            throw new ConflictException("La promoción no tiene un período de vigencia válido");
        }

        if (fechaActual.isBefore(promocion.getFechaInicio())) {
            throw new ConflictException("La promoción todavía no está vigente");
        }

        if (fechaActual.isAfter(promocion.getFechaFin())) {
            throw new ConflictException("La promoción ha expirado");
        }

        if (promocion.getDiaSemana() != null && fechaActual.getDayOfWeek() != promocion.getDiaSemana()) {

            throw new ConflictException("La promoción seleccionada no aplica para el día de hoy");
        }

        if (promocion.getTipoAplicacion() == null) {
            throw new ConflictException("La promoción no tiene un tipo de aplicación válido");
        }

        BigDecimal porcentaje = promocion.getPorcentajeDescuento();

        if (porcentaje == null || porcentaje.compareTo(BigDecimal.ZERO) <= 0 ||
                porcentaje.compareTo(BigDecimal.valueOf(100)) > 0) {

            throw new ConflictException("La promoción tiene un porcentaje de descuento inválido");
        }
    }

    private BigDecimal calcularDescuento(BigDecimal subtotalProductos, BigDecimal subtotalBoletos,
            Promocion promocion) {

        if (promocion == null) {
            return montoCero();
        }

        validarPromocion(promocion, LocalDate.now());

        BigDecimal baseDescuento = obtenerBaseDescuento(subtotalProductos, subtotalBoletos, promocion.getTipoAplicacion());

        return escalar(baseDescuento.multiply(promocion.getPorcentajeDescuento())
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP));
    }

    private BigDecimal obtenerBaseDescuento(BigDecimal subtotalProductos, BigDecimal subtotalBoletos,
            TipoAplicacionPromocion tipo) {

        return switch (tipo) {
            case PRODUCTOS -> subtotalProductos;
            case BOLETOS -> subtotalBoletos;
            case TODO -> subtotalProductos.add(subtotalBoletos);
        };
    }

    private BigDecimal montoCero() {
        return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal escalar(BigDecimal valor) {
        return valor.setScale(2, RoundingMode.HALF_UP);
    }
}
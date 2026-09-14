package com.devsv.cinemark_api.services;

import com.devsv.cinemark_api.dto.BoletoDTO;
import com.devsv.cinemark_api.dto.BoletoRequestDTO;
import com.devsv.cinemark_api.entities.*;
import com.devsv.cinemark_api.enums.EstadoBoleto;
import com.devsv.cinemark_api.enums.EstadoCompra;
import com.devsv.cinemark_api.enums.TipoAplicacionPromocion;
import com.devsv.cinemark_api.exceptions.ConflictException;
import com.devsv.cinemark_api.exceptions.ForbiddenException;
import com.devsv.cinemark_api.exceptions.ResourceNotFoundException;
import com.devsv.cinemark_api.interfaces.IBoletoService;
import com.devsv.cinemark_api.mappers.BoletoMapper;
import com.devsv.cinemark_api.repository.*;
import com.devsv.cinemark_api.utils.CodigoBoletoUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BoletoService implements IBoletoService {

    private static final long MINUTOS_RESERVA = 10;

    private final BoletoRepository boletoRepository;
    private final CompraRepository compraRepository;
    private final FuncionRepository funcionRepository;
    private final AsientoRepository asientoRepository;
    private final UsuarioRepository usuarioRepository;
    private final BoletoMapper boletoMapper;
    private final ReservaBoletoService reservaBoletoService;

    @Override
    @Transactional
    public BoletoDTO reservar(BoletoRequestDTO dto, String emailUsuario) {
        reservaBoletoService.liberarReservasVencidas();

        Usuario usuario = obtenerUsuarioAutenticado(emailUsuario);
        Compra compra = obtenerCompraConBloqueo(dto.getCompraId());

        validarPropietario(compra, usuario, "La compra no pertenece al usuario autenticado");
        validarCompraPendiente(compra);
        validarCompraVigente(compra);

        Funcion funcion = obtenerFuncion(dto.getFuncionId());
        Asiento asiento = obtenerAsientoConBloqueo(dto.getAsientoId());

        validarAsientoParaFuncion(asiento, funcion);
        validarAsientoDisponible(asiento, funcion);

        BigDecimal precio = obtenerPrecioValido(funcion);
        LocalDateTime ahora = LocalDateTime.now();

        Boleto boleto = Boleto.builder().compra(compra).funcion(funcion).asiento(asiento).precio(precio)
                .codigoQr(CodigoBoletoUtil.generar()).estado(EstadoBoleto.RESERVADO).fechaReserva(ahora)
                .fechaExpiracion(ahora.plusMinutes(MINUTOS_RESERVA)).build();

        try {
            Boleto guardado = boletoRepository.saveAndFlush(boleto);
            recalcularCompra(compra);

            return boletoMapper.toDTO(guardado);

        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("El asiento ya fue reservado por otro usuario");
        }
    }

    @Override
    @Transactional
    public BoletoDTO findById(Long id, String emailUsuario) {
        reservaBoletoService.liberarReservasVencidas();

        Usuario usuario = obtenerUsuarioAutenticado(emailUsuario);

        Boleto boleto = boletoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Boleto no encontrado con id: " + id));

        validarPermisoConsulta(boleto.getCompra(), usuario, "No tiene permiso para consultar este boleto");

        return boletoMapper.toDTO(boleto);
    }

    @Override
    @Transactional
    public List<BoletoDTO> findByCompra(Long compraId, String emailUsuario) {
        reservaBoletoService.liberarReservasVencidas();

        Usuario usuario = obtenerUsuarioAutenticado(emailUsuario);

        Compra compra = compraRepository.findById(compraId)
                .orElseThrow(() -> new ResourceNotFoundException("Compra no encontrada con id: " + compraId));

        validarPermisoConsulta(compra, usuario, "No tiene permiso para consultar los boletos de esta compra");

        return boletoMapper.toDtoList(boletoRepository.findByCompraId(compraId));
    }

    @Override
    @Transactional
    public List<BoletoDTO> findByFuncion(Long funcionId) {
        reservaBoletoService.liberarReservasVencidas();

        if (!funcionRepository.existsById(funcionId)) {
            throw new ResourceNotFoundException("Función no encontrada con id: " + funcionId);
        }

        return boletoMapper.toDtoList(boletoRepository.findByFuncionId(funcionId));
    }

    private Compra obtenerCompraConBloqueo(Long compraId) {
        return compraRepository.findWithLockById(compraId)
                .orElseThrow(() -> new ResourceNotFoundException("Compra no encontrada con id: " + compraId));
    }

    private Funcion obtenerFuncion(Long funcionId) {
        return funcionRepository.findById(funcionId)
                .orElseThrow(() -> new ResourceNotFoundException("Función no encontrada con id: " + funcionId));
    }

    private Asiento obtenerAsientoConBloqueo(Long asientoId) {
        return asientoRepository.findWithLockById(asientoId)
                .orElseThrow(() -> new ResourceNotFoundException("Asiento no encontrado con id: " + asientoId));
    }

    private void validarCompraPendiente(Compra compra) {
        if (compra.getEstado() != EstadoCompra.PENDIENTE) {
            throw new ConflictException("Solo se pueden agregar boletos a una compra pendiente");
        }
    }

    private void validarCompraVigente(Compra compra) {
        if (compra.getFechaExpiracion() != null && !compra.getFechaExpiracion().isAfter(LocalDateTime.now())) {
            throw new ConflictException("La compra ha expirado y ya no permite agregar boletos");
        }
    }

    private void validarAsientoParaFuncion(Asiento asiento, Funcion funcion) {
        if (!asiento.getSala().getId().equals(funcion.getSala().getId())) {
            throw new ConflictException("El asiento seleccionado no pertenece a la sala de la función");
        }

        if (!Boolean.TRUE.equals(asiento.getEstado())) {
            throw new ConflictException("El asiento seleccionado no está habilitado");
        }
    }

    private void validarAsientoDisponible(Asiento asiento, Funcion funcion) {
        if (boletoRepository.existsByFuncionIdAndAsientoId(funcion.getId(), asiento.getId())) {
            throw new ConflictException("El asiento ya está reservado para esta función");
        }
    }

    private BigDecimal obtenerPrecioValido(Funcion funcion) {
        BigDecimal precio = funcion.getPrecio();

        if (precio == null || precio.compareTo(BigDecimal.ZERO) < 0) {
            throw new ConflictException("La función no tiene un precio válido");
        }

        return precio.setScale(2, RoundingMode.HALF_UP);
    }

    private void recalcularCompra(Compra compra) {
        BigDecimal subtotalProductos = calcularSubtotalProductos(compra);
        BigDecimal subtotalBoletos = calcularSubtotalBoletos(compra);
        BigDecimal subtotalGeneral = subtotalProductos.add(subtotalBoletos).setScale(2, RoundingMode.HALF_UP);
        BigDecimal descuento = calcularDescuento(subtotalProductos, subtotalBoletos, compra.getPromocion());
        BigDecimal total = subtotalGeneral.subtract(descuento).max(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);

        compra.setSubtotal(subtotalGeneral);
        compra.setDescuento(descuento);
        compra.setTotal(total);

        compraRepository.save(compra);
    }

    private BigDecimal calcularSubtotalProductos(Compra compra) {
        if (compra.getDetalles() == null) {
            return montoCero();
        }

        return compra.getDetalles().stream().filter(detalle -> detalle != null && detalle.getSubtotal() != null)
                .map(DetalleCompra::getSubtotal).reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calcularSubtotalBoletos(Compra compra) {
        return boletoRepository.findByCompraId(compra.getId()).stream()
                .filter(boleto -> boleto.getEstado() == EstadoBoleto.RESERVADO ||
                        boleto.getEstado() == EstadoBoleto.VENDIDO).filter(boleto -> boleto.getPrecio() != null)
                .map(Boleto::getPrecio).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calcularDescuento(BigDecimal subtotalProductos, BigDecimal subtotalBoletos,
            Promocion promocion) {

        if (promocion == null) {
            return montoCero();
        }

        BigDecimal porcentaje = promocion.getPorcentajeDescuento();

        if (porcentaje == null || porcentaje.compareTo(BigDecimal.ZERO) <= 0 ||
                porcentaje.compareTo(BigDecimal.valueOf(99)) > 0) {
            throw new ConflictException("La promoción asociada a la compra tiene un porcentaje inválido");
        }

        TipoAplicacionPromocion tipo = promocion.getTipoAplicacion();

        if (tipo == null) {
            throw new ConflictException("La promoción no tiene un tipo de aplicación válido");
        }

        BigDecimal baseDescuento = switch (tipo) {
            case PRODUCTOS -> subtotalProductos;
            case BOLETOS -> subtotalBoletos;
            case TODO -> subtotalProductos.add(subtotalBoletos);
        };

        return baseDescuento.multiply(porcentaje).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }

    private Usuario obtenerUsuarioAutenticado(String emailUsuario) {
        if (emailUsuario == null || emailUsuario.isBlank()) {
            throw new ForbiddenException("Usuario no autenticado");
        }

        return usuarioRepository.findByEmailIgnoreCase(emailUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario autenticado no encontrado"));
    }

    private void validarPropietario(Compra compra, Usuario usuario, String mensaje) {
        validarCompraConUsuario(compra);

        if (!compra.getUsuario().getId().equals(usuario.getId())) {
            throw new ForbiddenException(mensaje);
        }
    }

    private void validarPermisoConsulta(Compra compra, Usuario usuario, String mensaje) {
        validarCompraConUsuario(compra);

        if (!esAdministrador() && !compra.getUsuario().getId().equals(usuario.getId())) {
            throw new ForbiddenException(mensaje);
        }
    }

    private void validarCompraConUsuario(Compra compra) {
        if (compra == null || compra.getUsuario() == null) {
            throw new ConflictException("El boleto no tiene una compra o usuario válido");
        }
    }

    private boolean esAdministrador() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        return authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                .anyMatch("ROLE_ADMIN"::equals);
    }

    private BigDecimal montoCero() {
        return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
    }
}
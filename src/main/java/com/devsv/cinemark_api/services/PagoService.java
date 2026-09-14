package com.devsv.cinemark_api.services;

import com.devsv.cinemark_api.dto.PagoDTO;
import com.devsv.cinemark_api.dto.PagoRequestDTO;
import com.devsv.cinemark_api.entities.Boleto;
import com.devsv.cinemark_api.entities.Compra;
import com.devsv.cinemark_api.entities.Pago;
import com.devsv.cinemark_api.entities.Usuario;
import com.devsv.cinemark_api.enums.*;
import com.devsv.cinemark_api.exceptions.BadRequestException;
import com.devsv.cinemark_api.exceptions.ConflictException;
import com.devsv.cinemark_api.exceptions.ForbiddenException;
import com.devsv.cinemark_api.exceptions.ResourceNotFoundException;
import com.devsv.cinemark_api.interfaces.IPagoService;
import com.devsv.cinemark_api.mappers.PagoMapper;
import com.devsv.cinemark_api.repository.BoletoRepository;
import com.devsv.cinemark_api.repository.CompraRepository;
import com.devsv.cinemark_api.repository.PagoRepository;
import com.devsv.cinemark_api.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PagoService implements IPagoService {

    private final PagoRepository pagoRepository;
    private final CompraRepository compraRepository;
    private final UsuarioRepository usuarioRepository;
    private final PagoMapper pagoMapper;
    private final BoletoRepository boletoRepository;

    @Override
    @Transactional(readOnly = true)
    public List<PagoDTO> findAll() {
        return pagoMapper.toDtoList(pagoRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public PagoDTO findById(Long id, String emailUsuario) {
        Usuario usuario = obtenerUsuarioAutenticado(emailUsuario);

        Pago pago = pagoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pago no encontrado con id: " + id));

        validarAccesoConsulta(pago, usuario);

        return pagoMapper.toDTO(pago);
    }

    @Override
    @Transactional(readOnly = true)
    public PagoDTO findByCompra(Long compraId, String emailUsuario) {
        Usuario usuario = obtenerUsuarioAutenticado(emailUsuario);

        Pago pago = pagoRepository.findByCompraId(compraId)
                .orElseThrow(() -> new ResourceNotFoundException("No existe pago para la compra: " + compraId));

        validarAccesoConsulta(pago, usuario);

        return pagoMapper.toDTO(pago);
    }

    @Override
    @Transactional
    public PagoDTO realizarPago(PagoRequestDTO dto, String emailUsuario) {
        validarDatosPago(dto);

        Usuario usuario = obtenerUsuarioAutenticado(emailUsuario);
        Compra compra = obtenerCompraConBloqueo(dto.getCompraId());

        validarPropietario(compra, usuario);
        validarCompraPagable(compra);

        LocalDateTime ahora = LocalDateTime.now();
        validarCompraVigente(compra, ahora);
        validarPagoDuplicado(compra);

        List<Boleto> boletosReservados = boletoRepository.findByCompraIdAndEstado(compra.getId(), EstadoBoleto.RESERVADO);

        validarContenidoCompra(compra, boletosReservados);
        validarReservasVigentes(boletosReservados, ahora);
        validarMonto(compra, dto);
        validarMetodoPago(dto);

        String referencia = prepararReferencia(dto);

        Pago pago = Pago.builder().compra(compra).monto(compra.getTotal()).metodoPago(dto.getMetodoPago())
                .referencia(referencia).fechaPago(ahora).estado(EstadoPago.APROBADO).build();

        Pago guardado = pagoRepository.save(pago);

        compra.setEstado(EstadoCompra.PAGADA);

        for (Boleto boleto : boletosReservados) {
            boleto.setEstado(EstadoBoleto.VENDIDO);
        }

        boletoRepository.saveAll(boletosReservados);
        compraRepository.save(compra);

        return pagoMapper.toDTO(guardado);
    }

    private Compra obtenerCompraConBloqueo(Long compraId) {
        return compraRepository.findWithLockById(compraId)
                .orElseThrow(() -> new ResourceNotFoundException("Compra no encontrada con id: " + compraId));
    }

    private void validarDatosPago(PagoRequestDTO dto) {
        if (dto == null) {
            throw new BadRequestException("Los datos del pago son obligatorios");
        }

        if (dto.getCompraId() == null) {
            throw new BadRequestException("La compra es obligatoria");
        }
    }

    private void validarPropietario(Compra compra, Usuario usuario) {
        if (!compra.getUsuario().getId().equals(usuario.getId())) {
            throw new ForbiddenException("La compra no pertenece al usuario autenticado");
        }
    }

    private void validarCompraPagable(Compra compra) {
        if (compra.getEstado() != EstadoCompra.PENDIENTE) {
            throw new ConflictException("La compra no está pendiente de pago");
        }
    }

    private void validarCompraVigente(Compra compra, LocalDateTime ahora) {
        if (compra.getFechaExpiracion() != null && !compra.getFechaExpiracion().isAfter(ahora)) {
            throw new ConflictException("La compra ha expirado y ya no puede ser pagada");
        }
    }

    private void validarPagoDuplicado(Compra compra) {
        if (pagoRepository.existsByCompraId(compra.getId())) {
            throw new ConflictException("La compra ya tiene un pago registrado");
        }
    }

    private void validarContenidoCompra(Compra compra, List<Boleto> boletosReservados) {
        boolean tieneProductos = compra.getDetalles() != null && !compra.getDetalles().isEmpty();
        boolean tieneBoletos = boletosReservados != null && !boletosReservados.isEmpty();

        if (!tieneProductos && !tieneBoletos) {
            throw new BadRequestException("La compra debe contener al menos un producto o un boleto");
        }
    }

    private void validarReservasVigentes(List<Boleto> boletosReservados, LocalDateTime ahora) {
        boolean existeReservaVencida = boletosReservados.stream()
                .anyMatch(boleto -> boleto.getFechaExpiracion() != null && !boleto.getFechaExpiracion().isAfter(ahora));

        if (existeReservaVencida) {
            throw new ConflictException("Uno o más boletos de la compra tienen la reserva vencida");
        }
    }

    private void validarMonto(Compra compra, PagoRequestDTO dto) {
        BigDecimal montoEsperado = compra.getTotal();

        if (montoEsperado == null) {
            throw new BadRequestException("La compra no tiene un total válido");
        }

        if (montoEsperado.compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException("El total de la compra no puede ser negativo");
        }

        if (dto.getMonto() == null) {
            throw new BadRequestException("El monto del pago es obligatorio");
        }

        if (dto.getMonto().compareTo(montoEsperado) != 0) {
            throw new BadRequestException(
                    "El monto enviado no coincide con el total de la compra. Total: " + montoEsperado);
        }
    }

    private void validarMetodoPago(PagoRequestDTO dto) {
        if (dto.getMetodoPago() == null) {
            throw new BadRequestException("El método de pago es obligatorio");
        }
    }

    private String prepararReferencia(PagoRequestDTO dto) {
        String referencia = limpiarReferencia(dto.getReferencia());

        if (dto.getMetodoPago() == MetodoPago.TARJETA && referencia == null) {
            throw new BadRequestException("La referencia es obligatoria para pagos con tarjeta");
        }

        return dto.getMetodoPago() == MetodoPago.EFECTIVO ? null : referencia;
    }

    private String limpiarReferencia(String referencia) {
        if (referencia == null || referencia.isBlank()) {
            return null;
        }

        String limpia = referencia.trim();

        if (limpia.length() > 100) {
            throw new BadRequestException("La referencia del pago no puede superar los 100 caracteres");
        }

        return limpia;
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

    private void validarAccesoConsulta(Pago pago, Usuario usuario) {
        if (pago.getCompra() == null || pago.getCompra().getUsuario() == null) {
            throw new ConflictException("El pago no tiene una compra o usuario válido");
        }

        if (!esAdministrador(usuario) && !pago.getCompra().getUsuario().getId().equals(usuario.getId())) {
            throw new ForbiddenException("No tiene permiso para consultar este pago");
        }
    }
}
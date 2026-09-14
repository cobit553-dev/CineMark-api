package com.devsv.cinemark_api.services;

import com.devsv.cinemark_api.dto.BoletoDTO;
import com.devsv.cinemark_api.dto.ComprobanteDTO;
import com.devsv.cinemark_api.dto.DetalleCompraDTO;
import com.devsv.cinemark_api.entities.Compra;
import com.devsv.cinemark_api.entities.Pago;
import com.devsv.cinemark_api.entities.Promocion;
import com.devsv.cinemark_api.enums.EstadoCompra;
import com.devsv.cinemark_api.enums.EstadoPago;
import com.devsv.cinemark_api.exceptions.ConflictException;
import com.devsv.cinemark_api.exceptions.ForbiddenException;
import com.devsv.cinemark_api.exceptions.ResourceNotFoundException;
import com.devsv.cinemark_api.interfaces.IComprobanteService;
import com.devsv.cinemark_api.mappers.BoletoMapper;
import com.devsv.cinemark_api.mappers.DetalleCompraMapper;
import com.devsv.cinemark_api.repository.BoletoRepository;
import com.devsv.cinemark_api.repository.CompraRepository;
import com.devsv.cinemark_api.repository.PagoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ComprobanteService implements IComprobanteService {

    private final CompraRepository compraRepository;
    private final PagoRepository pagoRepository;
    private final BoletoRepository boletoRepository;
    private final DetalleCompraMapper detalleCompraMapper;
    private final BoletoMapper boletoMapper;

    @Override
    @Transactional(readOnly = true)
    public ComprobanteDTO obtenerComprobante(Long compraId, String emailUsuario) {
        Compra compra = compraRepository.findById(compraId)
                .orElseThrow(() -> new ResourceNotFoundException("Compra no encontrada con id: " + compraId));

        validarAcceso(compra, emailUsuario);
        validarCompraPagada(compra);

        Pago pago = pagoRepository.findByCompraId(compraId)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el pago de la compra"));

        validarPagoAprobado(pago);

        List<DetalleCompraDTO> productos = compra.getDetalles().stream().map(detalleCompraMapper::toDTO).toList();

        List<BoletoDTO> boletos = boletoRepository.findByCompraId(compraId).stream().map(boletoMapper::toDTO).toList();

        Promocion promocion = compra.getPromocion();

        return ComprobanteDTO.builder().compraId(compra.getId()).codigoCompra(compra.getCodigoCompra())
                .fechaCompra(compra.getFecha()).usuarioId(compra.getUsuario().getId())
                .usuarioNombre(obtenerNombreCompleto(compra)).estadoCompra(compra.getEstado())
                .subtotal(compra.getSubtotal()).descuento(compra.getDescuento()).total(compra.getTotal())
                .promocionId(promocion != null ? promocion.getId() : null)
                .promocionNombre(promocion != null ? promocion.getNombre() : null)
                .porcentajeDescuento(promocion != null ? promocion.getPorcentajeDescuento() : BigDecimal.ZERO)
                .productos(productos).boletos(boletos).pagoId(pago.getId()).montoPagado(pago.getMonto())
                .metodoPago(pago.getMetodoPago()).referenciaPago(pago.getReferencia()).fechaPago(pago.getFechaPago())
                .estadoPago(pago.getEstado()).build();
    }

    private void validarAcceso(Compra compra, String emailUsuario) {
        if (!compra.getUsuario().getEmail().equalsIgnoreCase(emailUsuario)) {
            throw new ForbiddenException("No tiene permiso para consultar este comprobante");
        }
    }

    private void validarCompraPagada(Compra compra) {
        if (compra.getEstado() != EstadoCompra.PAGADA) {
            throw new ConflictException("La compra todavía no ha sido pagada");
        }
    }

    private void validarPagoAprobado(Pago pago) {
        if (pago.getEstado() != EstadoPago.APROBADO) {
            throw new ConflictException("El pago de la compra no está aprobado");
        }
    }

    private String obtenerNombreCompleto(Compra compra) {
        String nombre = compra.getUsuario().getNombre();
        String apellido = compra.getUsuario().getApellido();

        return apellido != null && !apellido.isBlank() ? nombre + " " + apellido : nombre;
    }
}
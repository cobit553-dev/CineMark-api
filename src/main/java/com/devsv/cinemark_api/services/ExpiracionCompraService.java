package com.devsv.cinemark_api.services;

import com.devsv.cinemark_api.dto.MovimientoInventarioDTO;
import com.devsv.cinemark_api.entities.Boleto;
import com.devsv.cinemark_api.entities.Compra;
import com.devsv.cinemark_api.entities.DetalleCompra;
import com.devsv.cinemark_api.entities.Inventario;
import com.devsv.cinemark_api.enums.EstadoBoleto;
import com.devsv.cinemark_api.enums.EstadoCompra;
import com.devsv.cinemark_api.enums.TipoMovimientoInventario;
import com.devsv.cinemark_api.interfaces.IMovimientoInventarioService;
import com.devsv.cinemark_api.repository.BoletoRepository;
import com.devsv.cinemark_api.repository.CompraRepository;
import com.devsv.cinemark_api.repository.InventarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExpiracionCompraService {

    private final CompraRepository compraRepository;
    private final InventarioRepository inventarioRepository;
    private final BoletoRepository boletoRepository;
    private final IMovimientoInventarioService movimientoInventarioService;

    @Transactional
    public void cancelarComprasVencidas() {
        LocalDateTime ahora = LocalDateTime.now();

        List<Compra> comprasVencidas = compraRepository.findByEstadoAndFechaExpiracionBefore(EstadoCompra.PENDIENTE, ahora);

        if (comprasVencidas.isEmpty()) {
            return;
        }

        int cantidadCancelada = 0;

        for (Compra compra : comprasVencidas) {
            devolverStock(compra);
            liberarBoletos(compra);

            compra.setEstado(EstadoCompra.CANCELADA);
            compraRepository.save(compra);

            cantidadCancelada++;
        }

        log.info("Compras vencidas canceladas: {}", cantidadCancelada);
    }

    private void devolverStock(Compra compra) {
        if (compra.getDetalles() == null || compra.getDetalles().isEmpty()) {
            return;
        }

        for (DetalleCompra detalle : compra.getDetalles()) {
            if (!esDetalleValido(detalle)) {
                continue;
            }

            Inventario inventario = inventarioRepository.findByProductoId(detalle.getProducto().getId()).orElse(null);

            if (inventario == null) {
                log.warn("No se encontró inventario para el producto id: {}", detalle.getProducto().getId());
                continue;
            }

            MovimientoInventarioDTO movimiento = MovimientoInventarioDTO.builder().inventarioId(inventario.getId())
                    .tipo(TipoMovimientoInventario.ENTRADA).cantidad(detalle.getCantidad())
                    .motivo("Devolución automática por expiración de compra " + compra.getCodigoCompra()).build();

            movimientoInventarioService.createSistema(movimiento);
        }
    }

    private void liberarBoletos(Compra compra) {
        List<Boleto> boletosReservados = boletoRepository.findByCompraIdAndEstado(compra.getId(), EstadoBoleto.RESERVADO);

        if (boletosReservados.isEmpty()) {
            return;
        }

        boletoRepository.deleteAll(boletosReservados);
        boletoRepository.flush();
    }

    private boolean esDetalleValido(DetalleCompra detalle) {
        return detalle != null && detalle.getProducto() != null && detalle.getProducto().getId() != null &&
                detalle.getCantidad() != null && detalle.getCantidad() > 0;
    }
}
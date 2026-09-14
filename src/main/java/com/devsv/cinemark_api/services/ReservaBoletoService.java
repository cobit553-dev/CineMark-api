package com.devsv.cinemark_api.services;

import com.devsv.cinemark_api.entities.Boleto;
import com.devsv.cinemark_api.entities.Compra;
import com.devsv.cinemark_api.entities.DetalleCompra;
import com.devsv.cinemark_api.entities.Promocion;
import com.devsv.cinemark_api.enums.EstadoBoleto;
import com.devsv.cinemark_api.enums.EstadoCompra;
import com.devsv.cinemark_api.enums.TipoAplicacionPromocion;
import com.devsv.cinemark_api.exceptions.ConflictException;
import com.devsv.cinemark_api.repository.BoletoRepository;
import com.devsv.cinemark_api.repository.CompraRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservaBoletoService {

    private final BoletoRepository boletoRepository;
    private final CompraRepository compraRepository;

    @Transactional
    public void liberarReservasVencidas() {
        List<Boleto> vencidos = boletoRepository.findByEstadoAndFechaExpiracionBefore(EstadoBoleto.RESERVADO, LocalDateTime.now());

        if (vencidos.isEmpty()) {
            return;
        }

        int cantidadLiberada = 0;

        for (Boleto boleto : vencidos) {
            Compra compraOriginal = boleto.getCompra();

            if (compraOriginal != null && compraOriginal.getEstado() == EstadoCompra.PENDIENTE) {
                Compra compra = compraRepository.findWithLockById(compraOriginal.getId()).orElse(compraOriginal);

                recalcularCompraSinBoleto(compra, boleto);
            }

            boletoRepository.delete(boleto);
            cantidadLiberada++;
        }

        boletoRepository.flush();

        log.info("Reservas de boletos vencidas liberadas: {}", cantidadLiberada);
    }

    private void recalcularCompraSinBoleto(Compra compra, Boleto boletoVencido) {
        BigDecimal subtotalProductos = calcularSubtotalProductos(compra);
        BigDecimal subtotalBoletos = calcularSubtotalBoletosRestantes(compra, boletoVencido);

        BigDecimal subtotalGeneral = subtotalProductos.add(subtotalBoletos).setScale(2, RoundingMode.HALF_UP);

        BigDecimal descuento = calcularDescuento(subtotalProductos, subtotalBoletos, compra.getPromocion());

        BigDecimal total = subtotalGeneral.subtract(descuento);

        if (total.compareTo(BigDecimal.ZERO) < 0) {
            total = BigDecimal.ZERO;
        }

        compra.setSubtotal(subtotalGeneral);
        compra.setDescuento(descuento);
        compra.setTotal(total.setScale(2, RoundingMode.HALF_UP));

        compraRepository.save(compra);
    }

    private BigDecimal calcularSubtotalProductos(Compra compra) {
        if (compra.getDetalles() == null || compra.getDetalles().isEmpty()) {
            return montoCero();
        }

        return compra.getDetalles().stream().filter(detalle -> detalle != null && detalle.getSubtotal() != null)
                .map(DetalleCompra::getSubtotal).reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calcularSubtotalBoletosRestantes(Compra compra, Boleto boletoVencido) {
        return boletoRepository.findByCompraId(compra.getId()).stream()
                .filter(boleto -> boleto.getId() != null && !boleto.getId().equals(boletoVencido.getId()))
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

    private BigDecimal montoCero() {
        return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
    }
}
package com.devsv.cinemark_api.services;

import com.devsv.cinemark_api.dto.BoletoDTO;
import com.devsv.cinemark_api.dto.CompraDTO;
import com.devsv.cinemark_api.dto.HistorialClienteDTO;
import com.devsv.cinemark_api.dto.PagoDTO;
import com.devsv.cinemark_api.entities.Boleto;
import com.devsv.cinemark_api.entities.Compra;
import com.devsv.cinemark_api.entities.Usuario;
import com.devsv.cinemark_api.exceptions.ResourceNotFoundException;
import com.devsv.cinemark_api.interfaces.IHistorialClienteService;
import com.devsv.cinemark_api.mappers.BoletoMapper;
import com.devsv.cinemark_api.mappers.CompraMapper;
import com.devsv.cinemark_api.mappers.PagoMapper;
import com.devsv.cinemark_api.repository.BoletoRepository;
import com.devsv.cinemark_api.repository.CompraRepository;
import com.devsv.cinemark_api.repository.PagoRepository;
import com.devsv.cinemark_api.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HistorialClienteService implements IHistorialClienteService {

    private final UsuarioRepository usuarioRepository;
    private final CompraRepository compraRepository;
    private final PagoRepository pagoRepository;
    private final BoletoRepository boletoRepository;
    private final CompraMapper compraMapper;
    private final PagoMapper pagoMapper;
    private final BoletoMapper boletoMapper;

    @Override
    @Transactional(readOnly = true)
    public HistorialClienteDTO obtenerHistorial(String emailUsuario) {
        Usuario usuario = obtenerUsuario(emailUsuario);

        List<Compra> compras = compraRepository.findByUsuarioIdOrderByFechaDesc(usuario.getId());
        List<CompraDTO> comprasDTO = compraMapper.toDtoList(compras);

        List<PagoDTO> pagos = new ArrayList<>();
        List<BoletoDTO> boletos = new ArrayList<>();

        for (Compra compra : compras) {
            pagoRepository.findByCompraId(compra.getId()).ifPresent(pago -> pagos.add(pagoMapper.toDTO(pago)));

            List<Boleto> boletosCompra = boletoRepository.findByCompraId(compra.getId());
            boletos.addAll(boletoMapper.toDtoList(boletosCompra));
        }

        return HistorialClienteDTO.builder().usuarioId(usuario.getId()).usuarioNombre(obtenerNombreCompleto(usuario))
                .compras(comprasDTO).pagos(pagos).boletos(boletos).build();
    }

    private Usuario obtenerUsuario(String emailUsuario) {
        return usuarioRepository.findByEmailIgnoreCase(emailUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario autenticado no encontrado"));
    }

    private String obtenerNombreCompleto(Usuario usuario) {
        String nombre = usuario.getNombre();
        String apellido = usuario.getApellido();

        return apellido != null && !apellido.isBlank() ? nombre + " " + apellido : nombre;
    }
}
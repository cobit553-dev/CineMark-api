package com.devsv.cinemark_api.services;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReservaBoletoScheduler {

    private final ReservaBoletoService reservaBoletoService;

    @Scheduled(fixedDelay = 60000)
    public void limpiarReservasVencidas() {
        reservaBoletoService.liberarReservasVencidas();
    }
}
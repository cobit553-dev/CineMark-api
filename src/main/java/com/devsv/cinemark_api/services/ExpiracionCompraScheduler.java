package com.devsv.cinemark_api.services;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ExpiracionCompraScheduler {

    private final ExpiracionCompraService expiracionCompraService;

    @Scheduled(fixedDelay = 60000)
    public void cancelarComprasVencidas() {
        expiracionCompraService.cancelarComprasVencidas();
    }
}
package com.devsv.cinemark_api.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public final class CodigoCompraUtil {

    private CodigoCompraUtil() {
    }

    public static String generar() {

        String fecha = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        String aleatorio = UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        return "CM-" + fecha + "-" + aleatorio;
    }
}
package com.devsv.cinemark_api.utils;

import java.util.UUID;

public final class CodigoBoletoUtil {

    private CodigoBoletoUtil() {
    }

    public static String generar() {
        return "BOL-" + UUID.randomUUID().toString().replace("-", "").toUpperCase();
    }
}
package com.devsv.cinemark_api.controllers;

import com.devsv.cinemark_api.services.ReportePdfService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@RestController
@CrossOrigin
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
public class ReporteController {

    private final ReportePdfService reportePdfService;

    @GetMapping("/ventas/pdf")
    public ResponseEntity<ByteArrayResource> generarPdfVentas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {

        byte[] pdf = reportePdfService.generarReporteVentas(fechaInicio, fechaFin);

        return crearRespuesta(pdf, "ventas", fechaInicio, fechaFin);
    }

    @GetMapping("/productos/pdf")
    public ResponseEntity<ByteArrayResource> generarPdfProductos(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {

        byte[] pdf = reportePdfService.generarReporteProductos(fechaInicio, fechaFin);

        return crearRespuesta(pdf, "productos", fechaInicio, fechaFin);
    }

    @GetMapping("/peliculas/pdf")
    public ResponseEntity<ByteArrayResource> generarPdfPeliculas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {

        byte[] pdf = reportePdfService.generarReportePeliculas(fechaInicio, fechaFin);

        return crearRespuesta(pdf, "peliculas", fechaInicio, fechaFin);
    }

    @GetMapping("/inventario-bajo/pdf")
    public ResponseEntity<ByteArrayResource> generarPdfInventario() {

        byte[] pdf = reportePdfService.generarReporteInventarioBajo();

        String nombreArchivo =
                "inventario-bajo_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".pdf";

        return respuestaPdf(pdf, nombreArchivo);
    }

    private ResponseEntity<ByteArrayResource> crearRespuesta(byte[] pdf, String tipo, LocalDate fechaInicio,
            LocalDate fechaFin) {

        DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyyMMdd");
        String nombreArchivo = tipo + "_" + fechaInicio.format(formato) + "_" + fechaFin.format(formato) + ".pdf";

        return respuestaPdf(pdf, nombreArchivo);
    }

    private ResponseEntity<ByteArrayResource> respuestaPdf(byte[] pdf, String nombreArchivo) {

        ByteArrayResource resource = new ByteArrayResource(pdf);

        return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment().filename(nombreArchivo).build()
                        .toString()).contentLength(pdf.length).body(resource);
    }
}
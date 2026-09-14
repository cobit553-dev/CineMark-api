package com.devsv.cinemark_api.services;

import com.devsv.cinemark_api.dto.ReporteInventarioDTO;
import com.devsv.cinemark_api.dto.ReportePeliculaDTO;
import com.devsv.cinemark_api.dto.ReporteProductoDTO;
import com.devsv.cinemark_api.dto.ReporteVentasDTO;
import com.devsv.cinemark_api.interfaces.IReporteService;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportePdfService {

    private static final float CM = 28.3464567f;

    private static final BaseColor COLOR_PRIMARIO = new BaseColor(76, 29, 149);
    private static final BaseColor COLOR_SECUNDARIO = new BaseColor(124, 58, 237);
    private static final BaseColor COLOR_FONDO_SUAVE = new BaseColor(248, 250, 252);
    private static final BaseColor COLOR_BORDE = new BaseColor(203, 213, 225);
    private static final BaseColor COLOR_TEXTO_SECUNDARIO = new BaseColor(71, 85, 105);
    private static final BaseColor COLOR_TOTAL = new BaseColor(237, 233, 254);

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final IReporteService reporteService;

    public byte[] generarReporteVentas(LocalDate fechaInicio, LocalDate fechaFin) {
        ReporteVentasDTO datos = reporteService.obtenerVentasPorRango(fechaInicio, fechaFin);

        try {
            ByteArrayOutputStream salida = new ByteArrayOutputStream();
            Document document = prepararDocumento(salida);
            agregarEncabezado(document, "REPORTE DE VENTAS", "Resumen general de ventas realizadas", fechaInicio, fechaFin);
            agregarTituloSeccion(document, "RESUMEN DE VENTAS");
            PdfPTable tabla = new PdfPTable(new float[]{3f, 2f});
            configurarTabla(tabla, 75);

            agregarEncabezadoTabla(tabla, "Concepto", "Resultado");

            tabla.addCell(celdaTexto("Compras pagadas"));
            tabla.addCell(celdaNumero(String.valueOf(datos.getCantidadCompras())));
            tabla.addCell(celdaTotalTexto("TOTAL DE VENTAS"));
            tabla.addCell(celdaTotalMoneda(datos.getTotalVentas()));

            document.add(tabla);

            return finalizarDocumento(document, salida);

        } catch (Exception exception) {
            throw new RuntimeException("No se pudo generar el reporte de ventas", exception);
        }
    }

    public byte[] generarReporteProductos(LocalDate fechaInicio, LocalDate fechaFin) {
        List<ReporteProductoDTO> datos = reporteService.obtenerProductosPorRango(fechaInicio, fechaFin);

        try {
            ByteArrayOutputStream salida = new ByteArrayOutputStream();
            Document document = prepararDocumento(salida);
            agregarEncabezado(document, "REPORTE DE PRODUCTOS VENDIDOS", "Detalle de productos de dulcería vendidos", fechaInicio, fechaFin);
            agregarTituloSeccion(document, "DETALLE DE PRODUCTOS");

            PdfPTable tabla = new PdfPTable(new float[]{4f, 2f, 2f});
            configurarTabla(tabla, 100);

            agregarEncabezadoTabla(tabla, "Producto", "Cantidad", "Total generado");

            for (ReporteProductoDTO item : datos) {
                tabla.addCell(celdaTexto(item.getProductoNombre()));
                tabla.addCell(celdaNumero(String.valueOf(item.getCantidadVendida())));
                tabla.addCell(celdaMoneda(item.getTotalGenerado()));
            }

            if (datos.isEmpty()) {
                agregarFilaVacia(tabla, "No se encontraron ventas de productos.", 3);
            }

            document.add(tabla);

            return finalizarDocumento(document, salida);

        } catch (Exception exception) {
            throw new RuntimeException("No se pudo generar el reporte de productos", exception);
        }
    }

    public byte[] generarReportePeliculas(LocalDate fechaInicio, LocalDate fechaFin) {
        List<ReportePeliculaDTO> datos = reporteService.obtenerPeliculasPorRango(fechaInicio, fechaFin);

        try {
            ByteArrayOutputStream salida = new ByteArrayOutputStream();
            Document document = prepararDocumento(salida);
            agregarEncabezado(document, "REPORTE DE PELÍCULAS", "Películas con boletos vendidos durante el período", fechaInicio, fechaFin);
            agregarTituloSeccion(document, "DETALLE DE PELÍCULAS");
            PdfPTable tabla = new PdfPTable(new float[]{4f, 2f, 2f});
            configurarTabla(tabla, 100);

            agregarEncabezadoTabla(tabla, "Película", "Boletos vendidos", "Total generado");

            for (ReportePeliculaDTO item : datos) {
                tabla.addCell(celdaTexto(item.getPeliculaTitulo()));
                tabla.addCell(celdaNumero(String.valueOf(item.getBoletosVendidos())));
                tabla.addCell(celdaMoneda(item.getTotalGenerado()));
            }

            if (datos.isEmpty()) {
                agregarFilaVacia(tabla, "No se encontraron boletos vendidos.", 3);
            }

            document.add(tabla);

            return finalizarDocumento(document, salida);

        } catch (Exception exception) {
            throw new RuntimeException("No se pudo generar el reporte de películas", exception);
        }
    }

    public byte[] generarReporteInventarioBajo() {
        List<ReporteInventarioDTO> datos = reporteService.obtenerInventarioBajo();

        try {
            ByteArrayOutputStream salida = new ByteArrayOutputStream();
            Document document = prepararDocumento(salida);

            agregarEncabezado(document, "REPORTE DE INVENTARIO BAJO", "Productos que requieren revisión de existencias", null, null);

            agregarTituloSeccion(document, "ESTADO DEL INVENTARIO");

            PdfPTable tabla = new PdfPTable(new float[]{4f, 2f, 2f, 3f});

            configurarTabla(tabla, 100);

            agregarEncabezadoTabla(tabla, "Producto", "Stock actual", "Stock mínimo", "Situación");

            for (ReporteInventarioDTO item : datos) {
                String situacion =
                        item.getStockActual() != null && item.getStockActual() == 0 ? "Sin existencias" : "Reabastecer";

                tabla.addCell(celdaTexto(item.getProductoNombre()));
                tabla.addCell(celdaNumero(String.valueOf(item.getStockActual())));
                tabla.addCell(celdaNumero(String.valueOf(item.getStockMinimo())));
                tabla.addCell(celdaTexto(situacion));
            }

            if (datos.isEmpty()) {
                agregarFilaVacia(tabla, "No existen productos con stock bajo.", 4);
            }

            document.add(tabla);

            return finalizarDocumento(document, salida);

        } catch (Exception exception) {
            throw new RuntimeException("No se pudo generar el reporte de inventario", exception);
        }
    }

    private Document prepararDocumento(ByteArrayOutputStream salida) throws DocumentException {

        Document document = crearDocumento();

        PdfWriter writer = PdfWriter.getInstance(document, salida);

        writer.setPageEvent(new FooterEvent(LocalDate.now().format(FORMATO_FECHA)));

        document.open();

        return document;
    }

    private byte[] finalizarDocumento(Document document, ByteArrayOutputStream salida) {
        document.close();
        return salida.toByteArray();
    }

    private Document crearDocumento() {
        return new Document(PageSize.LETTER, 2.2f * CM, 2.2f * CM, 2.2f * CM, 2.2f * CM);
    }

    private void agregarEncabezado(Document document, String tituloReporte, String descripcion, LocalDate fechaInicio,
            LocalDate fechaFin) throws DocumentException {

        PdfPTable tablaEncabezado = new PdfPTable(new float[]{1.2f, 4.8f});

        tablaEncabezado.setWidthPercentage(100);
        tablaEncabezado.setSpacingAfter(8);

        PdfPCell celdaLogo = new PdfPCell();
        celdaLogo.setBorder(Rectangle.NO_BORDER);
        celdaLogo.setVerticalAlignment(Element.ALIGN_MIDDLE);
        celdaLogo.setPadding(0);

        try {
            java.net.URL recurso = getClass().getResource("/static/Logo.jpg");

            if (recurso != null) {
                Image logo = Image.getInstance(recurso);
                logo.scaleToFit(65, 65);
                celdaLogo.addElement(logo);
            }

        } catch (Exception ignored) {
        }

        tablaEncabezado.addCell(celdaLogo);

        PdfPCell celdaEmpresa = new PdfPCell();
        celdaEmpresa.setBorder(Rectangle.NO_BORDER);
        celdaEmpresa.setVerticalAlignment(Element.ALIGN_MIDDLE);
        celdaEmpresa.setPaddingLeft(8);

        Font fuenteEmpresa = new Font(Font.FontFamily.HELVETICA, 17, Font.BOLD, COLOR_PRIMARIO);

        Font fuenteSistema = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, COLOR_TEXTO_SECUNDARIO);

        Paragraph empresa = new Paragraph("CineMark", fuenteEmpresa);

        empresa.setSpacingAfter(1);

        Paragraph sistema = new Paragraph("Sistema de Gestión de Cine", fuenteSistema);

        celdaEmpresa.addElement(empresa);
        celdaEmpresa.addElement(sistema);

        tablaEncabezado.addCell(celdaEmpresa);

        document.add(tablaEncabezado);

        agregarLineaSeparadora(document);

        Font fuenteTitulo = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD, BaseColor.BLACK);

        Paragraph titulo = new Paragraph(tituloReporte, fuenteTitulo);

        titulo.setAlignment(Element.ALIGN_CENTER);
        titulo.setSpacingBefore(12);
        titulo.setSpacingAfter(4);

        document.add(titulo);

        Font fuenteDescripcion = new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL, COLOR_TEXTO_SECUNDARIO);

        Paragraph parrafoDescripcion = new Paragraph(descripcion, fuenteDescripcion);

        parrafoDescripcion.setAlignment(Element.ALIGN_CENTER);
        parrafoDescripcion.setSpacingAfter(12);

        document.add(parrafoDescripcion);

        agregarInformacionReporte(document, fechaInicio, fechaFin);
    }

    private void agregarInformacionReporte(Document document, LocalDate fechaInicio,
            LocalDate fechaFin) throws DocumentException {

        PdfPTable tablaInfo = new PdfPTable(new float[]{1.5f, 3.5f});

        tablaInfo.setWidthPercentage(70);
        tablaInfo.setHorizontalAlignment(Element.ALIGN_LEFT);
        tablaInfo.setSpacingAfter(18);

        agregarFilaInformacion(tablaInfo, "Área:", "Administración");

        agregarFilaInformacion(tablaInfo, "Fecha de generación:", LocalDate.now().format(FORMATO_FECHA));

        if (fechaInicio != null && fechaFin != null) {
            agregarFilaInformacion(tablaInfo, "Período:",
                    fechaInicio.format(FORMATO_FECHA) + " - " + fechaFin.format(FORMATO_FECHA));
        } else {
            agregarFilaInformacion(tablaInfo, "Período:", "Estado actual");
        }

        document.add(tablaInfo);
    }

    private void agregarFilaInformacion(PdfPTable tabla, String etiqueta, String valor) {

        Font fuenteEtiqueta = new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD, COLOR_TEXTO_SECUNDARIO);

        Font fuenteValor = new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL, BaseColor.BLACK);

        PdfPCell celdaEtiqueta = new PdfPCell(new Phrase(etiqueta, fuenteEtiqueta));

        celdaEtiqueta.setBorder(Rectangle.NO_BORDER);
        celdaEtiqueta.setPaddingTop(2);
        celdaEtiqueta.setPaddingBottom(2);

        PdfPCell celdaValor = new PdfPCell(new Phrase(valor, fuenteValor));

        celdaValor.setBorder(Rectangle.NO_BORDER);
        celdaValor.setPaddingTop(2);
        celdaValor.setPaddingBottom(2);

        tabla.addCell(celdaEtiqueta);
        tabla.addCell(celdaValor);
    }

    private void agregarTituloSeccion(Document document, String titulo) throws DocumentException {

        Font fuente = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, COLOR_PRIMARIO);

        Paragraph parrafo = new Paragraph(titulo, fuente);

        parrafo.setSpacingAfter(7);

        document.add(parrafo);
    }

    private void agregarLineaSeparadora(Document document) throws DocumentException {

        PdfPTable linea = new PdfPTable(1);
        linea.setWidthPercentage(100);

        PdfPCell celda = new PdfPCell();
        celda.setFixedHeight(1.2f);
        celda.setBackgroundColor(COLOR_SECUNDARIO);
        celda.setBorder(Rectangle.NO_BORDER);

        linea.addCell(celda);

        document.add(linea);
    }

    private void configurarTabla(PdfPTable tabla, float porcentaje) {
        tabla.setWidthPercentage(porcentaje);
        tabla.setHorizontalAlignment(Element.ALIGN_LEFT);
        tabla.setSpacingAfter(12);
        tabla.setHeaderRows(1);
    }

    private void agregarEncabezadoTabla(PdfPTable tabla, String... columnas) {

        Font fuente = new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD, BaseColor.WHITE);

        for (String columna : columnas) {
            PdfPCell celda = new PdfPCell(new Phrase(columna, fuente));

            celda.setBackgroundColor(COLOR_PRIMARIO);
            celda.setBorderColor(COLOR_PRIMARIO);
            celda.setPaddingTop(7);
            celda.setPaddingBottom(7);
            celda.setPaddingLeft(6);
            celda.setVerticalAlignment(Element.ALIGN_MIDDLE);

            tabla.addCell(celda);
        }
    }

    private void agregarFilaVacia(PdfPTable tabla, String mensaje, int columnas) {
        PdfPCell celda = celdaTexto(mensaje);

        celda.setColspan(columnas);
        celda.setHorizontalAlignment(Element.ALIGN_CENTER);
        celda.setPaddingTop(12);
        celda.setPaddingBottom(12);

        tabla.addCell(celda);
    }

    private PdfPCell celdaTexto(String texto) {

        Font fuente = new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL, BaseColor.BLACK);

        PdfPCell celda = new PdfPCell(new Phrase(texto != null ? texto : "-", fuente));

        celda.setPaddingTop(6);
        celda.setPaddingBottom(6);
        celda.setPaddingLeft(6);
        celda.setPaddingRight(6);
        celda.setBorderColor(COLOR_BORDE);
        celda.setBackgroundColor(BaseColor.WHITE);
        celda.setVerticalAlignment(Element.ALIGN_MIDDLE);

        return celda;
    }

    private PdfPCell celdaNumero(String valor) {
        PdfPCell celda = celdaTexto(valor);
        celda.setHorizontalAlignment(Element.ALIGN_CENTER);

        return celda;
    }

    private PdfPCell celdaMoneda(BigDecimal valor) {
        PdfPCell celda = celdaTexto(formatearMoneda(valor));
        celda.setHorizontalAlignment(Element.ALIGN_RIGHT);

        return celda;
    }

    private PdfPCell celdaTotalTexto(String texto) {

        Font fuente = new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD, COLOR_PRIMARIO);
        PdfPCell celda = new PdfPCell(new Phrase(texto, fuente));

        celda.setBackgroundColor(COLOR_TOTAL);
        celda.setBorderColor(COLOR_BORDE);
        celda.setPaddingTop(7);
        celda.setPaddingBottom(7);
        celda.setPaddingLeft(6);

        return celda;
    }

    private PdfPCell celdaTotalMoneda(BigDecimal valor) {

        Font fuente = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, COLOR_PRIMARIO);

        PdfPCell celda = new PdfPCell(new Phrase(formatearMoneda(valor), fuente));

        celda.setBackgroundColor(COLOR_TOTAL);
        celda.setBorderColor(COLOR_BORDE);
        celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
        celda.setPaddingTop(7);
        celda.setPaddingBottom(7);
        celda.setPaddingRight(6);

        return celda;
    }

    private String formatearMoneda(BigDecimal valor) {
        BigDecimal monto = valor != null ? valor : BigDecimal.ZERO;

        return "$" + monto.setScale(2, RoundingMode.HALF_UP);
    }

    private static class FooterEvent extends PdfPageEventHelper {

        private final String fechaGeneracion;

        FooterEvent(String fechaGeneracion) {
            this.fechaGeneracion = fechaGeneracion;
        }

        @Override
        public void onEndPage(PdfWriter writer, Document document) {

            PdfContentByte contenido = writer.getDirectContent();
            BaseColor colorLinea = new BaseColor(203, 213, 225);
            contenido.setColorStroke(colorLinea);
            contenido.setLineWidth(0.6f);

            float yLinea = document.bottom() - 8;

            contenido.moveTo(document.leftMargin(), yLinea);
            contenido.lineTo(document.right(), yLinea);
            contenido.stroke();
            Font fuenteFooter = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL, BaseColor.GRAY);
            Phrase izquierda = new Phrase("CineMark - Sistema de Gestión de Cine", fuenteFooter);
            Phrase centro = new Phrase("Generado: " + fechaGeneracion, fuenteFooter);
            Phrase derecha = new Phrase("Página " + writer.getPageNumber(), fuenteFooter);
            float yTexto = document.bottom() - 22;

            ColumnText.showTextAligned(contenido, Element.ALIGN_LEFT, izquierda, document.leftMargin(), yTexto, 0);
            ColumnText.showTextAligned(contenido, Element.ALIGN_CENTER, centro,
                    (document.leftMargin() + document.right()) / 2, yTexto, 0);

            ColumnText.showTextAligned(contenido, Element.ALIGN_RIGHT, derecha, document.right(), yTexto, 0);
        }
    }
}
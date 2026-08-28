package com.proyecto.proyectoSpringBoot.service.impl;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.proyecto.proyectoSpringBoot.dto.response.ReporteStockResponse;
import com.proyecto.proyectoSpringBoot.mapper.AuditoriaMapper;
import com.proyecto.proyectoSpringBoot.model.entity.Auditoria;
import com.proyecto.proyectoSpringBoot.model.entity.Movimiento;
import com.proyecto.proyectoSpringBoot.model.enums.TipoOperacion;
import com.proyecto.proyectoSpringBoot.repository.AuditoriaRepository;
import com.proyecto.proyectoSpringBoot.repository.MovimientoRepository;
import com.proyecto.proyectoSpringBoot.service.interfaces.IReporteExportService;
import com.proyecto.proyectoSpringBoot.service.interfaces.IReporteService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReporteExportServiceImpl implements IReporteExportService {

    private final IReporteService reporteService;
    private final MovimientoRepository movimientoRepository;
    private final AuditoriaRepository auditoriaRepository;
    private final AuditoriaMapper auditoriaMapper;

    // ===== EXCEL: RESUMEN GENERAL =====
    @Override
    public byte[] exportarResumenExcel() {
        ReporteStockResponse reporte = reporteService.generarReporteGeneral();
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(headerFont);

            Sheet sheet1 = workbook.createSheet("Stock por Bodega");
            sheet1.setColumnWidth(0, 5000);
            sheet1.setColumnWidth(1, 6000);
            sheet1.setColumnWidth(2, 4000);
            Row titleRow1 = sheet1.createRow(0);
            Cell titleCell1 = titleRow1.createCell(0);
            titleCell1.setCellValue("LogiTrack - Stock por Bodega");
            titleCell1.setCellStyle(headerStyle);
            sheet1.addMergedRegion(new CellRangeAddress(0, 0, 0, 2));
            Row header1 = sheet1.createRow(1);
            String[] cols1 = {"ID Bodega", "Nombre Bodega", "Stock Total"};
            for (int i = 0; i < cols1.length; i++) {
                Cell c = header1.createCell(i);
                c.setCellValue(cols1[i]);
                c.setCellStyle(headerStyle);
            }
            int rowNum1 = 2;
            for (ReporteStockResponse.StockBodegaItem item : reporte.getStockPorBodega()) {
                Row row = sheet1.createRow(rowNum1++);
                row.createCell(0).setCellValue(item.getBodegaId());
                row.createCell(1).setCellValue(item.getBodegaNombre());
                row.createCell(2).setCellValue(item.getStockTotal());
            }

            Sheet sheet2 = workbook.createSheet("Productos Más Movidos");
            sheet2.setColumnWidth(0, 5000);
            sheet2.setColumnWidth(1, 8000);
            sheet2.setColumnWidth(2, 4000);
            Row titleRow2 = sheet2.createRow(0);
            Cell titleCell2 = titleRow2.createCell(0);
            titleCell2.setCellValue("LogiTrack - Productos Más Movidos");
            titleCell2.setCellStyle(headerStyle);
            sheet2.addMergedRegion(new CellRangeAddress(0, 0, 0, 2));
            Row header2 = sheet2.createRow(1);
            String[] cols2 = {"ID Producto", "Nombre Producto", "Total Movido"};
            for (int i = 0; i < cols2.length; i++) {
                Cell c = header2.createCell(i);
                c.setCellValue(cols2[i]);
                c.setCellStyle(headerStyle);
            }
            int rowNum2 = 2;
            for (ReporteStockResponse.ProductoMovidoItem item : reporte.getProductosMasMovidos()) {
                Row row = sheet2.createRow(rowNum2++);
                row.createCell(0).setCellValue(item.getProductoId());
                row.createCell(1).setCellValue(item.getProductoNombre());
                row.createCell(2).setCellValue(item.getTotalMovido());
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generando Excel: " + e.getMessage(), e);
        }
    }

    // ===== PDF: RESUMEN GENERAL =====
    @Override
    public byte[] exportarResumenPdf() {
        ReporteStockResponse reporte = reporteService.generarReporteGeneral();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);
        try {
            PdfWriter.getInstance(document, out);
            document.open();
            Font titleFont = new Font(Font.HELVETICA, 18, Font.BOLD, new Color(31, 97, 141));
            Font headerFont = new Font(Font.HELVETICA, 10, Font.BOLD, Color.WHITE);
            Font bodyFont = new Font(Font.HELVETICA, 10, Font.NORMAL, Color.BLACK);
            Font subtitleFont = new Font(Font.HELVETICA, 13, Font.BOLD, new Color(44, 62, 80));

            Paragraph title = new Paragraph("LogiTrack S.A. — Reporte General de Stock", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(5);
            document.add(title);
            Paragraph fecha = new Paragraph("Generado: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")), new Font(Font.HELVETICA, 9, Font.ITALIC, Color.GRAY));
            fecha.setAlignment(Element.ALIGN_CENTER);
            fecha.setSpacingAfter(20);
            document.add(fecha);

            Paragraph sub1 = new Paragraph("Stock por Bodega", subtitleFont);
            sub1.setSpacingAfter(8);
            document.add(sub1);
            PdfPTable table1 = new PdfPTable(3);
            table1.setWidthPercentage(100);
            table1.setWidths(new float[]{1f, 3f, 2f});
            String[] h1 = {"ID", "Bodega", "Stock Total"};
            for (String h : h1) {
                PdfPCell cell = new PdfPCell(new Phrase(h, headerFont));
                cell.setBackgroundColor(new Color(31, 97, 141));
                cell.setPadding(6);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table1.addCell(cell);
            }
            boolean alternate = false;
            for (ReporteStockResponse.StockBodegaItem item : reporte.getStockPorBodega()) {
                Color bg = alternate ? new Color(235, 245, 251) : Color.WHITE;
                PdfPCell c1 = new PdfPCell(new Phrase(String.valueOf(item.getBodegaId()), bodyFont));
                PdfPCell c2 = new PdfPCell(new Phrase(item.getBodegaNombre(), bodyFont));
                PdfPCell c3 = new PdfPCell(new Phrase(String.valueOf(item.getStockTotal()), bodyFont));
                for (PdfPCell c : new PdfPCell[]{c1, c2, c3}) {
                    c.setBackgroundColor(bg);
                    c.setPadding(5);
                }
                c3.setHorizontalAlignment(Element.ALIGN_CENTER);
                table1.addCell(c1); table1.addCell(c2); table1.addCell(c3);
                alternate = !alternate;
            }
            document.add(table1);
            document.add(new Paragraph(" "));

            Paragraph sub2 = new Paragraph("Productos Más Movidos", subtitleFont);
            sub2.setSpacingAfter(8);
            sub2.setSpacingBefore(15);
            document.add(sub2);
            PdfPTable table2 = new PdfPTable(3);
            table2.setWidthPercentage(100);
            table2.setWidths(new float[]{1f, 3f, 2f});
            String[] h2 = {"ID", "Producto", "Total Movido"};
            for (String h : h2) {
                PdfPCell cell = new PdfPCell(new Phrase(h, headerFont));
                cell.setBackgroundColor(new Color(31, 97, 141));
                cell.setPadding(6);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table2.addCell(cell);
            }
            alternate = false;
            for (ReporteStockResponse.ProductoMovidoItem item : reporte.getProductosMasMovidos()) {
                Color bg = alternate ? new Color(235, 245, 251) : Color.WHITE;
                PdfPCell c1 = new PdfPCell(new Phrase(String.valueOf(item.getProductoId()), bodyFont));
                PdfPCell c2 = new PdfPCell(new Phrase(item.getProductoNombre(), bodyFont));
                PdfPCell c3 = new PdfPCell(new Phrase(String.valueOf(item.getTotalMovido()), bodyFont));
                for (PdfPCell c : new PdfPCell[]{c1, c2, c3}) {
                    c.setBackgroundColor(bg);
                    c.setPadding(5);
                }
                c3.setHorizontalAlignment(Element.ALIGN_CENTER);
                table2.addCell(c1); table2.addCell(c2); table2.addCell(c3);
                alternate = !alternate;
            }
            document.add(table2);
            document.close();
        } catch (Exception e) {
            throw new RuntimeException("Error generando PDF: " + e.getMessage(), e);
        }
        return out.toByteArray();
    }

    // ===== EXCEL: MOVIMIENTOS POR RANGO DE FECHAS =====
    @Override
    public byte[] exportarMovimientosExcel(LocalDate desde, LocalDate hasta) {
        LocalDateTime inicio = desde.atStartOfDay();
        LocalDateTime fin = hasta.plusDays(1).atStartOfDay();
        List<Movimiento> movimientos = movimientoRepository.findByFechaBetween(inicio, fin);
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            org.apache.poi.ss.usermodel.Font hf = workbook.createFont();
            hf.setBold(true); hf.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(hf);

            Sheet sheet = workbook.createSheet("Movimientos");
            int[] widths = {3000, 5000, 6000, 6000, 6000, 8000};
            for (int i = 0; i < widths.length; i++) sheet.setColumnWidth(i, widths[i]);

            Row titleRow = sheet.createRow(0);
            Cell tc = titleRow.createCell(0);
            tc.setCellValue("LogiTrack - Movimientos " + desde + " a " + hasta);
            tc.setCellStyle(headerStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 5));

            Row header = sheet.createRow(1);
            String[] cols = {"ID", "Tipo", "Bodega Origen", "Bodega Destino", "Usuario", "Fecha"};
            for (int i = 0; i < cols.length; i++) {
                Cell c = header.createCell(i);
                c.setCellValue(cols[i]);
                c.setCellStyle(headerStyle);
            }
            int rowNum = 2;
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            for (Movimiento m : movimientos) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(m.getId());
                row.createCell(1).setCellValue(m.getTipoMovimiento().name());
                row.createCell(2).setCellValue(m.getBodegaOrigen() != null ? m.getBodegaOrigen().getNombre() : "-");
                row.createCell(3).setCellValue(m.getBodegaDestino() != null ? m.getBodegaDestino().getNombre() : "-");
                row.createCell(4).setCellValue(m.getUsuario() != null ? m.getUsuario().getNombre() + " " + m.getUsuario().getApellido() : "-");
                row.createCell(5).setCellValue(m.getFecha() != null ? m.getFecha().format(fmt) : "-");
            }
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generando Excel movimientos: " + e.getMessage(), e);
        }
    }

    // ===== PDF: MOVIMIENTOS POR RANGO DE FECHAS =====
    @Override
    public byte[] exportarMovimientosPdf(LocalDate desde, LocalDate hasta) {
        LocalDateTime inicio = desde.atStartOfDay();
        LocalDateTime fin = hasta.plusDays(1).atStartOfDay();
        List<Movimiento> movimientos = movimientoRepository.findByFechaBetween(inicio, fin);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4.rotate());
        try {
            PdfWriter.getInstance(document, out);
            document.open();
            Font titleFont = new Font(Font.HELVETICA, 16, Font.BOLD, new Color(31, 97, 141));
            Font headerFont = new Font(Font.HELVETICA, 9, Font.BOLD, Color.WHITE);
            Font bodyFont = new Font(Font.HELVETICA, 8, Font.NORMAL, Color.BLACK);

            Paragraph title = new Paragraph("LogiTrack — Movimientos del " + desde + " al " + hasta, titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(15);
            document.add(title);

            PdfPTable table = new PdfPTable(6);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{0.5f, 1.5f, 2f, 2f, 2f, 2f});
            String[] headers = {"ID", "Tipo", "Bodega Origen", "Bodega Destino", "Usuario", "Fecha"};
            for (String h : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(h, headerFont));
                cell.setBackgroundColor(new Color(31, 97, 141));
                cell.setPadding(5);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);
            }
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            boolean alt = false;
            for (Movimiento m : movimientos) {
                Color bg = alt ? new Color(235, 245, 251) : Color.WHITE;
                String[] vals = {
                    String.valueOf(m.getId()),
                    m.getTipoMovimiento().name(),
                    m.getBodegaOrigen() != null ? m.getBodegaOrigen().getNombre() : "-",
                    m.getBodegaDestino() != null ? m.getBodegaDestino().getNombre() : "-",
                    m.getUsuario() != null ? m.getUsuario().getNombre() : "-",
                    m.getFecha() != null ? m.getFecha().format(fmt) : "-"
                };
                for (String v : vals) {
                    PdfPCell c = new PdfPCell(new Phrase(v, bodyFont));
                    c.setBackgroundColor(bg);
                    c.setPadding(4);
                    table.addCell(c);
                }
                alt = !alt;
            }
            document.add(table);
            document.close();
        } catch (Exception e) {
            throw new RuntimeException("Error generando PDF movimientos: " + e.getMessage(), e);
        }
        return out.toByteArray();
    }

    // ===== EXCEL: LOGS DE AUDITORÍA ORGANIZADOS EN MÚLTIPLES HOJAS =====
    @Override
    public byte[] exportarAuditoriasExcel() {
        List<Auditoria> auditorias = auditoriaRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            // Estilo Encabezado Corporativo
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            org.apache.poi.ss.usermodel.Font hf = workbook.createFont();
            hf.setBold(true); hf.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(hf);

            // Estilo Subtítulos
            CellStyle subStyle = workbook.createCellStyle();
            subStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
            subStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            org.apache.poi.ss.usermodel.Font sf = workbook.createFont();
            sf.setBold(true); sf.setColor(IndexedColors.WHITE.getIndex());
            subStyle.setFont(sf);

            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

            // ===== HOJA 1: RESUMEN KPI DE AUDITORÍA =====
            Sheet sheetSummary = workbook.createSheet("Resumen Ejecutivo");
            sheetSummary.setColumnWidth(0, 8000);
            sheetSummary.setColumnWidth(1, 6000);

            Row r0 = sheetSummary.createRow(0);
            Cell c0 = r0.createCell(0);
            c0.setCellValue("LOGITRACK S.A. - RESUMEN DE AUDITORÍA Y CONTROL DE CAMBIOS");
            c0.setCellStyle(headerStyle);
            sheetSummary.addMergedRegion(new CellRangeAddress(0, 0, 0, 1));

            long countInserts = auditorias.stream().filter(a -> a.getTipoOperacion() == TipoOperacion.INSERT).count();
            long countUpdates = auditorias.stream().filter(a -> a.getTipoOperacion() == TipoOperacion.UPDATE).count();
            long countDeletes = auditorias.stream().filter(a -> a.getTipoOperacion() == TipoOperacion.DELETE).count();
            long countProductos = auditorias.stream().filter(a -> "Producto".equalsIgnoreCase(a.getEntidad())).count();
            long countBodegas = auditorias.stream().filter(a -> "Bodega".equalsIgnoreCase(a.getEntidad())).count();
            long countMovimientos = auditorias.stream().filter(a -> "Movimiento".equalsIgnoreCase(a.getEntidad())).count();

            String[][] summaryData = {
                {"Métrica de Auditoría", "Valor Registrado"},
                {"Total de Eventos Registrados", String.valueOf(auditorias.size())},
                {"Eventos de Creación (INSERT)", String.valueOf(countInserts)},
                {"Eventos de Modificación (UPDATE)", String.valueOf(countUpdates)},
                {"Eventos de Eliminación (DELETE)", String.valueOf(countDeletes)},
                {"Auditorías en Productos", String.valueOf(countProductos)},
                {"Auditorías en Bodegas", String.valueOf(countBodegas)},
                {"Auditorías en Movimientos", String.valueOf(countMovimientos)},
                {"Fecha de Generación del Informe", LocalDateTime.now().format(fmt)}
            };

            for (int i = 0; i < summaryData.length; i++) {
                Row row = sheetSummary.createRow(i + 2);
                Cell c1 = row.createCell(0);
                Cell c2 = row.createCell(1);
                c1.setCellValue(summaryData[i][0]);
                c2.setCellValue(summaryData[i][1]);

                if (i == 0) {
                    c1.setCellStyle(subStyle);
                    c2.setCellStyle(subStyle);
                }
            }

            // ===== HOJA 2: AUDITORÍA DE PRODUCTOS =====
            List<Auditoria> auditoriasProd = auditoriaRepository.findAll().stream()
                    .filter(a -> "Producto".equalsIgnoreCase(a.getEntidad()))
                    .sorted((a1, a2) -> a2.getId().compareTo(a1.getId()))
                    .collect(Collectors.toList());
            buildAuditoriaSheet(workbook, "Auditoría de Productos", auditoriasProd, headerStyle, fmt);

            // ===== HOJA 3: AUDITORÍA DE BODEGAS =====
            List<Auditoria> auditoriasBod = auditoriaRepository.findAll().stream()
                    .filter(a -> "Bodega".equalsIgnoreCase(a.getEntidad()))
                    .sorted((a1, a2) -> a2.getId().compareTo(a1.getId()))
                    .collect(Collectors.toList());
            buildAuditoriaSheet(workbook, "Auditoría de Bodegas", auditoriasBod, headerStyle, fmt);

            // ===== HOJA 4: AUDITORÍA DE MOVIMIENTOS =====
            List<Auditoria> auditoriasMov = auditoriaRepository.findAll().stream()
                    .filter(a -> "Movimiento".equalsIgnoreCase(a.getEntidad()))
                    .sorted((a1, a2) -> a2.getId().compareTo(a1.getId()))
                    .collect(Collectors.toList());
            buildAuditoriaSheet(workbook, "Auditoría de Movimientos", auditoriasMov, headerStyle, fmt);

            // ===== HOJA 5: MASTER LOG COMPLETO =====
            buildAuditoriaSheet(workbook, "Master Log Completo", auditorias, headerStyle, fmt);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generando Excel de auditorías: " + e.getMessage(), e);
        }
    }

    private void buildAuditoriaSheet(XSSFWorkbook workbook, String sheetName, List<Auditoria> auditorias, CellStyle headerStyle, DateTimeFormatter fmt) {
        Sheet sheet = workbook.createSheet(sheetName);
        int[] widths = {2500, 5500, 3500, 8000, 4000, 6500, 4500, 12000, 10000, 10000};
        for (int i = 0; i < widths.length; i++) sheet.setColumnWidth(i, widths[i]);

        Row titleRow = sheet.createRow(0);
        Cell tc = titleRow.createCell(0);
        tc.setCellValue("LogiTrack S.A. - " + sheetName);
        tc.setCellStyle(headerStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 9));

        Row header = sheet.createRow(1);
        String[] cols = {"ID Audit", "Fecha y Hora", "Entidad", "Nombre Recurso / Producto", "Operación", "Usuario Responsable", "Dirección IP", "Descripción de Cambios", "Valores Anteriores (JSON)", "Valores Nuevos (JSON)"};
        for (int i = 0; i < cols.length; i++) {
            Cell c = header.createCell(i);
            c.setCellValue(cols[i]);
            c.setCellStyle(headerStyle);
        }

        int rowNum = 2;
        for (Auditoria a : auditorias) {
            var resp = auditoriaMapper.toResponse(a);
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(a.getId());
            row.createCell(1).setCellValue(a.getFechaHora() != null ? a.getFechaHora().format(fmt) : "-");
            row.createCell(2).setCellValue(a.getEntidad() != null ? a.getEntidad() : "-");
            row.createCell(3).setCellValue(resp.getRecursoNombre() != null ? resp.getRecursoNombre() : "-");
            row.createCell(4).setCellValue(a.getTipoOperacion() != null ? a.getTipoOperacion().name() : "-");
            row.createCell(5).setCellValue(a.getUsuario() != null ? a.getUsuario().getEmail() : "SISTEMA");
            row.createCell(6).setCellValue(a.getIpAddress() != null ? a.getIpAddress() : "127.0.0.1");
            row.createCell(7).setCellValue(a.getDescripcion() != null ? a.getDescripcion() : "-");
            row.createCell(8).setCellValue(a.getValoresAnteriores() != null ? a.getValoresAnteriores() : "-");
            row.createCell(9).setCellValue(a.getValoresNuevos() != null ? a.getValoresNuevos() : "-");
        }
    }

    // ===== PDF: LOGS DE AUDITORÍA DETALLADOS Y ENRIQUECIDOS =====
    @Override
    public byte[] exportarAuditoriasPdf() {
        List<Auditoria> auditorias = auditoriaRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4.rotate(), 20, 20, 25, 25);
        try {
            PdfWriter.getInstance(document, out);
            document.open();
            Font titleFont = new Font(Font.HELVETICA, 16, Font.BOLD, new Color(31, 97, 141));
            Font headerFont = new Font(Font.HELVETICA, 8, Font.BOLD, Color.WHITE);
            Font bodyFont = new Font(Font.HELVETICA, 7, Font.NORMAL, Color.BLACK);
            Font bodyBold = new Font(Font.HELVETICA, 7, Font.BOLD, Color.BLACK);
            Font subtitleFont = new Font(Font.HELVETICA, 8, Font.ITALIC, Color.DARK_GRAY);

            Paragraph title = new Paragraph("LOGITRACK S.A. — INFORME OFICIAL DE AUDITORÍA Y TRAZABILIDAD", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(4);
            document.add(title);

            Paragraph fecha = new Paragraph("Generado el: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) + " | Total de Registros Evaluados: " + auditorias.size(), subtitleFont);
            fecha.setAlignment(Element.ALIGN_CENTER);
            fecha.setSpacingAfter(14);
            document.add(fecha);

            PdfPTable table = new PdfPTable(8);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{0.6f, 1.4f, 1.1f, 2.2f, 1.1f, 2.0f, 1.2f, 3.4f});
            String[] headers = {"ID", "Fecha/Hora", "Entidad", "Nombre Recurso / Producto", "Operación", "Usuario Responsable", "IP Client", "Descripción Completa de Modificaciones"};
            for (String h : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(h, headerFont));
                cell.setBackgroundColor(new Color(31, 97, 141));
                cell.setPadding(5);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);
            }

            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            for (Auditoria a : auditorias) {
                var resp = auditoriaMapper.toResponse(a);
                Color bg = Color.WHITE;
                if (a.getTipoOperacion() != null) {
                    switch (a.getTipoOperacion()) {
                        case INSERT -> bg = new Color(232, 248, 245);
                        case UPDATE -> bg = new Color(254, 249, 231);
                        case DELETE -> bg = new Color(250, 219, 216);
                    }
                }

                PdfPCell c1 = new PdfPCell(new Phrase(String.valueOf(a.getId()), bodyBold));
                PdfPCell c2 = new PdfPCell(new Phrase(a.getFechaHora() != null ? a.getFechaHora().format(fmt) : "-", bodyFont));
                PdfPCell c3 = new PdfPCell(new Phrase(a.getEntidad() != null ? a.getEntidad() : "-", bodyBold));
                PdfPCell c4 = new PdfPCell(new Phrase(resp.getRecursoNombre() != null ? resp.getRecursoNombre() : "-", bodyBold));
                PdfPCell c5 = new PdfPCell(new Phrase(a.getTipoOperacion() != null ? a.getTipoOperacion().name() : "-", bodyBold));
                PdfPCell c6 = new PdfPCell(new Phrase(a.getUsuario() != null ? a.getUsuario().getEmail() : "SISTEMA", bodyFont));
                PdfPCell c7 = new PdfPCell(new Phrase(a.getIpAddress() != null ? a.getIpAddress() : "127.0.0.1", bodyFont));
                PdfPCell c8 = new PdfPCell(new Phrase(a.getDescripcion() != null ? a.getDescripcion() : "-", bodyFont));

                for (PdfPCell c : new PdfPCell[]{c1, c2, c3, c4, c5, c6, c7, c8}) {
                    c.setBackgroundColor(bg);
                    c.setPadding(4);
                }
                c1.setHorizontalAlignment(Element.ALIGN_CENTER);
                c5.setHorizontalAlignment(Element.ALIGN_CENTER);

                table.addCell(c1); table.addCell(c2); table.addCell(c3); table.addCell(c4);
                table.addCell(c5); table.addCell(c6); table.addCell(c7); table.addCell(c8);
            }
            document.add(table);
            document.close();
        } catch (Exception e) {
            throw new RuntimeException("Error generando PDF de auditorías: " + e.getMessage(), e);
        }
        return out.toByteArray();
    }
}

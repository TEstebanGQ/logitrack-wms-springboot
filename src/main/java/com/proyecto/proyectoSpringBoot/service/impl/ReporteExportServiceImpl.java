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
import com.proyecto.proyectoSpringBoot.model.entity.Movimiento;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReporteExportServiceImpl implements IReporteExportService {

    private final IReporteService reporteService;
    private final MovimientoRepository movimientoRepository;

    // ===== EXCEL: RESUMEN GENERAL =====
    @Override
    public byte[] exportarResumenExcel() {
        ReporteStockResponse reporte = reporteService.generarReporteGeneral();
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            // Estilo encabezado
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(headerFont);

            // Hoja 1: Stock por Bodega
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

            // Hoja 2: Productos más movidos
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
            // Fuentes
            Font titleFont = new Font(Font.HELVETICA, 18, Font.BOLD, new Color(31, 97, 141));
            Font headerFont = new Font(Font.HELVETICA, 10, Font.BOLD, Color.WHITE);
            Font bodyFont = new Font(Font.HELVETICA, 10, Font.NORMAL, Color.BLACK);
            Font subtitleFont = new Font(Font.HELVETICA, 13, Font.BOLD, new Color(44, 62, 80));

            // Título
            Paragraph title = new Paragraph("LogiTrack S.A. — Reporte General de Stock", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(5);
            document.add(title);
            Paragraph fecha = new Paragraph("Generado: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")), new Font(Font.HELVETICA, 9, Font.ITALIC, Color.GRAY));
            fecha.setAlignment(Element.ALIGN_CENTER);
            fecha.setSpacingAfter(20);
            document.add(fecha);

            // Tabla Stock por Bodega
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

            // Tabla Productos más movidos
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
}

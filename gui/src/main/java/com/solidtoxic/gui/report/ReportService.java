package com.solidtoxic.gui.report;

import com.fasterxml.jackson.core.type.TypeReference;
import com.solidtoxic.gui.client.ApiClient;
import com.solidtoxic.gui.client.ApiResponse;
import com.solidtoxic.gui.client.BackendUnavailableException;
import com.solidtoxic.gui.util.AlertUtil;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

import java.awt.Color;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * Builds and saves a PDF report for a master table.
 *
 * Layout:
 *   - A4 landscape
 *   - Title + generated-at timestamp
 *   - Table with header row (grey background) and data rows (alternating white / light-blue)
 *   - State column colour-coded: A=green, I=orange, *=red
 *   - Automatic column width proportional to header text length
 *   - Rows that exceed the page height start a new page automatically
 */
public class ReportService {

    // ── Fonts ─────────────────────────────────────────────────────────────────
    private static final PDType1Font FONT_BOLD    = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
    private static final PDType1Font FONT_REGULAR = new PDType1Font(Standard14Fonts.FontName.HELVETICA);

    // ── Layout constants ──────────────────────────────────────────────────────
    private static final float MARGIN          = 36f;
    private static final float TITLE_SIZE      = 14f;
    private static final float HEADER_SIZE     = 9f;
    private static final float CELL_SIZE       = 8.5f;
    private static final float ROW_HEIGHT      = 16f;
    private static final float HEADER_HEIGHT   = 18f;
    private static final float TITLE_GAP       = 28f;  // space below title before table

    // ── Colours ───────────────────────────────────────────────────────────────
    private static final Color COL_HEADER      = new Color(60,  90, 130);
    private static final Color COL_ROW_EVEN    = Color.WHITE;
    private static final Color COL_ROW_ODD     = new Color(235, 242, 252);
    private static final Color COL_STATE_A     = new Color(56, 142, 60);
    private static final Color COL_STATE_I     = new Color(230, 119, 0);
    private static final Color COL_STATE_DEL   = new Color(198, 40, 40);
    private static final Color COL_TEXT_LIGHT  = Color.WHITE;
    private static final Color COL_TEXT_DARK   = new Color(30, 30, 30);
    private static final Color COL_GRID        = new Color(180, 180, 180);

    private final ApiClient api = ApiClient.getInstance();

    // ─────────────────────────────────────────────────────────────────────────
    // Public entry point
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Fetches all records from {@code apiPath}, renders a PDF, and prompts the user
     * to save it via a FileChooser.
     *
     * @param owner      JavaFX window owner for the FileChooser dialog
     * @param title      Report title shown at the top of the PDF
     * @param apiPath    e.g. "/api/v1/productora"
     * @param columns    ordered list of column descriptors (key → header label)
     */
    public void exportToPdf(Window owner, String title, String apiPath,
                            List<ColumnDef> columns) {
        // 1. Fetch data
        List<Map<String, Object>> rows;
        try {
            ApiResponse resp = api.get(apiPath);
            if (!resp.isSuccess()) {
                AlertUtil.showError("Report Error", "Failed to fetch data: " + resp.getBody());
                return;
            }
            rows = api.getMapper().readValue(resp.getBody(), new TypeReference<>() {});
        } catch (BackendUnavailableException e) {
            AlertUtil.showError("Connection Error", "Cannot reach backend: " + e.getMessage());
            return;
        } catch (Exception e) {
            AlertUtil.showError("Report Error", "Error reading data: " + e.getMessage());
            return;
        }

        // 2. Ask where to save
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Save Report as PDF");
        chooser.setInitialFileName(sanitizeFilename(title) + "_"
                + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmm"))
                + ".pdf");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        File target = chooser.showSaveDialog(owner);
        if (target == null) return; // user cancelled

        // 3. Build PDF
        try {
            buildPdf(target, title, columns, rows);
            AlertUtil.showInfo("Report saved to:\n" + target.getAbsolutePath());
        } catch (Exception e) {
            AlertUtil.showError("PDF Error", "Could not write PDF: " + e.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // PDF building
    // ─────────────────────────────────────────────────────────────────────────

    private void buildPdf(File target, String title,
                          List<ColumnDef> columns,
                          List<Map<String, Object>> rows) throws Exception {

        try (PDDocument doc = new PDDocument()) {

            // Compute column widths proportional to header label length,
            // then scaled to fill the printable width.
            float pageWidth  = PDRectangle.A4.getHeight(); // landscape
            float pageHeight = PDRectangle.A4.getWidth();
            float printWidth = pageWidth - 2 * MARGIN;

            float[] colWidths = computeColumnWidths(columns, printWidth);

            // First page
            float[] cursor = { 0f }; // cursor[0] = current Y from top (we'll track as remaining space)
            PDPage page = newPage(doc, pageWidth, pageHeight);
            PDPageContentStream cs = newContentStream(doc, page);

            // Title
            float topY = pageHeight - MARGIN;
            drawTitle(cs, title, pageWidth, topY);

            String timestamp = "Generated: "
                    + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            drawSubtitle(cs, timestamp, MARGIN, topY - 16f);

            float tableTop = topY - TITLE_GAP;
            cursor[0] = tableTop;

            // Header row
            cs = drawHeaderRow(doc, cs, page, columns, colWidths,
                    MARGIN, cursor, pageWidth, pageHeight);

            // Data rows
            int rowIndex = 0;
            for (Map<String, Object> row : rows) {
                // New page if needed
                if (cursor[0] - ROW_HEIGHT < MARGIN) {
                    cs.close();
                    page = newPage(doc, pageWidth, pageHeight);
                    cs = newContentStream(doc, page);
                    cursor[0] = pageHeight - MARGIN;
                    cs = drawHeaderRow(doc, cs, page, columns, colWidths,
                            MARGIN, cursor, pageWidth, pageHeight);
                }

                Color bg = (rowIndex % 2 == 0) ? COL_ROW_EVEN : COL_ROW_ODD;
                drawDataRow(cs, row, columns, colWidths, MARGIN, cursor[0], bg);
                cursor[0] -= ROW_HEIGHT;
                rowIndex++;
            }

            // Record count footer
            if (cursor[0] - 14f > MARGIN) {
                drawFooter(cs, rows.size() + " record(s)", MARGIN, cursor[0] - 10f);
            }

            cs.close();
            doc.save(target);
        }
    }

    // ── Title / subtitle / footer ─────────────────────────────────────────────

    private void drawTitle(PDPageContentStream cs, String text,
                           float pageWidth, float y) throws Exception {
        cs.beginText();
        cs.setFont(FONT_BOLD, TITLE_SIZE);
        cs.setNonStrokingColor(COL_TEXT_DARK);
        float tw = FONT_BOLD.getStringWidth(text) / 1000f * TITLE_SIZE;
        cs.newLineAtOffset((pageWidth - tw) / 2f, y);
        cs.showText(text);
        cs.endText();
    }

    private void drawSubtitle(PDPageContentStream cs, String text,
                               float x, float y) throws Exception {
        cs.beginText();
        cs.setFont(FONT_REGULAR, 7.5f);
        cs.setNonStrokingColor(new Color(100, 100, 100));
        cs.newLineAtOffset(x, y);
        cs.showText(text);
        cs.endText();
    }

    private void drawFooter(PDPageContentStream cs, String text,
                             float x, float y) throws Exception {
        cs.beginText();
        cs.setFont(FONT_REGULAR, 7.5f);
        cs.setNonStrokingColor(new Color(100, 100, 100));
        cs.newLineAtOffset(x, y);
        cs.showText(text);
        cs.endText();
    }

    // ── Header row ────────────────────────────────────────────────────────────

    private PDPageContentStream drawHeaderRow(PDDocument doc,
                                               PDPageContentStream cs,
                                               PDPage page,
                                               List<ColumnDef> columns,
                                               float[] colWidths,
                                               float startX,
                                               float[] cursor,
                                               float pageWidth,
                                               float pageHeight) throws Exception {
        float y = cursor[0];
        float x = startX;

        for (int i = 0; i < columns.size(); i++) {
            // Fill cell background
            cs.setNonStrokingColor(COL_HEADER);
            cs.addRect(x, y - HEADER_HEIGHT, colWidths[i], HEADER_HEIGHT);
            cs.fill();

            // Grid border
            cs.setStrokingColor(COL_GRID);
            cs.setLineWidth(0.4f);
            cs.addRect(x, y - HEADER_HEIGHT, colWidths[i], HEADER_HEIGHT);
            cs.stroke();

            // Header text
            cs.beginText();
            cs.setFont(FONT_BOLD, HEADER_SIZE);
            cs.setNonStrokingColor(COL_TEXT_LIGHT);
            cs.newLineAtOffset(x + 3f, y - HEADER_HEIGHT + 5f);
            cs.showText(clip(columns.get(i).header(), colWidths[i] - 6f, FONT_BOLD, HEADER_SIZE));
            cs.endText();

            x += colWidths[i];
        }
        cursor[0] = y - HEADER_HEIGHT;
        return cs;
    }

    // ── Data row ──────────────────────────────────────────────────────────────

    private void drawDataRow(PDPageContentStream cs,
                              Map<String, Object> row,
                              List<ColumnDef> columns,
                              float[] colWidths,
                              float startX,
                              float topY,
                              Color bg) throws Exception {
        float x = startX;
        for (int i = 0; i < columns.size(); i++) {
            String key = columns.get(i).key();
            Object val = row.get(key);
            String text = val != null ? val.toString() : "";

            // Cell background — special colour for estReg column
            Color cellBg = bg;
            Color textColor = COL_TEXT_DARK;
            if ("estReg".equals(key)) {
                cellBg = switch (text) {
                    case "A" -> COL_STATE_A;
                    case "I" -> COL_STATE_I;
                    case "*" -> COL_STATE_DEL;
                    default  -> bg;
                };
                textColor = COL_TEXT_LIGHT;
                text = switch (text) {
                    case "A" -> "Active";
                    case "I" -> "Inactive";
                    case "*" -> "Deleted";
                    default  -> text;
                };
            }

            cs.setNonStrokingColor(cellBg);
            cs.addRect(x, topY - ROW_HEIGHT, colWidths[i], ROW_HEIGHT);
            cs.fill();

            cs.setStrokingColor(COL_GRID);
            cs.setLineWidth(0.3f);
            cs.addRect(x, topY - ROW_HEIGHT, colWidths[i], ROW_HEIGHT);
            cs.stroke();

            cs.beginText();
            cs.setFont(FONT_REGULAR, CELL_SIZE);
            cs.setNonStrokingColor(textColor);
            cs.newLineAtOffset(x + 3f, topY - ROW_HEIGHT + 4.5f);
            cs.showText(clip(text, colWidths[i] - 6f, FONT_REGULAR, CELL_SIZE));
            cs.endText();

            x += colWidths[i];
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    /** Distributes printWidth among columns weighted by header text length (min 40px). */
    private float[] computeColumnWidths(List<ColumnDef> columns, float printWidth) {
        float[] weights = new float[columns.size()];
        float total = 0f;
        for (int i = 0; i < columns.size(); i++) {
            weights[i] = Math.max(40f, columns.get(i).header().length() * 7f);
            total += weights[i];
        }
        float[] widths = new float[columns.size()];
        for (int i = 0; i < columns.size(); i++) {
            widths[i] = printWidth * weights[i] / total;
        }
        return widths;
    }

    /** Clips text so it fits within maxWidth pixels at the given font size. */
    private String clip(String text, float maxWidth, PDType1Font font, float size) {
        try {
            while (text.length() > 1) {
                float w = font.getStringWidth(text) / 1000f * size;
                if (w <= maxWidth) return text;
                text = text.substring(0, text.length() - 1);
            }
        } catch (Exception ignored) {}
        return text;
    }

    private PDPage newPage(PDDocument doc, float width, float height) {
        PDPage p = new PDPage(new PDRectangle(width, height));
        doc.addPage(p);
        return p;
    }

    private PDPageContentStream newContentStream(PDDocument doc, PDPage page) throws Exception {
        return new PDPageContentStream(doc, page,
                PDPageContentStream.AppendMode.OVERWRITE, true, true);
    }

    private String sanitizeFilename(String s) {
        return s.replaceAll("[^a-zA-Z0-9_\\-]", "_");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Column definition
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Maps a JSON key from the API response to a column header label in the PDF.
     */
    public record ColumnDef(String key, String header) {}
}

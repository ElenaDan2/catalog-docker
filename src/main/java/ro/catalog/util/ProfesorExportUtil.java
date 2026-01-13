package ro.catalog.util;

import ro.catalog.model.AuthUser;
import ro.catalog.model.ProfesorAbsentaView;
import ro.catalog.model.ProfesorNotaView;
import ro.catalog.model.ProfesorRaportStudentRow;
import ro.catalog.model.ProfesorRaportSummary;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

public class ProfesorExportUtil {

    private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // =========================
    // XLS (HTML)
    // =========================

    public static void exportNoteXls(HttpServletResponse resp, AuthUser u, String materie,
                                     LocalDate from, LocalDate to, List<ProfesorNotaView> note) {
        try {
            resp.setCharacterEncoding("UTF-8");
            resp.setContentType("application/vnd.ms-excel");
            resp.setHeader("Content-Disposition", "attachment; filename=\"note_" + safe(materie) + "_" + from + "_" + to + ".xls\"");

            StringBuilder sb = new StringBuilder();
            sb.append("<html><head><meta charset='UTF-8'></head><body>");
            sb.append("<h2>Note - ").append(esc(materie)).append("</h2>");
            sb.append("<div>Profesor: ").append(esc(u.getNume())).append(" (").append(esc(u.getEmail())).append(")</div>");
            sb.append("<div>Perioada: ").append(from).append(" -> ").append(to).append("</div><br/>");

            sb.append("<table border='1' cellspacing='0' cellpadding='6'>");
            sb.append("<tr><th>Data</th><th>Student</th><th>Clasa</th><th>Materie</th><th>Nota</th></tr>");
            for (ProfesorNotaView n : note) {
                sb.append("<tr>")
                  .append("<td>").append(n.getData() == null ? "" : n.getData().format(DF)).append("</td>")
                  .append("<td>").append(esc(n.getStudentNume())).append("</td>")
                  .append("<td>").append(esc(n.getClasaNume())).append("</td>")
                  .append("<td>").append(esc(n.getMaterie())).append("</td>")
                  .append("<td>").append(n.getValoare()).append("</td>")
                  .append("</tr>");
            }
            sb.append("</table>");

            sb.append("</body></html>");

            byte[] bytes = sb.toString().getBytes(StandardCharsets.UTF_8);
            OutputStream os = resp.getOutputStream();
            os.write(bytes);
            os.flush();
        } catch (Exception e) {
            throw new RuntimeException("ProfesorExportUtil.exportNoteXls()", e);
        }
    }

    public static void exportAbsenteXls(HttpServletResponse resp, AuthUser u, String materie,
                                        LocalDate from, LocalDate to, List<ProfesorAbsentaView> abs) {
        try {
            resp.setCharacterEncoding("UTF-8");
            resp.setContentType("application/vnd.ms-excel");
            resp.setHeader("Content-Disposition", "attachment; filename=\"absente_" + safe(materie) + "_" + from + "_" + to + ".xls\"");

            StringBuilder sb = new StringBuilder();
            sb.append("<html><head><meta charset='UTF-8'></head><body>");
            sb.append("<h2>Absente - ").append(esc(materie)).append("</h2>");
            sb.append("<div>Profesor: ").append(esc(u.getNume())).append(" (").append(esc(u.getEmail())).append(")</div>");
            sb.append("<div>Perioada: ").append(from).append(" -> ").append(to).append("</div><br/>");

            sb.append("<table border='1' cellspacing='0' cellpadding='6'>");
            sb.append("<tr><th>Data</th><th>Student</th><th>Clasa</th><th>Materie</th><th>Status</th></tr>");
            for (ProfesorAbsentaView a : abs) {
                sb.append("<tr>")
                  .append("<td>").append(a.getData() == null ? "" : a.getData().format(DF)).append("</td>")
                  .append("<td>").append(esc(a.getStudentNume())).append("</td>")
                  .append("<td>").append(esc(a.getClasaNume())).append("</td>")
                  .append("<td>").append(esc(a.getMaterie())).append("</td>")
                  .append("<td>").append(a.isMotivata() ? "Motivata" : "Nemotivata").append("</td>")
                  .append("</tr>");
            }
            sb.append("</table>");

            sb.append("</body></html>");

            byte[] bytes = sb.toString().getBytes(StandardCharsets.UTF_8);
            OutputStream os = resp.getOutputStream();
            os.write(bytes);
            os.flush();
        } catch (Exception e) {
            throw new RuntimeException("ProfesorExportUtil.exportAbsenteXls()", e);
        }
    }

    public static void exportRaportXls(HttpServletResponse resp, AuthUser u, String materie,
                                       LocalDate from, LocalDate to,
                                       ProfesorRaportSummary summary,
                                       List<ProfesorRaportStudentRow> rows,
                                       int[] dist) {
        try {
            resp.setCharacterEncoding("UTF-8");
            resp.setContentType("application/vnd.ms-excel");
            resp.setHeader("Content-Disposition", "attachment; filename=\"raport_" + safe(materie) + "_" + from + "_" + to + ".xls\"");

            StringBuilder sb = new StringBuilder();
            sb.append("<html><head><meta charset='UTF-8'></head><body>");
            sb.append("<h2>Raport - ").append(esc(materie)).append("</h2>");
            sb.append("<div>Profesor: ").append(esc(u.getNume())).append(" (").append(esc(u.getEmail())).append(")</div>");
            sb.append("<div>Perioada: ").append(from).append(" -> ").append(to).append("</div><br/>");

            sb.append("<h3>Rezumat</h3>");
            sb.append("<table border='1' cellspacing='0' cellpadding='6'>")
              .append("<tr><th>Total note</th><th>Media generala</th><th>Total absente</th><th>Motivate</th><th>Nemotivate</th></tr>")
              .append("<tr>")
              .append("<td>").append(summary.getTotalNote()).append("</td>")
              .append("<td>").append(summary.getMedieGenerala()).append("</td>")
              .append("<td>").append(summary.getTotalAbsente()).append("</td>")
              .append("<td>").append(summary.getAbsMotivate()).append("</td>")
              .append("<td>").append(summary.getAbsNemotivate()).append("</td>")
              .append("</tr>")
              .append("</table><br/>");

            sb.append("<h3>Distributie note</h3>");
            sb.append("<table border='1' cellspacing='0' cellpadding='6'>");
            sb.append("<tr><th>Nota</th><th>Numar</th></tr>");
            for (int i = 1; i <= 10; i++) {
                sb.append("<tr><td>").append(i).append("</td><td>").append(dist[i]).append("</td></tr>");
            }
            sb.append("</table><br/>");

            sb.append("<h3>Detaliu pe elev</h3>");
            sb.append("<table border='1' cellspacing='0' cellpadding='6'>");
            sb.append("<tr><th>Student</th><th>Clasa</th><th>#Note</th><th>Medie</th><th>Abs Motivate</th><th>Abs Nemotivate</th></tr>");
            for (ProfesorRaportStudentRow r : rows) {
                sb.append("<tr>")
                  .append("<td>").append(esc(r.getStudentNume())).append("</td>")
                  .append("<td>").append(esc(r.getClasaNume())).append("</td>")
                  .append("<td>").append(r.getNrNote()).append("</td>")
                  .append("<td>").append(r.getMedie() == null ? "" : r.getMedie()).append("</td>")
                  .append("<td>").append(r.getAbsMotivate()).append("</td>")
                  .append("<td>").append(r.getAbsNemotivate()).append("</td>")
                  .append("</tr>");
            }
            sb.append("</table>");

            sb.append("</body></html>");

            byte[] bytes = sb.toString().getBytes(StandardCharsets.UTF_8);
            OutputStream os = resp.getOutputStream();
            os.write(bytes);
            os.flush();
        } catch (Exception e) {
            throw new RuntimeException("ProfesorExportUtil.exportRaportXls()", e);
        }
    }

    // =========================
    // PDF
    // =========================

    public static void exportNotePdf(HttpServletResponse resp, AuthUser u, String materie,
                                     LocalDate from, LocalDate to, List<ProfesorNotaView> note) {
        exportPdfLines(resp, "note_" + safe(materie) + "_" + from + "_" + to,
                buildNoteLines(u, materie, from, to, note));
    }

    public static void exportAbsentePdf(HttpServletResponse resp, AuthUser u, String materie,
                                        LocalDate from, LocalDate to, List<ProfesorAbsentaView> abs) {
        exportPdfLines(resp, "absente_" + safe(materie) + "_" + from + "_" + to,
                buildAbsLines(u, materie, from, to, abs));
    }

    public static void exportRaportPdf(HttpServletResponse resp, AuthUser u, String materie,
                                       LocalDate from, LocalDate to,
                                       ProfesorRaportSummary summary,
                                       List<ProfesorRaportStudentRow> rows,
                                       int[] dist) {
        exportPdfLines(resp, "raport_" + safe(materie) + "_" + from + "_" + to,
                buildRaportLines(u, materie, from, to, summary, rows, dist));
    }

    private static List<String> buildNoteLines(AuthUser u, String materie, LocalDate from, LocalDate to, List<ProfesorNotaView> note) {
        java.util.ArrayList<String> lines = new java.util.ArrayList<>();
        lines.add("NOTE - " + materie);
        lines.add("Profesor: " + u.getNume() + " (" + u.getEmail() + ")");
        lines.add("Perioada: " + from + " -> " + to);
        lines.add(" ");
        for (ProfesorNotaView n : note) {
            lines.add((n.getData() == null ? "" : n.getData().format(DF)) + " | " +
                    safeText(n.getStudentNume()) + " | " + safeText(n.getClasaNume()) + " | " + n.getValoare());
        }
        return lines;
    }

    private static List<String> buildAbsLines(AuthUser u, String materie, LocalDate from, LocalDate to, List<ProfesorAbsentaView> abs) {
        java.util.ArrayList<String> lines = new java.util.ArrayList<>();
        lines.add("ABSENTE - " + materie);
        lines.add("Profesor: " + u.getNume() + " (" + u.getEmail() + ")");
        lines.add("Perioada: " + from + " -> " + to);
        lines.add(" ");
        for (ProfesorAbsentaView a : abs) {
            lines.add((a.getData() == null ? "" : a.getData().format(DF)) + " | " +
                    safeText(a.getStudentNume()) + " | " + safeText(a.getClasaNume()) + " | " +
                    (a.isMotivata() ? "Motivata" : "Nemotivata"));
        }
        return lines;
    }

    private static List<String> buildRaportLines(AuthUser u, String materie, LocalDate from, LocalDate to,
                                                ProfesorRaportSummary s, List<ProfesorRaportStudentRow> rows, int[] dist) {
        java.util.ArrayList<String> lines = new java.util.ArrayList<>();
        lines.add("RAPORT - " + materie);
        lines.add("Profesor: " + u.getNume() + " (" + u.getEmail() + ")");
        lines.add("Perioada: " + from + " -> " + to);
        lines.add(" ");
        lines.add("Rezumat:");
        lines.add("Total note: " + s.getTotalNote() + " | Media generala: " + s.getMedieGenerala());
        lines.add("Total absente: " + s.getTotalAbsente() + " | Motivate: " + s.getAbsMotivate() + " | Nemotivate: " + s.getAbsNemotivate());
        lines.add(" ");
        lines.add("Distributie note:");
        for (int i = 1; i <= 10; i++) lines.add("Nota " + i + ": " + dist[i]);
        lines.add(" ");
        lines.add("Detaliu pe elev:");
        for (ProfesorRaportStudentRow r : rows) {
            lines.add(safeText(r.getStudentNume()) + " | " + safeText(r.getClasaNume()) +
                    " | #note=" + r.getNrNote() +
                    " | medie=" + (r.getMedie() == null ? "-" : r.getMedie()) +
                    " | absMot=" + r.getAbsMotivate() +
                    " | absNem=" + r.getAbsNemotivate());
        }
        return lines;
    }

    private static void exportPdfLines(HttpServletResponse resp, String filenameBase, List<String> lines) {
        try {
            resp.setContentType("application/pdf");
            resp.setHeader("Content-Disposition", "attachment; filename=\"" + filenameBase + ".pdf\"");

            PDDocument doc = new PDDocument();
            PdfWriter w = new PdfWriter(doc);

            for (String line : lines) w.line(line);

            w.close();
            OutputStream os = resp.getOutputStream();
            doc.save(os);
            doc.close();
            os.flush();
        } catch (Exception e) {
            throw new RuntimeException("ProfesorExportUtil.exportPdfLines()", e);
        }
    }

    private static class PdfWriter {
        private final PDDocument doc;
        private PDPage page;
        private PDPageContentStream cs;
        private float x = 50;
        private float y = 780;

        PdfWriter(PDDocument doc) throws Exception {
            this.doc = doc;
            newPage();
        }

        void newPage() throws Exception {
            if (cs != null) cs.close();
            page = new PDPage(PDRectangle.A4);
            doc.addPage(page);
            cs = new PDPageContentStream(doc, page);
            y = 780;
            cs.setFont(PDType1Font.HELVETICA, 10);
        }

        void line(String s) throws Exception {
            if (s == null) s = "";
            if (y < 70) newPage();

            cs.beginText();
            cs.newLineAtOffset(x, y);
            cs.showText(safeText(s));
            cs.endText();
            y -= 12;
        }

        void close() throws Exception {
            if (cs != null) cs.close();
        }
    }

    private static String esc(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    private static String safe(String s) {
        if (s == null) return "file";
        return s.replaceAll("[^a-zA-Z0-9._-]+", "_");
    }

    // PDFBox Type1 fonts -> mai safe fără diacritice
    private static String safeText(String s) {
        if (s == null) return "";
        return s.replace("Ș","S").replace("ș","s")
                .replace("Ț","T").replace("ț","t")
                .replace("Ă","A").replace("ă","a")
                .replace("Â","A").replace("â","a")
                .replace("Î","I").replace("î","i");
    }
}

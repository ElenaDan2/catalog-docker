package ro.catalog.util;

import ro.catalog.model.AbsentaView;
import ro.catalog.model.AuthUser;
import ro.catalog.model.NotaView;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class StudentExportUtil {

    private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static void exportXls(HttpServletResponse resp, AuthUser u, List<NotaView> note, List<AbsentaView> abs) {
        try {
            resp.setCharacterEncoding("UTF-8");
            resp.setContentType("application/vnd.ms-excel");
            resp.setHeader("Content-Disposition", "attachment; filename=\"catalog_" + safe(u.getEmail()) + ".xls\"");

            StringBuilder sb = new StringBuilder();
            sb.append("<html><head><meta charset='UTF-8'></head><body>");
            sb.append("<h2>Catalog scolar - ").append(esc(u.getNume())).append(" (").append(esc(u.getEmail())).append(")</h2>");

            sb.append("<h3>Note</h3>");
            sb.append("<table border='1' cellspacing='0' cellpadding='6'>");
            sb.append("<tr><th>Data</th><th>Materie</th><th>Nota</th></tr>");
            for (NotaView n : note) {
                sb.append("<tr>")
                  .append("<td>").append(n.getData().format(DF)).append("</td>")
                  .append("<td>").append(esc(n.getMaterie())).append("</td>")
                  .append("<td>").append(n.getValoare()).append("</td>")
                  .append("</tr>");
            }
            sb.append("</table>");

            sb.append("<h3>Absente</h3>");
            sb.append("<table border='1' cellspacing='0' cellpadding='6'>");
            sb.append("<tr><th>Data</th><th>Materie</th><th>Status</th></tr>");
            for (AbsentaView a : abs) {
                sb.append("<tr>")
                  .append("<td>").append(a.getData().format(DF)).append("</td>")
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
            throw new RuntimeException("StudentExportUtil.exportXls()", e);
        }
    }

    public static void exportPdf(HttpServletResponse resp, AuthUser u, List<NotaView> note, List<AbsentaView> abs) {
        try {
            resp.setContentType("application/pdf");
            resp.setHeader("Content-Disposition", "attachment; filename=\"catalog_" + safe(u.getEmail()) + ".pdf\"");

            org.apache.pdfbox.pdmodel.PDDocument doc = new org.apache.pdfbox.pdmodel.PDDocument();
            org.apache.pdfbox.pdmodel.PDPage page = new org.apache.pdfbox.pdmodel.PDPage(org.apache.pdfbox.pdmodel.common.PDRectangle.A4);
            doc.addPage(page);

            org.apache.pdfbox.pdmodel.PDPageContentStream cs = new org.apache.pdfbox.pdmodel.PDPageContentStream(doc, page);

            float x = 50, y = 780;
            cs.beginText();
            cs.setFont(org.apache.pdfbox.pdmodel.font.PDType1Font.HELVETICA_BOLD, 14);
            cs.newLineAtOffset(x, y);
            cs.showText("Catalog scolar - " + u.getNume() + " (" + u.getEmail() + ")");
            cs.endText();

            y -= 28;

            cs.beginText();
            cs.setFont(org.apache.pdfbox.pdmodel.font.PDType1Font.HELVETICA_BOLD, 12);
            cs.newLineAtOffset(x, y);
            cs.showText("Note");
            cs.endText();
            y -= 16;

            cs.setFont(org.apache.pdfbox.pdmodel.font.PDType1Font.HELVETICA, 10);
            int lines = 0;
            for (NotaView n : note) {
                if (y < 90) break;
                cs.beginText();
                cs.newLineAtOffset(x, y);
                cs.showText(n.getData().format(DF) + " | " + n.getMaterie() + " | " + n.getValoare());
                cs.endText();
                y -= 12;
                if (++lines > 35) break;
            }

            y -= 10;

            cs.beginText();
            cs.setFont(org.apache.pdfbox.pdmodel.font.PDType1Font.HELVETICA_BOLD, 12);
            cs.newLineAtOffset(x, y);
            cs.showText("Absente");
            cs.endText();
            y -= 16;

            lines = 0;
            for (AbsentaView a : abs) {
                if (y < 90) break;
                cs.beginText();
                cs.newLineAtOffset(x, y);
                cs.showText(a.getData().format(DF) + " | " + a.getMaterie() + " | " + (a.isMotivata() ? "Motivata" : "Nemotivata"));
                cs.endText();
                y -= 12;
                if (++lines > 35) break;
            }

            cs.close();

            OutputStream os = resp.getOutputStream();
            doc.save(os);
            doc.close();
            os.flush();

        } catch (Exception e) {
            throw new RuntimeException("StudentExportUtil.exportPdf()", e);
        }
    }

    private static String esc(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    private static String safe(String s) {
        if (s == null) return "user";
        return s.replaceAll("[^a-zA-Z0-9._-]+", "_");
    }
}

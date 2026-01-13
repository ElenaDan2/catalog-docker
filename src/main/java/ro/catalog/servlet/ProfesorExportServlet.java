package ro.catalog.servlet;

import ro.catalog.dao.AbsentaDAO;
import ro.catalog.dao.LogActiuneDAO;
import ro.catalog.dao.MaterieDAO;
import ro.catalog.dao.NotaDAO;
import ro.catalog.dao.ProfesorRaportDAO;
import ro.catalog.model.AuthUser;
import ro.catalog.model.ProfesorAbsentaView;
import ro.catalog.model.ProfesorNotaView;
import ro.catalog.model.ProfesorRaportStudentRow;
import ro.catalog.model.ProfesorRaportSummary;
import ro.catalog.util.ProfesorExportUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@WebServlet("/app/profesor/export")
public class ProfesorExportServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final MaterieDAO materieDAO = new MaterieDAO();
    private final NotaDAO notaDAO = new NotaDAO();
    private final AbsentaDAO absentaDAO = new AbsentaDAO();
    private final ProfesorRaportDAO raportDAO = new ProfesorRaportDAO();
    private final LogActiuneDAO logDAO = new LogActiuneDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        AuthUser u = (AuthUser) req.getSession().getAttribute("authUser");
        if (u == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        if (!"PROFESOR".equals(u.getRol())) {
            resp.sendError(403);
            return;
        }

        String entity = trim(req.getParameter("entity")); // note / absente / raport
        String fmt = trim(req.getParameter("fmt"));       // pdf / xls
        Integer materieId = parseIntOrNull(req.getParameter("materieId"));
        Integer clasaId = parseIntOrNull(req.getParameter("clasaId"));
        String q = req.getParameter("q") == null ? "" : req.getParameter("q").trim();

        LocalDate from = parseDateOrDefault(req.getParameter("from"), LocalDate.of(2025, 9, 1));
        LocalDate to = parseDateOrDefault(req.getParameter("to"), LocalDate.of(2026, 8, 31));
        if (from.isAfter(to)) { LocalDate tmp = from; from = to; to = tmp; }

        if (materieId == null || !materieDAO.profesorOwns(materieId, u.getId())) {
            resp.sendError(400, "Materie invalida pentru profesor.");
            return;
        }

        String materieName = materieDAO.findNameById(materieId);
        if (materieName == null) materieName = "materie";

        if ("note".equalsIgnoreCase(entity)) {
            List<ProfesorNotaView> note = notaDAO.listNoteForProfesor(u.getId(), materieId, clasaId, q, from, to);

            if ("pdf".equalsIgnoreCase(fmt)) {
                logDAO.log(u.getEmail(), u.getRol(), "EXPORT_NOTE_PDF materie_id=" + materieId);
                ProfesorExportUtil.exportNotePdf(resp, u, materieName, from, to, note);
                return;
            }
            if ("xls".equalsIgnoreCase(fmt)) {
                logDAO.log(u.getEmail(), u.getRol(), "EXPORT_NOTE_XLS materie_id=" + materieId);
                ProfesorExportUtil.exportNoteXls(resp, u, materieName, from, to, note);
                return;
            }
        }

        if ("absente".equalsIgnoreCase(entity)) {
            List<ProfesorAbsentaView> abs = absentaDAO.listAbsenteForProfesor(u.getId(), materieId, clasaId, q, from, to);

            if ("pdf".equalsIgnoreCase(fmt)) {
                logDAO.log(u.getEmail(), u.getRol(), "EXPORT_ABS_PDF materie_id=" + materieId);
                ProfesorExportUtil.exportAbsentePdf(resp, u, materieName, from, to, abs);
                return;
            }
            if ("xls".equalsIgnoreCase(fmt)) {
                logDAO.log(u.getEmail(), u.getRol(), "EXPORT_ABS_XLS materie_id=" + materieId);
                ProfesorExportUtil.exportAbsenteXls(resp, u, materieName, from, to, abs);
                return;
            }
        }

        if ("raport".equalsIgnoreCase(entity)) {
            ProfesorRaportSummary summary = raportDAO.summary(u.getId(), materieId, clasaId, q, from, to);
            List<ProfesorRaportStudentRow> rows = raportDAO.rows(u.getId(), materieId, clasaId, q, from, to);
            int[] dist = raportDAO.distributieNote(u.getId(), materieId, clasaId, q, from, to);

            if ("pdf".equalsIgnoreCase(fmt)) {
                logDAO.log(u.getEmail(), u.getRol(), "EXPORT_RAPORT_PDF materie_id=" + materieId);
                ProfesorExportUtil.exportRaportPdf(resp, u, materieName, from, to, summary, rows, dist);
                return;
            }
            if ("xls".equalsIgnoreCase(fmt)) {
                logDAO.log(u.getEmail(), u.getRol(), "EXPORT_RAPORT_XLS materie_id=" + materieId);
                ProfesorExportUtil.exportRaportXls(resp, u, materieName, from, to, summary, rows, dist);
                return;
            }
        }

        resp.sendError(400, "Parametri export invalizi.");
    }

    private static Integer parseIntOrNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        if (t.isEmpty()) return null;
        try { return Integer.parseInt(t); } catch (Exception e) { return null; }
    }

    private static LocalDate parseDateOrDefault(String s, LocalDate def) {
        if (s == null) return def;
        String t = s.trim();
        if (t.isEmpty()) return def;
        try { return LocalDate.parse(t); } catch (Exception e) { return def; }
    }

    private static String trim(String s) {
        return s == null ? "" : s.trim();
    }
}

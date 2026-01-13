package ro.catalog.servlet;

import ro.catalog.dao.AbsentaDAO;
import ro.catalog.dao.NotaDAO;
import ro.catalog.model.AbsentaView;
import ro.catalog.model.AuthUser;
import ro.catalog.model.MaterieNoteRow;
import ro.catalog.model.NotaView;
import ro.catalog.util.StudentExportUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

@WebServlet("/app/student/catalog")
public class StudentCatalogServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final NotaDAO notaDAO = new NotaDAO();
    private final AbsentaDAO absentaDAO = new AbsentaDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        AuthUser u = (AuthUser) req.getSession().getAttribute("authUser");
        if (u == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        if (!"STUDENT".equals(u.getRol())) {
            resp.sendError(403);
            return;
        }

        LocalDate from = LocalDate.of(2025, 9, 1);
        LocalDate to   = LocalDate.of(2026, 8, 31);

        String pFrom = req.getParameter("from");
        String pTo = req.getParameter("to");
        String materie = trimToNull(req.getParameter("materie"));
        Integer minNota = parseIntOrNull(req.getParameter("minNota"));
        Integer maxNota = parseIntOrNull(req.getParameter("maxNota"));

        if (pFrom != null && !pFrom.isBlank()) {
            try { from = LocalDate.parse(pFrom.trim()); } catch (Exception ignored) {}
        }
        if (pTo != null && !pTo.isBlank()) {
            try { to = LocalDate.parse(pTo.trim()); } catch (Exception ignored) {}
        }
        if (from.isAfter(to)) {
            LocalDate tmp = from; from = to; to = tmp;
        }

        List<NotaView> note = notaDAO.listNoteFiltered(u.getId(), from, to, materie, minNota, maxNota);
        List<AbsentaView> abs = absentaDAO.listAbsente(u.getId(), from, to);

        Map<String, MaterieNoteRow> map = new LinkedHashMap<>();
        for (NotaView n : note) {
            map.computeIfAbsent(n.getMaterie(), MaterieNoteRow::new).add(n);
        }
        List<MaterieNoteRow> rows = new ArrayList<>(map.values());
        rows.sort(Comparator.comparing(MaterieNoteRow::getMaterie, String.CASE_INSENSITIVE_ORDER));

        double medie = 0.0;
        if (!note.isEmpty()) {
            int s = 0;
            for (NotaView n : note) s += n.getValoare();
            medie = (double) s / (double) note.size();
        }

        int nemotivate = 0;
        for (AbsentaView a : abs) if (!a.isMotivata()) nemotivate++;

        MaterieNoteRow best = null, worst = null;
        for (MaterieNoteRow r : rows) {
            if (best == null || r.getMedie() > best.getMedie()) best = r;
            if (worst == null || r.getMedie() < worst.getMedie()) worst = r;
        }

        String export = req.getParameter("export");
        if ("xls".equalsIgnoreCase(export)) {
            StudentExportUtil.exportXls(resp, u, note, abs);
            return;
        }
        if ("pdf".equalsIgnoreCase(export)) {
            StudentExportUtil.exportPdf(resp, u, note, abs);
            return;
        }

        List<String> materii = notaDAO.listMateriiDistinct(
                u.getId(),
                LocalDate.of(2025, 9, 1),
                LocalDate.of(2026, 8, 31)
        );

        req.setAttribute("from", from.toString());
        req.setAttribute("to", to.toString());
        req.setAttribute("materieSelected", materie == null ? "" : materie);
        req.setAttribute("minNota", minNota == null ? "" : String.valueOf(minNota));
        req.setAttribute("maxNota", maxNota == null ? "" : String.valueOf(maxNota));

        req.setAttribute("materii", materii);
        req.setAttribute("rows", rows);
        req.setAttribute("abs", abs);

        req.setAttribute("kpi_total_note", note.size());
        req.setAttribute("kpi_medie", medie);
        req.setAttribute("kpi_nemotivate", nemotivate);
        req.setAttribute("kpi_best", best);
        req.setAttribute("kpi_worst", worst);

        req.getRequestDispatcher("/WEB-INF/student-catalog.jsp").forward(req, resp);
    }

    private static String trimToNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }

    private static Integer parseIntOrNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        if (t.isEmpty()) return null;
        try { return Integer.parseInt(t); } catch (Exception e) { return null; }
    }
}

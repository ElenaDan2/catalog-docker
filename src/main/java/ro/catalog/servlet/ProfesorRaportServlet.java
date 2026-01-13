package ro.catalog.servlet;

import ro.catalog.dao.ClasaDAO;
import ro.catalog.dao.MaterieDAO;
import ro.catalog.dao.ProfesorRaportDAO;
import ro.catalog.model.AuthUser;
import ro.catalog.model.Materie;
import ro.catalog.model.ProfesorRaportStudentRow;
import ro.catalog.model.ProfesorRaportSummary;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/app/profesor/raport")
public class ProfesorRaportServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final MaterieDAO materieDAO = new MaterieDAO();
    private final ClasaDAO clasaDAO = new ClasaDAO();
    private final ProfesorRaportDAO raportDAO = new ProfesorRaportDAO();

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

        List<Materie> materii = materieDAO.findByProfesor(u.getId());
        if (materii.isEmpty()) {
            req.setAttribute("user", u);
            req.setAttribute("materii", materii);
            req.setAttribute("clase", clasaDAO.findAll());
            req.setAttribute("error", "Nu ai nicio materie alocată (materie.profesor_id).");
            req.getRequestDispatcher("/WEB-INF/profesor-raport.jsp").forward(req, resp);
            return;
        }

        Integer materieIdSel = parseIntOrNull(req.getParameter("materieId"));
        if (materieIdSel == null || !materieDAO.profesorOwns(materieIdSel, u.getId())) {
            materieIdSel = materii.get(0).getId();
        }

        Integer clasaId = parseIntOrNull(req.getParameter("clasaId"));
        String q = trimToEmpty(req.getParameter("q"));

        LocalDate from = LocalDate.of(2025, 9, 1);
        LocalDate to = LocalDate.of(2026, 8, 31);
        LocalDate f = parseDateOrNull(req.getParameter("from"));
        LocalDate t = parseDateOrNull(req.getParameter("to"));
        if (f != null) from = f;
        if (t != null) to = t;
        if (from.isAfter(to)) { LocalDate tmp = from; from = to; to = tmp; }

        ProfesorRaportSummary summary = raportDAO.summary(u.getId(), materieIdSel, clasaId, q, from, to);
        List<ProfesorRaportStudentRow> rows = raportDAO.rows(u.getId(), materieIdSel, clasaId, q, from, to);
        int[] d = raportDAO.distributieNote(u.getId(), materieIdSel, clasaId, q, from, to);

        List<Integer> dist = new ArrayList<>(11);
        for (int i = 0; i <= 10; i++) dist.add(d[i]);

        List<ProfesorRaportStudentRow> withMedie = rows.stream()
                .filter(r -> r.getMedie() != null)
                .collect(Collectors.toList());

        List<ProfesorRaportStudentRow> top5 = withMedie.stream()
                .sorted(Comparator.comparing(ProfesorRaportStudentRow::getMedie).reversed())
                .limit(5)
                .collect(Collectors.toList());

        List<ProfesorRaportStudentRow> bottom5 = withMedie.stream()
                .sorted(Comparator.comparing(ProfesorRaportStudentRow::getMedie))
                .limit(5)
                .collect(Collectors.toList());

        req.setAttribute("user", u);
        req.setAttribute("materii", materii);
        req.setAttribute("materieIdSel", materieIdSel);
        req.setAttribute("clase", clasaDAO.findAll());
        req.setAttribute("clasaIdSel", clasaId == null ? "" : String.valueOf(clasaId));
        req.setAttribute("q", q);
        req.setAttribute("from", from.toString());
        req.setAttribute("to", to.toString());

        req.setAttribute("summary", summary);
        req.setAttribute("rows", rows);
        req.setAttribute("dist", dist);
        req.setAttribute("top5", top5);
        req.setAttribute("bottom5", bottom5);

        String ok = req.getParameter("ok");
        String err = req.getParameter("err");
        if (ok != null && !ok.isBlank()) req.setAttribute("success", ok);
        if (err != null && !err.isBlank()) req.setAttribute("error", err);

        req.getRequestDispatcher("/WEB-INF/profesor-raport.jsp").forward(req, resp);
    }

    private static Integer parseIntOrNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        if (t.isEmpty()) return null;
        try { return Integer.parseInt(t); } catch (Exception e) { return null; }
    }

    private static LocalDate parseDateOrNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        if (t.isEmpty()) return null;
        try { return LocalDate.parse(t); } catch (Exception e) { return null; }
    }

    private static String trimToEmpty(String s) {
        return s == null ? "" : s.trim();
    }
}

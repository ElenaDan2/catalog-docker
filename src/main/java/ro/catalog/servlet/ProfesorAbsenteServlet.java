package ro.catalog.servlet;

import ro.catalog.dao.AbsentaDAO;
import ro.catalog.dao.ClasaDAO;
import ro.catalog.dao.LogActiuneDAO;
import ro.catalog.dao.MaterieDAO;
import ro.catalog.dao.StudentDAO;
import ro.catalog.model.AuthUser;
import ro.catalog.model.Materie;
import ro.catalog.model.ProfesorAbsentaView;
import ro.catalog.model.Student;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

@WebServlet("/app/profesor/absente")
public class ProfesorAbsenteServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final MaterieDAO materieDAO = new MaterieDAO();
    private final ClasaDAO clasaDAO = new ClasaDAO();
    private final StudentDAO studentDAO = new StudentDAO();
    private final AbsentaDAO absentaDAO = new AbsentaDAO();
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

        List<Materie> materii = materieDAO.findByProfesor(u.getId());
        if (materii.isEmpty()) {
            req.setAttribute("user", u);
            req.setAttribute("error", "Nu ai nicio materie alocată (materie.profesor_id).");
            req.setAttribute("materii", materii);
            req.setAttribute("clase", clasaDAO.findAll());
            req.setAttribute("absente", java.util.Collections.emptyList());
            req.setAttribute("studenti", java.util.Collections.emptyList());
            req.getRequestDispatcher("/WEB-INF/profesor-absente.jsp").forward(req, resp);
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
        LocalDate parsedFrom = parseDateOrNull(req.getParameter("from"));
        LocalDate parsedTo = parseDateOrNull(req.getParameter("to"));
        if (parsedFrom != null) from = parsedFrom;
        if (parsedTo != null) to = parsedTo;
        if (from.isAfter(to)) {
            LocalDate tmp = from; from = to; to = tmp;
        }

        List<ProfesorAbsentaView> absente = absentaDAO.listAbsenteForProfesor(u.getId(), materieIdSel, clasaId, q, from, to);
        List<Student> studenti = studentDAO.search(q, null, null, clasaId);

        req.setAttribute("user", u);
        req.setAttribute("materii", materii);
        req.setAttribute("materieIdSel", materieIdSel);
        req.setAttribute("clase", clasaDAO.findAll());
        req.setAttribute("clasaIdSel", clasaId == null ? "" : String.valueOf(clasaId));
        req.setAttribute("q", q);
        req.setAttribute("from", from.toString());
        req.setAttribute("to", to.toString());
        req.setAttribute("absente", absente);
        req.setAttribute("studenti", studenti);

        String ok = req.getParameter("ok");
        String err = req.getParameter("err");
        if (ok != null && !ok.isBlank()) req.setAttribute("success", ok);
        if (err != null && !err.isBlank()) req.setAttribute("error", err);

        req.getRequestDispatcher("/WEB-INF/profesor-absente.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        AuthUser u = (AuthUser) req.getSession().getAttribute("authUser");
        if (u == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        if (!"PROFESOR".equals(u.getRol())) {
            resp.sendError(403);
            return;
        }

        String action = trimToNull(req.getParameter("action"));
        Integer materieId = parseIntOrNull(req.getParameter("materieId"));
        Integer clasaId = parseIntOrNull(req.getParameter("clasaId"));
        String q = trimToEmpty(req.getParameter("q"));
        LocalDate from = parseDateOrDefault(req.getParameter("from"), LocalDate.of(2025, 9, 1));
        LocalDate to = parseDateOrDefault(req.getParameter("to"), LocalDate.of(2026, 8, 31));
        if (from.isAfter(to)) { LocalDate tmp = from; from = to; to = tmp; }

        if (materieId == null || !materieDAO.profesorOwns(materieId, u.getId())) {
            redirectBack(req, resp, materieId, clasaId, q, from, to, "Materie invalidă pentru profesor.", true);
            return;
        }

        if ("add".equalsIgnoreCase(action)) {
            Integer studentId = parseIntOrNull(req.getParameter("studentId"));
            LocalDate dataAbsenta = parseDateOrNull(req.getParameter("data"));
            boolean motivata = "on".equalsIgnoreCase(req.getParameter("motivata"));

            if (studentId == null || dataAbsenta == null) {
                redirectBack(req, resp, materieId, clasaId, q, from, to, "Completează student și dată.", true);
                return;
            }

            absentaDAO.insertAbsenta(studentId, materieId, dataAbsenta, motivata);
            logDAO.log(u.getEmail(), u.getRol(), "ADD_ABSENTA student_id=" + studentId + ", materie_id=" + materieId + ", motivata=" + motivata);
            redirectBack(req, resp, materieId, clasaId, q, from, to, "Absență adăugată cu succes.", false);
            return;
        }

        if ("toggle".equalsIgnoreCase(action)) {
            Integer absentaId = parseIntOrNull(req.getParameter("absentaId"));
            boolean motivata = "1".equals(req.getParameter("motivata"));
            if (absentaId == null) {
                redirectBack(req, resp, materieId, clasaId, q, from, to, "AbsentaId lipsă.", true);
                return;
            }
            boolean ok = absentaDAO.setMotivataIfProfesorOwns(absentaId, u.getId(), motivata);
            if (ok) {
                logDAO.log(u.getEmail(), u.getRol(), "UPDATE_ABSENTA id=" + absentaId + ", motivata=" + motivata);
                redirectBack(req, resp, materieId, clasaId, q, from, to, "Status absență actualizat.", false);
            } else {
                redirectBack(req, resp, materieId, clasaId, q, from, to, "Nu poți modifica această absență.", true);
            }
            return;
        }

        if ("delete".equalsIgnoreCase(action)) {
            Integer absentaId = parseIntOrNull(req.getParameter("absentaId"));
            if (absentaId == null) {
                redirectBack(req, resp, materieId, clasaId, q, from, to, "AbsentaId lipsă.", true);
                return;
            }
            boolean ok = absentaDAO.deleteIfProfesorOwns(absentaId, u.getId());
            if (ok) {
                logDAO.log(u.getEmail(), u.getRol(), "DELETE_ABSENTA id=" + absentaId);
                redirectBack(req, resp, materieId, clasaId, q, from, to, "Absență ștearsă.", false);
            } else {
                redirectBack(req, resp, materieId, clasaId, q, from, to, "Nu poți șterge această absență.", true);
            }
            return;
        }

        redirectBack(req, resp, materieId, clasaId, q, from, to, "Acțiune necunoscută.", true);
    }

    private static void redirectBack(HttpServletRequest req, HttpServletResponse resp,
                                     Integer materieId, Integer clasaId, String q,
                                     LocalDate from, LocalDate to,
                                     String msg, boolean error) throws IOException {

        StringBuilder url = new StringBuilder();
        url.append(req.getContextPath()).append("/app/profesor/absente");
        url.append("?materieId=").append(materieId == null ? "" : materieId);
        url.append("&clasaId=").append(clasaId == null ? "" : clasaId);
        url.append("&q=").append(URLEncoder.encode(q == null ? "" : q, StandardCharsets.UTF_8));
        url.append("&from=").append(from);
        url.append("&to=").append(to);
        if (msg != null && !msg.isBlank()) {
            url.append(error ? "&err=" : "&ok=")
               .append(URLEncoder.encode(msg, StandardCharsets.UTF_8));
        }
        resp.sendRedirect(url.toString());
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

    private static LocalDate parseDateOrDefault(String s, LocalDate def) {
        LocalDate d = parseDateOrNull(s);
        return d == null ? def : d;
    }

    private static String trimToNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }

    private static String trimToEmpty(String s) {
        return s == null ? "" : s.trim();
    }
}

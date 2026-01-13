package ro.catalog.servlet;

import ro.catalog.dao.StudentDAO;
import ro.catalog.dao.AbsentaDAO;
import ro.catalog.dao.NotaDAO;
import ro.catalog.model.AbsentaView;
import ro.catalog.model.AuthUser;
import ro.catalog.model.MaterieNoteRow;
import ro.catalog.model.NotaView;
import ro.catalog.dao.TestareDAO;
import ro.catalog.model.TestareView;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

@WebServlet("/app/dashboard")
public class DashboardServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final StudentDAO studentDAO = new StudentDAO();
    private final NotaDAO notaDAO = new NotaDAO();
    private final AbsentaDAO absentaDAO = new AbsentaDAO();
    private final TestareDAO testareDAO = new TestareDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        AuthUser u = (AuthUser) req.getSession().getAttribute("authUser");
        if (u == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        // An școlar 2025–2026
        LocalDate anFrom = LocalDate.of(2025, 9, 1);
        LocalDate anTo   = LocalDate.of(2026, 8, 31);

        if ("STUDENT".equals(u.getRol())) {

            double medie = notaDAO.mediaPeStudent(u.getId(), anFrom, anTo);
            int materiiCuNote = notaDAO.countMateriiCuNote(u.getId(), anFrom, anTo);
            int nemotivate = absentaDAO.countNemotivate(u.getId(), anFrom, anTo);

            List<NotaView> note = notaDAO.listNote(u.getId(), anFrom, anTo);
            List<AbsentaView> abs = absentaDAO.listAbsente(u.getId(), anFrom, anTo);

            Map<String, MaterieNoteRow> map = new LinkedHashMap<>();
            for (NotaView n : note) {
                String m = n.getMaterie();
                map.computeIfAbsent(m, MaterieNoteRow::new).add(n);
            }
            List<MaterieNoteRow> rows = new ArrayList<>(map.values());
            rows.sort(Comparator.comparing(MaterieNoteRow::getMaterie, String.CASE_INSENSITIVE_ORDER));

            MaterieNoteRow best = null, worst = null;
            for (MaterieNoteRow r : rows) {
                if (best == null || r.getMedie() > best.getMedie()) best = r;
                if (worst == null || r.getMedie() < worst.getMedie()) worst = r;
            }

            req.setAttribute("user", u);
            req.setAttribute("an_scolar", "2025–2026");

            req.setAttribute("kpi_medie", medie);
            req.setAttribute("kpi_materii", materiiCuNote);
            req.setAttribute("kpi_nemotivate", nemotivate);

            req.setAttribute("rows", rows);
            req.setAttribute("abs", abs);
            req.setAttribute("kpi_best", best);
            req.setAttribute("kpi_worst", worst);

            Integer clasaId = studentDAO.getClasaIdByStudentId(u.getId());
            TestareView nextTest = (clasaId == null) ? null : testareDAO.nextForClass(clasaId);
            req.setAttribute("nextTest", nextTest);

            req.getRequestDispatcher("/WEB-INF/dashboard-student.jsp").forward(req, resp);
            return;
        }

        // ===================== PROFESOR DASHBOARD + GRAFICE =====================
        req.setAttribute("user", u);

        // 1) Absențe / zi (ultimele 30 zile)
        LocalDate end = LocalDate.now();
        LocalDate start = end.minusDays(29);

        Map<LocalDate, Integer> absMap = absentaDAO.countAbsenteByDayForProfesor(u.getId(), start, end);

        List<String> absLabels = new ArrayList<>();
        List<Integer> absValues = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            LocalDate d = start.plusDays(i);
            absLabels.add(d.toString());
            absValues.add(absMap.getOrDefault(d, 0));
        }

        // 2) Media notelor pe clasă (an școlar)
        Map<String, Double> mediiMap = notaDAO.avgNoteByClasaForProfesor(u.getId(), anFrom, anTo);
        List<String> mediiLabels = new ArrayList<>(mediiMap.keySet());
        List<Double> mediiValues = new ArrayList<>();
        for (String k : mediiLabels) mediiValues.add(mediiMap.get(k));

        req.setAttribute("absLabelsJson", toJsonStringArray(absLabels));
        req.setAttribute("absValuesJson", toJsonIntArray(absValues));

        req.setAttribute("mediiLabelsJson", toJsonStringArray(mediiLabels));
        req.setAttribute("mediiValuesJson", toJsonDoubleArray(mediiValues));

        req.getRequestDispatcher("/WEB-INF/dashboard-profesor.jsp").forward(req, resp);
    }

    // ===== JSON helpers (simplu, fără librării) =====

    private static String esc(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private static String toJsonStringArray(List<String> list) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append("\"").append(esc(list.get(i))).append("\"");
        }
        sb.append("]");
        return sb.toString();
    }

    private static String toJsonIntArray(List<Integer> list) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(list.get(i) == null ? 0 : list.get(i));
        }
        sb.append("]");
        return sb.toString();
    }

    private static String toJsonDoubleArray(List<Double> list) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) sb.append(",");
            Double v = list.get(i);
            if (v == null) sb.append("0");
            else sb.append(String.format(java.util.Locale.US, "%.2f", v));
        }
        sb.append("]");
        return sb.toString();
    }
}

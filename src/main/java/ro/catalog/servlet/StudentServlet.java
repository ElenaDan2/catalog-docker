package ro.catalog.servlet;

import ro.catalog.dao.StudentDAO;
import ro.catalog.model.Student;
import ro.catalog.dao.ClasaDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/studenti")
public class StudentServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final StudentDAO dao = new StudentDAO();
    private final ClasaDAO clasaDAO = new ClasaDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        String action = req.getParameter("action");
        if (action == null) action = "list";

        switch (action) {
            case "new":
                req.setAttribute("mode", "create");
                req.setAttribute("clase", clasaDAO.findAll());
                req.getRequestDispatcher("/WEB-INF/student-form.jsp").forward(req, resp);
                break;

            case "edit": {
                int id = Integer.parseInt(req.getParameter("id"));
                Student s = dao.findById(id);
                req.setAttribute("student", s);
                req.setAttribute("mode", "edit");
                req.setAttribute("clase", clasaDAO.findAll());
                req.getRequestDispatcher("/WEB-INF/student-form.jsp").forward(req, resp);
                break;
            }

            case "delete": {
                int id = Integer.parseInt(req.getParameter("id"));
                dao.delete(id);
                resp.sendRedirect(req.getContextPath() + "/studenti");
                break;
            }

            default: { // list + search/filter
                String q = req.getParameter("q");
                Integer minV = parseIntOrNull(req.getParameter("minVarsta"));
                Integer maxV = parseIntOrNull(req.getParameter("maxVarsta"));
                Integer clasaId = parseIntOrNull(req.getParameter("clasaId"));

                List<Student> list = dao.search(q, minV, maxV, clasaId);
                req.setAttribute("studenti", list);
                req.setAttribute("q", q);
                req.setAttribute("minVarsta", req.getParameter("minVarsta"));
                req.setAttribute("maxVarsta", req.getParameter("maxVarsta"));
                req.setAttribute("clase", clasaDAO.findAll());
                req.setAttribute("clasaId", req.getParameter("clasaId"));

                req.getRequestDispatcher("/studenti.jsp").forward(req, resp);
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        String action = req.getParameter("action");
        if (action == null) action = "";

        if ("create".equals(action)) {
            String nume = req.getParameter("nume");
            String email = req.getParameter("email");
            int varsta = Integer.parseInt(req.getParameter("varsta"));
            Integer clasaId = parseIntOrNull(req.getParameter("clasaId"));

            Student st = new Student(nume, email, varsta);
            st.setClasaId(clasaId);
            dao.insert(st);

            
            resp.sendRedirect(req.getContextPath() + "/studenti");
            return;
        }

        if ("update".equals(action)) {
            int id = Integer.parseInt(req.getParameter("id"));
            String nume = req.getParameter("nume");
            String email = req.getParameter("email");
            int varsta = Integer.parseInt(req.getParameter("varsta"));
            Integer clasaId = parseIntOrNull(req.getParameter("clasaId"));

            Student st = new Student(id, nume, email, varsta);
            st.setClasaId(clasaId);
            dao.update(st);

            resp.sendRedirect(req.getContextPath() + "/studenti");
            return;
        }

        resp.sendRedirect(req.getContextPath() + "/studenti");
    }

    private Integer parseIntOrNull(String s) {
        try {
            if (s == null || s.trim().isEmpty()) return null;
            return Integer.parseInt(s.trim());
        } catch (Exception e) {
            return null;
        }
    }
}

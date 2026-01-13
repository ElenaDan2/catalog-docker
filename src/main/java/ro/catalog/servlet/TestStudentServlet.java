package ro.catalog.servlet;

import ro.catalog.dao.StudentDAO;
import ro.catalog.model.Student;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/test-studenti")
public class TestStudentServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        response.setContentType("text/html");

        StudentDAO dao = new StudentDAO();
        List<Student> studenti = dao.search(null, null, null);


        response.getWriter().println("<h2>Lista studenti</h2>");

        for (Student s : studenti) {
            response.getWriter().println(
                s.getId() + " - " +
                s.getNume() + " - " +
                s.getEmail() + "<br>"
            );
        }
    }
}

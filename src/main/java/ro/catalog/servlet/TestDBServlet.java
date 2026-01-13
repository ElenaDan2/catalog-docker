package ro.catalog.servlet;

import ro.catalog.util.DBConnection;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

@WebServlet("/test-db")
public class TestDBServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        response.setContentType("text/html; charset=UTF-8");

        try (Connection conn = DBConnection.getConnection()) {
            response.getWriter().println("<h2>✅ Conexiune DB OK</h2>");
        } catch (SQLException e) {
            response.getWriter().println("<h2>❌ Eroare DB</h2>");
            response.getWriter().println("<pre>" + e.getMessage() + "</pre>");
        }
    }
}

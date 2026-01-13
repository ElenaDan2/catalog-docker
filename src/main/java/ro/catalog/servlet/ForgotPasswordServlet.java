package ro.catalog.servlet;

import ro.catalog.dao.AuthDAO;
import ro.catalog.dao.LogActiuneDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/forgot-password")
public class ForgotPasswordServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

    private final AuthDAO authDAO = new AuthDAO();
    private final LogActiuneDAO logDAO = new LogActiuneDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/forgot-password.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");

        String email = req.getParameter("email");
        String p1 = req.getParameter("parolaNoua");
        String p2 = req.getParameter("parolaNoua2");

        if (email == null || email.trim().isEmpty()) {
            req.setAttribute("error", "Introdu email-ul.");
            req.getRequestDispatcher("/WEB-INF/forgot-password.jsp").forward(req, resp);
            return;
        }
        if (p1 == null || p1.length() < 4) {
            req.setAttribute("error", "Parola trebuie să aibă minim 4 caractere.");
            req.getRequestDispatcher("/WEB-INF/forgot-password.jsp").forward(req, resp);
            return;
        }
        if (!p1.equals(p2)) {
            req.setAttribute("error", "Parolele nu coincid.");
            req.getRequestDispatcher("/WEB-INF/forgot-password.jsp").forward(req, resp);
            return;
        }

        String role = authDAO.resetPasswordByEmail(email.trim(), p1);

        if (role == null) {
            req.setAttribute("error", "Nu există niciun utilizator cu acest email.");
            req.getRequestDispatcher("/WEB-INF/forgot-password.jsp").forward(req, resp);
            return;
        }

        logDAO.log(email.trim(), role, "RESET_PASSWORD");

        req.setAttribute("success", "Parola a fost schimbată. Te poți autentifica acum.");
        req.getRequestDispatcher("/WEB-INF/forgot-password.jsp").forward(req, resp);
    }
    
    public String resetPasswordByEmail(String email, String parolaNoua) {
        // 1) profesor
        String sql1 = "UPDATE profesor SET parola=? WHERE email=?";
        // 2) student
        String sql2 = "UPDATE student SET parola=? WHERE email=?";

        try (java.sql.Connection c = ro.catalog.util.DBConnection.getConnection()) {

            try (java.sql.PreparedStatement ps = c.prepareStatement(sql1)) {
                ps.setString(1, parolaNoua);
                ps.setString(2, email);
                int updated = ps.executeUpdate();
                if (updated > 0) return "PROFESOR";
            }

            try (java.sql.PreparedStatement ps = c.prepareStatement(sql2)) {
                ps.setString(1, parolaNoua);
                ps.setString(2, email);
                int updated = ps.executeUpdate();
                if (updated > 0) return "STUDENT";
            }

            return null;

        } catch (Exception e) {
            throw new RuntimeException("AuthDAO.resetPasswordByEmail()", e);
        }
    }

}

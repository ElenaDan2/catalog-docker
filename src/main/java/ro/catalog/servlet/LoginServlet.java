package ro.catalog.servlet;

import ro.catalog.dao.AuthDAO;
import ro.catalog.dao.LogActiuneDAO;
import ro.catalog.model.AuthUser;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

    private final AuthDAO authDAO = new AuthDAO();
    private final LogActiuneDAO logDAO = new LogActiuneDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        String email = req.getParameter("email");
        String parola = req.getParameter("parola");

        AuthUser u = authDAO.authenticate(email, parola);

        if (u == null) {
            req.setAttribute("error", "Email/parola incorecte (sau cont inactiv).");
            req.getRequestDispatcher("/login.jsp").forward(req, resp);
            return;
        }

        HttpSession s = req.getSession(true);
        s.setAttribute("authUser", u);

        logDAO.log(u.getEmail(), u.getRol(), "LOGIN");

        resp.sendRedirect(req.getContextPath() + "/app/dashboard");
    }
}

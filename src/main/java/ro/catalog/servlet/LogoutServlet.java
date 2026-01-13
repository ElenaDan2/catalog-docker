package ro.catalog.servlet;

import ro.catalog.dao.LogActiuneDAO;
import ro.catalog.model.AuthUser;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

    private final LogActiuneDAO logDAO = new LogActiuneDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession s = req.getSession(false);
        if (s != null) {
            AuthUser u = (AuthUser) s.getAttribute("authUser");
            if (u != null) logDAO.log(u.getEmail(), u.getRol(), "LOGOUT");
            s.invalidate();
        }
        resp.sendRedirect(req.getContextPath() + "/login");
    }
}

package ro.catalog.dao;

import ro.catalog.model.AuthUser;
import ro.catalog.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AuthDAO {

    public AuthUser authenticate(String email, String parola) {
        if (email == null || parola == null) return null;

        // PROFESOR
        String sqlProf = "SELECT id, nume, email FROM profesor WHERE email=? AND parola=?";
        AuthUser u = findOne(sqlProf, email, parola, "PROFESOR");
        if (u != null) return u;

        // STUDENT
        String sqlStud = "SELECT id, nume, email FROM student WHERE email=? AND parola=?";
        return findOne(sqlStud, email, parola, "STUDENT");
    }

    private AuthUser findOne(String sql, String email, String parola, String rol) {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setString(2, parola);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new AuthUser(
                            rs.getInt("id"),
                            rs.getString("nume"),
                            rs.getString("email"),
                            rol
                    );
                }
            }
            return null;

        } catch (Exception e) {
            throw new RuntimeException("AuthDAO.authenticate()", e);
        }
    }

    // RESET PAROLA (caută după email în profesor/student și face UPDATE)
    public String resetPasswordByEmail(String email, String parolaNoua) {
        String sql1 = "UPDATE profesor SET parola=? WHERE email=?";
        String sql2 = "UPDATE student SET parola=? WHERE email=?";

        try (Connection c = DBConnection.getConnection()) {

            try (PreparedStatement ps = c.prepareStatement(sql1)) {
                ps.setString(1, parolaNoua);
                ps.setString(2, email);
                int updated = ps.executeUpdate();
                if (updated > 0) return "PROFESOR";
            }

            try (PreparedStatement ps = c.prepareStatement(sql2)) {
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

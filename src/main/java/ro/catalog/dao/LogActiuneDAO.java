package ro.catalog.dao;

import ro.catalog.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class LogActiuneDAO {

    public void log(String utilizator, String rol, String actiune) {
        String sql = "INSERT INTO log_actiune(utilizator, rol, actiune) VALUES (?,?,?)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, utilizator);
            ps.setString(2, rol);
            ps.setString(3, actiune);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace(); // nu blocăm aplicația dacă log-ul pică
        }
    }
}

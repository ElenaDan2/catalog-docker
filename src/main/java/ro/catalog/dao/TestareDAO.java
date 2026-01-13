package ro.catalog.dao;

import ro.catalog.model.TestareView;
import ro.catalog.util.DBConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class TestareDAO {

    public TestareView nextForClass(int clasaId) {
        String sql =
                "SELECT t.id, t.data_test, t.tip, t.descriere, m.nume AS materie " +
                "FROM testare t " +
                "JOIN materie m ON m.id = t.materie_id " +
                "WHERE t.clasa_id = ? AND t.data_test >= CURDATE() " +
                "ORDER BY t.data_test ASC " +
                "LIMIT 1";

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, clasaId);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;

                Date d = rs.getDate("data_test");
                return new TestareView(
                        rs.getInt("id"),
                        d == null ? null : d.toLocalDate(),
                        rs.getString("tip"),
                        rs.getString("descriere"),
                        rs.getString("materie")
                );
            }
        } catch (Exception e) {
            throw new RuntimeException("TestareDAO.nextForClass()", e);
        }
    }
}

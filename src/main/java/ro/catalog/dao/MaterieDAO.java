package ro.catalog.dao;

import ro.catalog.model.Materie;
import ro.catalog.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class MaterieDAO {

    public List<Materie> findByProfesor(int profesorId) {
        String sql = "SELECT id, nume, profesor_id FROM materie WHERE profesor_id=? ORDER BY nume";
        List<Materie> list = new ArrayList<>();

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, profesorId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Materie(
                            rs.getInt("id"),
                            rs.getString("nume"),
                            rs.getInt("profesor_id")
                    ));
                }
            }
            return list;
        } catch (Exception e) {
            throw new RuntimeException("MaterieDAO.findByProfesor()", e);
        }
    }

    public boolean profesorOwns(int materieId, int profesorId) {
        String sql = "SELECT 1 FROM materie WHERE id=? AND profesor_id=? LIMIT 1";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, materieId);
            ps.setInt(2, profesorId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            throw new RuntimeException("MaterieDAO.profesorOwns()", e);
        }
    }

    public String findNameById(int materieId) {
        String sql = "SELECT nume FROM materie WHERE id=? LIMIT 1";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, materieId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString("nume") : null;
            }
        } catch (Exception e) {
            throw new RuntimeException("MaterieDAO.findNameById()", e);
        }
    }
}

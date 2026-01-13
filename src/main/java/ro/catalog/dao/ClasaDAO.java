package ro.catalog.dao;

import ro.catalog.model.Clasa;
import ro.catalog.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ClasaDAO {

    public List<Clasa> findAll() {
        String sql = "SELECT id, an, litera FROM clasa ORDER BY an, litera";
        List<Clasa> list = new ArrayList<>();

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new Clasa(
                        rs.getInt("id"),
                        rs.getInt("an"),
                        rs.getString("litera")
                ));
            }
            return list;

        } catch (Exception e) {
            throw new RuntimeException("ClasaDAO.findAll()", e);
        }
    }
}

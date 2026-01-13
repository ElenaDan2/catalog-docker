package ro.catalog.dao;

import ro.catalog.model.Student;
import ro.catalog.util.DBConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {

    private static final String TABLE = "student";

    private String baseSelect() {
        return "SELECT s.id, s.nume, s.email, s.parola, s.data_nasterii, s.activ, s.clasa_id, " +
               "CONCAT(c.an, c.litera) AS clasa_nume " +
               "FROM " + TABLE + " s " +
               "LEFT JOIN clasa c ON s.clasa_id = c.id ";
    }

    private Student map(ResultSet rs) throws SQLException {
        Student s = new Student();
        s.setId(rs.getInt("id"));
        s.setNume(rs.getString("nume"));
        s.setEmail(rs.getString("email"));
        s.setParola(rs.getString("parola"));

        Date dn = rs.getDate("data_nasterii");
        s.setDataNasterii(dn != null ? dn.toLocalDate() : null);

        s.setActiv(rs.getBoolean("activ"));

        Integer clasaId = (Integer) rs.getObject("clasa_id"); // poate fi null
        s.setClasaId(clasaId);
        s.setClasaNume(rs.getString("clasa_nume")); // poate fi null

        return s;
    }

    public List<Student> findAll() {
        String sql = baseSelect() + " ORDER BY s.id";
        List<Student> list = new ArrayList<>();

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) list.add(map(rs));

        } catch (SQLException e) {
            throw new RuntimeException("findAll()", e);
        }
        return list;
    }

    public Student findById(int id) {
        String sql = baseSelect() + " WHERE s.id=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("findById()", e);
        }
        return null;
    }

    public Integer getClasaIdByStudentId(int studentId) {
        String sql = "SELECT clasa_id FROM student WHERE id = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Object v = rs.getObject("clasa_id");
                    return (v == null) ? null : ((Number) v).intValue();
                }
            }
            return null;

        } catch (SQLException e) {
            throw new RuntimeException("getClasaIdByStudentId()", e);
        }
    }

    
    public int insert(Student s) {
        String sql = "INSERT INTO " + TABLE + " (nume,email,parola,data_nasterii,activ,clasa_id) VALUES (?,?,?,?,?,?)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, s.getNume());
            ps.setString(2, s.getEmail());
            ps.setString(3, s.getParola());

            LocalDate dn = s.getDataNasterii();
            if (dn == null) ps.setNull(4, Types.DATE);
            else ps.setDate(4, Date.valueOf(dn));

            ps.setBoolean(5, s.isActiv());

            if (s.getClasaId() == null) ps.setNull(6, Types.INTEGER);
            else ps.setInt(6, s.getClasaId());

            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
            return 0;

        } catch (SQLException e) {
            throw new RuntimeException("insert()", e);
        }
    }

    public void update(Student s) {
        String sql = "UPDATE " + TABLE + " SET nume=?, email=?, parola=?, data_nasterii=?, activ=?, clasa_id=? WHERE id=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, s.getNume());
            ps.setString(2, s.getEmail());
            ps.setString(3, s.getParola());

            LocalDate dn = s.getDataNasterii();
            if (dn == null) ps.setNull(4, Types.DATE);
            else ps.setDate(4, Date.valueOf(dn));

            ps.setBoolean(5, s.isActiv());

            if (s.getClasaId() == null) ps.setNull(6, Types.INTEGER);
            else ps.setInt(6, s.getClasaId());

            ps.setInt(7, s.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("update()", e);
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM " + TABLE + " WHERE id=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("delete()", e);
        }
    }

    // (optional) filtrare și după clasaId
    public List<Student> search(String q, Integer minAge, Integer maxAge, Integer clasaId) {
        String like = "%" + (q == null ? "" : q.trim()) + "%";

        StringBuilder sql = new StringBuilder(
                baseSelect() + " WHERE (s.nume LIKE ? OR s.email LIKE ?)"
        );

        if (minAge != null) sql.append(" AND TIMESTAMPDIFF(YEAR, s.data_nasterii, CURDATE()) >= ?");
        if (maxAge != null) sql.append(" AND TIMESTAMPDIFF(YEAR, s.data_nasterii, CURDATE()) <= ?");
        if (clasaId != null) sql.append(" AND s.clasa_id = ?");

        sql.append(" ORDER BY s.id");

        List<Student> list = new ArrayList<>();

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql.toString())) {

            int i = 1;
            ps.setString(i++, like);
            ps.setString(i++, like);

            if (minAge != null) ps.setInt(i++, minAge);
            if (maxAge != null) ps.setInt(i++, maxAge);
            if (clasaId != null) ps.setInt(i++, clasaId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("search()", e);
        }

        return list;
    }

    // compatibil cu ce ai deja în StudentServlet
    public List<Student> search(String q, Integer minAge, Integer maxAge) {
        return search(q, minAge, maxAge, null);
    }

    public List<Student> search(String q) {
        return search(q, null, null, null);
    }
}

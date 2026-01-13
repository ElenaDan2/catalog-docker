package ro.catalog.dao;

import ro.catalog.model.AbsentaView;
import ro.catalog.model.ProfesorAbsentaView;
import ro.catalog.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.time.LocalDate;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AbsentaDAO {

    // ===================== STUDENT =====================

    public int countNemotivate(int studentId, LocalDate from, LocalDate to) {
        String sql = "SELECT COUNT(*) AS cnt " +
                "FROM absenta " +
                "WHERE student_id=? AND data_absenta BETWEEN ? AND ? " +
                "AND (motivata=0 OR motivata IS NULL)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            ps.setDate(2, java.sql.Date.valueOf(from));
            ps.setDate(3, java.sql.Date.valueOf(to));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("cnt");
                return 0;
            }
        } catch (Exception e) {
            throw new RuntimeException("AbsentaDAO.countNemotivate()", e);
        }
    }

    public List<AbsentaView> listAbsente(int studentId, LocalDate from, LocalDate to) {
        String sql = "SELECT a.data_absenta, a.motivata, m.nume AS materie " +
                "FROM absenta a JOIN materie m ON m.id=a.materie_id " +
                "WHERE a.student_id=? AND a.data_absenta BETWEEN ? AND ? " +
                "ORDER BY a.data_absenta DESC, m.nume";

        List<AbsentaView> list = new ArrayList<>();

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            ps.setDate(2, java.sql.Date.valueOf(from));
            ps.setDate(3, java.sql.Date.valueOf(to));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    java.sql.Date d = rs.getDate("data_absenta");
                    list.add(new AbsentaView(
                            d == null ? null : d.toLocalDate(),
                            rs.getString("materie"),
                            rs.getBoolean("motivata")
                    ));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("AbsentaDAO.listAbsente()", e);
        }

        return list;
    }

    // ===================== PROFESOR =====================

    public List<ProfesorAbsentaView> listAbsenteForProfesor(int profesorId,
                                                            Integer materieId,
                                                            Integer clasaId,
                                                            String q,
                                                            LocalDate from,
                                                            LocalDate to) {

        if (from == null) from = LocalDate.of(1900, 1, 1);
        if (to == null) to = LocalDate.now();

        StringBuilder sql = new StringBuilder(
                "SELECT a.id, a.data_absenta, a.motivata, " +
                        "s.nume AS student_nume, CONCAT(c.an, c.litera) AS clasa_nume, " +
                        "m.nume AS materie " +
                "FROM absenta a " +
                "JOIN student s ON s.id = a.student_id " +
                "LEFT JOIN clasa c ON c.id = s.clasa_id " +
                "JOIN materie m ON m.id = a.materie_id " +
                "WHERE m.profesor_id = ? " +
                "AND a.data_absenta BETWEEN ? AND ? "
        );

        List<Object> params = new ArrayList<>();
        params.add(profesorId);
        params.add(java.sql.Date.valueOf(from));
        params.add(java.sql.Date.valueOf(to));

        if (materieId != null) {
            sql.append(" AND m.id = ? ");
            params.add(materieId);
        }
        if (clasaId != null) {
            sql.append(" AND s.clasa_id = ? ");
            params.add(clasaId);
        }
        if (q != null && !q.trim().isEmpty()) {
            sql.append(" AND (s.nume LIKE ? OR s.email LIKE ?) ");
            String like = "%" + q.trim() + "%";
            params.add(like);
            params.add(like);
        }

        sql.append(" ORDER BY a.data_absenta DESC, s.nume ASC, m.nume ASC ");

        List<ProfesorAbsentaView> list = new ArrayList<>();

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql.toString())) {

            bindParams(ps, params);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ProfesorAbsentaView v = new ProfesorAbsentaView();
                    v.setId(rs.getInt("id"));

                    java.sql.Date d = rs.getDate("data_absenta");
                    v.setData(d == null ? null : d.toLocalDate());

                    v.setMotivata(rs.getBoolean("motivata"));
                    v.setStudentNume(rs.getString("student_nume"));
                    v.setClasaNume(rs.getString("clasa_nume"));
                    v.setMaterie(rs.getString("materie"));
                    list.add(v);
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("AbsentaDAO.listAbsenteForProfesor()", e);
        }

        return list;
    }

    public boolean insertAbsenta(Integer studentId, Integer materieId, LocalDate data, boolean motivata) {
        String sql = "INSERT INTO absenta (student_id, materie_id, data_absenta, motivata) VALUES (?,?,?,?)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            ps.setInt(2, materieId);
            ps.setDate(3, java.sql.Date.valueOf(data));
            ps.setBoolean(4, motivata);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("AbsentaDAO.insertAbsenta()", e);
        }
    }

    public boolean setMotivataIfProfesorOwns(Integer absentaId, int profesorId, boolean motivata) {
        String sql =
                "UPDATE absenta a " +
                "JOIN materie m ON m.id = a.materie_id " +
                "SET a.motivata = ? " +
                "WHERE a.id = ? AND m.profesor_id = ?";

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setBoolean(1, motivata);
            ps.setInt(2, absentaId);
            ps.setInt(3, profesorId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("AbsentaDAO.setMotivataIfProfesorOwns()", e);
        }
    }

    public boolean deleteIfProfesorOwns(Integer absentaId, int profesorId) {
        String sql =
                "DELETE a FROM absenta a " +
                "JOIN materie m ON m.id = a.materie_id " +
                "WHERE a.id = ? AND m.profesor_id = ?";

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, absentaId);
            ps.setInt(2, profesorId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("AbsentaDAO.deleteIfProfesorOwns()", e);
        }
    }

    // ===================== GRAFIC PROFESOR =====================

    public Map<LocalDate, Integer> countAbsenteByDayForProfesor(int profesorId, LocalDate from, LocalDate to) {
        String sql =
                "SELECT a.data_absenta AS d, COUNT(*) AS cnt " +
                "FROM absenta a " +
                "JOIN materie m ON m.id = a.materie_id " +
                "WHERE m.profesor_id = ? AND a.data_absenta BETWEEN ? AND ? " +
                "GROUP BY a.data_absenta " +
                "ORDER BY a.data_absenta";

        Map<LocalDate, Integer> map = new LinkedHashMap<>();

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, profesorId);
            ps.setDate(2, java.sql.Date.valueOf(from));
            ps.setDate(3, java.sql.Date.valueOf(to));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    java.sql.Date d = rs.getDate("d");
                    int cnt = rs.getInt("cnt");
                    if (d != null) map.put(d.toLocalDate(), cnt);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("AbsentaDAO.countAbsenteByDayForProfesor()", e);
        }

        return map;
    }

    // ===================== helper =====================

    private void bindParams(PreparedStatement ps, List<Object> params) throws SQLException {
        for (int i = 0; i < params.size(); i++) {
            Object p = params.get(i);
            int idx = i + 1;

            if (p instanceof Integer) ps.setInt(idx, (Integer) p);
            else if (p instanceof Boolean) ps.setBoolean(idx, (Boolean) p);
            else if (p instanceof java.sql.Date) ps.setDate(idx, (java.sql.Date) p);
            else ps.setString(idx, String.valueOf(p));
        }
    }
}

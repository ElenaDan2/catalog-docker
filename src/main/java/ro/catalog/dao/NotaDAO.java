package ro.catalog.dao;

import ro.catalog.model.NotaView;
import ro.catalog.model.ProfesorNotaView;
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

public class NotaDAO {

    // ===================== STUDENT =====================

    public double mediaPeStudent(int studentId, LocalDate from, LocalDate to) {
        String sql = "SELECT AVG(valoare) AS medie " +
                "FROM nota WHERE student_id=? AND data_nota BETWEEN ? AND ?";

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            ps.setDate(2, java.sql.Date.valueOf(from));
            ps.setDate(3, java.sql.Date.valueOf(to));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getDouble("medie");
                return 0.0;
            }
        } catch (Exception e) {
            throw new RuntimeException("NotaDAO.mediaPeStudent()", e);
        }
    }

    public int countMateriiCuNote(int studentId, LocalDate from, LocalDate to) {
        String sql = "SELECT COUNT(DISTINCT materie_id) AS cnt " +
                "FROM nota WHERE student_id=? AND data_nota BETWEEN ? AND ?";

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
            throw new RuntimeException("NotaDAO.countMateriiCuNote()", e);
        }
    }

    public List<NotaView> listNote(int studentId, LocalDate from, LocalDate to) {
        return listNoteFiltered(studentId, from, to, null, null, null);
    }

    public List<NotaView> listNoteFiltered(int studentId, LocalDate from, LocalDate to,
                                           String materie, Integer minNota, Integer maxNota) {

        StringBuilder sql = new StringBuilder(
                "SELECT n.data_nota, n.valoare, m.nume AS materie " +
                "FROM nota n JOIN materie m ON m.id=n.materie_id " +
                "WHERE n.student_id=? AND n.data_nota BETWEEN ? AND ? "
        );

        List<Object> params = new ArrayList<>();
        params.add(studentId);
        params.add(java.sql.Date.valueOf(from));
        params.add(java.sql.Date.valueOf(to));

        if (materie != null && !materie.trim().isEmpty()) {
            sql.append(" AND m.nume = ? ");
            params.add(materie.trim());
        }
        if (minNota != null) {
            sql.append(" AND n.valoare >= ? ");
            params.add(minNota);
        }
        if (maxNota != null) {
            sql.append(" AND n.valoare <= ? ");
            params.add(maxNota);
        }

        sql.append(" ORDER BY n.data_nota DESC, m.nume, n.valoare DESC");

        List<NotaView> list = new ArrayList<>();

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql.toString())) {

            bindParams(ps, params);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    java.sql.Date d = rs.getDate("data_nota");
                    list.add(new NotaView(
                            d == null ? null : d.toLocalDate(),
                            rs.getString("materie"),
                            rs.getInt("valoare")
                    ));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("NotaDAO.listNoteFiltered()", e);
        }

        return list;
    }

    public List<String> listMateriiDistinct(int studentId, LocalDate from, LocalDate to) {
        String sql = "SELECT DISTINCT m.nume AS materie " +
                "FROM nota n JOIN materie m ON m.id=n.materie_id " +
                "WHERE n.student_id=? AND n.data_nota BETWEEN ? AND ? " +
                "ORDER BY m.nume";

        List<String> list = new ArrayList<>();

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            ps.setDate(2, java.sql.Date.valueOf(from));
            ps.setDate(3, java.sql.Date.valueOf(to));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(rs.getString("materie"));
            }

        } catch (Exception e) {
            throw new RuntimeException("NotaDAO.listMateriiDistinct()", e);
        }

        return list;
    }

    // ===================== PROFESOR =====================

    public List<ProfesorNotaView> listNoteForProfesor(int profesorId,
                                                      Integer materieId,
                                                      Integer clasaId,
                                                      String q,
                                                      LocalDate from,
                                                      LocalDate to) {

        if (from == null) from = LocalDate.of(1900, 1, 1);
        if (to == null) to = LocalDate.now();

        StringBuilder sql = new StringBuilder(
                "SELECT n.id, n.data_nota, n.valoare, " +
                        "s.nume AS student_nume, CONCAT(c.an, c.litera) AS clasa_nume, " +
                        "m.nume AS materie " +
                "FROM nota n " +
                "JOIN student s ON s.id = n.student_id " +
                "LEFT JOIN clasa c ON c.id = s.clasa_id " +
                "JOIN materie m ON m.id = n.materie_id " +
                "WHERE m.profesor_id = ? " +
                "AND n.data_nota BETWEEN ? AND ? "
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

        sql.append(" ORDER BY n.data_nota DESC, s.nume ASC, m.nume ASC ");

        List<ProfesorNotaView> list = new ArrayList<>();

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql.toString())) {

            bindParams(ps, params);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ProfesorNotaView v = new ProfesorNotaView();
                    v.setId(rs.getInt("id"));

                    java.sql.Date d = rs.getDate("data_nota");
                    v.setData(d == null ? null : d.toLocalDate());

                    v.setValoare(rs.getInt("valoare"));
                    v.setStudentNume(rs.getString("student_nume"));
                    v.setClasaNume(rs.getString("clasa_nume"));
                    v.setMaterie(rs.getString("materie"));
                    list.add(v);
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("NotaDAO.listNoteForProfesor()", e);
        }

        return list;
    }

    public boolean insertNota(Integer studentId, Integer materieId, Integer valoare, LocalDate data) {
        String sql = "INSERT INTO nota (student_id, materie_id, valoare, data_nota) VALUES (?,?,?,?)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            ps.setInt(2, materieId);
            ps.setInt(3, valoare);
            ps.setDate(4, java.sql.Date.valueOf(data));

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("NotaDAO.insertNota()", e);
        }
    }

    public boolean deleteIfProfesorOwns(Integer notaId, int profesorId) {
        String sql =
                "DELETE n FROM nota n " +
                "JOIN materie m ON m.id = n.materie_id " +
                "WHERE n.id = ? AND m.profesor_id = ?";

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, notaId);
            ps.setInt(2, profesorId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("NotaDAO.deleteIfProfesorOwns()", e);
        }
    }

    // ===================== GRAFIC PROFESOR =====================

    public Map<String, Double> avgNoteByClasaForProfesor(int profesorId, LocalDate from, LocalDate to) {
        String sql =
                "SELECT c.an, c.litera, AVG(n.valoare) AS medie " +
                "FROM nota n " +
                "JOIN student s ON s.id = n.student_id " +
                "JOIN clasa c ON c.id = s.clasa_id " +
                "JOIN materie m ON m.id = n.materie_id " +
                "WHERE m.profesor_id = ? AND n.data_nota BETWEEN ? AND ? " +
                "GROUP BY c.an, c.litera " +
                "ORDER BY c.an, c.litera";

        Map<String, Double> map = new LinkedHashMap<>();

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, profesorId);
            ps.setDate(2, java.sql.Date.valueOf(from));
            ps.setDate(3, java.sql.Date.valueOf(to));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String label = rs.getInt("an") + rs.getString("litera");
                    double medie = rs.getDouble("medie");
                    map.put(label, medie);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("NotaDAO.avgNoteByClasaForProfesor()", e);
        }

        return map;
    }

    // ===================== helper =====================

    private void bindParams(PreparedStatement ps, List<Object> params) throws SQLException {
        for (int i = 0; i < params.size(); i++) {
            Object p = params.get(i);
            int idx = i + 1;

            if (p instanceof Integer) ps.setInt(idx, (Integer) p);
            else if (p instanceof java.sql.Date) ps.setDate(idx, (java.sql.Date) p);
            else ps.setString(idx, String.valueOf(p));
        }
    }
}

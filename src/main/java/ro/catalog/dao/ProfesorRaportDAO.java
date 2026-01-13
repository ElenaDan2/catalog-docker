package ro.catalog.dao;

import ro.catalog.model.ProfesorRaportStudentRow;
import ro.catalog.model.ProfesorRaportSummary;
import ro.catalog.util.DBConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ProfesorRaportDAO {

    public ProfesorRaportSummary summary(int profesorId, int materieId, Integer clasaId, String q,
                                         LocalDate from, LocalDate to) {

        int totalNote = 0;
        double medieGenerala = 0.0;

        // NOTE summary
        {
            StringBuilder sql = new StringBuilder(
                    "SELECT COUNT(*) AS total, AVG(n.valoare) AS medie " +
                    "FROM nota n " +
                    "JOIN student s ON s.id = n.student_id " +
                    "JOIN materie m ON m.id = n.materie_id " +
                    "WHERE m.profesor_id = ? AND n.materie_id = ? AND n.data_nota BETWEEN ? AND ? "
            );

            List<Object> params = new ArrayList<>();
            params.add(profesorId);
            params.add(materieId);
            params.add(Date.valueOf(from));
            params.add(Date.valueOf(to));

            addStudentFilters(sql, params, clasaId, q);

            try (Connection c = DBConnection.getConnection();
                 PreparedStatement ps = c.prepareStatement(sql.toString())) {

                bind(ps, params);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        totalNote = rs.getInt("total");
                        medieGenerala = rs.getDouble("medie");
                        if (rs.wasNull()) medieGenerala = 0.0;
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException("ProfesorRaportDAO.summary(note)", e);
            }
        }

        int totalAbs = 0, absMot = 0, absNem = 0;

        // ABSENTE summary
        {
            StringBuilder sql = new StringBuilder(
                    "SELECT COUNT(*) AS total, " +
                    "SUM(CASE WHEN a.motivata=1 THEN 1 ELSE 0 END) AS mot, " +
                    "SUM(CASE WHEN a.motivata=0 OR a.motivata IS NULL THEN 1 ELSE 0 END) AS nem " +
                    "FROM absenta a " +
                    "JOIN student s ON s.id = a.student_id " +
                    "JOIN materie m ON m.id = a.materie_id " +
                    "WHERE m.profesor_id = ? AND a.materie_id = ? AND a.data_absenta BETWEEN ? AND ? "
            );

            List<Object> params = new ArrayList<>();
            params.add(profesorId);
            params.add(materieId);
            params.add(Date.valueOf(from));
            params.add(Date.valueOf(to));

            addStudentFilters(sql, params, clasaId, q);

            try (Connection c = DBConnection.getConnection();
                 PreparedStatement ps = c.prepareStatement(sql.toString())) {

                bind(ps, params);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        totalAbs = rs.getInt("total");
                        absMot = rs.getInt("mot");
                        absNem = rs.getInt("nem");
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException("ProfesorRaportDAO.summary(abs)", e);
            }
        }

        return new ProfesorRaportSummary(totalNote, medieGenerala, totalAbs, absMot, absNem);
    }

    public List<ProfesorRaportStudentRow> rows(int profesorId, int materieId, Integer clasaId, String q,
                                              LocalDate from, LocalDate to) {

        StringBuilder sql = new StringBuilder(
                "SELECT s.id AS student_id, s.nume AS student_nume, CONCAT(c.an, c.litera) AS clasa_nume, " +
                "COALESCE(n.nr_note,0) AS nr_note, n.medie AS medie, " +
                "COALESCE(a.abs_motivate,0) AS abs_motivate, COALESCE(a.abs_nemotivate,0) AS abs_nemotivate " +
                "FROM student s " +
                "LEFT JOIN clasa c ON c.id = s.clasa_id " +
                "LEFT JOIN ( " +
                "   SELECT n.student_id, COUNT(*) AS nr_note, AVG(n.valoare) AS medie " +
                "   FROM nota n JOIN materie m ON m.id=n.materie_id " +
                "   WHERE m.profesor_id=? AND n.materie_id=? AND n.data_nota BETWEEN ? AND ? " +
                "   GROUP BY n.student_id " +
                ") n ON n.student_id = s.id " +
                "LEFT JOIN ( " +
                "   SELECT a.student_id, " +
                "          SUM(CASE WHEN a.motivata=1 THEN 1 ELSE 0 END) AS abs_motivate, " +
                "          SUM(CASE WHEN a.motivata=0 OR a.motivata IS NULL THEN 1 ELSE 0 END) AS abs_nemotivate " +
                "   FROM absenta a JOIN materie m ON m.id=a.materie_id " +
                "   WHERE m.profesor_id=? AND a.materie_id=? AND a.data_absenta BETWEEN ? AND ? " +
                "   GROUP BY a.student_id " +
                ") a ON a.student_id = s.id " +
                "WHERE (s.nume LIKE ? OR s.email LIKE ?) "
        );

        List<Object> params = new ArrayList<>();
        params.add(profesorId);
        params.add(materieId);
        params.add(Date.valueOf(from));
        params.add(Date.valueOf(to));

        params.add(profesorId);
        params.add(materieId);
        params.add(Date.valueOf(from));
        params.add(Date.valueOf(to));

        String like = "%" + (q == null ? "" : q.trim()) + "%";
        params.add(like);
        params.add(like);

        if (clasaId != null) {
            sql.append(" AND s.clasa_id = ? ");
            params.add(clasaId);
        }

        sql.append(" ORDER BY s.nume ASC");

        List<ProfesorRaportStudentRow> list = new ArrayList<>();

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql.toString())) {

            bind(ps, params);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Double medie = (Double) rs.getObject("medie");
                    list.add(new ProfesorRaportStudentRow(
                            rs.getInt("student_id"),
                            rs.getString("student_nume"),
                            rs.getString("clasa_nume"),
                            rs.getInt("nr_note"),
                            medie,
                            rs.getInt("abs_motivate"),
                            rs.getInt("abs_nemotivate")
                    ));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("ProfesorRaportDAO.rows()", e);
        }

        return list;
    }

    // index 1..10
    public int[] distributieNote(int profesorId, int materieId, Integer clasaId, String q,
                                 LocalDate from, LocalDate to) {

        int[] d = new int[11];

        StringBuilder sql = new StringBuilder(
                "SELECT n.valoare, COUNT(*) AS cnt " +
                "FROM nota n " +
                "JOIN student s ON s.id = n.student_id " +
                "JOIN materie m ON m.id = n.materie_id " +
                "WHERE m.profesor_id=? AND n.materie_id=? AND n.data_nota BETWEEN ? AND ? "
        );

        List<Object> params = new ArrayList<>();
        params.add(profesorId);
        params.add(materieId);
        params.add(Date.valueOf(from));
        params.add(Date.valueOf(to));

        addStudentFilters(sql, params, clasaId, q);

        sql.append(" GROUP BY n.valoare ORDER BY n.valoare");

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql.toString())) {

            bind(ps, params);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int val = rs.getInt("valoare");
                    int cnt = rs.getInt("cnt");
                    if (val >= 1 && val <= 10) d[val] = cnt;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("ProfesorRaportDAO.distributieNote()", e);
        }

        return d;
    }

    private static void addStudentFilters(StringBuilder sql, List<Object> params, Integer clasaId, String q) {
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
    }

    private static void bind(PreparedStatement ps, List<Object> params) throws Exception {
        for (int i = 0; i < params.size(); i++) {
            Object p = params.get(i);
            int idx = i + 1;
            if (p instanceof Integer) ps.setInt(idx, (Integer) p);
            else if (p instanceof Date) ps.setDate(idx, (Date) p);
            else ps.setString(idx, String.valueOf(p));
        }
    }
}

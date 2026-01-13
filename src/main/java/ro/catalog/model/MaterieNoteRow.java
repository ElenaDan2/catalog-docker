package ro.catalog.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MaterieNoteRow {
    private final String materie;
    private final List<NotaView> note = new ArrayList<>();

    public MaterieNoteRow(String materie) {
        this.materie = materie;
    }

    public String getMaterie() { return materie; }

    public List<NotaView> getNote() { return Collections.unmodifiableList(note); }

    public void add(NotaView n) { note.add(n); }

    public int getNrNote() { return note.size(); }

    public double getMedie() {
        if (note.isEmpty()) return 0.0;
        int s = 0;
        for (NotaView n : note) s += n.getValoare();
        return (double) s / (double) note.size();
    }

    public LocalDate getUltimaData() {
        LocalDate max = null;
        for (NotaView n : note) {
            if (n.getData() == null) continue;
            if (max == null || n.getData().isAfter(max)) max = n.getData();
        }
        return max;
    }

    public int getUltimaNota() {
        NotaView best = null;
        for (NotaView n : note) {
            if (n.getData() == null) continue;
            if (best == null || n.getData().isAfter(best.getData())) best = n;
        }
        return best == null ? 0 : best.getValoare();
    }
}

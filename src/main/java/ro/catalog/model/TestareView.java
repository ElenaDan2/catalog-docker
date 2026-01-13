package ro.catalog.model;

import java.time.LocalDate;

public class TestareView {
    private final int id;
    private final LocalDate data;
    private final String tip;
    private final String descriere;
    private final String materie;

    public TestareView(int id, LocalDate data, String tip, String descriere, String materie) {
        this.id = id;
        this.data = data;
        this.tip = tip;
        this.descriere = descriere;
        this.materie = materie;
    }

    public int getId() { return id; }
    public LocalDate getData() { return data; }
    public String getTip() { return tip; }
    public String getDescriere() { return descriere; }
    public String getMaterie() { return materie; }
}

package ro.catalog.model;

import java.time.LocalDate;

public class NotaView {
    private final LocalDate data;
    private final String materie;
    private final int valoare;

    public NotaView(LocalDate data, String materie, int valoare) {
        this.data = data;
        this.materie = materie;
        this.valoare = valoare;
    }

    public LocalDate getData() { return data; }
    public String getMaterie() { return materie; }
    public int getValoare() { return valoare; }
}

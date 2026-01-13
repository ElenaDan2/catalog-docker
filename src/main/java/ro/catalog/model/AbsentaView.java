package ro.catalog.model;

import java.time.LocalDate;

public class AbsentaView {
    private final LocalDate data;
    private final String materie;
    private final boolean motivata;

    public AbsentaView(LocalDate data, String materie, boolean motivata) {
        this.data = data;
        this.materie = materie;
        this.motivata = motivata;
    }

    public LocalDate getData() { return data; }
    public String getMaterie() { return materie; }
    public boolean isMotivata() { return motivata; }
}

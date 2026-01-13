package ro.catalog.model;

import java.io.Serializable;

public class ProfesorRaportSummary implements Serializable {
    private static final long serialVersionUID = 1L;

    private final int totalNote;
    private final double medieGenerala;
    private final int totalAbsente;
    private final int absMotivate;
    private final int absNemotivate;

    public ProfesorRaportSummary(int totalNote, double medieGenerala,
                                 int totalAbsente, int absMotivate, int absNemotivate) {
        this.totalNote = totalNote;
        this.medieGenerala = medieGenerala;
        this.totalAbsente = totalAbsente;
        this.absMotivate = absMotivate;
        this.absNemotivate = absNemotivate;
    }

    public int getTotalNote() { return totalNote; }
    public double getMedieGenerala() { return medieGenerala; }
    public int getTotalAbsente() { return totalAbsente; }
    public int getAbsMotivate() { return absMotivate; }
    public int getAbsNemotivate() { return absNemotivate; }
}

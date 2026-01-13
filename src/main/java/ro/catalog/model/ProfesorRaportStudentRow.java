package ro.catalog.model;

import java.io.Serializable;

public class ProfesorRaportStudentRow implements Serializable {
    private static final long serialVersionUID = 1L;

    private final int studentId;
    private final String studentNume;
    private final String clasaNume;
    private final int nrNote;
    private final Double medie; // poate fi null dacă nu are note
    private final int absMotivate;
    private final int absNemotivate;

    public ProfesorRaportStudentRow(int studentId, String studentNume, String clasaNume,
                                    int nrNote, Double medie, int absMotivate, int absNemotivate) {
        this.studentId = studentId;
        this.studentNume = studentNume;
        this.clasaNume = clasaNume;
        this.nrNote = nrNote;
        this.medie = medie;
        this.absMotivate = absMotivate;
        this.absNemotivate = absNemotivate;
    }

    public int getStudentId() { return studentId; }
    public String getStudentNume() { return studentNume; }
    public String getClasaNume() { return clasaNume; }
    public int getNrNote() { return nrNote; }
    public Double getMedie() { return medie; }
    public int getAbsMotivate() { return absMotivate; }
    public int getAbsNemotivate() { return absNemotivate; }
}

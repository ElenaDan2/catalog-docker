package ro.catalog.model;

import java.time.LocalDate;

public class ProfesorNotaView {
    private int id;
    private LocalDate data;
    private int valoare;
    private String studentNume;
    private String clasaNume;
    private String materie;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }

    public int getValoare() { return valoare; }
    public void setValoare(int valoare) { this.valoare = valoare; }

    public String getStudentNume() { return studentNume; }
    public void setStudentNume(String studentNume) { this.studentNume = studentNume; }

    public String getClasaNume() { return clasaNume; }
    public void setClasaNume(String clasaNume) { this.clasaNume = clasaNume; }

    public String getMaterie() { return materie; }
    public void setMaterie(String materie) { this.materie = materie; }
}

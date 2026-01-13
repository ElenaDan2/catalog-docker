// ===== Student.java (ro.catalog.model) - ADAUGA ASTE DOUA CONSTRUCTOARE =====
package ro.catalog.model;

import java.time.LocalDate;
import java.time.Period;

public class Student {
    private int id;
    private String nume;
    private String email;
    private String parola;
    private LocalDate dataNasterii;
    private boolean activ;
    private Integer clasaId;
    private String clasaNume; // ex: "12A"

    public Student() {}

    public Student(String nume, String email, String parola, LocalDate dataNasterii, boolean activ) {
        this.nume = nume;
        this.email = email;
        this.parola = parola;
        this.dataNasterii = dataNasterii;
        this.activ = activ;
    }

    public Student(int id, String nume, String email, String parola, LocalDate dataNasterii, boolean activ) {
        this.id = id;
        this.nume = nume;
        this.email = email;
        this.parola = parola;
        this.dataNasterii = dataNasterii;
        this.activ = activ;
    }

    // ✅ ca sa-ti dispara erorile din StudentServlet (cele cu (String,String,int) si (int,String,String,int))
    public Student(String nume, String email, int varsta) {
        this(nume, email, "", LocalDate.now().minusYears(varsta), true);
    }

    public Student(int id, String nume, String email, int varsta) {
        this(id, nume, email, "", LocalDate.now().minusYears(varsta), true);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNume() { return nume; }
    public void setNume(String nume) { this.nume = nume; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getParola() { return parola; }
    public void setParola(String parola) { this.parola = parola; }

    public LocalDate getDataNasterii() { return dataNasterii; }
    public void setDataNasterii(LocalDate dataNasterii) { this.dataNasterii = dataNasterii; }

    public boolean isActiv() { return activ; }
    public void setActiv(boolean activ) { this.activ = activ; }

    public int getVarsta() {
        if (dataNasterii == null) return 0;
        return Period.between(dataNasterii, LocalDate.now()).getYears();
    }

    public Integer getClasaId() { return clasaId; }
    public void setClasaId(Integer clasaId) { this.clasaId = clasaId; }

    public String getClasaNume() { return clasaNume; }
    public void setClasaNume(String clasaNume) { this.clasaNume = clasaNume; }
}

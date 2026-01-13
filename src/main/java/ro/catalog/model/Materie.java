package ro.catalog.model;

import java.io.Serializable;

public class Materie implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String nume;
    private int profesorId;

    public Materie() {}

    public Materie(int id, String nume, int profesorId) {
        this.id = id;
        this.nume = nume;
        this.profesorId = profesorId;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getNume() {
        return nume;
    }
    public void setNume(String nume) {
        this.nume = nume;
    }

    public int getProfesorId() {
        return profesorId;
    }
    public void setProfesorId(int profesorId) {
        this.profesorId = profesorId;
    }
}

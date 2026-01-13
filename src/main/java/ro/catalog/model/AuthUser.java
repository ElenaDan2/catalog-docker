package ro.catalog.model;

import java.io.Serializable;

public class AuthUser implements Serializable {
	private static final long serialVersionUID = 1L;

    private final int id;
    private final String nume;
    private final String email;
    private final String rol; // "PROFESOR" / "STUDENT"

    public AuthUser(int id, String nume, String email, String rol) {
        this.id = id;
        this.nume = nume;
        this.email = email;
        this.rol = rol;
    }

    public int getId() { return id; }
    public String getNume() { return nume; }
    public String getEmail() { return email; }
    public String getRol() { return rol; }
}

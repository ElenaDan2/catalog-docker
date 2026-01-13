package ro.catalog.model;

public class Clasa {
    private int id;
    private int an;
    private String litera;

    public Clasa() {}

    public Clasa(int id, int an, String litera) {
        this.id = id;
        this.an = an;
        this.litera = litera;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getAn() { return an; }
    public void setAn(int an) { this.an = an; }

    public String getLitera() { return litera; }
    public void setLitera(String litera) { this.litera = litera; }

    public String getNume() { return an + litera; } // ex "12A"
}

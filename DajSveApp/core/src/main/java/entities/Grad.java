package entities;

import java.util.List;

/**
 * Created by Filip on 8.11.2016..
 */

public class Grad {

    int id;
    String naziv;

    public Grad() {
    }

    public Grad(int id, String naziv) {
        this.id = id;
        this.naziv = naziv;
    }

    public String getNaziv() {
        return naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public static List<Grad> getAll(){

        //ovo jos nisam napravio i nemam pojma sta trebam
        List<Grad> lista = null;

        return lista;
    }
}

package org.gymify.entities;

import java.util.Date;

public class Responsable_Salle extends User {
    public int id_salle;
    public Responsable_Salle(int id, int id_salle,String nom, String prenom, String email, String password, Date date_naissance, String image_url) {
        super(String.valueOf(id), nom, prenom, email, password,date_naissance, image_url);
    }

    public Responsable_Salle(String nom, String prenom, String email, String password,
                             Date date_naissance, String image_url) {
        super(nom, prenom, email, password, "Responsable_Salle", date_naissance, image_url);}
    public int getId_Salle() {
        return id_salle;
    }

    public void setId_Salle(int id_Salle) {
        this.id_salle = id_Salle;
    }
    @Override
    public String toString() {
        return "Responsable_Salle{" + super.toString() + "}";
    }
}

package org.gymify.entities;

import java.util.Date;

public class Responsable_Salle extends User {
    public int id_salle;
    public Responsable_Salle(int id_User, int id_salle,String nom, String prenom, String email, String password, Date dateNaissance, String imageURL) {
        super(String.valueOf(id_User), nom, prenom, email, password,dateNaissance, imageURL);
        this.id_salle = id_User;
    }

    public Responsable_Salle(String nom, String prenom, String email, String password,
                      Date dateNaissance, String imageURL) {
        super(nom, prenom, email, password, "responsable_salle", dateNaissance, imageURL);}
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

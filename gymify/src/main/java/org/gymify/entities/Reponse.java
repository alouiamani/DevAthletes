package org.gymify.entities;

import java.sql.Timestamp;

public class Reponse {
    private int id_Reponse;
    private int id_rec; // ID de la réclamation
    private int id_user_admin; // ID de l'Admin qui répond
    private String message;
    private Timestamp dateReponse;
    private Reclamation reclamation; // Association avec la réclamation

    public Reponse() {}

    public Reponse(int id_Reponse, int id_rec, int id_user_admin, String message, Timestamp dateReponse) {
        this.id_Reponse = id_Reponse;
        this.id_rec = id_rec;
        this.id_user_admin = id_user_admin;
        this.message = message;
        this.dateReponse = dateReponse;
    }

    public Reponse(int id_rec, int id_user_admin, String message) {
        this.id_rec = id_rec;
        this.id_user_admin = id_user_admin;
        this.message = message;
        this.dateReponse = new Timestamp(System.currentTimeMillis()); // Date actuelle
    }

    public Reponse(int idReponse, int idRec, String message, Timestamp dateReponse) {
    }

    public int getId_Reponse() {
        return id_Reponse;
    }

    public void setId_Reponse(int id_Reponse) {
        this.id_Reponse = id_Reponse;
    }

    public int getId_rec() {
        return id_rec;
    }

    public void setId_rec(int id_rec) {
        this.id_rec = id_rec;
    }

    public int getId_user_admin() {
        return id_user_admin;
    }

    public void setId_user_admin(int id_user_admin) {
        this.id_user_admin = id_user_admin;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Timestamp getDateReponse() {
        return dateReponse;
    }

    public void setDateReponse(Timestamp dateReponse) {
        this.dateReponse = dateReponse;
    }

    public Reclamation getReclamation() {
        return reclamation;
    }

    public void setReclamation(Reclamation reclamation) {
        this.reclamation = reclamation;
    }

    @Override
    public String toString() {
        return "Reponse{" +
                "id_Reponse=" + id_Reponse +
                ", id_rec=" + id_rec +
                ", id_user_admin=" + id_user_admin +
                ", message='" + message + '\'' +
                ", dateReponse=" + dateReponse +
                ", reclamation=" + reclamation +
                '}';
    }
}

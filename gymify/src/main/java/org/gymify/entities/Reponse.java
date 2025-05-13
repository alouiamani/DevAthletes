package org.gymify.entities;

import java.sql.Timestamp;

public class Reponse {
    private int id;
    private int reclamation_id;
    private int admin_id;
    private String message;
    private Timestamp dateReponse;
    private Reclamation reclamation;

    public Reponse() {}

    public Reponse(int id, int reclamation_id, int admin_id, String message, Timestamp dateReponse) {
        this.id = id;
        this.reclamation_id = reclamation_id;
        this.admin_id = admin_id;
        this.message = message;
        this.dateReponse = dateReponse;
    }

    public Reponse(int reclamation_id, int admin_id, String message) {
        this.reclamation_id = reclamation_id;
        this.admin_id = admin_id;
        this.message = message;
        this.dateReponse = new Timestamp(System.currentTimeMillis());
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getReclamation_id() {
        return reclamation_id;
    }

    public void setReclamation_id(int reclamation_id) {
        this.reclamation_id = reclamation_id;
    }

    public int getAdmin_id() {
        return admin_id;
    }

    public void setAdmin_id(int admin_id) {
        this.admin_id = admin_id;
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
                "id=" + id +
                ", reclamation_id=" + reclamation_id +
                ", admin_id=" + admin_id +
                ", message='" + message + '\'' +
                ", dateReponse=" + dateReponse +
                ", reclamation=" + reclamation +
                '}';
    }
}
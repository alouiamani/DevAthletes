package entities;

import java.sql.Timestamp;

public class Reponse {
    private int id_Reponse;
    private int id_rec;
    private String message;
    private Timestamp dateReponse;
    private Reclamation reclamation;
    public Reponse() {}
    public Reponse(int id_Reponse, int id_rec, String message, Timestamp dateReponse) {
        this.id_Reponse = id_Reponse;
        this.id_rec = id_rec;
        this.message = message;
        this.dateReponse = dateReponse;
    }
    public Reponse(int id_rec, String message) {
        this.id_rec = id_rec;
        this.message = message;

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
                ", message='" + message + '\'' +
                ", dateReponse=" + dateReponse +
                ", reclamation=" + reclamation +
                '}';
    }
}

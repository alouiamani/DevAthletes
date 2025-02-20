package Gimify.Pi.entities;

public class Reclamation {
    private int id_reclamation;
    private int id_user;
    private String sujet;
    private String description;
    private String statut;
    private User user;
    public Reclamation() {}
    public Reclamation(int id_reclamation, int id_user, String sujet, String description, String statut) {
        this.id_reclamation = id_reclamation;
        this.id_user = id_user;
        this.sujet = sujet;
        this.description = description;
        this.statut = statut;

    }
    public Reclamation(int id_user, String sujet, String description, String statut, User user) {
        this.id_user = id_user;
        this.sujet = sujet;
        this.description = description;
        this.statut = statut;
        this.user = user;
    }

    public Reclamation(int id_user, String sujet, String description) {
        this.id_user = id_user;
        this.sujet = sujet;
        this.description = description;

    }



    public int getId_reclamation() {
        return id_reclamation;
    }

    public void setId_reclamation(int id_reclamation) {
        this.id_reclamation = id_reclamation;
    }

    public int getId_user() {
        return id_user;
    }

    public void setId_user(int id_user) {
        this.id_user = id_user;
    }

    public String getSujet() {
        return sujet;
    }

    public void setSujet(String sujet) {
        this.sujet = sujet;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Reclamation{" +
                "id_reclamation=" + id_reclamation +
                ", id_user=" + id_user +
                ", sujet='" + sujet + '\'' +
                ", description='" + description + '\'' +
                ", statut='" + (statut != null ? statut : "Non défini") + '\'' +
                ", user=" + (user != null ? user.getNom() : "Aucun") +
                '}';
    }


}

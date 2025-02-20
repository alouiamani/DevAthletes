package tn.gimify.entities;
import jakarta.persistence.*;
import java.util.Date;

public class Abonnement {
    private int id_Abonnement;
    private Date Date_Début;
    private Date Date_Fin;
    private type_Abonnement type_Abonnement;

    @ManyToOne
    @JoinColumn(name = "id_Salle")
    private Salle salle;
    public Abonnement() {}

    public Abonnement( Date Date_Début , Date Date_Fin, type_Abonnement type_Abonnement) {

        this.Date_Début = Date_Début;
        this.Date_Fin = Date_Fin;
        this.type_Abonnement = type_Abonnement;
    }

    public Abonnement(int id_Abonnement , Date Date_Début , Date Date_Fin, type_Abonnement type_Abonnement) {
        this.id_Abonnement = id_Abonnement;
        this.Date_Début = Date_Début;
        this.Date_Fin = Date_Fin;
        this.type_Abonnement = type_Abonnement;
    }
    public int getId_Abonnement() {
        return id_Abonnement;
    }
    public void setId_Abonnement(int id_Abonnement) {
        this.id_Abonnement = id_Abonnement;
    }

    public Date getDate_Début() {
        return Date_Début;
    }

    public void setDate_Début(Date date_Début) {
        Date_Début = date_Début;
    }
    public Date getDate_Fin() {
        return Date_Fin;
    }
    public void setDate_Fin(Date date_Fin) {
        Date_Fin = date_Fin;
    }
    public type_Abonnement getType_Abonnement() {
        return type_Abonnement;
    }
    public void setType_Abonnement(type_Abonnement type_Abonnement) {
        this.type_Abonnement = type_Abonnement;
    }
    public Salle getSalle() { return salle; }
    public void setSalle(Salle salle) { this.salle = salle; }
    @Override
    public String toString() {
        return "l'id d'abonnement est : " + id_Abonnement + " le date de debut de l'abonnement est : " + Date_Début
                + " le date de fin d'abonnement est : " + Date_Fin + " le type d'abonnement est : " + type_Abonnement;
    }
}

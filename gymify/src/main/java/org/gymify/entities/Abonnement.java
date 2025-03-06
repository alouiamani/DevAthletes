package org.gymify.entities;

import java.sql.Date;

public class Abonnement {
    private int id_Abonnement;
    private Date Date_Début;
    private Date Date_Fin;
    private type_Abonnement type_Abonnement;
    private Double tarif;
<<<<<<< HEAD
=======
    private Salle salle;
    private Activité activite; // Added field for Activité
>>>>>>> aea46390f69b2bb6a9b587a90aaa9dc13d0bcff7

    private Salle salle;
    private Activité activite;

    public String getTypeActivite() {
        return typeActivite;
    }

    public void setTypeActivite(String typeActivite) {
        this.typeActivite = typeActivite;
    }

    private String typeActivite;
    public Abonnement() {}

<<<<<<< HEAD


    public Abonnement(Date Date_Début, Date Date_Fin, type_Abonnement type_Abonnement, Salle salle, Double tarif, Activité activite,String typeActivite) {
=======
    public Abonnement(Date Date_Début, Date Date_Fin, type_Abonnement type_Abonnement, Salle salle, Double tarif) {
>>>>>>> aea46390f69b2bb6a9b587a90aaa9dc13d0bcff7
        this.Date_Début = Date_Début;
        this.Date_Fin = Date_Fin;
        this.type_Abonnement = type_Abonnement;
        this.salle = salle;
        this.tarif = tarif;
        this.activite = activite;
        this.typeActivite = typeActivite;


    }

    public int getId_Abonnement() {
        return id_Abonnement;
    }

    public void setId_Abonnement(int idAbonnement) {
        this.id_Abonnement = idAbonnement;
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

    public Double getTarif() {
        return tarif;
    }

    public void setTarif(Double tarif) {
        this.tarif = tarif;
    }

    public Salle getSalle() {
        return salle;
    }

    public void setSalle(Salle salle) {
        this.salle = salle;
    }

<<<<<<< HEAD
=======
    // Added getter and setter for Activité
>>>>>>> aea46390f69b2bb6a9b587a90aaa9dc13d0bcff7
    public Activité getActivite() {
        return activite;
    }

    public void setActivite(Activité activite) {
        this.activite = activite;
    }

<<<<<<< HEAD



=======
>>>>>>> aea46390f69b2bb6a9b587a90aaa9dc13d0bcff7
    @Override
    public String toString() {
        return "Abonnement ID: " + id_Abonnement +
                ", Début: " + Date_Début +
                ", Fin: " + Date_Fin +
                ", Type: " + type_Abonnement +
                ", Tarif: " + tarif + "DT" +
                ", Activité: " + (activite != null ? activite.getNom() : "Aucune");
    }
<<<<<<< HEAD


}
=======
}
>>>>>>> aea46390f69b2bb6a9b587a90aaa9dc13d0bcff7

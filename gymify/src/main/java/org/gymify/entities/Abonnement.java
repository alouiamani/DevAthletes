package org.gymify.entities;

import java.sql.Date;

public class Abonnement {
    private int id_Abonnement;
    private Date Date_Début;
    private Date Date_Fin;
    private type_Abonnement type_Abonnement;
    private Double tarif;
    private Salle salle;
    private Activité activite; // Added field for Activité

    public Abonnement() {}

    public Abonnement(Date Date_Début, Date Date_Fin, type_Abonnement type_Abonnement, Salle salle, Double tarif) {
        this.Date_Début = Date_Début;
        this.Date_Fin = Date_Fin;
        this.type_Abonnement = type_Abonnement;
        this.salle = salle;
        this.tarif = tarif;
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

    public Double getTarif() {
        return tarif;
    }

    public void setTarif(Double tarif) {
        this.tarif = tarif;
    }

    public Salle getSalle() {
        System.out.println("Appel de getSalle() - Salle : " + salle);
        return salle;
    }

    public void setSalle(Salle salle) {
        this.salle = salle;
    }

    // Added getter and setter for Activité
    public Activité getActivite() {
        return activite;
    }

    public void setActivite(Activité activite) {
        this.activite = activite;
    }

    @Override
    public String toString() {
        return "Abonnement ID: " + id_Abonnement +
                ", Début: " + Date_Début +
                ", Fin: " + Date_Fin +
                ", Type: " + type_Abonnement +
                ", Tarif: " + tarif + "DT";
    }
}
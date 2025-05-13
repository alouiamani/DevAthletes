package org.gymify.entities;

public class Abonnement {
    private int id_Abonnement;
    private type_Abonnement type_Abonnement;
    private Double tarif;
    private Salle salle;
    private Activité activite;
    private String typeActivite;

    public Abonnement() {}

    public Abonnement(type_Abonnement type_Abonnement, Salle salle, Double tarif, Activité activite, String typeActivite) {
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

    public Activité getActivite() {
        return activite;
    }

    public void setActivite(Activité activite) {
        this.activite = activite;
    }

    public String getTypeActivite() {
        return typeActivite;
    }

    public void setTypeActivite(String typeActivite) {
        this.typeActivite = typeActivite;
    }

    @Override
    public String toString() {
        return "Abonnement ID: " + id_Abonnement +
                ", Type: " + type_Abonnement +
                ", Tarif: " + tarif + "DT" +
                ", Activité: " + (activite != null ? activite.getNom() : "Aucune");
    }
}
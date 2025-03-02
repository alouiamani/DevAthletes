package org.gymify.entities;


public class AbonnementData {
    private Long Id_Abonnement;
    private Long sportifId;
    private Long Salle_Id;
    private double montant;

    // Constructeur
    public AbonnementData(Long Id_Abonnement, Long sportifId, Long Salle_Id, double montant) {
        this.Id_Abonnement = Id_Abonnement;
        this.sportifId = sportifId;
        this.Salle_Id = Salle_Id;
        this.montant = montant;
    }

    // Getters et Setters
    public Long getAbonnementId() {
        return Id_Abonnement;
    }

    public void setAbonnementId(Long abonnementId) {
        this.Id_Abonnement = abonnementId;
    }

    public Long getSportifId() {
        return sportifId;
    }

    public void setSportifId(int sportifId) {
        this.sportifId = (long) sportifId;
    }

    public Long getSalleId() {
        return Salle_Id;
    }

    public void setSalleId(Long salleId) {
        this.Salle_Id = salleId;
    }

    public double getMontant() {
        return montant;
    }

    public void setMontant(double montant) {
        this.montant = montant;
    }
}
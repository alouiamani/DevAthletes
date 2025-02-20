package tn.gimify.entities;



import java.time.LocalDate;

public class Paiement {
    private int idPaiement;
    private int idAbonnement;
    private int idUser;
    private double montant;
    private LocalDate datePaiement;
    private String methode;
    private String statut;

    // Constructeur
    public Paiement(int idAbonnement, int idUser, double montant, LocalDate datePaiement, String methode, String statut) {
        this.idAbonnement = idAbonnement;
        this.idUser = idUser;
        this.montant = montant;
        this.datePaiement = datePaiement;
        this.methode = methode;
        this.statut = statut;
    }

    // Getters et Setters
    public int getIdPaiement() { return idPaiement; }
    public void setIdPaiement(int idPaiement) { this.idPaiement = idPaiement; }

    public int getIdAbonnement() { return idAbonnement; }
    public void setIdAbonnement(int idAbonnement) { this.idAbonnement = idAbonnement; }

    public int getIdUser() { return idUser; }
    public void setIdUser(int idUser) { this.idUser = idUser; }

    public double getMontant() { return montant; }
    public void setMontant(double montant) { this.montant = montant; }

    public LocalDate getDatePaiement() { return datePaiement; }
    public void setDatePaiement(LocalDate datePaiement) { this.datePaiement = datePaiement; }

    public String getMethode() { return methode; }
    public void setMethode(String methode) { this.methode = methode; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
}


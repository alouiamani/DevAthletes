package org.gymify.entities;



import com.google.api.client.util.DateTime;

import java.time.LocalDate;

public class Paiement {
    private int id_paiement;
    private double amount;
    private String statut;
    private String payment_intent_id;
    private DateTime created_at;
    private DateTime updated_at;
    private String currency;
    private DateTime date_debut;
    private DateTime date_fin;
    private int id_user;
    private int id_abonnement;

    public Paiement() {
    }
    public Paiement(int id_paiement, double amount,String statut,String payment_intent_id,DateTime created_at,DateTime updated_at,String currency,DateTime date_debut,DateTime date_fin,int id_user,int id_abonnement) {
        this.id_paiement = id_paiement;
        this.amount = amount;
        this.statut = statut;
        this.payment_intent_id = payment_intent_id;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.currency = currency;
        this.date_debut = date_debut;
        this.date_fin = date_fin;
        this.id_user = id_user;
        this.id_abonnement = id_abonnement;
    }
    public int getId_abonnement() {
        return id_abonnement;
    }

    public void setId_abonnement(int id_abonnement) {
        this.id_abonnement = id_abonnement;
    }

    public int getId_user() {
        return id_user;
    }

    public void setId_user(int id_user) {
        this.id_user = id_user;
    }

    public DateTime getDate_fin() {
        return date_fin;
    }

    public void setDate_fin(DateTime date_fin) {
        this.date_fin = date_fin;
    }

    public DateTime getDate_debut() {
        return date_debut;
    }

    public void setDate_debut(DateTime date_debut) {
        this.date_debut = date_debut;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public DateTime getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(DateTime updated_at) {
        this.updated_at = updated_at;
    }

    public DateTime getCreated_at() {
        return created_at;
    }

    public void setCreated_at(DateTime created_at) {
        this.created_at = created_at;
    }

    public String getPayment_intent_id() {
        return payment_intent_id;
    }

    public void setPayment_intent_id(String payment_intent_id) {
        this.payment_intent_id = payment_intent_id;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public int getId_paiement() {
        return id_paiement;
    }

    public void setId_paiement(int id_paiement) {
        this.id_paiement = id_paiement;
    }
}


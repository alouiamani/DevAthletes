package org.gymify.entities;

import java.util.Date;

public class Produit {
    private int id_p;
    private String nom_p;
    private double prix_p;
    private int stock_p;
    private String categorie_p;
    private String image_path;
    private Date updated_at;

    public Produit() {}

    public Produit(int id_p, String nom_p, double prix_p, int stock_p, String categorie_p, String image_path, Date updated_at) {
        this.id_p = id_p;
        this.nom_p = nom_p;
        this.prix_p = prix_p;
        this.stock_p = stock_p;
        this.categorie_p = categorie_p;
        this.image_path = image_path;
        this.updated_at = updated_at;
    }

    public int getId_p() { return id_p; }
    public void setId_p(int id_p) { this.id_p = id_p; }

    public String getNom_p() { return nom_p; }
    public void setNom_p(String nom_p) { this.nom_p = nom_p; }

    public double getPrix_p() { return prix_p; }
    public void setPrix_p(double prix_p) { this.prix_p = prix_p; }

    public int getStock_p() { return stock_p; }
    public void setStock_p(int stock_p) { this.stock_p = stock_p; }

    public String getCategorie_p() { return categorie_p; }
    public void setCategorie_p(String categorie_p) { this.categorie_p = categorie_p; }

    public String getImage_path() { return image_path; }
    public void setImage_path(String image_path) { this.image_path = image_path; }

    public Date getUpdated_at() { return updated_at; }
    public void setUpdated_at(Date updated_at) { this.updated_at = updated_at; }
} 
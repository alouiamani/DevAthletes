package entities;

import java.util.ArrayList;
import java.util.List;

public class Produit {
    private int id_p;
    private String nom_p;
    private float prix_p;
    private int stock_p;
    private String categorie_p;
    private String image_path;

    private List<Commande> commandes = new ArrayList<>();

    public Produit() {
    }

    public Produit(int id_p, String nom_p, float prix_p, int stock_p, String categorie_p) {
        this.id_p = id_p;
        this.nom_p = nom_p;
        this.prix_p = prix_p;
        this.stock_p = stock_p;
        this.categorie_p = categorie_p;
    }

    public Produit(String nom_p, float prix_p, int stock_p, String categorie_p) {
        this.nom_p = nom_p;
        this.prix_p = prix_p;
        this.stock_p = stock_p;
        this.categorie_p = categorie_p;
    }

    public Produit(String nom_p, float prix_p, int stock_p, String categorie_p, String image_path) {
        this.nom_p = nom_p;
        this.prix_p = prix_p;
        this.stock_p = stock_p;
        this.categorie_p = categorie_p;
        this.image_path = image_path;
    }

    public Produit(int id_p, String nom_p, float prix_p, int stock_p, String categorie_p, String image_path) {
        this.id_p = id_p;
        this.nom_p = nom_p;
        this.prix_p = prix_p;
        this.stock_p = stock_p;
        this.categorie_p = categorie_p;
        this.image_path = image_path;
    }

    public int getId_p() {
        return id_p;
    }

    public void setId_p(int id_p) {
        this.id_p = id_p;
    }

    public String getNom_p() {
        return nom_p;
    }

    public void setNom_p(String nom_p) {
        this.nom_p = nom_p;
    }

    public float getPrix_p() {
        return prix_p;
    }

    public void setPrix_p(float prix_p) {
        this.prix_p = prix_p;
    }

    public int getStock_p() {
        return stock_p;
    }

    public void setStock_p(int stock_p) {
        this.stock_p = stock_p;
    }

    public String getCategorie_p() {
        return categorie_p;
    }

    public void setCategorie_p(String categorie_p) {
        this.categorie_p = categorie_p;
    }

    public String getImage_path() {
        return image_path;
    }

    public void setImage_path(String image_path) {
        this.image_path = image_path;
    }

    public void ajouterCommande(Commande commande) {
        this.commandes.add(commande);
    }

    public List<Commande> getCommandes() {
        return commandes;
    }

    @Override
    public String toString() {
        return "Produit{" +
                "id_p=" + id_p +
                ", nom_p='" + nom_p + '\'' +
                ", prix_p=" + prix_p +
                ", stock_p=" + stock_p +
                ", categorie_p='" + categorie_p + '\'' +
                ", image_path='" + image_path + '\'' +
                '}';
    }
}

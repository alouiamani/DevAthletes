package entities;
import java.util.List;
import java.util.ArrayList;
import java.sql.Timestamp;

public class Commande {
    private int id_c;
    private float total_c;
    private String statut_c;
    private Timestamp dateC;
    private List<CommandeProduit> commandeProduits = new ArrayList<>();
    private int user_id;
    private List<Produit> produits;

    public Commande() {}

    public Commande(int id_c, float total_c, String statut_c, Timestamp dateC) {
        this.id_c = id_c;
        this.total_c = total_c;
        this.statut_c = statut_c;
        this.dateC = dateC;
    }

    public Commande(float total_c, String statut_c) {
        this.total_c = total_c;
        this.statut_c = statut_c;
        this.dateC = new Timestamp(System.currentTimeMillis());
    }

    public int getId_c() {
        return id_c;
    }

    public void setId_c(int id_c) {
        this.id_c = id_c;
    }

    public float getTotal_c() {
        return total_c;
    }

    public void setTotal_c(float total_c) {
        this.total_c = total_c;
    }

    public String getStatut_c() {
        return statut_c;
    }

    public void setStatu_c(String statu_c) {
        this.statut_c = statu_c;
    }

    public Timestamp getDateC() {
        return dateC;
    }

    public void setDateC(Timestamp dateC) {
        this.dateC = dateC;
    }

    public List<CommandeProduit> getCommandeProduits() {
        return commandeProduits;
    }

    public void ajouterCommandeProduit(CommandeProduit commandeProduit) {
        this.commandeProduits.add(commandeProduit);
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public List<Produit> getProduits() {
        return produits;
    }

    public void setProduits(List<Produit> produits) {
        this.produits = produits;
    }

    @Override
    public String toString() {
        return "Commande{" +
                "id_c=" + id_c +
                ", total_c=" + total_c +
                ", statu_c='" + statut_c + '\'' +
                ", dateC=" + dateC +
                ", commandeProduits=" + commandeProduits +
                '}';
    }
}
package entities;
import java.util.List;
import java.util.ArrayList;
import java.sql.Date;

public class Commande {
    private int id_c;
    private float total_c;
    private String statut_c;
    private Date dateC;
    private List<CommandeProduit> commandeProduits = new ArrayList<>();

    public Commande() {}

    public Commande(int id_c, float total_c, String statut_c, Date dateC) {
        this.id_c = id_c;
        this.total_c = total_c;
        this.statut_c = statut_c;
        this.dateC = dateC;
    }

    public Commande(float total_c, String statut_c) {
        this.total_c = total_c;
        this.statut_c = statut_c;
        this.dateC = new Date(System.currentTimeMillis());
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

    public Date getDateC() {
        return dateC;
    }

    public void setDateC(Date dateC) {
        this.dateC = dateC;
    }

    public List<CommandeProduit> getCommandeProduits() {
        return commandeProduits;
    }

    public void ajouterCommandeProduit(CommandeProduit commandeProduit) {
        this.commandeProduits.add(commandeProduit);
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
package entities;



class CommandeProduit {
    private int commande_id;
    private int produit_id;
    private int quantite;

    public CommandeProduit(int commande_id, int produit_id, int quantite) {
        this.commande_id = commande_id;
        this.produit_id = produit_id;
        this.quantite = quantite;
    }

    public int getCommande_id() {
        return commande_id;
    }

    public void setCommande_id(int commande_id) {
        this.commande_id = commande_id;
    }

    public int getProduit_id() {
        return produit_id;
    }

    public void setProduit_id(int produit_id) {
        this.produit_id = produit_id;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    @Override
    public String toString() {
        return "CommandeProduit{" +
                "commande_id=" + commande_id +
                ", produit_id=" + produit_id +
                ", quantite=" + quantite +
                '}';
    }
}

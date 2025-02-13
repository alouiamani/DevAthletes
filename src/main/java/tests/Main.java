package tests;

import entities.Commande;
import entities.Produit;
import services.ServiceCommande;
import services.ServiceProduit;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        // Services pour manipuler les produits et commandes
        ServiceProduit serviceProduit = new ServiceProduit();
        ServiceCommande serviceCommande = new ServiceCommande();

        // Création d'un produit
        Produit p1 = new Produit("vitamine", 300, 40, "complement");

        // Variables pour stocker les IDs
        int produitId = -1;
        int commandeId = -1;

        try {
            // Ajouter un produit
            serviceProduit.ajouter(p1);
            System.out.println("Produit ajouté avec succès !");

            // Récupérer l'ID du produit ajouté
            for (Produit produit : serviceProduit.afficher()) {
                if (produit.getNom_p().equals(p1.getNom_p())) {
                    produitId = produit.getId_p();
                    break;
                }
            }

            if (produitId != -1) {
                System.out.println("ID du produit ajouté : " + produitId);

                // Créer une nouvelle commande
                Commande c1 = new Commande(0, "En cours"); // 0 total initial
                commandeId = serviceCommande.ajouter(c1);
                System.out.println("Commande créée avec succès avec ID : " + commandeId);

                // Lier le produit à la commande avec une quantité
                int quantite = 4;
                if (commandeId != -1) {
                    serviceCommande.ajouterCommandeProduit(commandeId, produitId, quantite);
                    System.out.println("Produit lié à la commande avec succès !");
                }

                // Mettre à jour le total de la commande
                double total = serviceProduit.getPrix(produitId) * quantite;
                serviceCommande.mettreAJourTotal(commandeId, total);
                System.out.println("Total de la commande mis à jour avec succès !");
            }

            // Afficher toutes les commandes
            System.out.println("\n=== Liste des commandes ===");
            for (Commande commande : serviceCommande.afficher()) {
                System.out.println(commande);
            }

        } catch (SQLException e) {
            System.out.println("Erreur lors de l'exécution : " + e.getMessage());
        }
    }
}

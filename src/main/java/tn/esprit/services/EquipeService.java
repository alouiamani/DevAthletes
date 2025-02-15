package tn.esprit.services;

import tn.esprit.entities.Equipe;
import tn.esprit.utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EquipeService implements IService<Equipe>{

    private Connection connection;
    private static final int MAX_MEMBRES = 6;
    private int membresActuels;

    public EquipeService() {
        connection = MyDataBase.getInstance().getConnection();
        if (connection == null) {
            System.err.println(" Erreur : Connexion à la base de données non établie !");
        } else {
            membresActuels = compterMembres();
        }
    }


    private int compterMembres() {
        String req = "SELECT COUNT(*) AS total FROM equipe";
        try (Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(req)) {
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            System.err.println(" Erreur lors du comptage des membres : " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }


    public void ajouter(Equipe equipe) throws SQLException {
        if (membresActuels >= MAX_MEMBRES) {
            System.err.println(" Impossible d'ajouter plus de " + MAX_MEMBRES + " membres dans l'équipe.");
            return;
        }

        String req = "INSERT INTO equipe (nom) VALUES (?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(req)) {
            preparedStatement.setString(1, equipe.getNom());
            preparedStatement.executeUpdate();
            membresActuels++; // Incrémenter le compteur après ajout
            System.out.println(" Équipe ajoutée avec succès ! Nombre total : " + membresActuels);
        } catch (SQLException e) {
            System.err.println(" Erreur lors de l'ajout de l'équipe : " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }


    public void modifier(Equipe equipe) throws SQLException {
        String req = "UPDATE equipe SET nom=? WHERE id=?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(req)) {
            preparedStatement.setString(1, equipe.getNom());
            preparedStatement.setInt(2, equipe.getId());
            int rowsUpdated = preparedStatement.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println(" Équipe modifiée avec succès !");
            } else {
                System.err.println(" Aucune équipe trouvée avec l'ID : " + equipe.getId());
            }
        } catch (SQLException e) {
            System.err.println(" Erreur lors de la modification de l'équipe : " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }


    public void supprimer(int id) throws SQLException {
        String req = "DELETE FROM equipe WHERE id=?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(req)) {
            preparedStatement.setInt(1, id);
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                membresActuels--; // Décrémenter le compteur après suppression réussie
                System.out.println(" Équipe supprimée avec succès ! Nombre total : " + membresActuels);
            } else {
                System.err.println(" Aucune équipe trouvée avec l'ID : " + id);
            }
        } catch (SQLException e) {
            System.err.println(" Erreur lors de la suppression de l'équipe : " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }


    public List<Equipe> afficher() throws SQLException {
        List<Equipe> equipes = new ArrayList<>();
        String req = "SELECT * FROM equipe";

        try (Statement statement = connection.createStatement(); ResultSet rs = statement.executeQuery(req)) {
            while (rs.next()) {
                Equipe equipe = new Equipe();
                equipe.setId(rs.getInt("id"));
                equipe.setNom(rs.getString("nom"));
                equipes.add(equipe);
            }
            System.out.println(" Liste des équipes récupérée avec succès.");
        } catch (SQLException e) {
            System.err.println(" Erreur lors de l'affichage des équipes : " + e.getMessage());
            e.printStackTrace();
            throw e;
        }

        return equipes;
    }


    public Equipe chercherParId(int id) throws SQLException {
        String req = "SELECT * FROM equipe WHERE id=?";
        Equipe equipe = null;

        try (PreparedStatement preparedStatement = connection.prepareStatement(req)) {
            preparedStatement.setInt(1, id);

            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    equipe = new Equipe();
                    equipe.setId(rs.getInt("id"));
                    equipe.setNom(rs.getString("nom"));
                    System.out.println(" Équipe trouvée : " + equipe.getNom());
                } else {
                    System.err.println(" Aucune équipe trouvée avec l'ID : " + id);
                }
            }
        } catch (SQLException e) {
            System.err.println(" Erreur lors de la recherche de l'équipe : " + e.getMessage());
            e.printStackTrace();
            throw e;
        }

        return equipe;
    }
}

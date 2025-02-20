package tn.esprit.services;

import tn.esprit.entities.Equipe;
import tn.esprit.utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EquipeService implements IService<Equipe> {
    private final Connection connection;

    public EquipeService() throws SQLException {
        connection = MyDataBase.getInstance().getConnection();
        if (connection == null) {
            throw new SQLException("Erreur : Connexion à la base de données non établie !");
        }
    }

    @Override
    public void ajouter(Equipe equipe) throws SQLException {
        String req = "INSERT INTO equipe (nom) VALUES (?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(req)) {
            preparedStatement.setString(1, equipe.getNom());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Erreur lors de l'ajout de l'équipe : " + e.getMessage());
        }
    }

    @Override
    public void modifier(Equipe equipe) throws SQLException {
        String req = "UPDATE equipe SET nom=? WHERE id=?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(req)) {
            preparedStatement.setString(1, equipe.getNom());
            preparedStatement.setInt(2, equipe.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Erreur lors de la modification de l'équipe : " + e.getMessage());
        }
    }

    @Override
    public void supprimer(int id) throws SQLException {
        String req = "DELETE FROM equipe WHERE id=?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(req)) {
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Erreur lors de la suppression de l'équipe : " + e.getMessage());
        }
    }

    @Override
    public List<Equipe> afficher() throws SQLException {
        List<Equipe> equipes = new ArrayList<>();
        String req = "SELECT * FROM equipe";
        try (Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(req)) {
            while (rs.next()) {
                Equipe equipe = new Equipe();
                equipe.setId(rs.getInt("id"));
                equipe.setNom(rs.getString("nom"));
                equipes.add(equipe);
            }
        } catch (SQLException e) {
            throw new SQLException("Erreur lors de la récupération des équipes : " + e.getMessage());
        }
        return equipes;
    }
}

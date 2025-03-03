package org.gymify.services;

import org.gymify.entities.Equipe;
import org.gymify.entities.EventType;
import org.gymify.utils.gymifyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EquipeService implements IServiceEvent<Equipe> {
    private final Connection connection;

    public EquipeService() throws SQLException {
        connection = gymifyDataBase.getInstance().getConnection();
        if (connection == null) {
            throw new SQLException("Erreur : Connexion à la base de données non établie !");
        }
    }

    @Override
    public void ajouter(Equipe equipe) throws SQLException {
        if (isNomEquipeExist(equipe.getNom())) {
            throw new SQLException("Une équipe avec ce nom existe déjà.");
        }
        String req = "INSERT INTO equipe (nom, image_url, niveau, nombre_membres) VALUES (?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(req)) {
            preparedStatement.setString(1, equipe.getNom());
            preparedStatement.setString(2, equipe.getImageUrl());
            preparedStatement.setString(3, equipe.getNiveau().toString());
            preparedStatement.setInt(4, equipe.getNombreMembres());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Erreur lors de l'ajout de l'équipe : " + e.getMessage());
        }
    }

    @Override
    public void modifier(Equipe equipe) throws SQLException {
        String req = "UPDATE equipe SET nom=?, image_url=?, niveau=?, nombre_membres=? WHERE id=?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(req)) {
            preparedStatement.setString(1, equipe.getNom());
            preparedStatement.setString(2, equipe.getImageUrl());
            preparedStatement.setString(3, equipe.getNiveau().toString());
            preparedStatement.setInt(4, equipe.getNombreMembres());
            preparedStatement.setInt(5, equipe.getId());
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
                equipe.setImageUrl(rs.getString("image_url"));
                equipe.setNiveau(EventType.Niveau.valueOf(rs.getString("niveau")));
                equipe.setNombreMembres(rs.getInt("nombre_membres"));
                equipes.add(equipe);
            }
        } catch (SQLException e) {
            throw new SQLException("Erreur lors de la récupération des équipes : " + e.getMessage());
        }
        return equipes;
    }

    public boolean isNomEquipeExist(String nom) throws SQLException {
        String req = "SELECT COUNT(*) FROM equipe WHERE nom = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(req)) {
            preparedStatement.setString(1, nom);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new SQLException("Erreur lors de la vérification de l'existence du nom de l'équipe : " + e.getMessage());
        }
        return false;
    }

    public List<Equipe> recuperer() throws SQLException {
        List<Equipe> equipes = new ArrayList<>();
        String req = "SELECT * FROM equipe";
        try (Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(req)) {
            while (rs.next()) {
                Equipe equipe = new Equipe(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("image_url"),
                        EventType.Niveau.valueOf(rs.getString("niveau")),
                        rs.getInt("nombre_membres")
                );
                equipes.add(equipe);
            }
        }
        return equipes;
    }
}

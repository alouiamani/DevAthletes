package org.gymify.services;

import org.gymify.entities.Equipe;
import org.gymify.entities.Event;
import org.gymify.entities.EventType;
import org.gymify.entities.User;
import org.gymify.utils.gymifyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EquipeService implements IServiceEvent<Equipe> {
    private final Connection connection;
    private final ServiceUser userService; // Add ServiceUser to handle User modifications

    public EquipeService() throws SQLException {
        connection = gymifyDataBase.getInstance().getConnection();
        if (connection == null) {
            throw new SQLException("Erreur : Connexion à la base de données non établie !");
        }
        userService = new ServiceUser(); // Initialize ServiceUser
    }

    @Override
    public void ajouter(Equipe equipe) throws SQLException {
        if (isNomEquipeExist(equipe.getNom())) {
            throw new SQLException("Une équipe avec ce nom existe déjà.");
        }
        String req = "INSERT INTO equipe (nom, image_url, niveau, nombre_membres) VALUES (?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(req, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, equipe.getNom());
            preparedStatement.setString(2, equipe.getImageUrl());
            preparedStatement.setString(3, equipe.getNiveau().toString());
            preparedStatement.setInt(4, equipe.getNombreMembres());
            preparedStatement.executeUpdate();
            try (ResultSet rs = preparedStatement.getGeneratedKeys()) {
                if (rs.next()) {
                    equipe.setId(rs.getInt(1));
                }
            }
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

    @Override
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
        } catch (SQLException e) {
            throw new SQLException("Erreur lors de la récupération des équipes : " + e.getMessage());
        }
        return equipes;
    }

    public boolean isNomEquipeExist(String nom) throws SQLException {
        String req = "SELECT COUNT(*) FROM equipe WHERE nom = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(req)) {
            preparedStatement.setString(1, nom);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Erreur lors de la vérification de l'existence du nom de l'équipe : " + e.getMessage());
        }
        return false;
    }

    // Method to get the number of Sportif members in an Equipe
    public int getNombreSportifsInEquipe(int equipeId) throws SQLException {
        String req = "SELECT COUNT(*) FROM user WHERE role = 'sportif' AND id_equipe = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(req)) {
            pstmt.setInt(1, equipeId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    // Method to add a Sportif to an Equipe
    public void addSportifToEquipe(User sportif, int equipeId) throws SQLException {
        // Check if the Sportif already belongs to an Equipe
        if (sportif.getId_equipe() != null) {
            throw new SQLException("Ce sportif appartient déjà à une équipe (ID: " + sportif.getId_equipe() + ").");
        }

        // Check the number of members in the Equipe
        int currentMembers = getNombreSportifsInEquipe(equipeId);
        if (currentMembers >= 8) {
            throw new SQLException("L'équipe est complète (8 membres maximum).");
        }

        // Update the Sportif's id_equipe using ServiceUser
        sportif.setId_equipe(equipeId);
        userService.modifier(sportif); // Use ServiceUser to modify the User

        // Update the Equipe's nombreMembres
        String updateEquipeReq = "UPDATE equipe SET nombre_membres = nombre_membres + 1 WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(updateEquipeReq)) {
            pstmt.setInt(1, equipeId);
            pstmt.executeUpdate();
        }
    }

    // Method to get Equipe by ID
    public Equipe getEquipeById(int equipeId) throws SQLException {
        String req = "SELECT * FROM equipe WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(req)) {
            pstmt.setInt(1, equipeId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Equipe equipe = new Equipe(
                            rs.getInt("id"),
                            rs.getString("nom"),
                            rs.getString("image_url"),
                            EventType.Niveau.valueOf(rs.getString("niveau")),
                            rs.getInt("nombre_membres")
                    );
                    return equipe;
                }
            }
        }
        return null;
    }
}
package org.gymify.services;

import org.gymify.entities.Equipe;
import org.gymify.entities.EventType;
import org.gymify.entities.User;
import org.gymify.utils.gymifyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class EquipeService implements IServiceEvent<Equipe> {
    private final Connection connection;
    private final ServiceUser userService;
    private static final Logger LOGGER = Logger.getLogger(EquipeService.class.getName());

    public EquipeService() throws SQLException {
        connection = gymifyDataBase.getInstance().getConnection();
        if (connection == null) {
            throw new SQLException("Erreur : Connexion à la base de données non établie !");
        }
        userService = new ServiceUser();
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
        }
    }

    @Override
    public void supprimer(int id) throws SQLException {
        String req = "DELETE FROM equipe WHERE id=?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(req)) {
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
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
        }
        return equipes;
    }

    @Override
    public List<Equipe> recuperer() throws SQLException {
        return afficher();
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
        }
        return false;
    }

    public void addSportifToEquipe(User sportif, int equipeId) throws SQLException {
        Equipe equipe = getEquipeById(equipeId);
        if (equipe == null) {
            throw new SQLException("Équipe avec ID " + equipeId + " non trouvée.");
        }

        int currentMembers = equipe.getNombreMembres();
        if (currentMembers >= 8) {
            throw new SQLException("L'équipe " + equipeId + " est complète (8 membres maximum).");
        }

        sportif.setId_equipe(equipeId);
        userService.modifier(sportif);

        int newCount = currentMembers + 1;
        String updateEquipe = "UPDATE equipe SET nombre_membres = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(updateEquipe)) {
            pstmt.setInt(1, newCount);
            pstmt.setInt(2, equipeId);
            pstmt.executeUpdate();
            equipe.setNombreMembres(newCount);
            LOGGER.info("Sportif " + sportif.getEmail() + " ajouté à l'équipe ID " + equipeId + ", nouveau nombre de membres: " + newCount);
        }
    }

    public Equipe getEquipeById(int equipeId) throws SQLException {
        String req = "SELECT * FROM equipe WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(req)) {
            pstmt.setInt(1, equipeId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Equipe(
                            rs.getInt("id"),
                            rs.getString("nom"),
                            rs.getString("image_url"),
                            EventType.Niveau.valueOf(rs.getString("niveau")),
                            rs.getInt("nombre_membres")
                    );
                }
            }
        }
        return null;
    }

    public boolean isUserInTeamForEvent(String email, int eventId) throws SQLException {
        String req = "SELECT COUNT(*) FROM user u " +
                "JOIN equipe_event ee ON u.equipe_id = ee.equipe_id " +
                "WHERE u.email = ? AND ee.event_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(req)) {
            pstmt.setString(1, email);
            pstmt.setInt(2, eventId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }
}
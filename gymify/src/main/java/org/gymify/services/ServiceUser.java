package org.gymify.services;

import org.gymify.entities.Entraineur;
import org.gymify.entities.User;
import org.gymify.utils.gymifyDataBase;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ServiceUser implements IGestionUser<User> {
    private final Connection connection;

    public ServiceUser() {
        this.connection = gymifyDataBase.getInstance().getConnection();
        if (this.connection == null) {
            throw new IllegalStateException("❌ La connexion à la base de données n'a pas été initialisée !");
        }
    }

    @Override
    public void ajouter(User user) throws SQLException {
        if (user == null || user.getEmail() == null || user.getNom() == null || user.getPrenom() == null) {
            throw new IllegalArgumentException("Tous les champs requis doivent être remplis !");
        }

        String req = "INSERT INTO user (nom, prenom, email, password, role, dateNaissance, imageURL, specialite, id_equipe) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(req, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, user.getNom());
            pstmt.setString(2, user.getPrenom());
            pstmt.setString(3, user.getEmail());
            String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt(12));
            pstmt.setString(4, hashedPassword);
            pstmt.setString(5, user.getRole());
            if (user.getDateNaissance() != null) {
                pstmt.setDate(6, new java.sql.Date(user.getDateNaissance().getTime()));
            } else {
                pstmt.setNull(6, java.sql.Types.DATE);
            }
            pstmt.setString(7, user.getImageURL());
            if (user instanceof Entraineur) {
                pstmt.setString(8, ((Entraineur) user).getSpecialite());
            } else {
                pstmt.setNull(8, java.sql.Types.VARCHAR);
            }
            if (user.getId_equipe() != null) {
                pstmt.setInt(9, user.getId_equipe());
            } else {
                pstmt.setNull(9, java.sql.Types.INTEGER);
            }

            pstmt.executeUpdate();

            // Retrieve the generated ID
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    user.setId_User(rs.getInt(1));
                }
            }
            System.out.println("✅ Utilisateur ajouté avec succès !");
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de l'ajout de l'utilisateur : " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void modifier(User user) throws SQLException {
        String req = "UPDATE user SET nom=?, prenom=?, email=?, password=?, role=?, dateNaissance=?, imageURL=?, specialite=?, id_equipe=? WHERE id_User=?";
        try (PreparedStatement pstmt = connection.prepareStatement(req)) {
            pstmt.setString(1, user.getNom());
            pstmt.setString(2, user.getPrenom());
            pstmt.setString(3, user.getEmail());
            pstmt.setString(4, BCrypt.hashpw(user.getPassword(), BCrypt.gensalt(12)));
            pstmt.setString(5, user.getRole());
            pstmt.setDate(6, user.getDateNaissance() != null ? new Date(user.getDateNaissance().getTime()) : null);
            pstmt.setString(7, user.getImageURL());
            if (user instanceof Entraineur) {
                pstmt.setString(8, ((Entraineur) user).getSpecialite());
            } else {
                pstmt.setNull(8, java.sql.Types.VARCHAR);
            }
            if (user.getId_equipe() != null) {
                pstmt.setInt(9, user.getId_equipe());
            } else {
                pstmt.setNull(9, java.sql.Types.INTEGER);
            }
            pstmt.setInt(10, user.getId_User());
            pstmt.executeUpdate();
            System.out.println("✅ Utilisateur modifié avec succès !");
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la modification de l'utilisateur : " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void supprimer(int id_User) throws SQLException {
        String checkQuery = "SELECT COUNT(*) FROM user WHERE id_User = ?";
        try (PreparedStatement checkStmt = connection.prepareStatement(checkQuery)) {
            checkStmt.setInt(1, id_User);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) == 0) {
                System.err.println("⚠️ Aucun utilisateur trouvé avec cet ID.");
                return;
            }
        }

        String req = "DELETE FROM user WHERE id_User=?";
        try (PreparedStatement pstmt = connection.prepareStatement(req)) {
            pstmt.setInt(1, id_User);
            pstmt.executeUpdate();
            System.out.println("✅ Utilisateur supprimé avec succès !");
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la suppression de l'utilisateur : " + e.getMessage());
            throw e;
        }
    }

    @Override
    public List<User> afficher() throws SQLException {
        List<User> users = new ArrayList<>();
        String req = "SELECT * FROM user";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(req)) {
            while (rs.next()) {
                User user = new User(
                        rs.getInt("id_User"), rs.getString("nom"), rs.getString("prenom"),
                        rs.getString("email"), rs.getString("password"), rs.getString("role"));
                user.setDateNaissance(rs.getDate("dateNaissance"));
                user.setImageURL(rs.getString("imageURL"));
                user.setId_equipe(rs.getInt("id_equipe") == 0 ? null : rs.getInt("id_equipe")); // Handle id_equipe

                if ("Entraîneur".equals(user.getRole())) {
                    String specialite = rs.getString("specialite");
                    Entraineur entraineur = new Entraineur(user.getNom(), user.getPrenom(), user.getEmail(), user.getPassword(), user.getDateNaissance(), user.getRole(), specialite);
                    entraineur.setId_User(user.getId_User());
                    entraineur.setDateNaissance(user.getDateNaissance());
                    entraineur.setImageURL(user.getImageURL());
                    users.add(entraineur);
                } else {
                    users.add(user);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de l'affichage des utilisateurs : " + e.getMessage());
            throw e;
        }
        return users;
    }

    public List<User> afficherPourResponsableAvecStream() throws SQLException {
        return afficher().stream()
                .filter(user -> "Responsable_salle".equalsIgnoreCase(user.getRole()))
                .collect(Collectors.toList());
    }

    public List<User> afficherPourResponsable() throws SQLException {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM user WHERE role = 'sportif' OR role = 'entraîneur'";
        try (PreparedStatement pstmt = connection.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                User user;
                if ("entraîneur".equalsIgnoreCase(rs.getString("role"))) {
                    user = new Entraineur(
                            rs.getInt("id_User"),
                            rs.getString("nom"),
                            rs.getString("prenom"),
                            rs.getString("email"),
                            rs.getString("password"),
                            rs.getDate("dateNaissance"),
                            rs.getString("role"),
                            rs.getString("specialite")
                    );
                } else {
                    user = new User(
                            rs.getInt("id_User"),
                            rs.getString("nom"),
                            rs.getString("prenom"),
                            rs.getString("email"),
                            rs.getString("password"),
                            rs.getString("role")
                    );
                    user.setDateNaissance(rs.getDate("dateNaissance"));
                }
                user.setImageURL(rs.getString("imageURL"));
                user.setId_equipe(rs.getInt("id_equipe") == 0 ? null : rs.getInt("id_equipe"));
                users.add(user);
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur SQL lors de la récupération des utilisateurs pour le responsable : " + e.getMessage());
            throw e;
        }
        return users;
    }

    public Optional<User> authentifier(String email, String password) {
        String query = "SELECT * FROM user WHERE email = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String hashedPassword = rs.getString("password");
                if (BCrypt.checkpw(password, hashedPassword)) {
                    User user = new User(
                            rs.getInt("id_User"),
                            rs.getString("nom"),
                            rs.getString("prenom"),
                            rs.getString("email"),
                            rs.getString("password"),
                            rs.getString("role")
                    );
                    user.setDateNaissance(rs.getDate("dateNaissance"));
                    user.setImageURL(rs.getString("imageURL"));
                    user.setId_equipe(rs.getInt("id_equipe") == 0 ? null : rs.getInt("id_equipe"));
                    return Optional.of(user);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur SQL lors de l'authentification : " + e.getMessage());
            throw new RuntimeException("Erreur d'authentification utilisateur", e);
        }
        return Optional.empty();
    }

    public boolean loginValidate(String email, String password) {
        String query = "SELECT password FROM user WHERE email = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String hashedPassword = rs.getString("password");
                return BCrypt.checkpw(password, hashedPassword);
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur SQL lors de la validation du login : " + e.getMessage());
        }
        return false;
    }

    public boolean inscrire(User user) throws SQLException {
        String checkQuery = "SELECT COUNT(*) FROM user WHERE email = ?";
        try (PreparedStatement checkStmt = connection.prepareStatement(checkQuery)) {
            checkStmt.setString(1, user.getEmail());
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                System.err.println("⚠️ Cet email est déjà utilisé !");
                return false;
            }
        }
        ajouter(user);
        return true;
    }

    public boolean emailExiste(String email) {
        String query = "SELECT COUNT(*) FROM user WHERE email = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur SQL : " + e.getMessage());
        }
        return false;
    }

    public List<User> rechercherParRole(String role) throws SQLException {
        String sql = "SELECT * FROM user";
        List<User> users = new ArrayList<>();
        try (PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                User user;
                if ("Entraîneur".equalsIgnoreCase(rs.getString("role"))) {
                    user = new Entraineur(
                            rs.getInt("id_User"), rs.getString("nom"), rs.getString("prenom"),
                            rs.getString("email"), rs.getString("password"), rs.getDate("dateNaissance"),
                            rs.getString("role"), rs.getString("specialite")
                    );
                } else {
                    user = new User(
                            rs.getInt("id_User"), rs.getString("nom"), rs.getString("prenom"),
                            rs.getString("email"), rs.getString("password"), rs.getString("role")
                    );
                    user.setDateNaissance(rs.getDate("dateNaissance"));
                }
                user.setImageURL(rs.getString("imageURL"));
                user.setId_equipe(rs.getInt("id_equipe") == 0 ? null : rs.getInt("id_equipe"));
                users.add(user);
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur SQL lors de la récupération des utilisateurs : " + e.getMessage());
            throw e;
        }
        return users.stream()
                .filter(user -> user.getRole().equalsIgnoreCase(role))
                .collect(Collectors.toList());
    }

    public Optional<User> getUtilisateurById(int idUser) throws SQLException {
        String query = "SELECT * FROM user WHERE id_User = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, idUser);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                User user = new User(
                        resultSet.getInt("id_User"),
                        resultSet.getString("nom"),
                        resultSet.getString("prenom"),
                        resultSet.getString("email"),
                        resultSet.getString("password"),
                        resultSet.getString("role")
                );
                user.setDateNaissance(resultSet.getDate("dateNaissance"));
                user.setImageURL(resultSet.getString("imageURL"));
                user.setId_equipe(resultSet.getInt("id_equipe") == 0 ? null : resultSet.getInt("id_equipe"));
                if ("Entraîneur".equalsIgnoreCase(user.getRole())) {
                    Entraineur entraineur = new Entraineur(
                            user.getNom(), user.getPrenom(), user.getEmail(),
                            user.getPassword(), user.getDateNaissance(),
                            user.getRole(), resultSet.getString("specialite")
                    );
                    entraineur.setId_User(user.getId_User());
                    entraineur.setImageURL(user.getImageURL());
                    return Optional.of(entraineur);
                }
                return Optional.of(user);
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération de l'utilisateur avec ID: " + idUser);
            throw new SQLException("Erreur lors de la récupération de l'utilisateur", e);
        }
        return Optional.empty();
    }

    public int getTotalUsers() {
        int total = 0;
        String query = "SELECT COUNT(*) FROM user";
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                total = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return total;
    }

    public List<User> getUtilisateurByRole(String role) throws SQLException {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM user WHERE role = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, role);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                User user;
                if ("Entraîneur".equalsIgnoreCase(rs.getString("role"))) {
                    user = new Entraineur(
                            rs.getInt("id_User"),
                            rs.getString("nom"),
                            rs.getString("prenom"),
                            rs.getString("email"),
                            rs.getString("password"),
                            rs.getDate("dateNaissance"),
                            rs.getString("role"),
                            rs.getString("specialite")
                    );
                } else {
                    user = new User(
                            rs.getInt("id_User"),
                            rs.getString("nom"),
                            rs.getString("prenom"),
                            rs.getString("email"),
                            rs.getString("password"),
                            rs.getString("role")
                    );
                    user.setDateNaissance(rs.getDate("dateNaissance"));
                }
                user.setImageURL(rs.getString("imageURL"));
                user.setId_equipe(rs.getInt("id_equipe") == 0 ? null : rs.getInt("id_equipe"));
                users.add(user);
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur SQL lors de la récupération des utilisateurs par rôle : " + e.getMessage());
            throw e;
        }
        return users;
    }
}
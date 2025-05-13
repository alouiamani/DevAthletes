package org.gymify.services;

import org.gymify.entities.Entraineur;
import org.gymify.entities.User;
import org.gymify.utils.gymifyDataBase;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ServiceUser implements IGestionUser<User> {
    private Connection connection;
    private BCryptPasswordEncoder bcryptEncoder;

    public ServiceUser() {
        this.connection = gymifyDataBase.getInstance().getConnection();
        if (this.connection == null) {
            throw new IllegalStateException("❌ La connexion à la base de données n'a pas été initialisée !");
        }
        this.bcryptEncoder = new BCryptPasswordEncoder(13); // Cost factor 13 for $2y$ hashes
    }

    private Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = gymifyDataBase.getInstance().getConnection();
                if (connection == null) {
                    throw new SQLException("❌ Impossible d'obtenir une connexion à la base de données !");
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération de la connexion : " + e.getMessage());
            throw new RuntimeException("Erreur de connexion à la base de données", e);
        }
        return connection;
    }

    @Override

    public void ajouter(User user) throws SQLException {
        if (user == null || user.getEmail() == null || user.getNom() == null || user.getPrenom() == null) {
            throw new IllegalArgumentException("Tous les champs requis doivent être remplis !");
        }

        // Vérifier si l'email existe déjà
        String checkQuery = "SELECT COUNT(*) FROM user WHERE email = ?";
        try (PreparedStatement checkStmt = getConnection().prepareStatement(checkQuery)) {
            checkStmt.setString(1, user.getEmail());
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                throw new SQLException("Cet email est déjà utilisé !");
            }
        }

        String req = "INSERT INTO user (nom, prenom, email, password, role, specialite, date_naissance, image_url) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = getConnection().prepareStatement(req)) {
            pstmt.setString(1, user.getNom());
            pstmt.setString(2, user.getPrenom());
            pstmt.setString(3, user.getEmail());
            String hashedPassword = bcryptEncoder.encode(user.getPassword());
            hashedPassword = hashedPassword.replaceFirst("^\\$2a\\$", "\\$2y\\$");
            pstmt.setString(4, hashedPassword);

            pstmt.setString(5, user.getRole());
            if (user instanceof Entraineur) {
                pstmt.setString(6, ((Entraineur) user).getSpecialite());
            } else {
                pstmt.setNull(6, Types.VARCHAR);
            }
            if (user.getDate_naissance() != null) {
                pstmt.setDate(7, new java.sql.Date(user.getDate_naissance().getTime()));
            } else {
                pstmt.setNull(7, Types.DATE);
            }
            pstmt.setString(8, user.getImage_url());

            int rowsAffected = pstmt.executeUpdate();
            System.out.println("✅ Utilisateur ajouté avec succès ! Lignes affectées : " + rowsAffected);
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de l'ajout de l'utilisateur : " + e.getMessage());
            throw new SQLException("Erreur lors de l'ajout de l'utilisateur : " + e.getMessage(), e);
        }
    }

    @Override
    public void modifier(User user) throws SQLException {
        String req = "UPDATE user SET nom = ?, prenom = ?, email = ?, role = ?, date_naissance = ?, image_url = ?, specialite = ?" +
                (user.getPassword() != null && !user.getPassword().isEmpty() ? ", password = ?" : "") +
                " WHERE id = ?";

        try (PreparedStatement pstmt = getConnection().prepareStatement(req)) {
            pstmt.setString(1, user.getNom());
            pstmt.setString(2, user.getPrenom());
            pstmt.setString(3, user.getEmail());
            pstmt.setString(4, user.getRole());
            if (user.getDate_naissance() != null) {
                pstmt.setDate(5, new java.sql.Date(user.getDate_naissance().getTime()));
            } else {
                pstmt.setNull(5, Types.DATE);
            }
            pstmt.setString(6, user.getImage_url());
            if (user instanceof Entraineur) {
                pstmt.setString(7, ((Entraineur) user).getSpecialite());
            } else {
                pstmt.setNull(7, Types.VARCHAR);
            }

            int parameterIndex = 8;
            if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                String hashedPassword = bcryptEncoder.encode(user.getPassword());
                hashedPassword = hashedPassword.replaceFirst("^\\$2a\\$", "\\$2y\\$");
                pstmt.setString(parameterIndex, hashedPassword);
                parameterIndex++;
            }
            pstmt.setInt(parameterIndex, user.getId());
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Aucun utilisateur mis à jour. ID introuvable : " + user.getId());
            }
            System.out.println("✅ Utilisateur modifié avec succès ! Lignes affectées : " + rowsAffected);
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la modification de l'utilisateur : " + e.getMessage());
            throw e;
        }
    }
    @Override
    public void supprimer(int id) throws SQLException {
        String checkQuery = "SELECT COUNT(*) FROM user WHERE id = ?";
        try (PreparedStatement checkStmt = getConnection().prepareStatement(checkQuery)) {
            checkStmt.setInt(1, id);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) == 0) {
                System.err.println("⚠️ Aucun utilisateur trouvé avec cet ID.");
                return;
            }
        }

        String req = "DELETE FROM user WHERE id=?";
        try (PreparedStatement pstmt = getConnection().prepareStatement(req)) {
            pstmt.setInt(1, id);
            int rowsAffected = pstmt.executeUpdate();
            System.out.println("✅ Utilisateur supprimé avec succès ! Lignes affectées : " + rowsAffected);
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la suppression de l'utilisateur : " + e.getMessage());
            throw e;
        }
    }

    public List<User> afficher() throws SQLException {
        List<User> users = new ArrayList<>();
        String req = "SELECT * FROM user";

        System.out.println("📋 Exécution de la requête : " + req);
        try (Statement stmt = getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(req)) {
            int count = 0;
            while (rs.next()) {
                count++;
                System.out.println("Utilisateur trouvé : ID=" + rs.getInt("id") + ", Nom=" + rs.getString("nom") + ", Role=" + rs.getString("role"));
                User user = new User(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("role")
                );
                // Gérer la date de naissance de manière sécurisée
                try {
                    Date dateNaissance = rs.getDate("date_naissance");
                    if (!rs.wasNull()) {
                        user.setDate_naissance(dateNaissance);
                    } else {
                        user.setDate_naissance(null);
                    }
                } catch (SQLException e) {
                    System.err.println("⚠️ Erreur lors de la lecture de date_naissance pour ID=" + rs.getInt("id") + ": " + e.getMessage());
                    user.setDate_naissance(null); // Définir comme null en cas d'erreur
                }
                user.setImage_url(rs.getString("image_url"));
                if ("entraineur".equalsIgnoreCase(rs.getString("role"))) {
                    Entraineur entraineur = new Entraineur(
                            user.getNom(), user.getPrenom(), user.getEmail(),
                            user.getPassword(), user.getDate_naissance(),
                            user.getRole(), rs.getString("specialite")
                    );
                    entraineur.setId(user.getId());
                    entraineur.setDate_naissance(user.getDate_naissance());
                    entraineur.setImage_url(user.getImage_url());
                    users.add(entraineur);
                    users.add(user);

                } else {
                    users.add(user);
                }
            }
            System.out.println("✅ Nombre total d'utilisateurs récupérés : " + count);
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de l'affichage des utilisateurs : " + e.getMessage());
            throw e;
        }
        return users;
    }

    public List<User> afficherPourResponsableAvecStream() throws SQLException {
        return afficher().stream()
                .filter(user -> "responsable_salle".equalsIgnoreCase(user.getRole()))
                .collect(Collectors.toList());
    }

    public List<User> afficherPourResponsable() throws SQLException {
        List<User> users = new ArrayList<>();
        // Modifier la requête pour inclure tous les utilisateurs ou un ensemble spécifique
        String query = "SELECT * FROM user"; // Récupère tous les utilisateurs

        try (PreparedStatement pstmt = getConnection().prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                User user;
                if ("entraineur".equalsIgnoreCase(rs.getString("role"))) {
                    user = new Entraineur(
                            rs.getInt("id"),
                            rs.getString("nom"),
                            rs.getString("prenom"),
                            rs.getString("email"),
                            rs.getString("password"),
                            rs.getDate("date_naissance"),
                            rs.getString("role"),
                            rs.getString("specialite")
                    );
                } else {
                    user = new User(
                            rs.getInt("id"),
                            rs.getString("nom"),
                            rs.getString("prenom"),
                            rs.getString("email"),
                            rs.getString("password"),
                            rs.getString("role")
                    );
                    user.setDate_naissance(rs.getDate("date_naissance"));
                }
                user.setImage_url(rs.getString("image_url"));
                users.add(user);
            }
            System.out.println("✅ Utilisateurs pour responsable récupérés : " + users.size());
        } catch (SQLException e) {
            System.err.println("❌ Erreur SQL lors de la récupération des utilisateurs pour le responsable : " + e.getMessage());
            throw e;
        }
        return users;
    }
    public List<User> afficherPourResponsableAvecStreams() throws SQLException {
        return afficher().stream() // Récupère tous les utilisateurs
                .filter(user -> "sportif".equalsIgnoreCase(user.getRole()) || "entraîneur".equalsIgnoreCase(user.getRole()))
                .collect(Collectors.toList());
    }


    public Optional<User> authentifier(String email, String password) {
        String query = "SELECT * FROM user WHERE email = ?";

        try (PreparedStatement stmt = getConnection().prepareStatement(query)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String hashedPassword = rs.getString("password");

                if (bcryptEncoder.matches(password, hashedPassword)) {
                    User user = new User(
                            rs.getInt("id"),
                            rs.getString("nom"),
                            rs.getString("prenom"),
                            rs.getString("email"),
                            rs.getString("password"),
                            rs.getString("role"),
                            rs.getInt("id")
                    );
                    user.setDate_naissance(rs.getDate("date_naissance"));
                    user.setImage_url(rs.getString("image_url"));
                    System.out.println("✅ Utilisateur authentifié : " + user.getEmail());
                    return Optional.of(user);
                }
            }
            System.out.println("⚠️ Échec de l'authentification pour : " + email);
        } catch (SQLException e) {
            System.err.println("❌ Erreur SQL lors de l'authentification : " + e.getMessage());
            throw new RuntimeException("Erreur d'authentification utilisateur", e);
        }

        return Optional.empty();
    }

    public boolean loginValidate(String email, String password) {
        String query = "SELECT password FROM user WHERE email = ?";

        try (PreparedStatement stmt = getConnection().prepareStatement(query)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String hashedPassword = rs.getString("password");
                boolean isValid = bcryptEncoder.matches(password, hashedPassword);
                System.out.println("✅ Validation login pour " + email + " : " + isValid);
                return isValid;
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur SQL lors de la validation du login : " + e.getMessage());
        }
        return false;
    }

    public boolean inscrire(User user) throws SQLException {
        String checkQuery = "SELECT COUNT(*) FROM user WHERE email = ?";
        try (PreparedStatement checkStmt = getConnection().prepareStatement(checkQuery)) {
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

        try (PreparedStatement stmt = getConnection().prepareStatement(query)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                boolean exists = rs.getInt(1) > 0;
                System.out.println("📧 Vérification email " + email + " : " + exists);
                return exists;
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur SQL : " + e.getMessage());
        }
        return false;
    }

    public List<User> rechercherParRole(String role) throws SQLException {
        String sql = "SELECT * FROM user WHERE role = ?";

        List<User> users = new ArrayList<>();

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, role);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                User user;
                if ("entraineur".equalsIgnoreCase(rs.getString("role"))) {
                    user = new Entraineur(
                            rs.getInt("id"),
                            rs.getString("nom"),
                            rs.getString("prenom"),
                            rs.getString("email"),
                            rs.getString("password"),
                            rs.getDate("date_naissance"),
                            rs.getString("role"),
                            rs.getString("specialite")
                    );
                } else {
                    user = new User(
                            rs.getInt("id"),
                            rs.getString("nom"),
                            rs.getString("prenom"),
                            rs.getString("email"),
                            rs.getString("password"),
                            rs.getString("role")
                    );
                    user.setDate_naissance(rs.getDate("date_naissance"));
                }
                user.setImage_url(rs.getString("image_url"));
                users.add(user);
            }
            System.out.println("✅ Utilisateurs trouvés pour le rôle " + role + " : " + users.size());
        } catch (SQLException e) {
            System.err.println("❌ Erreur SQL lors de la récupération des utilisateurs : " + e.getMessage());
            throw e;
        }

        return users;
    }

    public Optional<User> getUtilisateurById(int id) throws SQLException {
        String query = "SELECT * FROM user WHERE id = ?";

        try (PreparedStatement preparedStatement = getConnection().prepareStatement(query)) {
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                User user = new User(
                        resultSet.getInt("id"),
                        resultSet.getString("nom"),
                        resultSet.getString("prenom"),
                        resultSet.getString("email"),
                        resultSet.getString("password"),
                        resultSet.getString("role")
                );
                user.setDate_naissance(resultSet.getDate("date_naissance"));
                        user.setImage_url(resultSet.getString("image_url"));
                if ("entraineur".equalsIgnoreCase(user.getRole())) {
                    Entraineur entraineur = new Entraineur(
                            user.getNom(), user.getPrenom(), user.getEmail(),
                            user.getPassword(), user.getDate_naissance(),
                            user.getRole(), resultSet.getString("specialite")
                    );
                    entraineur.setId(user.getId());
                    entraineur.setImage_url(user.getImage_url());
                    return Optional.of(entraineur);
                }
                return Optional.of(user);
            }
            System.out.println("⚠️ Aucun utilisateur trouvé pour l'ID : " + id);
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération de l'utilisateur avec ID: " + id);
            throw new SQLException("Erreur lors de la récupération de l'utilisateur", e);
        }

        return Optional.empty();
    }

    public int getTotalUsers() {
        int total = 0;
        String query = "SELECT COUNT(*) FROM user";

        try (PreparedStatement stmt = getConnection().prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                total = rs.getInt(1);
            }
            System.out.println("📊 Nombre total d'utilisateurs : " + total);
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors du comptage des utilisateurs : " + e.getMessage());
        }
        return total;
    }

    public int getSalleIdByUserId(int id) throws SQLException {
        String query = "SELECT id FROM user WHERE id = ? AND role = 'responsable_salle'";

        try (PreparedStatement pstmt = getConnection().prepareStatement(query)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération de l'ID de la salle : " + e.getMessage());
            throw e;
        }
        return -1;
    }

    public void dissocierSalle(int idSalle) throws SQLException {
        String query = "UPDATE user SET id = NULL WHERE id = ?";
        try (PreparedStatement stmt = getConnection().prepareStatement(query)) {
            stmt.setInt(1, idSalle);
            int rowsAffected = stmt.executeUpdate();
            System.out.println("✅ Salle dissociée pour " + rowsAffected + " utilisateurs");
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la dissociation de la salle : " + e.getMessage());
            throw e;
        }
    }

    public void mettreAJourIdSalle(int idUser, int idSalle) throws SQLException {
        String query = "UPDATE user SET id = ? WHERE id = ?";
        try (PreparedStatement pstmt = getConnection().prepareStatement(query)) {
            pstmt.setInt(1, idSalle);
            pstmt.setInt(2, idUser);
            int rowsAffected = pstmt.executeUpdate();
            System.out.println("✅ ID de la salle mis à jour pour l'utilisateur avec ID: " + idUser);
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la mise à jour de l'ID de la salle : " + e.getMessage());
            throw e;
        }
    }

    public boolean isSalleDejaAssociee(int idSalle) throws SQLException {
        String query = "SELECT COUNT(*) FROM user WHERE id = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(query)) {
            ps.setInt(1, idSalle);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                boolean isAssociated = rs.getInt(1) > 0;
                System.out.println("🏋️ Salle ID " + idSalle + " déjà associée : " + isAssociated);
                return isAssociated;
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la vérification de l'association de la salle : " + e.getMessage());
            throw e;
        }
        return false;
    }

    public List<User> getUtilisateurByRole(String role) throws SQLException {
        return rechercherParRole(role); // Remplacement de la méthode non implémentée
    }
}
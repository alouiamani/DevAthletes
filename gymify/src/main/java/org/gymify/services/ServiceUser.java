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

<<<<<<< HEAD
        String req = "INSERT INTO user (nom, prenom, email, password, role, specialite, dateNaissance, imageURL, id_Salle) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(req)) {
=======
        String req = "INSERT INTO user (nom, prenom, email, password, role, dateNaissance, imageURL, specialite, id_equipe) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(req, Statement.RETURN_GENERATED_KEYS)) {
>>>>>>> aea46390f69b2bb6a9b587a90aaa9dc13d0bcff7
            pstmt.setString(1, user.getNom());
            pstmt.setString(2, user.getPrenom());
            pstmt.setString(3, user.getEmail());
            String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt(12));
            pstmt.setString(4, hashedPassword);
            pstmt.setString(5, user.getRole());
<<<<<<< HEAD

            // Gestion de la spécialité
=======
            if (user.getDateNaissance() != null) {
                pstmt.setDate(6, new java.sql.Date(user.getDateNaissance().getTime()));
            } else {
                pstmt.setNull(6, java.sql.Types.DATE);
            }
            pstmt.setString(7, user.getImageURL());
>>>>>>> aea46390f69b2bb6a9b587a90aaa9dc13d0bcff7
            if (user instanceof Entraineur) {
                pstmt.setString(6, ((Entraineur) user).getSpecialite());
            } else {
                pstmt.setNull(6, java.sql.Types.VARCHAR);
            }

            // Gestion de la date de naissance
            if (user.getDateNaissance() != null) {
                pstmt.setDate(7, new java.sql.Date(user.getDateNaissance().getTime()));
            } else {
                pstmt.setNull(7, java.sql.Types.DATE);
            }

            pstmt.setString(8, user.getImageURL());

            // Gestion de l'ID de la salle
            if ("Responsable_salle".equalsIgnoreCase(user.getRole())) {
                pstmt.setInt(9, user.getId_Salle()); // Ajouter l'ID de la salle pour les responsables
            } else {
                pstmt.setNull(9, java.sql.Types.INTEGER); // Pas d'ID de salle pour les autres rôles
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
<<<<<<< HEAD
        String req = "UPDATE user SET nom=?, prenom=?, email=?, password=?, role=?, dateNaissance=?, imageURL=?, specialite=?, id_Salle=? WHERE id_User=?";

=======
        String req = "UPDATE user SET nom=?, prenom=?, email=?, password=?, role=?, dateNaissance=?, imageURL=?, specialite=?, id_equipe=? WHERE id_User=?";
>>>>>>> aea46390f69b2bb6a9b587a90aaa9dc13d0bcff7
        try (PreparedStatement pstmt = connection.prepareStatement(req)) {
            pstmt.setString(1, user.getNom());
            pstmt.setString(2, user.getPrenom());
            pstmt.setString(3, user.getEmail());
            pstmt.setString(4, BCrypt.hashpw(user.getPassword(), BCrypt.gensalt(12)));
            pstmt.setString(5, user.getRole());
            pstmt.setDate(6, user.getDateNaissance() != null ? new Date(user.getDateNaissance().getTime()) : null);
            pstmt.setString(7, user.getImageURL());
<<<<<<< HEAD

            // Gestion de la spécialité
=======
>>>>>>> aea46390f69b2bb6a9b587a90aaa9dc13d0bcff7
            if (user instanceof Entraineur) {
                pstmt.setString(8, ((Entraineur) user).getSpecialite());
            } else {
                pstmt.setNull(8, java.sql.Types.VARCHAR);
            }
<<<<<<< HEAD

            // Gestion de l'ID de la salle
            if ("Responsable_salle".equalsIgnoreCase(user.getRole())) {
                pstmt.setInt(9, user.getId_Salle()); // Mettre à jour l'ID de la salle pour les responsables
            } else {
                pstmt.setNull(9, java.sql.Types.INTEGER); // Pas d'ID de salle pour les autres rôles
            }

=======
            if (user.getId_equipe() != null) {
                pstmt.setInt(9, user.getId_equipe());
            } else {
                pstmt.setNull(9, java.sql.Types.INTEGER);
            }
>>>>>>> aea46390f69b2bb6a9b587a90aaa9dc13d0bcff7
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
<<<<<<< HEAD
                user.setDateNaissance(rs.getDate("dateNaissance")); // ✅ Ajout de dateNaissance
                user.setImageURL(rs.getString("imageURL"));
                user.setRole(rs.getString("role"));// ✅ Ajout de imageURL

                // Vérification du rôle de l'utilisateur
=======
                user.setDateNaissance(rs.getDate("dateNaissance"));
                user.setImageURL(rs.getString("imageURL"));
                user.setId_equipe(rs.getInt("id_equipe") == 0 ? null : rs.getInt("id_equipe")); // Handle id_equipe

>>>>>>> aea46390f69b2bb6a9b587a90aaa9dc13d0bcff7
                if ("Entraîneur".equals(user.getRole())) {
                    String specialite = rs.getString("specialite");
                    Entraineur entraineur = new Entraineur(user.getNom(), user.getPrenom(), user.getEmail(), user.getPassword(), user.getDateNaissance(), user.getRole(), specialite);
                    entraineur.setId_User(user.getId_User());
                    entraineur.setDateNaissance(user.getDateNaissance());
                    entraineur.setImageURL(user.getImageURL());
                    users.add(entraineur);
                } else if ("responsable_salle".equals(user.getRole())) {
                    // Si l'utilisateur est un responsable de salle, ajouter l'ID de la salle
                    int salleId = rs.getInt("id_Salle"); // Récupérer l'ID de la salle
                    user.setId_Salle(salleId); // Ajouter à l'objet User
                    users.add(user);
                } else {
                    // Sinon, ajouter l'utilisateur normalement
                    users.add(user);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de l'affichage des utilisateurs : " + e.getMessage());
            throw e;
        }
        return users;
    }

<<<<<<< HEAD
     public List<User> afficherPourResponsableAvecStream() throws SQLException {
        return afficher().stream() // Récupère tous les utilisateurs
                .filter(user -> "responsable_Salle".equalsIgnoreCase(user.getRole())) // Filtre pour les responsables de salle
=======
    public List<User> afficherPourResponsableAvecStream() throws SQLException {
        return afficher().stream()
                .filter(user -> "Responsable_salle".equalsIgnoreCase(user.getRole()))
>>>>>>> aea46390f69b2bb6a9b587a90aaa9dc13d0bcff7
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
                            rs.getString("role"),
                            rs.getInt("id_Salle")
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
<<<<<<< HEAD
    public int getSalleIdByUserId(int id_User) throws SQLException {
        String query = "SELECT id_salle FROM user WHERE id_user = ? AND role = 'responsable_Salle'";
=======
>>>>>>> aea46390f69b2bb6a9b587a90aaa9dc13d0bcff7

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
<<<<<<< HEAD
    public void dissocierSalle(int idSalle) throws SQLException {
        String query = "UPDATE user SET id_Salle = NULL WHERE id_Salle = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, idSalle);
            stmt.executeUpdate();
        }
    }
    public void mettreAJourIdSalle(int idUser, int idSalle) throws SQLException {
        String query = "UPDATE user SET id_Salle = ? WHERE id_User = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, idSalle);
            pstmt.setInt(2, idUser);
            pstmt.executeUpdate();
            System.out.println("✅ ID de la salle mis à jour pour l'utilisateur avec ID: " + idUser);
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la mise à jour de l'ID de la salle : " + e.getMessage());
            throw e;
        }
    }
    public boolean isSalleDejaAssociee(int idSalle) throws SQLException {
        // Vérifier dans la table User si un utilisateur a l'ID de salle correspondant
        String query = "SELECT COUNT(*) FROM User WHERE id_Salle = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, idSalle);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0; // Si le comptage est supérieur à 0, la salle est déjà associée
            }
        }
        return false;
    }


}
=======
}
>>>>>>> aea46390f69b2bb6a9b587a90aaa9dc13d0bcff7

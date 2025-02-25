package org.gymify.services;

import org.gymify.entities.Entraineur;
import org.gymify.entities.User;
import org.gymify.utils.gymifyDataBase;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

        String req = "INSERT INTO user (nom, prenom, email, password, role, dateNaissance, imageURL, specialite) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(req)) {
            pstmt.setString(1, user.getNom());
            pstmt.setString(2, user.getPrenom());
            pstmt.setString(3, user.getEmail());

            // Hachage du mot de passe
            String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt(12));
            pstmt.setString(4, hashedPassword);

            pstmt.setString(5, user.getRole());

            // Gestion de la date de naissance
            if (user.getDateNaissance() != null) {
                pstmt.setDate(6, new java.sql.Date(user.getDateNaissance().getTime()));
            } else {
                pstmt.setNull(6, java.sql.Types.DATE);
            }

            pstmt.setString(7, user.getImageURL());

            // Gestion de la spécialité
            if (user instanceof Entraineur) {
                pstmt.setString(8, ((Entraineur) user).getSpecialite());
            } else {
                pstmt.setNull(8, java.sql.Types.VARCHAR);
            }

            pstmt.executeUpdate();
            System.out.println("✅ Utilisateur ajouté avec succès !");
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de l'ajout de l'utilisateur : " + e.getMessage());
            throw e;
        }
    }


    @Override
    public void modifier(User user) throws SQLException {
        String req = "UPDATE user SET nom=?, prenom=?, email=?, password=?, role=?, dateNaissance=?, imageURL=?, specialite=? WHERE id_User=?";

        try (PreparedStatement pstmt = connection.prepareStatement(req)) {
            pstmt.setString(1, user.getNom());
            pstmt.setString(2, user.getPrenom());
            pstmt.setString(3, user.getEmail());
            pstmt.setString(4, BCrypt.hashpw(user.getPassword(), BCrypt.gensalt(12))); // 🔒 Hashage du mot de passe
            pstmt.setString(5, user.getRole());
            pstmt.setDate(6, new Date(user.getDateNaissance().getTime())); // ✅ Gestion correcte de la date
            pstmt.setString(7, user.getImageURL());

            if (user instanceof Entraineur) {
                Entraineur entraineur = (Entraineur) user;
                pstmt.setString(8, entraineur.getSpecialite());  // Mettre à jour la spécialité des entraîneurs
            } else {
                pstmt.setNull(8, java.sql.Types.VARCHAR);
            }

            pstmt.setInt(9, user.getId_User());
            pstmt.executeUpdate();
            System.out.println("✅ Utilisateur modifié avec succès !");
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la modification de l'utilisateur : " + e.getMessage());
            throw e;
        }
        ;
    }

    @Override
    public void supprimer(int id_User) throws SQLException {
        // Vérifier si l'utilisateur existe avant de le supprimer
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


    public List<User> afficher() throws SQLException {
        List<User> users = new ArrayList<>();
        String req = "SELECT * FROM user";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(req)) {

            while (rs.next()) {
                User user = new User(
                        rs.getInt("id_User"), rs.getString("nom"), rs.getString("prenom"),
                        rs.getString("email"), rs.getString("password"), rs.getString("role"));
                user.setDateNaissance(rs.getDate("dateNaissance")); // ✅ Ajout de dateNaissance
                user.setImageURL(rs.getString("imageURL")); // ✅ Ajout de imageURL

                // Récupérer la spécialité si l'utilisateur est un entraîneur
                if ("Entraîneur".equals(user.getRole())) {
                    String specialite = rs.getString("specialite");
                    Entraineur entraineur = new Entraineur(user.getNom(), user.getPrenom(), user.getEmail(), user.getPassword(),user.getDateNaissance(), user.getRole(), specialite);
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
    public List<User> afficherPourResponsable() {
        List<User> userList = new ArrayList<>();
        String sql = "SELECT * FROM user WHERE role IN ('Sportif', 'Entraîneur')";

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            int count = 0;
            while (rs.next()) {
                String role = rs.getString("role");

                if ("Entraîneur".equalsIgnoreCase(role)) {
                    Entraineur entraineur = new Entraineur(
                            rs.getString("nom"),
                            rs.getString("prenom"),
                            rs.getString("email"),
                            rs.getString("password"),
                            rs.getDate("date_naissance"),
                            role,
                            rs.getString("specialite")
                    );
                    entraineur.setId_User(rs.getInt("id_user"));
                    entraineur.setImageURL(rs.getString("imageURL"));
                    userList.add(entraineur);
                } else {
                    User user = new User(
                            rs.getString("nom"),
                            rs.getString("prenom"),
                            rs.getString("email"),
                            rs.getString("password"),
                            role,
                            rs.getDate("date_naissance"),
                            rs.getString("imageURL")
                    );
                    user.setId_User(rs.getInt("id_user"));
                    userList.add(user);
                }
                count++;
            }

            System.out.println("✅ Nombre d'utilisateurs récupérés pour Responsable : " + count);

        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de l'affichage des utilisateurs pour Responsable : " + e.getMessage());
        }

        return userList;
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
        // Vérifier si l'email existe déjà
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
                return rs.getInt(1) > 0; // ✅ Retourne true si l'email est déjà utilisé
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur SQL : " + e.getMessage());
        }
        return false;
    }
}

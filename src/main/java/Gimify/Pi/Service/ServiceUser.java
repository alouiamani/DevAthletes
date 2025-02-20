package Gimify.Pi.Service;

import Gimify.Pi.entities.Entraineur;
import Gimify.Pi.entities.User;
import Gimify.Pi.utils.MyData;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class ServiceUser implements IService <User>{
    Connection connection = MyData.getInstance().getConnection();
    public ServiceUser() {}

    @Override
    public void ajouter(User user) throws SQLException {
        String email = user.getEmail();
        if (email == null || email.trim().isEmpty() || !email.matches("^[a-zA-Z0-9._%+-]+@gmail\\.com$")) {
            throw new IllegalArgumentException("L'email doit être une adresse valide @gmail.com");
        }
        String req = "INSERT INTO user ( nom, prenom, email, password, role, Specialite) VALUES (?,?, ?, ?, ?, ?)";

        PreparedStatement pstmt = connection.prepareStatement(req);

            pstmt.setString(1, user.getNom());
            pstmt.setString(2, user.getPrenom());
            pstmt.setString(3, user.getEmail());
            pstmt.setString(4, user.getPassword());
            pstmt.setString(5, user.getRole());

            if (user instanceof Entraineur) {
                pstmt.setString(6, ((Entraineur) user).getSpecialite());
            } else {
                pstmt.setNull(6, Types.VARCHAR);
            }
            pstmt.executeUpdate();
            System.out.println("Responsable de salle ajouté");

        }

    @Override
    public void modifier(User user) throws SQLException {
        String req = "UPDATE user SET nom=?, prenom=?, email=?, password=?, role=?, specialite=? WHERE id_User=?";
        PreparedStatement pstmt = connection.prepareStatement(req);
            pstmt.setString(1, user.getNom());
            pstmt.setString(2, user.getPrenom());
            pstmt.setString(3, user.getEmail());
            pstmt.setString(4,user.getPassword());
            pstmt.setString(5, user.getRole());
            if (user instanceof Entraineur) {
                pstmt.setString(6, ((Entraineur) user).getSpecialite());
            } else {
                pstmt.setNull(6, Types.VARCHAR);
            }
            pstmt.setInt(7, user.getId_User());
            pstmt.executeUpdate();
            System.out.println(" Utilisateur modifié avec succès !");

    }

    @Override
    public void supprimer(int id_User) throws SQLException {
        String req = "DELETE FROM user WHERE id_User=?";
        PreparedStatement pstmt = connection.prepareStatement(req);
            pstmt.setInt(1, id_User);
            pstmt.executeUpdate();
            System.out.println("Utilisateur supprimé avec succès !");

    }

    @Override
    public List<User> afficher() throws SQLException {
        List<User> users = new ArrayList<>();
        String req = "SELECT * FROM user";

        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(req);

        while (rs.next()) { // ✅ Boucle correcte : ne saute pas d'enregistrement
            User user;
            String role = rs.getString("role");

            // Vérifier si c'est un entraîneur pour ajouter "specialite"
            if ("Entraineur".equals(role)) {
                user = new Entraineur(rs.getInt("id_User"), rs.getString("nom"), rs.getString("prenom"),
                        rs.getString("email"), rs.getString("Password"),
                        rs.getString("specialite"));
            } else {
                user = new User(rs.getInt("id_User"), rs.getString("nom"), rs.getString("prenom"),
                        rs.getString("Password"), rs.getString("role"),
                        rs.getString("email"));
            }

            users.add(user); // ✅ Ajout correct dans la liste
        }

        return users;
    }

    public User chercherParRole(String role) throws SQLException {
        String req = "SELECT * FROM user WHERE role=?";
        User user = null;

        PreparedStatement pstmt = connection.prepareStatement(req);
            pstmt.setString(1, role);
            ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    if (rs.next()) {
                        String userrole = rs.getString("role");

                        // Vérifier si c'est un entraîneur pour ajouter "specialite"
                        if ("Entraineur".equals(userrole)) {
                            user = new Entraineur(rs.getInt("id_User"), rs.getString("nom"), rs.getString("prenom"),
                                    rs.getString("Password"), rs.getString("email"),  // ✅ Correction ici
                                    rs.getString("specialite"));
                        } else {
                            user = new User(rs.getInt("id_User"), rs.getString("nom"), rs.getString("prenom"),
                                    rs.getString("Password"), rs.getString("role"),  // ✅ Correction ici
                                    rs.getString("email"));
                        }
                    }

                }



        return user;
    }
}

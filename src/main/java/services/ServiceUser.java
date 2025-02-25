package services;

import entities.User;
import utils.MyDatabase;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ServiceUser implements IService<User> {
    private final Connection connection;

    public ServiceUser() {
        this.connection = MyDatabase.getInstance().getConnection();
    }

    @Override
    public int ajouter(User user) throws SQLException {
        // Validate and normalize the role
        String role = user.getRole().trim().toLowerCase();
        if (!role.equals("admin") && !role.equals("user") && !role.equals("sportif")) {
            throw new SQLException("Invalid role. Allowed roles are: admin, user, sportif");
        }

        String req = "INSERT INTO user (nom, prenom, email, password, role, dateNaissance, imageURL) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(req, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, user.getNom());
            pstmt.setString(2, user.getPrenom());
            pstmt.setString(3, user.getEmail());
            
            // Hash the password
            String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
            pstmt.setString(4, hashedPassword);
            
            // Set the normalized role
            pstmt.setString(5, role);
            
            // Handle date
            if (user.getDateNaissance() != null) {
                pstmt.setDate(6, new java.sql.Date(user.getDateNaissance().getTime()));
            } else {
                pstmt.setNull(6, java.sql.Types.DATE);
            }
            
            pstmt.setString(7, user.getImageURL());
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating user failed, no rows affected.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }
        }
    }

    @Override
    public void modifier(User user) throws SQLException {
        String req = "UPDATE user SET nom=?, prenom=?, email=?, role=?, dateNaissance=?, imageURL=? WHERE id_User=?";

        try (PreparedStatement pstmt = connection.prepareStatement(req)) {
            pstmt.setString(1, user.getNom());
            pstmt.setString(2, user.getPrenom());
            pstmt.setString(3, user.getEmail());
            pstmt.setString(4, user.getRole());
            
            if (user.getDateNaissance() != null) {
                pstmt.setDate(5, new java.sql.Date(user.getDateNaissance().getTime()));
            } else {
                pstmt.setNull(5, java.sql.Types.DATE);
            }
            
            pstmt.setString(6, user.getImageURL());
            pstmt.setInt(7, user.getId_User());
            
            pstmt.executeUpdate();
        }
    }

    @Override
    public void supprimer(int id) throws SQLException {
        String req = "DELETE FROM user WHERE id_User=?";
        try (PreparedStatement pstmt = connection.prepareStatement(req)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
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
                    rs.getInt("id_User"),
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    rs.getString("email"),
                    rs.getString("password"),
                    rs.getString("role")
                );
                user.setDateNaissance(rs.getDate("dateNaissance"));
                user.setImageURL(rs.getString("imageURL"));
                users.add(user);
            }
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
                    return Optional.of(user);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public void createTestAdmin() {
        try {
            String sql = "INSERT INTO user (nom, prenom, email, password, role) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, "Test");
            ps.setString(2, "Admin");
            ps.setString(3, "test@admin.com");
            // Generate a new hash for password "admin"
            String hashedPassword = BCrypt.hashpw("admin", BCrypt.gensalt());
            ps.setString(4, hashedPassword);
            ps.setString(5, "admin");
            ps.executeUpdate();
            System.out.println("Test admin created successfully");
            System.out.println("Email: test@admin.com");
            System.out.println("Password: admin");
        } catch (SQLException e) {
            System.err.println("Error creating test admin: " + e.getMessage());
        }
    }
} 
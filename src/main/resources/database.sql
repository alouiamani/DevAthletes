-- Create the database if it doesn't exist
CREATE DATABASE IF NOT EXISTS projet_produit_commande;
USE projet_produit_commande;

-- Create tables only if they don't exist (removed DROP TABLE statements)
CREATE TABLE IF NOT EXISTS user (
    id_User INT PRIMARY KEY AUTO_INCREMENT,
    nom VARCHAR(50) NOT NULL,
    prenom VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    dateNaissance DATE,
    imageURL VARCHAR(255),
    active BOOLEAN DEFAULT TRUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS produit (
    id_p INT PRIMARY KEY AUTO_INCREMENT,
    nom_p VARCHAR(100) NOT NULL,
    prix_p FLOAT NOT NULL,
    stock_p INT NOT NULL,
    categorie_p VARCHAR(50) NOT NULL,
    image_path VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS commande (
    id_c INT PRIMARY KEY AUTO_INCREMENT,
    total_c FLOAT NOT NULL,
    statut_c VARCHAR(50) NOT NULL,
    dateC TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    user_id INT,
    FOREIGN KEY (user_id) REFERENCES user(id_User) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS commande_produit (
    commande_id INT,
    produit_id INT,
    quantite INT NOT NULL,
    PRIMARY KEY (commande_id, produit_id),
    FOREIGN KEY (commande_id) REFERENCES commande(id_c) ON DELETE CASCADE,
    FOREIGN KEY (produit_id) REFERENCES produit(id_p) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Insert default admin user with working password hash
INSERT IGNORE INTO user (nom, prenom, email, password, role) 
VALUES ('Admin', 'Admin', 'admin@admin.com', 
'$2a$10$dPFhQG5qHLnEqYNBWqEQqOmY/qZMqNSR8BIq1sX5KBdpWfgZzCApS', 'admin'); 
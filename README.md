Système de Gestion de Salle de Sport

Aperçu
Ce projet est une application desktop développée dans le cadre des cours de l'École d'Ingénieurs Esprit pour l'année universitaire 2024-2025. Elle est conçue pour gérer plusieurs salles de sport, permettant aux administrateurs, responsables de salles, entraîneurs et sportifs de gérer diverses opérations telles que la gestion des utilisateurs, des activités, des abonnements, des événements, des cours, des paiements, des avis et des blogs. L'application vise à rationaliser les opérations des salles de sport et à améliorer l'expérience utilisateur grâce à une interface intuitive et réactive.
Fonctionnalités

Panneau d'administration : Gérer les utilisateurs, les salles de sport, les activités, les réclamations et les produits pour toutes les succursales.
Tableau de bord du responsable de salle : Ajouter des abonnements pour chaque salle, des événements et des équipes spécifiques à la salle attribuée.
Interface entraîneur : Organiser et gérer les cours et les planning.
Portail sportif : Consulter les activités, abonnements, événements,cours et produit ,effectuer des paiements, gérer les avis et contribuer aux blogs,participer a une evenement.
Support multi-salles : Gestion centralisée pour plusieurs succursales avec des fonctionnalités spécifiques à chaque site.
Design réactif : Optimisé pour une utilisation sur ordinateurs, tablettes et appareils mobiles.

Pile Technologique
Frontend

fxml: Pour une interface utilisateur dynamique et réactive.
CSS : Pour un style moderne et efficace.
Backend

Java
Xampp: Pour stocker les données des utilisateurs, informations des salles, abonnements, etc.

Autres Outils

JWT (JSON Web Tokens) : Pour une authentification et une autorisation sécurisées.
Stripe : Pour le traitement des paiements en ligne.
Git : Pour le contrôle de version.
GitHub : Pour l'hébergement du dépôt du projet.
Recaptcha: Pour securité
Google Calendar: pour gerer les cours
Meteo: Pour afficher le meteo


Structure du Répertoire
gymify/
├── java/                   
│   ├── controllers/                
│   ├── entities/                   
│   └──services/  
    └──tests/
    └──utils/
    
├── resources/ 
    └──assets/
│   ├── config/  
    └── images/
│   ├── readme-images/                 
│   ├── recommender/                 
│   └── Uploads/
├── pom.xml
├── README.md                  
└── .gitignore                  

Premiers Pas
Prérequis

Java 
Xampp (instance locale)
Git
Compte Stripe pour l'intégration des paiements

Installation

Cloner le dépôt :git clone https://github.com/alouiamani/DevAthletes/


Naviguer dans le répertoire du projet :cd DevAthletes


Installer les dépendances:
maven:reload project


Configurer les variables d'environnement :
Créer un fichier gymifyDataBase
Ajouter les variables suivantes : URL = "jdbc:mysql://localhost:3306/projweb";



Lancement de l'Application

Démarrer l'application:
javafx:run


Utilisation

Administrateur : Connectez-vous pour gérer les utilisateurs, les salles, les réclamations et les produits.
Responsable de salle : Accédez au tableau de bord pour ajouter des abonnements, événements ou équipes pour votre salle.
Entraîneur : Créez et gérez des cours et des plannings.
Sportif : Consulter les activités, abonnements, événements,cours et produit ,effectuer des paiements, gérer les avis et contribuer aux blogs,participer a une evenement.

Étiquettes (Topics)

Application disktop
java
AI
machine learning
Xampp
intégration-paiement


Remerciements
Ce projet a été réalisé sous la direction du corps professoral de l'École d'Ingénieurs Esprit. Un grand merci à nos professeurs  Madame Chaima et Monsieur Moataz pour leur soutien et leurs retours tout au long du développement.

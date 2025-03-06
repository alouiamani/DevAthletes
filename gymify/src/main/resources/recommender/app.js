const express = require('express');
const cors = require('cors');
const userRoutes = require('./routes/userRoutes');
const courseRoutes = require('./routes/courseRoutes');

const app = express();
app.use(cors());
app.use(express.json());

// Routes
app.use('/api/users', userRoutes);
app.use('/api/courses', courseRoutes);

// Middleware de gestion des erreurs
app.use((err, req, res, next) => {
    console.error(err.stack);
    res.status(500).json({ message: 'Une erreur est survenue sur le serveur' });
});

const PORT = 3000;
app.listen(PORT, () => {
    console.log(`Serveur en cours d'exécution sur le port ${PORT}`);
})

const pool = require('./config/db');

pool.query('SELECT 1 + 1 AS solution')
    .then(([rows]) => {
        console.log('Connexion à la base de données réussie :', rows);
    })
    .catch((error) => {
        console.error('Erreur de connexion à la base de données :', error);
    });
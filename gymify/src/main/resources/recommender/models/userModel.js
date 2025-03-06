const pool = require('../config/db');

class UserModel {
    static async getInfoSportifByUserId(userId) {
        const query = 'SELECT age, poids, taille, sexe, objectif FROM infosportif WHERE sportif_id = ?';
        const [rows] = await pool.query(query, [userId]);
        return rows[0];
    }
}

module.exports = UserModel;
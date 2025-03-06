const UserModel = require('../models/userModel');

class UserController {
    static async getUserData(req, res) {
        const userId = req.params.userId;
        try {
            const userData = await UserModel.getInfoSportifByUserId(userId);
            if (!userData) {
                return res.status(404).json({ message: 'Utilisateur non trouvé' });
            }
            res.json(userData);
        } catch (error) {
            res.status(500).json({ message: 'Erreur lors de la récupération des informations utilisateur' });
        }
    }
}

module.exports = UserController;
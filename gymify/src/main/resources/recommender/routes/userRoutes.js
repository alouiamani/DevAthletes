const express = require('express');
const UserController = require('../controllers/userController');

const router = express.Router();

router.get('/:userId', UserController.getUserData);

module.exports = router;
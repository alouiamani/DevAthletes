const CourseModel = require('../models/courseModel');

class CourseController {
    static async getAllCourses(req, res) {
        try {
            const courses = await CourseModel.getAllCourses();
            res.json(courses);
        } catch (error) {
            res.status(500).json({ message: 'Erreur lors de la récupération des cours' });
        }
    }
}

module.exports = CourseController;
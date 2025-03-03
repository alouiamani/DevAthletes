const pool = require('../config/db');

class CourseModel {
    static async getAllCourses() {
        const query = 'SELECT * FROM cours';
        const [rows] = await pool.query(query);
        return rows;
    }
}

module.exports = CourseModel;
package model.dao;

import db.DB;
import model.dao.impl.SchoolDaoJDBC;
import model.dao.impl.StudentDaoJDBC;

public class DaoFactory {

	public static StudentDao createStudentDao() {
		return new StudentDaoJDBC(DB.getConnection());
	}
	
	public static SchoolDao createSchoolDao() {
		return new SchoolDaoJDBC(DB.getConnection());
	}
}

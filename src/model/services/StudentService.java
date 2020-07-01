package model.services;

import java.util.List;

import model.dao.DaoFactory;
import model.dao.StudentDao;
import model.entities.Student;

public class StudentService {

	private StudentDao dao = DaoFactory.createStudentDao();

	public List<Student> findAll() {
		return dao.findAll();
	}
	
	public void saveOrUptade(Student obj) {
		if(obj.getId() == null) {
			dao.insert(obj);
		}
		else {
			dao.update(obj);
		}
	}
	
	public void remove(Student obj) {
		dao.deleteById(obj.getId());
	}
}

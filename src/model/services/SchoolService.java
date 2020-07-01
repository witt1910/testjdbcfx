package model.services;

import java.util.List;

import model.dao.DaoFactory;
import model.dao.SchoolDao;
import model.entities.School;

public class SchoolService {

	private SchoolDao dao = DaoFactory.createSchoolDao();

	public List<School> findAll() {
		return dao.findAll();
	}
	
	public void saveOrUptade(School obj) {
		if(obj.getId() == null) {
			dao.insert(obj);
		}
		else {
			dao.update(obj);
		}
	}
	
	public void remove(School obj) {
		dao.deleteById(obj.getId());
	}
}

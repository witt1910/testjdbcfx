package model.dao;

import java.util.List;

import model.entities.School;

public interface SchoolDao {

	void insert(School obj);
	void update(School obj);
	void deleteById(Integer id);
	School findById(Integer id);
	List<School> findAll();
	
}

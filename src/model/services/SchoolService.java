package model.services;

import java.util.ArrayList;
import java.util.List;

import model.entities.School;

public class SchoolService {

	public List<School> findAll(){
		List<School> list = new ArrayList<>();
		list.add(new School(1, "Chapeuzinho Vermelho"));
		list.add(new School(2, "Branca de Neve"));
		list.add(new School(3, "Ze Carioca"));
		return list;
	}
}

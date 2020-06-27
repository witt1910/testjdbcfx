package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mysql.jdbc.Statement;

import db.DB;
import db.DbException;
import model.dao.StudentDao;
import model.entities.School;
import model.entities.Student;

public class StudentDaoJDBC implements StudentDao {

	private Connection conn;

	public StudentDaoJDBC(Connection conn) {
		this.conn = conn;
	}

	@Override
	public void insert(Student obj) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement(
					"INSERT INTO student " + "(Name, BirthDate, Demand, Grade, SchoolId) " 
					+ "VALUES (?, ?, ?, ?, ?)",
					Statement.RETURN_GENERATED_KEYS);

			st.setString(1, obj.getName());
			st.setDate(2, new java.sql.Date(obj.getBirthDate().getTime()));
			st.setString(3, obj.getDemand());
			st.setInt(4, obj.getGrade());
			st.setInt(5, obj.getSchool().getId());

			int rowsAffected = st.executeUpdate();

			if (rowsAffected > 0) {
				ResultSet rs = st.getGeneratedKeys();
				if (rs.next()) {
					int id = rs.getInt(1);
					obj.setId(id);
				}
				DB.closeResultSet(rs);
			} else {
				throw new DbException("Unexpected error! No rows affected!");
			}
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
		}
	}

	@Override
	public void update(Student obj) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("UPDATE student "
					+ "SET Name = ?, BirthDate = ?, Demand = ?, Grade = ?, SchoolId = ? " 
					+ "WHERE id = ?");

			st.setString(1, obj.getName());
			st.setDate(2, new java.sql.Date(obj.getBirthDate().getTime()));
			st.setString(3, obj.getDemand());
			st.setInt(4, obj.getGrade());
			st.setInt(5, obj.getSchool().getId());
			st.setInt(6, obj.getId());

			st.executeUpdate();

		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
		}
	}

	@Override
	public void deleteById(Integer id) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("DELETE FROM student WHERE Id = ?");
			
			st.setInt(1, id);
			
			int rows = st.executeUpdate();
			if (rows == 0) {
				throw new DbException("Insert valid Id.");
			}
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
		}
	}

	@Override
	public Student findById(Integer id) {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement("SELECT student.*, school.Name as SchName " 
					+ "FROM student INNER JOIN school "
					+ "ON student.SchoolId = school.Id " 
					+ "WHERE student.Id = ?");

			st.setInt(1, id);
			rs = st.executeQuery();
			if (rs.next()) {
				School sch = instantiateSchool(rs);
				Student obj = instantiateStudent(rs, sch);
				return obj;
			}
			return null;
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeResultSet(rs);
			DB.closeStatement(st);
		}
	}

	private Student instantiateStudent(ResultSet rs, School sch) throws SQLException {
		Student obj = new Student();
		obj.setId(rs.getInt("Id"));
		obj.setName(rs.getString("Name"));
		obj.setBirthDate(rs.getDate("BirthDate"));
		obj.setDemand(rs.getString("Demand"));
		obj.setGrade(rs.getInt("Grade"));
		obj.setSchool(sch);
		return obj;
	}

	private School instantiateSchool(ResultSet rs) throws SQLException {
		School sch = new School();
		sch.setId(rs.getInt("SchoolId"));
		sch.setName(rs.getString("SchName"));
		return sch;
	}

	@Override
	public List<Student> findAll() {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement("SELECT student.*, school.Name as SchName " + "FROM student INNER JOIN school "
					+ "ON student.SchoolId = school.Id " + "ORDER BY Name");

			rs = st.executeQuery();

			List<Student> list = new ArrayList<>();
			Map<Integer, School> map = new HashMap<>();

			while (rs.next()) {

				School sch = map.get(rs.getInt("SchoolId"));
				if (sch == null) {
					sch = instantiateSchool(rs);
					map.put(rs.getInt("SchoolId"), sch);
				}
				Student obj = instantiateStudent(rs, sch);
				list.add(obj);
			}
			return list;
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeResultSet(rs);
			DB.closeStatement(st);
		}
	}

	@Override
	public List<Student> findBySchool(School school) {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement(
					"SELECT student.*, school.Name as SchName " 
					+ "FROM student INNER JOIN school "
					+ "ON student.SchoolId = school.Id "
					+ "WHERE SchoolId = ? ORDER BY Name");

			st.setInt(1, school.getId());

			rs = st.executeQuery();

			List<Student> list = new ArrayList<>();
			Map<Integer, School> map = new HashMap<>();

			while (rs.next()) {

				School sch = map.get(rs.getInt("SchoolId"));
				if (sch == null) {
					sch = instantiateSchool(rs);
					map.put(rs.getInt("SchoolId"), sch);
				}
				Student obj = instantiateStudent(rs, sch);
				list.add(obj);
			}
			return list;
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeResultSet(rs);
			DB.closeStatement(st);
		}
	}
}

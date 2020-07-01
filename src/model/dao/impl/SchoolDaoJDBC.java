package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.mysql.jdbc.Statement;

import db.DB;
import db.DbException;
import db.DbIntegrityException;
import model.dao.SchoolDao;
import model.entities.School;

public class SchoolDaoJDBC implements SchoolDao {
	
	private Connection conn;

	public SchoolDaoJDBC(Connection conn) {
		this.conn = conn;
	}

	@Override
	public void insert(School obj) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement(
					"INSERT INTO school (Name) " 
					+ "VALUES (?)",
					Statement.RETURN_GENERATED_KEYS);

			st.setString(1, obj.getName());

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
	public void update(School obj) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement(
					"UPDATE school SET Name = ? " 
					+ "WHERE Id = ?");

			st.setString(1, obj.getName());
			st.setInt(2, obj.getId());

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
			st = conn.prepareStatement("DELETE FROM school WHERE Id = ?");
			
			st.setInt(1, id);
			
			int rows = st.executeUpdate();
			if (rows == 0) {
				throw new DbException("Insert a valid Id.");
			}
		}
		catch (SQLException e) {
			throw new DbIntegrityException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
		}
	}

	@Override
	public School findById(Integer id) {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement(
					"SELECT * FROM school WHERE Id = ?");
			st.setInt(1, id);
			rs = st.executeQuery();
			if (rs.next()) {
				School sch = instantiateSchool(rs);
				return sch;
			}
			return null;
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeResultSet(rs);
			DB.closeStatement(st);
		}
	}

	private School instantiateSchool(ResultSet rs) throws SQLException {
		School sch = new School();
		sch.setId(rs.getInt("Id"));
		sch.setName(rs.getString("Name"));
		return sch;
	}

	@Override
	public List<School> findAll() {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement(
					"SELECT * FROM school ORDER BY Name");
			rs = st.executeQuery();

			List<School> list = new ArrayList<>();
			
			while(rs.next()) {
				School sch = instantiateSchool(rs);
				list.add(sch);
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

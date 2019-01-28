/**
 * UserDaoJDBC.java
 *
 *  @author Daniel Bispo <danielvbispo@outlook.com>
 *  Created on 27 de jan de 2019
 *  GNU License
 *
 */
package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import db.DB;
import db.DbException;
import model.dao.CrudDAO;
import model.entities.User;
import model.entities.UserProfile;

/**
 * Implementation of CrudDAO for UserDaoJDBC entity. It uses JDBC connection
 * only.
 */
public class UserDaoJDBC implements CrudDAO<User> {

	private Connection conn;

	public UserDaoJDBC(Connection conn) {
		this.conn = conn;
	}

	@Override
	// A new User object has to be created before. Then use that as the
	// parameter for this method.
	public void insert(User obj) {

		String sql = "INSERT INTO user (login, password, user_name, email, profile_id, profile_info, active) VALUES (?, ?, ?, ?, ?, ?, ?)";

		PreparedStatement pstmt = null;

		try {

			/*
			 * Statement.RETURN_GENERATED_KEYS is needed as second parameter to get the key
			 * generated by the database, since it will be used to update the object's id
			 */
			pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			pstmt.setString(1, obj.getLogin());
			pstmt.setString(2, obj.getPassword());
			pstmt.setString(3, obj.getUserName());
			pstmt.setString(4, obj.getEmail());
			pstmt.setInt(5, obj.getUserProfile().getId());
			pstmt.setString(6, obj.getUserProfile().getUserProfile());
			pstmt.setBoolean(7, obj.isActive());

			// Get the amount of the inserted row
			int insRows = pstmt.executeUpdate();

			if (insRows > 0) {
				ResultSet rs = pstmt.getGeneratedKeys();

				if (rs.next()) {
					obj.setId(rs.getInt(1)); // Set a new object Id according to database
				}
			}

		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(pstmt);
		}
	}

	@Override
	// An new User object has to be created before. Then use that as the
	// parameter for this method
	public void upDate(User obj) {

		String sql = "UPDATE user SET login=?, password=?, user_name=?, email=?, profile_id=?, profile_info=?, active=? WHERE id=?";

		PreparedStatement pstmt = null;

		try {

			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, obj.getLogin());
			pstmt.setString(2, obj.getPassword());
			pstmt.setString(3, obj.getUserName());
			pstmt.setString(4, obj.getEmail());
			pstmt.setInt(5, obj.getUserProfile().getId());
			pstmt.setString(6, obj.getUserProfile().getUserProfile());
			pstmt.setBoolean(7, obj.isActive());
			pstmt.setInt(8, obj.getId());

			pstmt.executeUpdate();

		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(pstmt);
		}
	}

	@Override
	public void deleteById(int id) {

		String sql = "DELETE FROM user WHERE id=?";

		PreparedStatement pstmt = null;

		try {

			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, id);
			pstmt.executeUpdate();

		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(pstmt);
		}
	}

	@Override
	public User findById(int id) {

		String sql = "SELECT * FROM user WHERE id=?";

		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, id);

			rs = pstmt.executeQuery();

			if (rs.next()) {

				return createUserObj(rs);
			}

			return null;

		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(pstmt);
			DB.closeResultSet(rs);
		}
	}

	@Override
	public List<User> findAll() {

		// A list which contains all User's elements read from database
		List<User> userList = new ArrayList<>();

		String sql = "SELECT * FROM user ORDER BY user_name";

		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {

			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();

			while (rs.next()) {

				userList.add(createUserObj(rs));
			}

			return userList;

		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(pstmt);
			DB.closeResultSet(rs);
		}
	}

	// Instantiate an User object used by findAll()
	private User createUserObj(ResultSet rs) throws SQLException {

		User user = new User();

		user.setId(rs.getInt("id"));
		user.setLogin(rs.getString("login"));
		user.setPassword(rs.getString("password"));
		user.setUserName(rs.getString("user_name"));
		user.setEmail(rs.getString("email"));
		user.setUserProfile(createUserProfileObj(rs));
		user.setActive(rs.getBoolean("active"));

		return user;
	}

	// instantiate an UserProfile object used by createUserObj
	private UserProfile createUserProfileObj(ResultSet rs) throws SQLException {

		UserProfile userProfile = new UserProfile();

		userProfile.setId(rs.getInt("profile_id"));
		userProfile.setUserProfile(rs.getString("profile_info"));

		return userProfile;
	}
	
	public User findByUserLoggin(String loggin) {
		
		User userObj = new User();
		
		String sql = "SELECT * FROM user WHERE login=?";
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, loggin);
			
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				userObj = createUserObj(rs);
				return userObj;
			}
			
			return null;
			
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(pstmt);
			DB.closeResultSet(rs);
		}		
	}
}

package cn.edu.nju.ws.camo.webservice.user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import cn.edu.nju.ws.camo.webservice.connect.DBConnFactory;
import cn.edu.nju.ws.camo.webservice.util.SetSerialization;

public class UserProfileFactory {

	private static UserProfileFactory instance = null;
	private UserProfileFactory() {
	}
	
	public static UserProfileFactory getInstance() {
		if(instance == null)
			instance = new UserProfileFactory();
		return instance;
	}
	
	public boolean addUser(String name, String pwd, String email, String sex) {
		name = SetSerialization.rmIllegal(name);
		email = SetSerialization.rmIllegal(email);
		sex = SetSerialization.rmIllegal(sex);
		pwd = SetSerialization.rmIllegal(pwd);
		if(pwd.length()>200)
			return false;
		int sexInt = -1;
		sex = sex.toLowerCase();
		if(sex.equals("male")) {
			sexInt = 1;
		} else if(sex.equals("female")) {
			sexInt = 0;
		} else {
			return false;
		}
		try {
			if(getUserByMail(email).length() == 0)
				return true;
			Connection sourceConn = DBConnFactory.getInstance().dbConnect(DBConnFactory.USER_CONN);
			String sqlStr = "insert into user(pwd,name,email,sex) values(?,?,?,?)";
			PreparedStatement stmt = sourceConn.prepareStatement(sqlStr);
			stmt.setString(1, pwd);
			stmt.setString(2, name);
			stmt.setString(3, email);
			stmt.setInt(4, sexInt);
			stmt.executeUpdate();
			stmt.close();
			sourceConn.close();
		} catch (Throwable e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public String getUserByMail(String email) {
		String userInfo = "";
		email = SetSerialization.rmIllegal(email);
		ArrayList<String> valueList = new ArrayList<String>();
		try {
			Connection sourceConn = DBConnFactory.getInstance().dbConnect(DBConnFactory.USER_CONN);
			String sqlStr = "select * from user where email=?";
			PreparedStatement stmt = sourceConn.prepareStatement(sqlStr);
			stmt.setString(1, email);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				String sex = "male";
				if(rs.getInt("sex")==0)
					sex = "female";
				valueList.add(rs.getString("id"));
				valueList.add(rs.getString("name"));
				valueList.add(email);
				valueList.add(sex);
			}
			rs.close();
			stmt.close();
			sourceConn.close();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		if(valueList.size()>0) {
			userInfo = SetSerialization.serialize1(valueList);
		}
		return userInfo;
	}
	
	public String getUserById(int uid) {
		String userInfo = "";
		ArrayList<String> valueList = new ArrayList<String>();
		try {
			Connection sourceConn = DBConnFactory.getInstance().dbConnect(DBConnFactory.USER_CONN);
			String sqlStr = "select * from user where id=?";
			PreparedStatement stmt = sourceConn.prepareStatement(sqlStr);
			stmt.setInt(1, uid);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				String sex = "male";
				if(rs.getInt("sex")==0)
					sex = "female";
				valueList.add(String.valueOf(uid));
				valueList.add(rs.getString("name"));
				valueList.add(rs.getString("email"));
				valueList.add(sex);
			}
			rs.close();
			stmt.close();
			sourceConn.close();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		if(valueList.size()>0) {
			userInfo = SetSerialization.serialize1(valueList);
		}
		return userInfo;
	}
	
	public String getUserByPwd(String email, String pwd) {
		String userInfo = "";
		email = SetSerialization.rmIllegal(email);
		pwd = SetSerialization.rmIllegal(pwd);
		ArrayList<String> valueList = new ArrayList<String>();
		try {
			Connection sourceConn = DBConnFactory.getInstance().dbConnect(DBConnFactory.USER_CONN);
			String sqlStr = "select * from user where email=? and pwd=?";
			PreparedStatement stmt = sourceConn.prepareStatement(sqlStr);
			stmt.setString(1, email);
			stmt.setString(2, pwd);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				String sex = "male";
				if(rs.getInt("sex")==0)
					sex = "female";
				valueList.add(rs.getString("id"));
				valueList.add(rs.getString("name"));
				valueList.add(email);
				valueList.add(sex);
			}
			rs.close();
			stmt.close();
			sourceConn.close();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		if(valueList.size()>0) {
			userInfo = SetSerialization.serialize1(valueList);
		}
		return userInfo;
	}
	
	
}

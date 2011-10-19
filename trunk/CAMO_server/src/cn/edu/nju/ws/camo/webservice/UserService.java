package cn.edu.nju.ws.camo.webservice;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.jws.WebService;

import cn.edu.nju.ws.camo.webservice.connect.DBConnFactory;
import cn.edu.nju.ws.camo.webservice.util.SetSerialization;

@WebService(endpointInterface="cn.edu.nju.ws.camo.webservice.IUserService") 
public class UserService implements IUserService {

	public String addPreference(int uid, String inst, String mediaType,
			String instType, String labelName, int uAction, int subscribe) {
		int updateLine = updatePreference(uid, inst, uAction, subscribe);
		if(updateLine>0)
			return "1";
		inst = SetSerialization.rmIllegal(inst);
		mediaType = SetSerialization.rmIllegal(mediaType);
		instType = SetSerialization.rmIllegal(instType);
		if(mediaType.trim().equals("") && mediaType.equals("movie")==false && mediaType.equals("music")==false && mediaType.equals("photo")==false)
			return "0";
		try {
			Connection sourceConn = DBConnFactory.getInstance().dbConnect(DBConnFactory.USER_CONN);
			String sqlStr = "insert into preference(uid,inst,media_type,inst_type,label_name,u_action,subscribe,u_time) values(?,?,?,?,?,?,?,?)";
			PreparedStatement stmt = sourceConn.prepareStatement(sqlStr);
			Timestamp curTime = new Timestamp(System.currentTimeMillis());
			stmt.setInt(1, uid);
			stmt.setString(2, inst);
			stmt.setString(3, mediaType);
			stmt.setString(4, instType);
			stmt.setString(5, labelName);
			stmt.setInt(6, uAction);
			stmt.setInt(7, subscribe);
			stmt.setTimestamp(8, curTime);
			stmt.executeUpdate();
			stmt.close();
			sourceConn.close();
		} catch (Throwable e) {
			e.printStackTrace();
			return "0";
		}
		return "1";
	}
	
	private int updatePreference(int uid, String inst, int uAction, int subscribe) {
		inst = SetSerialization.rmIllegal(inst);
		int line=0;
		try {
			Connection sourceConn = DBConnFactory.getInstance().dbConnect(DBConnFactory.USER_CONN);
			String sqlStr = "update preference set u_action=?,subscribe=?,u_time=? where uid=? and inst=?";
			PreparedStatement stmt = sourceConn.prepareStatement(sqlStr);
			Timestamp curTime = new Timestamp(System.currentTimeMillis());
			stmt.setInt(1, uAction);
			stmt.setInt(2, subscribe);
			stmt.setTimestamp(3, curTime);
			stmt.setInt(4, uid);
			stmt.setString(5, inst);
			line = stmt.executeUpdate();
			stmt.close();
			sourceConn.close();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return line;
	}

	public String delPreference(int uid, String inst) {
		inst = SetSerialization.rmIllegal(inst);
		int line=0;
		try {
			Connection sourceConn = DBConnFactory.getInstance().dbConnect(DBConnFactory.USER_CONN);
			String sqlStr = "delete from preference where uid=? and inst=?";
			PreparedStatement stmt = sourceConn.prepareStatement(sqlStr);
			stmt.setInt(1, uid);
			stmt.setString(2, inst);
			line = stmt.executeUpdate();
			stmt.close();
			sourceConn.close();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return String.valueOf(line);
	}

	public String addUser(String name, String pwd, String email, String sex) {
		name = SetSerialization.rmIllegal(name);
		email = SetSerialization.rmIllegal(email);
		sex = SetSerialization.rmIllegal(sex);
		pwd = SetSerialization.rmIllegal(pwd);
		int sexInt = -1;
		sex = sex.toLowerCase();
		if(sex.equals("male")) {
			sexInt = 1;
		} else if(sex.equals("female")) {
			sexInt = 0;
		} else {
			return "0";
		}
		try {
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
			return "0";
		}
		return "1";
	}

	public String getPreference(int uid, String mediaType, String instType,
			int uAction) {
		if(mediaType.trim().equals("") && instType.trim().equals("")) {
			return getPreferenceNoType(uid, uAction);
		} else if(instType.trim().equals("")) {
			return getPreferenceNoInstType(uid, mediaType, uAction);
		}
		String preferInfo = "";
		ArrayList<String> valueList = new ArrayList<String>();
		mediaType = SetSerialization.rmIllegal(mediaType);
		instType = SetSerialization.rmIllegal(instType);
		try {
			Connection sourceConn = DBConnFactory.getInstance().dbConnect(DBConnFactory.USER_CONN);
			String sqlStr = "select inst,label_name,subscribe,u_time from preference where uid=? and media_type=? and inst_type=? and u_action=? order by u_time desc";
			PreparedStatement stmt = sourceConn.prepareStatement(sqlStr);
			stmt.setInt(1, uid);
			stmt.setString(2, mediaType);
			stmt.setString(3, instType);
			stmt.setInt(4, uAction);
			ResultSet rs = stmt.executeQuery();
			while(rs.next()) {
				ArrayList<String> termSet = new ArrayList<String>();
				termSet.add(String.valueOf(uid));
				termSet.add(rs.getString("inst"));
				termSet.add(mediaType);
				termSet.add(instType);
				termSet.add(rs.getString("label_name"));
				termSet.add(String.valueOf(uAction));
				termSet.add(String.valueOf(rs.getInt("subscribe")));
				termSet.add(String.valueOf(rs.getTimestamp("u_time").getTime()));
				valueList.add(SetSerialization.serialize1(termSet));
			}
			preferInfo = SetSerialization.serialize2(valueList);
			rs.close();
			stmt.close();
			sourceConn.close();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return preferInfo;
	}
	
	private String getPreferenceNoInstType(int uid, String mediaType, int uAction) {
		String preferInfo = "";
		ArrayList<String> valueList = new ArrayList<String>();
		mediaType = SetSerialization.rmIllegal(mediaType);
		try {
			Connection sourceConn = DBConnFactory.getInstance().dbConnect(DBConnFactory.USER_CONN);
			String sqlStr = "select inst,label_name,inst_type,subscribe,u_time from preference where uid=? and media_type=? and u_action=? order by u_time desc";
			PreparedStatement stmt = sourceConn.prepareStatement(sqlStr);
			stmt.setInt(1, uid);
			stmt.setString(2, mediaType);
			stmt.setInt(3, uAction);
			ResultSet rs = stmt.executeQuery();
			while(rs.next()) {
				ArrayList<String> termSet = new ArrayList<String>();
				termSet.add(String.valueOf(uid));
				termSet.add(rs.getString("inst"));
				termSet.add(mediaType);
				termSet.add(rs.getString("inst_type"));
				termSet.add(rs.getString("label_name"));
				termSet.add(String.valueOf(uAction));
				termSet.add(String.valueOf(rs.getInt("subscribe")));
				termSet.add(String.valueOf(rs.getTimestamp("u_time").getTime()));
				valueList.add(SetSerialization.serialize1(termSet));
			}
			preferInfo = SetSerialization.serialize2(valueList);
			rs.close();
			stmt.close();
			sourceConn.close();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return preferInfo;
	}
	
	private String getPreferenceNoType(int uid, int uAction) {
		String preferInfo = "";
		ArrayList<String> valueList = new ArrayList<String>();
		try {
			Connection sourceConn = DBConnFactory.getInstance().dbConnect(DBConnFactory.USER_CONN);
			String sqlStr = "select inst,label_name,media_type,inst_type,subscribe,u_time from preference where uid=? and u_action=? order by u_time desc";
			PreparedStatement stmt = sourceConn.prepareStatement(sqlStr);
			stmt.setInt(1, uid);
			stmt.setInt(2, uAction);
			ResultSet rs = stmt.executeQuery();
			while(rs.next()) {
				ArrayList<String> termSet = new ArrayList<String>();
				termSet.add(String.valueOf(uid));
				termSet.add(rs.getString("inst"));
				termSet.add(rs.getString("media_type"));
				termSet.add(rs.getString("inst_type"));
				termSet.add(rs.getString("label_name"));
				termSet.add(String.valueOf(uAction));
				termSet.add(String.valueOf(rs.getInt("subscribe")));
				termSet.add(String.valueOf(rs.getTimestamp("u_time").getTime()));
				valueList.add(SetSerialization.serialize1(termSet));
			}
			preferInfo = SetSerialization.serialize2(valueList);
			rs.close();
			stmt.close();
			sourceConn.close();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return preferInfo;
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
	
	
	
	public String getUserByMail(String email, String pwd) {
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
	
	public String addFriend(int uid1, int uid2) {
		try {
			if(isFriend(uid1, uid2))
				return "1";
			Connection sourceConn = DBConnFactory.getInstance().dbConnect(DBConnFactory.USER_CONN);
			String sqlStr = "insert into friends(u_id1,u_id2,f_time) values(?,?,?)";
			PreparedStatement stmt = sourceConn.prepareStatement(sqlStr);
			Timestamp curTime = new Timestamp(System.currentTimeMillis());
			stmt.setInt(1, uid1);
			stmt.setInt(2, uid2);
			stmt.setTimestamp(3, curTime);
			stmt.executeUpdate();
			stmt.close();
			sourceConn.close();
		} catch (Throwable e) {
			e.printStackTrace();
			return "0";
		}
		return "1";
	}
	
	public String delFriend(int uid1, int uid2) {
		int line=0;
		try {
			Connection sourceConn = DBConnFactory.getInstance().dbConnect(DBConnFactory.USER_CONN);
			String sqlStr = "delete from friends where u_id1=? and u_id2=?";
			PreparedStatement stmt = sourceConn.prepareStatement(sqlStr);
			stmt.setInt(1, uid1);
			stmt.setInt(2, uid2);
			line = stmt.executeUpdate();
			stmt.close();
			sourceConn.close();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return String.valueOf(line);
	}
	
	private boolean isFriend(int uid1, int uid2) {
		boolean is = false;
		try {
			Connection sourceConn = DBConnFactory.getInstance().dbConnect(DBConnFactory.USER_CONN);
			String sqlStr = "select * from friends where (u_id1=? and u_id2=?) or (u_id2=? and u_id1=?)";
			PreparedStatement stmt = sourceConn.prepareStatement(sqlStr);
			stmt.setInt(1, uid1);
			stmt.setInt(2, uid2);
			stmt.setInt(3, uid1);
			stmt.setInt(4, uid2);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				is = true;
			}
			rs.close();
			stmt.close();
			sourceConn.close();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return is;
	}
	
	public String getAllFriends(int uid1) {
		String friends = "";
		List<String> friendList = new ArrayList<String>();
		try {
			Connection sourceConn = DBConnFactory.getInstance().dbConnect(DBConnFactory.USER_CONN);
			String sqlStr = "select user1.id as id1, user1.name as name1, user1.email as mail1, user1.sex as sex1, user2.id as id2, user2.name as name2, user2.email as mail2, user2.sex as sex2, friends.f_time as f_time from friends join(user as user1, user as user2) on(friends.u_id1=user1.id and friends.u_id2=user2.id) where friends.u_id1=? or friends.u_id2=? order by f_time desc";
			PreparedStatement stmt = sourceConn.prepareStatement(sqlStr);
			stmt.setInt(1, uid1);
			stmt.setInt(2, uid1);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				List<String> termList = new ArrayList<String>();
				if(rs.getInt("id1") != uid1) {
					String sex = "male";
					if(rs.getInt("sex1")==0)
						sex = "female";
					termList.add(String.valueOf(rs.getInt("id1")));
					termList.add(rs.getString("name1"));
					termList.add(rs.getString("mail1"));
					termList.add(sex);
				}
				else {
					String sex = "male";
					if(rs.getInt("sex2")==0)
						sex = "female";
					termList.add(String.valueOf(rs.getInt("id2")));
					termList.add(rs.getString("name2"));
					termList.add(rs.getString("mail2"));
					termList.add(sex);
				}
				termList.add(String.valueOf(rs.getTimestamp("f_time").getTime()));
				friendList.add(SetSerialization.serialize1(termList));
			}
			if(friendList.size()>0)
				friends = SetSerialization.serialize2(friendList);
			rs.close();
			stmt.close();
			sourceConn.close();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return friends;
	}
}

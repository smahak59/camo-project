package cn.edu.nju.ws.camo.webservice.user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;

import javax.jws.WebService;

import cn.edu.nju.ws.camo.webservice.connect.DBConnFactory;
import cn.edu.nju.ws.camo.webservice.util.SetSerialization;

@WebService(endpointInterface="cn.edu.nju.ws.camo.webservice.user.IUserService") 
public class UserService implements IUserService {

	public void addPreference(int uid, String inst, String mediaType,
			String instType, int uAction, int subscribe) {
		inst = SetSerialization.rmIllegal(inst);
		mediaType = SetSerialization.rmIllegal(mediaType);
		instType = SetSerialization.rmIllegal(instType);
		try {
			Connection sourceConn = DBConnFactory.getInstance().dbConnect(DBConnFactory.USER_CONN);
			String sqlStr = "insert into preference(uid,inst,media_type,inst_type,u_action,subscribe,u_time) values(?,?,?,?,?,?,?)";
			PreparedStatement stmt = sourceConn.prepareStatement(sqlStr);
			stmt.setInt(1, uid);
			stmt.setString(2, mediaType);
			stmt.setString(3, instType);
			stmt.setInt(4, uAction);
			stmt.setInt(5, subscribe);
			stmt.setLong(6, (new Date()).getTime());
			stmt.executeUpdate();
			stmt.close();
			sourceConn.close();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public void addUser(String name, String email, String sex) {
		name = SetSerialization.rmIllegal(name);
		email = SetSerialization.rmIllegal(email);
		sex = SetSerialization.rmIllegal(sex);
		int sexInt = -1;
		sex = sex.toLowerCase();
		if(sex.equals("male")) {
			sexInt = 1;
		} else if(sex.equals("female")) {
			sexInt = 0;
		} else {
			return;
		}
		try {
			Connection sourceConn = DBConnFactory.getInstance().dbConnect(DBConnFactory.USER_CONN);
			String sqlStr = "insert into user(name,email,sex) values(?,?,?)";
			PreparedStatement stmt = sourceConn.prepareStatement(sqlStr);
			stmt.setString(1, name);
			stmt.setString(2, email);
			stmt.setInt(3, sexInt);
			stmt.executeUpdate();
			stmt.close();
			sourceConn.close();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public String getPreference(int uid, String mediaType, String instType,
			int uAction) {
		if(mediaType == null && instType == null) {
			return getPreferenceNoType(uid, uAction);
		} else if(instType == null) {
			return getPreferenceNoInstType(uid, mediaType, uAction);
		}
		String preferInfo = "";
		ArrayList<String> valueList = new ArrayList<String>();
		mediaType = SetSerialization.rmIllegal(mediaType);
		instType = SetSerialization.rmIllegal(instType);
		try {
			Connection sourceConn = DBConnFactory.getInstance().dbConnect(DBConnFactory.USER_CONN);
			String sqlStr = "select inst,subscribe,u_time from preference where uid=? and media_type=? and inst_type=? and u_action=?";
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
				termSet.add(String.valueOf(uAction));
				termSet.add(String.valueOf(rs.getInt("subscribe")));
				termSet.add(String.valueOf(rs.getLong("u_time")));
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
			String sqlStr = "select inst,inst_type,subscribe,u_time from preference where uid=? and media_type=? and u_action=?";
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
				termSet.add(String.valueOf(uAction));
				termSet.add(String.valueOf(rs.getInt("subscribe")));
				termSet.add(String.valueOf(rs.getLong("u_time")));
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
			String sqlStr = "select inst,media_type,inst_type,subscribe,u_time from preference where uid=? and u_action=?";
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
				termSet.add(String.valueOf(uAction));
				termSet.add(String.valueOf(rs.getInt("subscribe")));
				termSet.add(String.valueOf(rs.getLong("u_time")));
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

	public String getSubscribe(int uid, String mediaType, String instType) {
		if(mediaType == null && instType == null) {
			return getSubscribeNoType(uid);
		} else if(instType == null) {
			return getSubscribeNoInstType(uid, mediaType);
		}
		String preferInfo = "";
		ArrayList<String> valueList = new ArrayList<String>();
		mediaType = SetSerialization.rmIllegal(mediaType);
		instType = SetSerialization.rmIllegal(instType);
		try {
			Connection sourceConn = DBConnFactory.getInstance().dbConnect(DBConnFactory.USER_CONN);
			String sqlStr = "select inst,u_action,u_time from preference where uid=? and media_type=? and inst_type=? and subscribe=1";
			PreparedStatement stmt = sourceConn.prepareStatement(sqlStr);
			stmt.setInt(1, uid);
			stmt.setString(2, mediaType);
			stmt.setString(3, instType);
			ResultSet rs = stmt.executeQuery();
			while(rs.next()) {
				ArrayList<String> termSet = new ArrayList<String>();
				termSet.add(String.valueOf(uid));
				termSet.add(rs.getString("inst"));
				termSet.add(mediaType);
				termSet.add(instType);
				termSet.add(String.valueOf(rs.getInt("u_action")));
				termSet.add(String.valueOf(rs.getLong("u_time")));
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
	
	private String getSubscribeNoInstType(int uid, String mediaType) {
		String preferInfo = "";
		ArrayList<String> valueList = new ArrayList<String>();
		mediaType = SetSerialization.rmIllegal(mediaType);
		try {
			Connection sourceConn = DBConnFactory.getInstance().dbConnect(DBConnFactory.USER_CONN);
			String sqlStr = "select inst,inst_type,u_action,u_time from preference where uid=? and media_type=? and subscribe=1";
			PreparedStatement stmt = sourceConn.prepareStatement(sqlStr);
			stmt.setInt(1, uid);
			stmt.setString(2, mediaType);
			ResultSet rs = stmt.executeQuery();
			while(rs.next()) {
				ArrayList<String> termSet = new ArrayList<String>();
				termSet.add(String.valueOf(uid));
				termSet.add(rs.getString("inst"));
				termSet.add(mediaType);
				termSet.add(rs.getString("inst_type"));
				termSet.add(String.valueOf(rs.getInt("u_action")));
				termSet.add(String.valueOf(rs.getLong("u_time")));
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
	
	private String getSubscribeNoType(int uid) {
		String preferInfo = "";
		ArrayList<String> valueList = new ArrayList<String>();
		try {
			Connection sourceConn = DBConnFactory.getInstance().dbConnect(DBConnFactory.USER_CONN);
			String sqlStr = "select inst,media_type,inst_type,u_action,u_time from preference where uid=? and subscribe=1";
			PreparedStatement stmt = sourceConn.prepareStatement(sqlStr);
			stmt.setInt(1, uid);
			ResultSet rs = stmt.executeQuery();
			while(rs.next()) {
				ArrayList<String> termSet = new ArrayList<String>();
				termSet.add(String.valueOf(uid));
				termSet.add(rs.getString("inst"));
				termSet.add(rs.getString("media_type"));
				termSet.add(rs.getString("inst_type"));
				termSet.add(String.valueOf(rs.getInt("u_action")));
				termSet.add(String.valueOf(rs.getLong("u_time")));
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
	
	public String getUser(int uid) {
		String userInfo = "";
		ArrayList<String> valueList = new ArrayList<String>();
		try {
			Connection sourceConn = DBConnFactory.getInstance().dbConnect(DBConnFactory.USER_CONN);
			String sqlStr = "select * from user where u_id=?";
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
				valueList.add(rs.getString(sex));
			}
			rs.close();
			stmt.close();
			sourceConn.close();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(valueList.size()>0) {
			userInfo = SetSerialization.serialize1(valueList);
		}
		return userInfo;
	}
}

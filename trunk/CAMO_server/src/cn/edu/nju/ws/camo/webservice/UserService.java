package cn.edu.nju.ws.camo.webservice;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;

import javax.jws.WebService;

import cn.edu.nju.ws.camo.webservice.connect.DBConnFactory;
import cn.edu.nju.ws.camo.webservice.util.SetSerialization;

@WebService(endpointInterface="cn.edu.nju.ws.camo.webservice.IUserService") 
public class UserService implements IUserService {

	public String addPreference(int uid, String inst, String mediaType,
			String instType, String labelName, int uAction, int subscribe) {
		int updateLine = updatePreference(uid, instType, uAction, subscribe);
		if(updateLine>0)
			return "1";
		inst = SetSerialization.rmIllegal(inst);
		mediaType = SetSerialization.rmIllegal(mediaType);
		instType = SetSerialization.rmIllegal(instType);
		if(mediaType.equals("movie")==false && mediaType.equals("music")==false && mediaType.equals("photo")==false)
			return "0";
		try {
			Connection sourceConn = DBConnFactory.getInstance().dbConnect(DBConnFactory.USER_CONN);
			String sqlStr = "insert into preference(uid,inst,media_type,inst_type,label_name,u_action,subscribe,u_time) values(?,?,?,?,?,?,?,?)";
			PreparedStatement stmt = sourceConn.prepareStatement(sqlStr);
			stmt.setInt(1, uid);
			stmt.setString(2, inst);
			stmt.setString(3, mediaType);
			stmt.setString(4, instType);
			stmt.setString(5, labelName);
			stmt.setInt(6, uAction);
			stmt.setInt(7, subscribe);
			stmt.setLong(8, (new Date()).getTime());
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
			stmt.setInt(1, uAction);
			stmt.setInt(2, subscribe);
			stmt.setLong(3, (new Date()).getTime());
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

	public String addUser(String name, String email, String sex) {
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
			return "0";
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
			return "0";
		}
		return "1";
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
			String sqlStr = "select inst,label_name,subscribe,u_time from preference where uid=? and media_type=? and inst_type=? and u_action=?";
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
			String sqlStr = "select inst,label_name,inst_type,subscribe,u_time from preference where uid=? and media_type=? and u_action=?";
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
			String sqlStr = "select inst,label_name,media_type,inst_type,subscribe,u_time from preference where uid=? and u_action=?";
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
			String sqlStr = "select inst,label_name,u_action,u_time from preference where uid=? and media_type=? and inst_type=? and subscribe=1";
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
				termSet.add(rs.getString("label_name"));
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
			String sqlStr = "select inst,label_name,inst_type,u_action,u_time from preference where uid=? and media_type=? and subscribe=1";
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
				termSet.add(rs.getString("label_name"));
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
			String sqlStr = "select inst,label_name,media_type,inst_type,u_action,u_time from preference where uid=? and subscribe=1";
			PreparedStatement stmt = sourceConn.prepareStatement(sqlStr);
			stmt.setInt(1, uid);
			ResultSet rs = stmt.executeQuery();
			while(rs.next()) {
				ArrayList<String> termSet = new ArrayList<String>();
				termSet.add(String.valueOf(uid));
				termSet.add(rs.getString("inst"));
				termSet.add(rs.getString("media_type"));
				termSet.add(rs.getString("inst_type"));
				termSet.add(rs.getString("label_name"));
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
	
	public String getUserById(int uid) {
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
	
	public String getUserByMail(String email) {
		String userInfo = "";
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
}

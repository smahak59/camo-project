package cn.edu.nju.ws.camo.webservice.user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;

import cn.edu.nju.ws.camo.webservice.connect.DBConnFactory;
import cn.edu.nju.ws.camo.webservice.util.SetSerialization;

public class UserPreferenceFactory {

	private static UserPreferenceFactory instance = null;
	private UserPreferenceFactory() {}
	
	public static UserPreferenceFactory getInstance() {
		if(instance == null)
			instance = new UserPreferenceFactory();
		return instance;
	}
	
	public boolean addPreference(int uid, String inst, String mediaType,
			String instType, String labelName, int uAction, int subscribe) {
		int updateLine = updatePreference(uid, inst, uAction, subscribe);
		if(updateLine>0)
			return true;
		inst = SetSerialization.rmIllegal(inst);
		mediaType = SetSerialization.rmIllegal(mediaType);
		instType = SetSerialization.rmIllegal(instType);
		if(mediaType.trim().equals("") && mediaType.equals("movie")==false && mediaType.equals("music")==false && mediaType.equals("photo")==false)
			return false;
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
			return false;
		}
		return true;
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
	
	public int delPreference(int uid, String inst) {
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
		return line;
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
}

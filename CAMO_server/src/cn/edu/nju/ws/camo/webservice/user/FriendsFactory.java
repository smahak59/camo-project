package cn.edu.nju.ws.camo.webservice.user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import cn.edu.nju.ws.camo.webservice.connect.DBConnFactory;
import cn.edu.nju.ws.camo.webservice.interestgp.InterestGpFactory;
import cn.edu.nju.ws.camo.webservice.util.SetSerialization;

public class FriendsFactory {
	
	private static FriendsFactory instance = null;
	private FriendsFactory() {}
	
	public static FriendsFactory getInstance() {
		if(instance == null)
			instance = new FriendsFactory();
		return instance;
	}

	public boolean addFriend(int uid1, int uid2) {
		try {
			if(isFriends(uid1, uid2))
				return true;
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
			return false;
		}
		delFriendRequest(uid1, uid2);
		delFriendRequest(uid2, uid1);
		return true;
	}
	
	public boolean isFriends(int uid1, int uid2) {
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
	
	public boolean delFriendRequest(int userFrom, int userTo) {
		try {
			Connection sourceConn = DBConnFactory.getInstance().dbConnect(DBConnFactory.USER_CONN);
			String sqlStr = "delete from friend_rq where u_from=? and u_to=?";
			PreparedStatement stmt = sourceConn.prepareStatement(sqlStr);
			stmt.setInt(1, userFrom);
			stmt.setInt(2, userTo);
			stmt.executeUpdate();
			stmt.close();
			sourceConn.close();
		} catch (Throwable e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public int delFriend(int uid1, int uid2) {
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
		InterestGpFactory.getInstance().setRecommandedUserRmd(uid1, uid2);
		return line;
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
	
	public boolean hasFriendRequest(int userFrom, int userTo) {
		boolean result = false;
		try {
			Connection sourceConn = DBConnFactory.getInstance().dbConnect(DBConnFactory.USER_CONN);
			String sqlStr = "select * friend_rq where u_from=? and u_to=?";
			PreparedStatement stmt = sourceConn.prepareStatement(sqlStr);
			stmt.setInt(1, userFrom);
			stmt.setInt(2, userTo);
			ResultSet rs = stmt.executeQuery();
			if(rs.next()) {
				result = true;
			}
			stmt.executeQuery();
			stmt.close();
			sourceConn.close();
		} catch (Throwable e) {
			e.printStackTrace();
			return false;
		}
		return result;
	}
	
	public boolean addFriendRequest(int userFrom, int userTo) {
		if(isFriends(userFrom, userTo))
			return false;
		if(hasFriendRequest(userFrom,userTo))
			return false;
		try {
			Connection sourceConn = DBConnFactory.getInstance().dbConnect(DBConnFactory.USER_CONN);
			String sqlStr = "insert into friend_rq(u_from,u_to,status,in_time) values(?,?,?,?)";
			PreparedStatement stmt = sourceConn.prepareStatement(sqlStr);
			Timestamp curTime = new Timestamp(System.currentTimeMillis());
			stmt.setInt(1, userFrom);
			stmt.setInt(2, userTo);
			stmt.setInt(3, 0);
			stmt.setTimestamp(4, curTime);
			stmt.executeUpdate();
			stmt.close();
			sourceConn.close();
		} catch (Throwable e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public String getAllRequests(int userTo) {
		String result = "";
		List<String> userList = new ArrayList<String>();
		try {
			Connection sourceConn = DBConnFactory.getInstance().dbConnect(DBConnFactory.USER_CONN);
			String sqlStr = "select user.id,user.name,user.email,user.sex from friend_rq join(user) on(friend_rq.u_from=user.id) where friend_rq.u_to=? order by friend_rq.in_time desc";
			PreparedStatement stmt = sourceConn.prepareStatement(sqlStr);
			stmt.setInt(1, userTo);
			ResultSet rs = stmt.executeQuery();
			while(rs.next()) {
				List<String> termList = new ArrayList<String>();
				String sex = "male";
				if(rs.getInt(4)==0)
					sex = "female";
				termList.add(String.valueOf(rs.getInt(1)));
				termList.add(rs.getString(2));
				termList.add(rs.getString(3));
				termList.add(sex);
				userList.add(SetSerialization.serialize1(termList));
			}
			result = SetSerialization.serialize2(userList);
			stmt.close();
			sourceConn.close();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return result;
	}
}

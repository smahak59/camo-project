package cn.edu.nju.ws.camo.webservice.interestgp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import cn.edu.nju.ws.camo.webservice.connect.DBConnFactory;
import cn.edu.nju.ws.camo.webservice.util.SetSerialization;
import cn.edu.nju.ws.camo.webservice.view.LabelAndTypeFinder;
import cn.edu.nju.ws.camo.webservice.view.UriInjection;

public class InterestGpFactory {

	private static InterestGpFactory instance = null;
	private InterestGpFactory() {}
	
	public static InterestGpFactory getInstance() {
		if(instance == null)
			instance = new InterestGpFactory();
		return instance;
	}
	
	public String addInterest(int uid, String media, String mediaType, String artist) {
		media = SetSerialization.rmIllegal(media);
		artist = SetSerialization.rmIllegal(artist);
		try {
			Connection sourceConn = DBConnFactory.getInstance().dbConnect(DBConnFactory.USER_CONN);
			String sqlStr = "insert into media_favor(u_id,media,media_type,artist,in_time) values(?,?,?,?,?)";
			PreparedStatement stmt = sourceConn.prepareStatement(sqlStr);
			Timestamp curTime = new Timestamp(System.currentTimeMillis());
			stmt.setInt(1, uid);
			stmt.setString(2, media);
			stmt.setString(3, mediaType);
			stmt.setString(4, artist);
			stmt.setTimestamp(5, curTime);
			stmt.executeUpdate();
			stmt.close();
			sourceConn.close();
		} catch (Throwable e) {
			e.printStackTrace();
			return "0";
		}
		return "1";
	}

	public String delInterest(int uid, String media, String artist) {
		media = SetSerialization.rmIllegal(media);
		artist = SetSerialization.rmIllegal(artist);
		try {
			Connection sourceConn = DBConnFactory.getInstance().dbConnect(DBConnFactory.USER_CONN);
			String sqlStr = "delete from media_favor where u_id=? and media=? and artist=?";
			PreparedStatement stmt = sourceConn.prepareStatement(sqlStr);
			stmt.setInt(1, uid);
			stmt.setString(2, media);
			stmt.setString(3, artist);
			stmt.executeUpdate();
			stmt.close();
			sourceConn.close();
		} catch (Throwable e) {
			e.printStackTrace();
			return "0";
		}
		return "1";
	}
	
	public String getFavorArtist(int uid, String media) {
		String result = "";
		List<String> artistList = new ArrayList<String>();
		Map<String, String[]> artistSet = new HashMap<String, String[]>();
		media = SetSerialization.rmIllegal(media);
		try {
			Connection sourceConn = DBConnFactory.getInstance().dbConnect(DBConnFactory.USER_CONN);
			String sqlStr = "select artist from media_favor where u_id=? and media=? order by in_time desc";
			PreparedStatement stmt = sourceConn.prepareStatement(sqlStr);
			stmt.setInt(1, uid);
			stmt.setString(2, media);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				String[] value = {"",""};
				artistSet.put(rs.getString(1), value);
			}
			rs.close();
			stmt.close();
			sourceConn.close();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		try {
			UriInjection.initLabelAndType(artistSet);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		Iterator<Entry<String, String[]>>  itr = artistSet.entrySet().iterator();
		while(itr.hasNext()) {
			Entry<String, String[]> entry = itr.next();
			List<String> termList = new ArrayList<String>();
			termList.add(entry.getKey());
			termList.add(entry.getValue()[0]);
			termList.add(entry.getValue()[1]);
			artistList.add(SetSerialization.serialize1(termList));
		}
		
		result = SetSerialization.serialize2(artistList);
		return result;
	}
	
	private boolean isUserIgnore(int uid1, int uid2) {
		boolean result = false;
		try {
			Connection sourceConn = DBConnFactory.getInstance().dbConnect(DBConnFactory.USER_CONN);
			String sqlStr = "select * from ignore_rmd_request where u_from=? and u_to=?";
			PreparedStatement stmt = sourceConn.prepareStatement(sqlStr);
			stmt.setInt(1, uid1);
			stmt.setInt(2, uid2);
			ResultSet rs = stmt.executeQuery();
			if(rs.next())
				result = true;
			rs.close();
			stmt.close();
			sourceConn.close();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return result;
	}
	
	
	public String setRecommandedUserIgnore(int uid1, int uid2) {
		if(isUserIgnore(uid1, uid2)) 
			return "1";
		try {
			Connection sourceConn = DBConnFactory.getInstance().dbConnect(DBConnFactory.USER_CONN);
			String sqlStr = "insert into ignore_rmd_request(u_from,u_to,in_time) values(?,?,?)";
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
	
//	public String setRecommandedUserFriends(int uid1, int uid2) {
//		try {
//			Connection sourceConn = DBConnFactory.getInstance().dbConnect(DBConnFactory.USER_CONN);
//			String sqlStr = "update interest_gp set status=2 where (u_id1=? and u_id2=?) or (u_id2=? and u_id1=?)";
//			PreparedStatement stmt = sourceConn.prepareStatement(sqlStr);
//			stmt.setInt(1, uid1);
//			stmt.setInt(2, uid2);
//			stmt.setInt(3, uid1);
//			stmt.setInt(4, uid2);
//			stmt.executeUpdate();
//			stmt.close();
//			sourceConn.close();
//		} catch (Throwable e) {
//			e.printStackTrace();
//			return "0";
//		}
//		return "1";
//	}
	
	public String setRecommandedUserRmd(int uid1, int uid2) {
		try {
			Connection sourceConn = DBConnFactory.getInstance().dbConnect(DBConnFactory.USER_CONN);
			String sqlStr = "delete from ignore_rmd_request where u_from=? and u_to=?";
			PreparedStatement stmt = sourceConn.prepareStatement(sqlStr);
			stmt.setInt(1, uid1);
			stmt.setInt(2, uid2);
			stmt.executeUpdate();
			stmt.close();
			sourceConn.close();
		} catch (Throwable e) {
			e.printStackTrace();
			return "0";
		}
		return "1";
//		try {
//			Connection sourceConn = DBConnFactory.getInstance().dbConnect(DBConnFactory.USER_CONN);
//			String sqlStr = "update interest_gp set status=1 where (u_id1=? and u_id2=?) or (u_id2=? and u_id1=?)";
//			PreparedStatement stmt = sourceConn.prepareStatement(sqlStr);
//			stmt.setInt(1, uid1);
//			stmt.setInt(2, uid2);
//			stmt.setInt(3, uid1);
//			stmt.setInt(4, uid2);
//			stmt.executeUpdate();
//			stmt.close();
//			sourceConn.close();
//		} catch (Throwable e) {
//			e.printStackTrace();
//			return "0";
//		}
//		return "1";
	}
	
	
	/**
	 *  查找不在ignore列表里的，并且不是friends的user
	 * @param uid
	 * @param media
	 * @return
	 */
	/**
	 * @param uid
	 * @param media
	 * @return
	 */
	public String getRecommandedUser(int uid, String media) {
		String result = "";
		List<String> resultList = new ArrayList<String>();
		Thread recommandedFinder = new RecommandedUserFinder(uid, media);
		recommandedFinder.start();
		Thread friendsFinder = new FriendsFinder(uid);
		friendsFinder.start();
		Thread ignoresFinder = new IgnoreRmdFinder(uid);
		ignoresFinder.start();
		try {
			recommandedFinder.join();
			//(uid,name,email,sex,artist,rule)
			List<Object[]> userInfos = ((RecommandedUserFinder)recommandedFinder).getUserInfo();
			friendsFinder.join();
			Set<Integer> friends = ((FriendsFinder)friendsFinder).getFriends();
			ignoresFinder.join();
			Set<Integer> ignores = ((IgnoreRmdFinder)ignoresFinder).getIgnores();
			// (1)remove friends and ignores. 
			Iterator<Object[]> itr = userInfos.iterator();
			while(itr.hasNext()) {
				Object[] userInfo = itr.next();
				if(friends.contains((Integer)userInfo[0]))
					itr.remove();
				else if(ignores.contains((Integer)userInfo[0]))
					itr.remove();
			}
			// (2)find labels and types of artists.
			List<LabelAndTypeFinder> finderList = new ArrayList<LabelAndTypeFinder>();
			BlockingQueue<Runnable> bkQueue = new LinkedBlockingQueue<Runnable>();
			ThreadPoolExecutor threadExec = new ThreadPoolExecutor(3, 5, 7, TimeUnit.DAYS, bkQueue);
			for(Object[] userInfo : userInfos) {
				LabelAndTypeFinder newFinder = new LabelAndTypeFinder((String)userInfo[4]);
				threadExec.execute(newFinder);
				finderList.add(newFinder);
			}
			threadExec.shutdown();
			threadExec.awaitTermination(7, TimeUnit.DAYS);
			for(int i=0; i<finderList.size(); i++) {
				Object[] userInfo = userInfos.get(i);
				LabelAndTypeFinder finder = finderList.get(i);
				List<String> userTerms = new ArrayList<String>();
				int rmdUserId = (Integer)userInfo[0];
				String rmdUserName = (String)userInfo[1];
				String rmdUserMail = (String)userInfo[2];
				String rmdUserSex = (String)userInfo[3];
				userTerms.add(String.valueOf(rmdUserId));
				userTerms.add(rmdUserName);
				userTerms.add(rmdUserMail);
				userTerms.add(rmdUserSex);
				String rmdUserProfile = SetSerialization.serialize1(userTerms);
				String rmdUserArtist = finder.getResult();
				List<String> rmdUserInfoList = new ArrayList<String>();
				rmdUserInfoList.add(rmdUserProfile);
				rmdUserInfoList.add(rmdUserArtist);
				rmdUserInfoList.add((String)userInfo[5]);
				resultList.add(SetSerialization.serialize2(rmdUserInfoList));
			}
			result = SetSerialization.serialize3(resultList);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	class RecommandedUserFinder extends Thread {
		
		List<Object[]> userInfo = new ArrayList<Object[]>();
		private int uid;
		private String media;
		
		RecommandedUserFinder(int uid, String media) {
			this.uid = uid;
			this.media = media;
		}
		
		@Override
		public void run() {
			try {
				Connection sourceConn = DBConnFactory.getInstance().dbConnect(DBConnFactory.USER_CONN);
				String sqlStr = "select user1.id,user1.name,user1.email,user1.sex,gp.artist1,user2.id,user2.name,user2.email,user2.sex,gp.artist2,gp.rule from interest_gp as gp join(user as user1,user as user2) on(gp.u_id1=user1.id and gp.u_id2=user2.id) where (gp.u_id1=? or gp.u_id2=?) and gp.media=? order by gp.in_time desc";
				PreparedStatement stmt = sourceConn.prepareStatement(sqlStr);
				stmt.setInt(1, uid);
				stmt.setInt(2, uid);
				stmt.setString(3, media);
				ResultSet rs = stmt.executeQuery();
				while (rs.next()) {
					if(rs.getInt(1)==uid) {
						String sex = "male";
						if(rs.getInt(9)==0)
							sex = "female";
						Object[] value = {rs.getInt(6),rs.getString(7),rs.getString(8),sex,rs.getString(10),rs.getInt(11)};
						userInfo.add(value);
					} else {
						String sex = "male";
						if(rs.getInt(4)==0)
							sex = "female";
						Object[] value = {rs.getInt(1),rs.getString(2),rs.getString(3),sex,rs.getString(5),rs.getInt(11)};
						userInfo.add(value);
					}
				}
				rs.close();
				stmt.close();
				sourceConn.close();
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
		
		public List<Object[]> getUserInfo() {
			return this.userInfo;
		}
	}
	
	class FriendsFinder extends Thread {
		
		private int uid;
		private Set<Integer> friends = new HashSet<Integer>();
		
		FriendsFinder(int uid) {
			this.uid = uid;
		}
		
		@Override
		public void run() {
			try {
				Connection sourceConn = DBConnFactory.getInstance().dbConnect(DBConnFactory.USER_CONN);
				String sqlStr = "select u_id1,u_id2 from friends where u_id1=? or u_id2=?";
				PreparedStatement stmt = sourceConn.prepareStatement(sqlStr);
				stmt.setInt(1, uid);
				stmt.setInt(2, uid);
				ResultSet rs = stmt.executeQuery();
				while (rs.next()) {
					if(rs.getInt(1)==uid) {
						friends.add(rs.getInt(2));
					} else {
						friends.add(rs.getInt(1));
					}
				}
				rs.close();
				stmt.close();
				sourceConn.close();
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
		
		public Set<Integer> getFriends() {
			return this.friends;
		}
	}
	
	class IgnoreRmdFinder extends Thread {
		private int uid;
		private Set<Integer> ignores = new HashSet<Integer>();
		
		IgnoreRmdFinder(int uid) {
			this.uid = uid;
		}
		
		@Override
		public void run() {
			try {
				Connection sourceConn = DBConnFactory.getInstance().dbConnect(DBConnFactory.USER_CONN);
				String sqlStr = "select u_to from ignore_rmd_request where u_from=?";
				PreparedStatement stmt = sourceConn.prepareStatement(sqlStr);
				stmt.setInt(1, uid);
				ResultSet rs = stmt.executeQuery();
				while(rs.next()) {
					ignores.add(rs.getInt(1));
				}
				rs.close();
				stmt.close();
				sourceConn.close();
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}

		public Set<Integer> getIgnores() {
			return ignores;
		}
		
	}
}

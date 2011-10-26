package cn.edu.nju.ws.camo.webservice.interestgp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
	
	public String addInterest(int uid, String userName, String media, String mediaType, String artist) {
		media = SetSerialization.rmIllegal(media);
		artist = SetSerialization.rmIllegal(artist);
		try {
			Connection sourceConn = DBConnFactory.getInstance().dbConnect(DBConnFactory.ISTGP_CONN);
			String sqlStr = "insert into media_favor(u_id,u_name,media,media_type,artist,in_time) values(?,?,?,?,?,?)";
			PreparedStatement stmt = sourceConn.prepareStatement(sqlStr);
			Timestamp curTime = new Timestamp(System.currentTimeMillis());
			stmt.setInt(1, uid);
			stmt.setString(2, userName);
			stmt.setString(3, media);
			stmt.setString(4, mediaType);
			stmt.setString(5, artist);
			stmt.setTimestamp(6, curTime);
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
			Connection sourceConn = DBConnFactory.getInstance().dbConnect(DBConnFactory.ISTGP_CONN);
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
			Connection sourceConn = DBConnFactory.getInstance().dbConnect(DBConnFactory.ISTGP_CONN);
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
			Connection sourceConn = DBConnFactory.getInstance().dbConnect(DBConnFactory.ISTGP_CONN);
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
			Connection sourceConn = DBConnFactory.getInstance().dbConnect(DBConnFactory.ISTGP_CONN);
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
	
	public String setRecommandedUserRmd(int uid1, int uid2) {
		try {
			Connection sourceConn = DBConnFactory.getInstance().dbConnect(DBConnFactory.ISTGP_CONN);
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
	}
	
	public String getRecommandedUserForMusic(int uid, String music) {
		String result = "";
		List<String> resultList = new ArrayList<String>();
		Thread rmdRule1Finder = new RmdCommomMediaLikedUserFinder(uid, music);
		if(RuleJob.COMMOM_ROLE_RULE)
			rmdRule1Finder.start();
		Thread recommandedFinder = new RmdUserForMusicFinder(music);
		recommandedFinder.start();
		Thread friendsFinder = new FriendsFinder(uid);
		friendsFinder.start();
		Thread ignoresFinder = new IgnoreRmdFinder(uid);
		ignoresFinder.start();
		
		try {
			recommandedFinder.join();
			// (uid,uname,music,in_time,rule)
			List<Object[]> userInfos = ((RmdUserForMusicFinder)recommandedFinder).getUserInfo();
			if(RuleJob.COMMOM_ROLE_RULE)
				rmdRule1Finder.join();
			userInfos.addAll(((RmdCommomMediaLikedUserFinder)rmdRule1Finder).getUserInfo());
			friendsFinder.join();
			Set<Integer> friends = ((FriendsFinder)friendsFinder).getFriends();
			ignoresFinder.join();
			Set<Integer> ignores = ((IgnoreRmdFinder)ignoresFinder).getIgnores();
			// (1)remove friends and ignores. 
			Iterator<Object[]> itr = userInfos.iterator();
			Set<Integer> hasAddedUserId = new HashSet<Integer>();
			while(itr.hasNext()) {
				Object[] userInfo = itr.next();
				if(isLegalRule((Integer)userInfo[4])==false) 
					itr.remove();
				else if(friends.contains((Integer)userInfo[0]))
					itr.remove();
				else if(ignores.contains((Integer)userInfo[0]))
					itr.remove();
				else if(hasAddedUserId.contains((Integer)userInfo[0])) 
					itr.remove();
				else if((Integer)userInfo[0] == uid) 
					itr.remove();
				else {
					hasAddedUserId.add((Integer)userInfo[0]);
				}
			}
			// (2)find labels and types of movies.
			List<LabelAndTypeFinder> finderList = new ArrayList<LabelAndTypeFinder>();
			BlockingQueue<Runnable> bkQueue = new LinkedBlockingQueue<Runnable>();
			ThreadPoolExecutor threadExec = new ThreadPoolExecutor(3, 5, 7, TimeUnit.DAYS, bkQueue);
			for(Object[] userInfo : userInfos) {
				LabelAndTypeFinder newFinder = new LabelAndTypeFinder((String)userInfo[2]);
				threadExec.execute(newFinder);
				finderList.add(newFinder);
			}
			threadExec.shutdown();
			threadExec.awaitTermination(7, TimeUnit.DAYS);
			Set<Integer> addedUserId = new HashSet<Integer>();
			for(int i=0; i<finderList.size(); i++) {
				Object[] userInfo = userInfos.get(i);
				LabelAndTypeFinder finder = finderList.get(i);
				List<String> userTerms = new ArrayList<String>();
				int rmdUserId = (Integer)userInfo[0];
				if(addedUserId.contains(rmdUserId))
					continue;
				addedUserId.add(rmdUserId);
				String rmdUserName = (String)userInfo[1];
				userTerms.add(String.valueOf(rmdUserId));
				userTerms.add(rmdUserName);
				String rmdUserProfile = SetSerialization.serialize1(userTerms);
				String rmdUserMusic = finder.getResult();
				List<String> rmdUserInfoList = new ArrayList<String>();
				rmdUserInfoList.add(rmdUserProfile);
				rmdUserInfoList.add(rmdUserMusic);
				rmdUserInfoList.add(((Long)userInfo[3]).toString());
				rmdUserInfoList.add(((Integer)userInfo[4]).toString());
				resultList.add(SetSerialization.serialize2(rmdUserInfoList));
			}
			result = SetSerialization.serialize3(resultList);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	
	/**
	 *  查找不在ignore列表里的，并且不是friends的user
	 * @param uid
	 * @param movie
	 * @return (uid,uname;artist,label,type;in_time;rule)
	 */
	public String getRecommandedUserForMovie(int uid, String movie) {
		String result = "";		
		List<String> resultList = new ArrayList<String>();
		Thread rmdRule1Finder = new RmdCommomRoleLikedUserFinder(uid, movie);
		if(RuleJob.COMMOM_ROLE_RULE)
			rmdRule1Finder.start();
		Thread recommandedFinder = new RmdUserForMovieFinder(uid, movie);
		recommandedFinder.start();
		Thread friendsFinder = new FriendsFinder(uid);
		friendsFinder.start();
		Thread ignoresFinder = new IgnoreRmdFinder(uid);
		ignoresFinder.start();
		try {
			recommandedFinder.join();
			//(id,name,artist,in_time,rule)
			List<Object[]> userInfos = ((RmdUserForMovieFinder)recommandedFinder).getUserInfo();
			if(RuleJob.COMMOM_ROLE_RULE)
				rmdRule1Finder.join();
			userInfos.addAll(((RmdCommomRoleLikedUserFinder)rmdRule1Finder).getUserInfo());
			friendsFinder.join();
			Set<Integer> friends = ((FriendsFinder)friendsFinder).getFriends();
			ignoresFinder.join();
			Set<Integer> ignores = ((IgnoreRmdFinder)ignoresFinder).getIgnores();
			// (1)remove friends and ignores. 
			Iterator<Object[]> itr = userInfos.iterator();
			Set<Integer> hasAddedUserId = new HashSet<Integer>();
			while(itr.hasNext()) {
				Object[] userInfo = itr.next();
				if(isLegalRule((Integer)userInfo[4])==false) 
					itr.remove();
				else if(friends.contains((Integer)userInfo[0]))
					itr.remove();
				else if(ignores.contains((Integer)userInfo[0]))
					itr.remove();
				else if(hasAddedUserId.contains((Integer)userInfo[0]))
					itr.remove();
				else if((Integer)userInfo[0] == uid) 
					itr.remove();
				else 
					hasAddedUserId.add((Integer)userInfo[0]);
			}
			// (2)find labels and types of artists.
			List<LabelAndTypeFinder> finderList = new ArrayList<LabelAndTypeFinder>();
			BlockingQueue<Runnable> bkQueue = new LinkedBlockingQueue<Runnable>();
			ThreadPoolExecutor threadExec = new ThreadPoolExecutor(3, 5, 7, TimeUnit.DAYS, bkQueue);
			for(Object[] userInfo : userInfos) {
				LabelAndTypeFinder newFinder = new LabelAndTypeFinder((String)userInfo[2]);
				threadExec.execute(newFinder);
				finderList.add(newFinder);
			}
			threadExec.shutdown();
			threadExec.awaitTermination(7, TimeUnit.DAYS);
			Set<Integer> addedUserId = new HashSet<Integer>();
			for(int i=0; i<finderList.size(); i++) {
				Object[] userInfo = userInfos.get(i);
				LabelAndTypeFinder finder = finderList.get(i);
				List<String> userTerms = new ArrayList<String>();
				int rmdUserId = (Integer)userInfo[0];
				if(addedUserId.contains(rmdUserId))
					continue;
				addedUserId.add(rmdUserId);
				String rmdUserName = (String)userInfo[1];
				userTerms.add(String.valueOf(rmdUserId));
				userTerms.add(rmdUserName);
				String rmdUserProfile = SetSerialization.serialize1(userTerms);
				String rmdUserArtist = finder.getResult();
				List<String> rmdUserInfoList = new ArrayList<String>();
				rmdUserInfoList.add(rmdUserProfile);
				rmdUserInfoList.add(rmdUserArtist);
				rmdUserInfoList.add(((Long)userInfo[3]).toString());
				rmdUserInfoList.add(((Integer)userInfo[4]).toString());
				resultList.add(SetSerialization.serialize2(rmdUserInfoList));
			}
			result = SetSerialization.serialize3(resultList);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	private boolean isLegalRule(int ruleId) {
		boolean legal = false;
		switch (ruleId) {
		case RuleJob.COMMOM_INTEREST_RULE:
			legal = true;
			break;
		case SpouseMovieRuleJob.ruleId:
			legal = true;
			break;
		case CooperatorMovieRuleJob.ruleId:
			legal = true;
			break;
		case SeriesMusicRuleJob.ruleId:
			legal = true;
			break;
		default:
			legal = false;
			break;
		}
		return legal;
	}
	
	class RmdCommomMediaLikedUserFinder extends Thread {
		
		List<Object[]> userInfo = new ArrayList<Object[]>();
		private int uid;
		private String media;
		
		RmdCommomMediaLikedUserFinder(int uid, String media) {
			this.uid = uid;
			this.media = media;
		}
		
		@Override
		public void run() {
			try {
				Connection sourceConn = DBConnFactory.getInstance().dbConnect(DBConnFactory.ISTGP_CONN);
				String sqlStr = "select mf2.u_id,mf2.u_name,mf2.in_time from media_favor as mf1 join(media_favor as mf2) on(mf1.media=mf2.media) where mf1.u_id=? and mf1.media=?";
				PreparedStatement stmt = sourceConn.prepareStatement(sqlStr);
				stmt.setInt(1, uid);
				stmt.setString(2, media);
				ResultSet rs = stmt.executeQuery();
				while (rs.next()) {
					Object[] value = {rs.getInt(1),rs.getString(2),media,rs.getTimestamp(3).getTime(),RuleJob.COMMOM_INTEREST_RULE};
					userInfo.add(value);
				}
				rs.close();
				stmt.close();
				sourceConn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
		
		public List<Object[]> getUserInfo() {
			return this.userInfo;
		}
	}
	
	
	
	class RmdCommomRoleLikedUserFinder extends Thread {
		
		List<Object[]> userInfo = new ArrayList<Object[]>();
		private int uid;
		private String media;
		
		RmdCommomRoleLikedUserFinder(int uid, String media) {
			this.uid = uid;
			this.media = media;
		}
		
		@Override
		public void run() {
			try {
				Connection sourceConn = DBConnFactory.getInstance().dbConnect(DBConnFactory.ISTGP_CONN);
				String sqlStr = "select mf2.u_id,mf2.u_name,mf2.artist,mf2.in_time from media_favor as mf1 join(media_favor as mf2) on(mf1.media=mf2.media and mf1.artist=mf2.artist) where mf1.u_id=? and mf1.media=?";
				PreparedStatement stmt = sourceConn.prepareStatement(sqlStr);
				stmt.setInt(1, uid);
				stmt.setString(2, media);
				ResultSet rs = stmt.executeQuery();
				while (rs.next()) {
					Object[] value = {rs.getInt(1),rs.getString(2),rs.getString(3),rs.getTimestamp(4).getTime(),RuleJob.COMMOM_INTEREST_RULE};
					userInfo.add(value);
				}
				rs.close();
				stmt.close();
				sourceConn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		public List<Object[]> getUserInfo() {
			return this.userInfo;
		}
	}
	
	class RmdUserForMusicFinder extends Thread {
		
		// (uid,uname,movie,in_time,rule)
		List<Object[]> userInfo = new ArrayList<Object[]>();
		private String music;
		
		RmdUserForMusicFinder(String media) {
			this.music = media;
		}
		
		@Override
		public void run() {
			try {
				Connection sourceConn = DBConnFactory.getInstance().dbConnect(DBConnFactory.ISTGP_CONN);
				String sqlStr = "select mf.u_id,mf.u_name,mf.media,mf.in_time,temptb.rule from media_favor as mf join((select music,rule from music_gp where gp_id in (select gp_id from music_gp where music=?)) as temptb) on(mf.media=temptb.music)";
				PreparedStatement stmt = sourceConn.prepareStatement(sqlStr);
				stmt.setString(1, music);
				ResultSet rs = stmt.executeQuery();
				while (rs.next()) {
					Object[] value = {rs.getInt(1),rs.getString(2),rs.getString(3),rs.getTimestamp(4).getTime(),rs.getInt(5)};
					userInfo.add(value);
				}
				rs.close();
				stmt.close();
				sourceConn.close();
			} catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		public List<Object[]> getUserInfo() {
			return this.userInfo;
		}
	}
	
	class RmdUserForMovieFinder extends Thread {
		
		List<Object[]> userInfo = new ArrayList<Object[]>();
		private int uid;
		private String movie;
		
		RmdUserForMovieFinder(int uid, String media) {
			this.uid = uid;
			this.movie = media;
		}
		
		@Override
		public void run() {
			try {
				Connection sourceConn = DBConnFactory.getInstance().dbConnect(DBConnFactory.ISTGP_CONN);
				String sqlStr = "select mf1.u_id,mf1.u_name,mf1.artist,mf1.in_time,mf2.u_id,mf2.u_name,mf2.artist,mf2.in_time,gp.rule from movie_artist_gp as gp join(media_favor as mf1,media_favor as mf2) on(gp.movie=mf1.media and gp.artist1=mf1.artist and gp.movie=mf2.media and gp.artist2=mf2.artist) where gp.movie=? and (mf1.u_id=? or mf2.u_id=?)";
				PreparedStatement stmt = sourceConn.prepareStatement(sqlStr);
				stmt.setString(1, movie);
				stmt.setInt(2, uid);
				stmt.setInt(3, uid);
				ResultSet rs = stmt.executeQuery();
				while (rs.next()) {
					if(rs.getInt(1)==uid) {
						Object[] value = {rs.getInt(5),rs.getString(6),rs.getString(7),rs.getTimestamp(8).getTime(),rs.getInt(9)};
						userInfo.add(value);
					} else {
						Object[] value = {rs.getInt(1),rs.getString(2),rs.getString(3),rs.getTimestamp(4).getTime(),rs.getInt(9)};
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
				Connection sourceConn = DBConnFactory.getInstance().dbConnect(DBConnFactory.ISTGP_CONN);
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

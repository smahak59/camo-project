package cn.edu.nju.ws.camo.android.user.interestgp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.edu.nju.ws.camo.android.connect.ServerParam;
import cn.edu.nju.ws.camo.android.connect.WebService;
import cn.edu.nju.ws.camo.android.rdf.RdfFactory;
import cn.edu.nju.ws.camo.android.rdf.UriInstance;
import cn.edu.nju.ws.camo.android.user.User;
import cn.edu.nju.ws.camo.android.util.Command;
import cn.edu.nju.ws.camo.android.util.SetSerialization;

public class InterestGroup {

	private User curUser = null;
	private static Map<Integer, String> rulesToSuggest = null;
	
	public InterestGroup(User user) {
		this.curUser = user;
		if(rulesToSuggest == null) {
			rulesToSuggest = new HashMap<Integer, String>();
			initRuleToSuggest();
		}
	}
	
	public Command getUserIgnoreCmd(User user) {
		return new IgnoreRmdUserCmd(curUser, user);
	}
	
	public Command getUserRecommandedCmd(User user) {
		return new RmdUserCmd(curUser, user);
	}
	
	/**
	 * 根据目前用户正在观看的movie推荐用户
	 * @param curMovie: 
	 * @return 推荐的用户以及其兴趣(movie+artist)，按时间排序
	 */
	public List<RmdFeedback> getRecommandedMovieUser(UriInstance curMovie) {
		List<RmdFeedback> rmdUserList = new ArrayList<RmdFeedback>();
		Object[] paramValues = { curUser.getId(), curMovie.getUri()};
		String naiveResult = WebService.getInstance().runFunction(
				ServerParam.INTERESET_GP_URL, "getRecommandedMovieUser", paramValues);
		if (naiveResult.equals(""))
			return rmdUserList;
		if (naiveResult.equals(ServerParam.NETWORK_ERROR1))
			return rmdUserList;
		List<String> naiveRecordList = SetSerialization.deserialize3(naiveResult);
		for(String naiveRecord : naiveRecordList) {
			List<String> naiveUnitList = SetSerialization.deserialize2(naiveRecord);
			if(naiveUnitList.size()<4)
				continue;
			String naiveUserProfile = naiveUnitList.get(0);
			String naiveArtist = naiveUnitList.get(1);
			Long createTime = Long.valueOf(naiveUnitList.get(2));
			int ruleId = Integer.valueOf(naiveUnitList.get(3));
			List<String> naiveUserProfileUnits = SetSerialization.deserialize1(naiveUserProfile);
			User rmdUserInfo = new User(Integer.valueOf(naiveUserProfileUnits.get(0)));
			rmdUserInfo.setName(naiveUserProfileUnits.get(1));
			List<String> naiveArtistUnits = SetSerialization.deserialize1(naiveArtist);
			if(naiveArtistUnits.size()==1) {
				naiveArtistUnits.add("NO NAME");
				naiveArtistUnits.add("");
			}
			UriInstance artist = RdfFactory.getInstance().createInstance(naiveArtistUnits.get(0), curMovie.getMediaType(), naiveArtistUnits.get(2), SetSerialization.instNameNomalize(naiveArtistUnits.get(1)));
			MediaArtistInterest newUserInterest = new MediaArtistInterest(rmdUserInfo, curMovie, artist);
			RmdFeedbackForMovie rmdUser = new RmdFeedbackForMovie(newUserInterest, ruleId);
			rmdUser.setTime(createTime);
			rmdUserList.add(rmdUser);
		}
		Collections.sort(rmdUserList, Collections.reverseOrder());
		return rmdUserList;
	}
	
	
	
	/**
	 * 根据目前用户正在收听的音乐推荐用户
	 * @param curMusic
	 * @return 推荐的用户以及其兴趣(music)，按时间排序
	 */
	public List<RmdFeedback> getRecommandedMusicUser(UriInstance curMusic) {
		List<RmdFeedback> rmdUserList = new ArrayList<RmdFeedback>();
		Object[] paramValues = { curUser.getId(), curMusic.getUri()};
		String naiveResult = WebService.getInstance().runFunction(
				ServerParam.INTERESET_GP_URL, "getRecommandedMusicUser", paramValues);
		if (naiveResult.equals(""))
			return rmdUserList;
		if (naiveResult.equals(ServerParam.NETWORK_ERROR1))
			return rmdUserList;
		List<String> naiveRecordList = SetSerialization.deserialize3(naiveResult);
		for(String naiveRecord : naiveRecordList) {
			List<String> naiveUnitList = SetSerialization.deserialize2(naiveRecord);
			if(naiveUnitList.size()<4)
				continue;
			String naiveUserProfile = naiveUnitList.get(0);
			String naiveMusic = naiveUnitList.get(1);
			Long createTime = Long.valueOf(naiveUnitList.get(2));
			int ruleId = Integer.valueOf(naiveUnitList.get(3));
			List<String> naiveUserProfileUnits = SetSerialization.deserialize1(naiveUserProfile);
			User rmdUserInfo = new User(Integer.valueOf(naiveUserProfileUnits.get(0)));
			rmdUserInfo.setName(naiveUserProfileUnits.get(1));
			List<String> naiveMusicUnits = SetSerialization.deserialize1(naiveMusic);
			if(naiveMusicUnits.size()==1) {
				naiveMusicUnits.add("NO NAME");
				naiveMusicUnits.add("");
			}
			UriInstance music = RdfFactory.getInstance().createInstance(naiveMusicUnits.get(0), curMusic.getMediaType(), naiveMusicUnits.get(2), SetSerialization.instNameNomalize(naiveMusicUnits.get(1)));
			MediaInterest newUserInterest = new MediaInterest(rmdUserInfo, music);
			RmdFeedbackForMusic rmdUser = new RmdFeedbackForMusic(newUserInterest, ruleId);
			rmdUser.setTime(createTime);
			rmdUserList.add(rmdUser);
		}
		Collections.sort(rmdUserList, Collections.reverseOrder());
		return rmdUserList;
	}
	
	private void initRuleToSuggest() {
		rulesToSuggest.put(0, "share other interests");
		rulesToSuggest.put(1, "choose a day to date");
		rulesToSuggest.put(2, "play games of double players");
		rulesToSuggest.put(11, "share CDs with each other");
	}
	
	public static String getRuleSuggestion(int ruleId) {
		return rulesToSuggest.get(ruleId);
	}
	
	class IgnoreRmdUserCmd implements Command {
		
		private User user1;
		private User user2;

		IgnoreRmdUserCmd(User user1, User user2) {
			this.user1 = user1;
			this.user2 = user2;
		}
		
		public void execute() {
			Object[] paramValues = {user1.getId(), user2.getId()};
			WebService.getInstance().runFunction(ServerParam.INTERESET_GP_URL,
					"setRecommandedUserIgnore", paramValues);
		}
	}
	
	class RmdUserCmd implements Command {

		private User user1;
		private User user2;

		RmdUserCmd(User user1, User user2) {
			this.user1 = user1;
			this.user2 = user2;
		}
		
		public void execute() {
			Object[] paramValues = {user1.getId(), user2.getId()};
			WebService.getInstance().runFunction(ServerParam.INTERESET_GP_URL,
					"setRecommandedUserRmd", paramValues);
		}
	}
}

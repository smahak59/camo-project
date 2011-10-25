package cn.edu.nju.ws.camo.android.interestgp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.edu.nju.ws.camo.android.connect.server.ServerParam;
import cn.edu.nju.ws.camo.android.connect.server.WebService;
import cn.edu.nju.ws.camo.android.operate.command.Command;
import cn.edu.nju.ws.camo.android.rdf.RdfFactory;
import cn.edu.nju.ws.camo.android.rdf.UriInstance;
import cn.edu.nju.ws.camo.android.util.SetSerialization;
import cn.edu.nju.ws.camo.android.util.User;

public class InterestGroup {

	private User curUser = null;
	
	public InterestGroup(User user) {
		this.curUser = user;
	}
	
	public Command getUserIgnoreCmd(User user) {
		return new IgnoreRmdUserCmd(curUser, user);
	}
	
	public Command getUserRecommandedCmd(User user) {
		return new RmdUserCmd(curUser, user);
	}
	
	/**
	 * ����Ŀǰ�û����ڹۿ���movie�Ƽ��û�
	 * @param curMovie: 
	 * @return �Ƽ����û��Լ�����Ȥ(movie+artist)����ʱ������
	 */
	public List<RmdFeedbackForMovie> getRecommandedMovieUser(UriInstance curMovie) {
		List<RmdFeedbackForMovie> rmdUserList = new ArrayList<RmdFeedbackForMovie>();
		Object[] paramValues = { curUser.getId(), curMovie.getUri()};
		String naiveResult = WebService.getInstance().runFunction(
				ServerParam.INTERESET_GP_URL, "getRecommandedUserForMovie", paramValues);
		if (naiveResult.equals(""))
			return rmdUserList;
		if (naiveResult.equals(ServerParam.NETWORK_ERROR1))
			return null;
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
			UriInstance artist = RdfFactory.getInstance().createInstance(naiveArtistUnits.get(0), curMovie.getMediaType(), naiveArtistUnits.get(2), naiveArtistUnits.get(1));
			MediaArtistInterest newUserInterest = new MediaArtistInterest(rmdUserInfo, curMovie, artist);
			RmdFeedbackForMovie rmdUser = new RmdFeedbackForMovie(newUserInterest, ruleId);
			rmdUser.setTime(createTime);
			rmdUserList.add(rmdUser);
		}
		Collections.sort(rmdUserList, Collections.reverseOrder());
		return rmdUserList;
	}
	
	
	
	/**
	 * ����Ŀǰ�û����������������Ƽ��û�
	 * @param curMusic
	 * @return �Ƽ����û��Լ�����Ȥ(music)����ʱ������
	 */
	public List<RmdFeedbackForMusic> getRecommandedMusicUser(UriInstance curMusic) {
		List<RmdFeedbackForMusic> rmdUserList = new ArrayList<RmdFeedbackForMusic>();
		Object[] paramValues = { curUser.getId(), curMusic.getUri()};
		String naiveResult = WebService.getInstance().runFunction(
				ServerParam.INTERESET_GP_URL, "getRecommandedUserForMusic", paramValues);
		if (naiveResult.equals(""))
			return rmdUserList;
		if (naiveResult.equals(ServerParam.NETWORK_ERROR1))
			return null;
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
			UriInstance music = RdfFactory.getInstance().createInstance(naiveMusicUnits.get(0), curMusic.getMediaType(), naiveMusicUnits.get(2), naiveMusicUnits.get(1));
			MediaInterest newUserInterest = new MediaInterest(rmdUserInfo, music);
			RmdFeedbackForMusic rmdUser = new RmdFeedbackForMusic(newUserInterest, ruleId);
			rmdUser.setTime(createTime);
			rmdUserList.add(rmdUser);
		}
		Collections.sort(rmdUserList, Collections.reverseOrder());
		return rmdUserList;
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

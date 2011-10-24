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
	
	public Command setUserIgnore(User user) {
		return new IgnoreRmdUserCmd(curUser, user);
	}
	
	public Command setUserRecommanded(User user) {
		return new RmdUserCmd(curUser, user);
	}
	
	/**
	 * @param media: 暂时仅支持movie
	 * @return 推荐的用户以及其兴趣，按时间排序
	 */
	public List<RecommandFeedback> getRecommandedMovieUser(UriInstance media) {
		List<RecommandFeedback> rmdUserList = new ArrayList<RecommandFeedback>();
		Object[] paramValues = { curUser.getId(), media.getUri()};
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
			UriInstance artist = RdfFactory.getInstance().createInstance(naiveArtistUnits.get(0), media.getMediaType(), naiveArtistUnits.get(2), naiveArtistUnits.get(1));
			MediaArtistInterest newUserInterest = new MediaArtistInterest(rmdUserInfo, media, artist);
			RecommandFeedback rmdUser = new RecommandFeedback(newUserInterest, ruleId);
			rmdUser.setTime(createTime);
			rmdUserList.add(rmdUser);
		}
		Collections.sort(rmdUserList, Collections.reverseOrder());
		return rmdUserList;
	}
}

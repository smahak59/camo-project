package cn.edu.nju.ws.camo.webservice;

import javax.jws.WebService;

import cn.edu.nju.ws.camo.webservice.interestgp.DelJobForMovie;
import cn.edu.nju.ws.camo.webservice.interestgp.InterestGpFactory;
import cn.edu.nju.ws.camo.webservice.interestgp.MiningJobForMovie;
import cn.edu.nju.ws.camo.webservice.interestgp.MiningService;

@WebService(endpointInterface="cn.edu.nju.ws.camo.webservice.InterestGroupService") 
public class InterestGroupService implements IInterestGroupService {

	public String addInterest(int uid, String media, String mediaType, String artist) {
		String success = InterestGpFactory.getInstance().addInterest(uid, media, mediaType, artist);
		if(media.equals("movie")) {
			Runnable job1 = new MiningJobForMovie(uid, media, artist);
			MiningService.getInstance().executeJob(job1);
		}
		return success;
	}

	public String delInterest(int uid, String media, String artist) {
		String success = InterestGpFactory.getInstance().delInterest(uid, media, artist);
		if(media.equals("movie")) {
			Runnable job1 = new DelJobForMovie(uid, media, artist);
			MiningService.getInstance().executeJob(job1);
		}
		return success;
	}
	
	public String setRecommandedUserIgnore(int uid1, int uid2) {
		return InterestGpFactory.getInstance().setRecommandedUserIgnore(uid1, uid2);
	}
	
	public String setRecommandedUserRmd(int uid1, int uid2) {
		return InterestGpFactory.getInstance().setRecommandedUserRmd(uid1, uid2);
	}

	public String getFavorArtist(int uid, String media) {
		return InterestGpFactory.getInstance().getFavorArtist(uid, media);
	}

	public String getRecommandedUser(int uid, String media) {
		return InterestGpFactory.getInstance().getRecommandedUser(uid, media);
	}
}

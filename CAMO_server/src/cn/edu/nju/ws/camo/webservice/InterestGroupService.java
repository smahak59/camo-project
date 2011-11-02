package cn.edu.nju.ws.camo.webservice;

import javax.jws.WebService;

import cn.edu.nju.ws.camo.webservice.interestgp.InterestGpFactory;

@WebService(endpointInterface="cn.edu.nju.ws.camo.webservice.InterestGroupService") 
public class InterestGroupService implements IInterestGroupService {

	public String addInterest(int uid, String userName, String media, String mediaType, String artist) {
		String success = InterestGpFactory.getInstance().addInterest(uid, userName, media, mediaType, artist);
		return success;
	}

	public String delInterest(int uid, String media, String artist) {
		String success = InterestGpFactory.getInstance().delInterest(uid, media, artist);
		return success;
	}
	
	public String setRecommandedUserIgnore(int uid1, int uid2) {
		return InterestGpFactory.getInstance().setRecommandedUserIgnore(uid1, uid2);
	}
	
	public String setRecommandedUserRmd(int uid1, int uid2) {
		return InterestGpFactory.getInstance().setRecommandedUserRmd(uid1, uid2);
	}

	public String getFavoredArtist(int uid, String media) {
		return InterestGpFactory.getInstance().getFavorArtist(uid, media);
	}
	
	public String isFavoredMedia(int uid, String media) {
		return InterestGpFactory.getInstance().isFavoredMedia(uid, media);
	}

	public String getRecommandedMovieUser(int uid, String media) {
		return InterestGpFactory.getInstance().getRecommandedUserForMovie(uid, media);
	}
	
	public String getRecommandedMusicUser(int uid, String music) {
		return InterestGpFactory.getInstance().getRecommandedUserForMusic(uid, music);
	}
}

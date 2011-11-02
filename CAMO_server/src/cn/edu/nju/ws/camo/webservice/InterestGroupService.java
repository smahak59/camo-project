package cn.edu.nju.ws.camo.webservice;

import javax.jws.WebService;

import cn.edu.nju.ws.camo.webservice.interestgp.InterestGpFactory;

@WebService(endpointInterface="cn.edu.nju.ws.camo.webservice.InterestGroupService") 
public class InterestGroupService implements IInterestGroupService {

	public String addInterest(int uid, String userName, String media, String mediaType, String artist) {
		boolean success = InterestGpFactory.getInstance().addInterest(uid, userName, media, mediaType, artist);
		if(success)
			return "1";
		else
			return "0";
	}

	public String delInterest(int uid, String media, String artist) {
		boolean success = InterestGpFactory.getInstance().delInterest(uid, media, artist);
		if(success)
			return "1";
		else
			return "0";
	}
	
	public String setRecommandedUserIgnore(int uid1, int uid2) {
		boolean success = InterestGpFactory.getInstance().setRecommandedUserIgnore(uid1, uid2);
		if(success)
			return "1";
		else
			return "0";
	}
	
	public String setRecommandedUserRmd(int uid1, int uid2) {
		boolean success = InterestGpFactory.getInstance().setRecommandedUserRmd(uid1, uid2);
		if(success)
			return "1";
		else
			return "0";
	}

	public String getFavoredArtist(int uid, String media) {
		return InterestGpFactory.getInstance().getFavorArtist(uid, media);
	}
	
	public String isFavoredMedia(int uid, String media) {
		boolean is = InterestGpFactory.getInstance().isFavoredMedia(uid, media);
		if(is)
			return "1";
		else
			return "0";
	}

	public String getRecommandedMovieUser(int uid, String media) {
		return InterestGpFactory.getInstance().getRecommandedUserForMovie(uid, media);
	}
	
	public String getRecommandedMusicUser(int uid, String music) {
		return InterestGpFactory.getInstance().getRecommandedUserForMusic(uid, music);
	}
	
	public String testConnection() {
		return "1";
	}
}

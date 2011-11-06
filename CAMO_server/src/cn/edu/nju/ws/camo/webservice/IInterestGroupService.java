package cn.edu.nju.ws.camo.webservice;

import javax.jws.WebService;

@WebService
public interface IInterestGroupService {

	public String addInterest(int uid, String userName, int userSex, String media, String mediaType, String artist);
	
	public String delInterest(int uid, String media, String artist);
	
	public String getFavoredArtist(int uid, String media);
	
	public String getRecommandedMovieUser(int uid, int usex, String movie);
	
	public String getRecommandedMusicUser(int uid, int usex, String music);
	
	public String setRecommandedUserIgnore(int uid1, int uid2);
	
	public String setRecommandedUserRmd(int uid1, int uid2);
	
	public String getIgnoredUsers(int uid);
	
	public String testConnection();
}

package cn.edu.nju.ws.camo.webservice;

import javax.jws.WebService;

@WebService
public interface IInterestGroupService {

	public String addInterest(int uid, String media, String mediaType, String artist);
	
	public String delInterest(int uid, String media, String artist);
	
	public String getFavorArtist(int uid, String media);
	
	public String getRecommandedUser(int uid, String media);
	
}

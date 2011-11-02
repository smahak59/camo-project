package cn.edu.nju.ws.camo.webservice;

import javax.jws.WebService;

import cn.edu.nju.ws.camo.webservice.user.FriendsFactory;
import cn.edu.nju.ws.camo.webservice.user.UserPreferenceFactory;
import cn.edu.nju.ws.camo.webservice.user.UserProfileFactory;

@WebService(endpointInterface="cn.edu.nju.ws.camo.webservice.IUserService") 
public class UserService implements IUserService {

	public String addPreference(int uid, String inst, String mediaType,
			String instType, String labelName, int uAction, int subscribe) {
		boolean result = UserPreferenceFactory.getInstance().addPreference(uid, inst, mediaType, instType, labelName, uAction, subscribe);
		if(result)
			return "1";
		else
			return "0";
	}
	

	public String delPreference(int uid, String inst) {
		return String.valueOf(UserPreferenceFactory.getInstance().delPreference(uid, inst));
	}

	public String addUser(String name, String pwd, String email, String sex) {
		boolean result = UserProfileFactory.getInstance().addUser(name, pwd, email, sex);
		if(result)
			return "1";
		else
			return "0";
	}

	public String getPreference(int uid, String mediaType, String instType,
			int uAction) {
		return UserPreferenceFactory.getInstance().getPreference(uid, mediaType, instType, uAction);
	}

	public String getUserById(int uid) {
		return UserProfileFactory.getInstance().getUserById(uid);
	}
	
	
	public String getUserByPwd(String email, String pwd) {
		return UserProfileFactory.getInstance().getUserByPwd(email, pwd);
	}
	
	public String getUserByMail(String email) {
		return UserProfileFactory.getInstance().getUserByMail(email);
	}

	public String addFriend(int uid1, int uid2) {
		boolean success = FriendsFactory.getInstance().addFriend(uid1, uid2);
		if(success)
			return "1";
		else
			return "0";
	}
	
	public String delFriend(int uid1, int uid2) {
		return String.valueOf(FriendsFactory.getInstance().delFriend(uid1, uid2));
	}
	
	public String isFriends(int uid1, int uid2) {
		boolean is = FriendsFactory.getInstance().isFriends(uid1, uid2);
		if(is)
			return "1";
		else
			return "0";
	}
	
	public String getAllFriends(int uid1) {
		return FriendsFactory.getInstance().getAllFriends(uid1);
	}
	
	public String hasFriendRequest(int userFrom, int userTo) {
		boolean has = FriendsFactory.getInstance().hasFriendRequest(userFrom, userTo);
		if(has)
			return "1";
		else
			return "0";
	}
	
	public String addFriendRequest(int userFrom, int userTo) {
		boolean success = FriendsFactory.getInstance().addFriendRequest(userFrom, userTo);
		if(success)
			return "1";
		else
			return "0";
	}
	
	public String delFriendRequest(int userFrom, int userTo) {
		boolean success = FriendsFactory.getInstance().delFriendRequest(userFrom, userTo);
		if(success)
			return "1";
		else
			return "0";
	}
	
	public String getAllRequests(int userTo) {
		return FriendsFactory.getInstance().getAllRequests(userTo);
	}
	
	public String testConnection() {
		return "1";
	}
}

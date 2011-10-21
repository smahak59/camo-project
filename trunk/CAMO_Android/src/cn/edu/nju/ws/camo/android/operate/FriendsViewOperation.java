package cn.edu.nju.ws.camo.android.operate;

import java.util.ArrayList;
import java.util.List;

import cn.edu.nju.ws.camo.android.connect.server.ServerParam;
import cn.edu.nju.ws.camo.android.connect.server.WebService;
import cn.edu.nju.ws.camo.android.util.Friends;
import cn.edu.nju.ws.camo.android.util.SetSerialization;
import cn.edu.nju.ws.camo.android.util.User;

public class FriendsViewOperation {

	public static List<Friends> viewAllFriends(User curUser) {
		List<Friends> friendsList = new ArrayList<Friends>();
		Object[] params = { curUser.getId() };
		String naiveResult = WebService.getInstance().runFunction(
				ServerParam.USER_URL, "getAllFriends", params);
		if(naiveResult.equals(ServerParam.NETWORK_ERROR1))
			return null;
		if (naiveResult.equals(""))
			return friendsList;
		List<String> naiveFriendsList = SetSerialization.deserialize2(naiveResult);
		for(String naiveFriend : naiveFriendsList) {
			Friends newFriends = null;
			List<String> naiveTermList = SetSerialization.deserialize1(naiveFriend);
			if(naiveTermList.size()<5)
				continue;
			User newUser = new User(Integer.valueOf(naiveTermList.get(0)));
			newUser.setName(naiveTermList.get(1));
			newUser.setEmail(naiveTermList.get(2));
			newUser.setSex(naiveTermList.get(3));
			newFriends = new Friends(curUser, newUser);
			newFriends.setTime(Long.valueOf(naiveTermList.get(4)));
			friendsList.add(newFriends);
		}
		return friendsList;
	}
	
	public static List<User> getAllRequests(User curUser) {
		List<User> userList = new ArrayList<User>();
		Object[] params = { curUser.getId() };
		String naiveResult = WebService.getInstance().runFunction(
				ServerParam.USER_URL, "getAllRequests", params);
		if(naiveResult.equals(ServerParam.NETWORK_ERROR1))
			return null;
		if (naiveResult.equals(""))
			return userList;
		List<String> naiveUserList = SetSerialization.deserialize2(naiveResult);
		for(String naiveUser : naiveUserList) {
			List<String> naiveTermList = SetSerialization.deserialize1(naiveUser);
			if(naiveTermList.size()<4)
				continue;
			User newUser = new User(Integer.valueOf(naiveTermList.get(0)));
			newUser.setName(naiveTermList.get(1));
			newUser.setEmail(naiveTermList.get(2));
			newUser.setSex(naiveTermList.get(3));
			userList.add(newUser);
		}
		return userList;
	}
}

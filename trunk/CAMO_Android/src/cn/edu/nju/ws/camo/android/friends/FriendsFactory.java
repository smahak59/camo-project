package cn.edu.nju.ws.camo.android.friends;

import java.util.ArrayList;
import java.util.List;

import cn.edu.nju.ws.camo.android.connect.server.ServerParam;
import cn.edu.nju.ws.camo.android.connect.server.WebService;
import cn.edu.nju.ws.camo.android.operate.command.Command;
import cn.edu.nju.ws.camo.android.util.SetSerialization;
import cn.edu.nju.ws.camo.android.util.User;


public class FriendsFactory {

	private static FriendsFactory instance = null;
	private FriendsFactory(){}
	
	public static FriendsFactory getInstance() {
		if(instance == null)
			instance = new FriendsFactory();
		return instance;
	}
	
	public Command createAddFriendCmd(Friends friends) {
		return new AddFriendCommand(friends.getUser1(), friends.getUser2());
	}
	
	public Command createDelFriendCmd(Friends friends) {
		return new DelFriendCommand(friends.getUser1(), friends.getUser2());
	}
	
	public Command createAddFriendReqCmd(User user1, User user2) {
		return new DelFriendCommand(user1, user2);
	}
	
	public List<Friends> viewAllFriends(User curUser) {
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
	
	public List<User> viewAllRequests(User curUser) {
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
	
	class AddFriendCommand implements Command  {

		private User user1;
		private User user2;
		
		AddFriendCommand(User user1, User user2) {
			this.user1 = user1;
			this.user2 = user2;
		}

		public void execute() {
			Object[] paramValues = {user1.getId(),user2.getId()};
			WebService.getInstance().runFunction(ServerParam.USER_URL,
					"addFriend", paramValues);
		}
	}
	
	class AddFriendReqCommand implements Command {

		private User user1;
		private User user2;
		
		AddFriendReqCommand(User user1, User user2) {
			this.user1 = user1;
			this.user2 = user2;
		}
		
		public void execute() {
			Object[] paramValues = {user1.getId(),user2.getId()};
			WebService.getInstance().runFunction(ServerParam.USER_URL,
					"addFriendRequest", paramValues);
		}
	}
	
	class DelFriendCommand implements Command  {

		private User user1;
		private User user2;
		
		DelFriendCommand(User user1, User user2) {
			this.user1 = user1;
			this.user2 = user2;
		}
		
		public void execute() {
			Object[] paramValues = {user1.getId(),user2.getId()};
			WebService.getInstance().runFunction(ServerParam.USER_URL,
					"delFriend", paramValues);
		}
	}
	
	class DelFriendReqCommand {

		private User user1;
		private User user2;
		
		DelFriendReqCommand(User user1, User user2) {
			this.user1 = user1;
			this.user2 = user2;
		}
		
		public void execute() {
			Object[] paramValues = {user1.getId(),user2.getId()};
			WebService.getInstance().runFunction(ServerParam.USER_URL,
					"delFriendRequest", paramValues);
		}
	}
}

package cn.edu.nju.ws.camo.android.friends;

import cn.edu.nju.ws.camo.android.connect.server.ServerParam;
import cn.edu.nju.ws.camo.android.connect.server.WebService;
import cn.edu.nju.ws.camo.android.util.User;

public class DelFriendReqCommand {

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

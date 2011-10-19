package cn.edu.nju.ws.camo.android.operate;

import cn.edu.nju.ws.camo.android.connect.server.ServerParam;
import cn.edu.nju.ws.camo.android.connect.server.WebService;

public class AddFriendCommand implements Command  {

	private int uid1;
	private int uid2;
	
	AddFriendCommand(int uid1, int uid2) {
		this.uid1 = uid1;
		this.uid2 = uid2;
	}

	public void execute() {
		Object[] paramValues = {uid1,uid2};
		WebService.getInstance().runFunction(ServerParam.USER_URL,
				"addFriend", paramValues);
	}
	
}

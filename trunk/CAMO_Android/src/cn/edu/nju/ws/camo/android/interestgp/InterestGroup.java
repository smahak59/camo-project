package cn.edu.nju.ws.camo.android.interestgp;

import cn.edu.nju.ws.camo.android.operate.command.Command;
import cn.edu.nju.ws.camo.android.util.User;

public class InterestGroup {

	private User curUser = null;
	
	public InterestGroup(User user) {
		this.curUser = user;
	}
	
	public Command setUserIgnore(User user) {
		return new IgnoreRmdUserCmd(curUser, user);
	}
	
	public Command setUserRecommanded(User user) {
		return new RmdUserCmd(curUser, user);
	}
	
	
}

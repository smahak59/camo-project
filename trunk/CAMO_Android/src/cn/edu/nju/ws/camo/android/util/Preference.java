package cn.edu.nju.ws.camo.android.util;

/**
 * @author Hang Zhang
 *
 */
public class Preference {

	private User user;
	private RdfInstance inst;
	
	Preference(User user, RdfInstance inst) {
		this.user = user;
		this.inst = inst;
	}
	
	public User getUser() {
		return user;
	}

	public RdfInstance getInst() {
		return inst;
	}

}

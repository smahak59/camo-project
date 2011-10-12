package cn.edu.nju.ws.camo.android.util;

/**
 * @author Hang Zhang
 *
 */
public class Preference {

	private User user;
	private UriInstance inst;
	
	Preference(User user, UriInstance inst) {
		this.user = user;
		this.inst = inst;
	}
	
	public User getUser() {
		return user;
	}

	public UriInstance getInst() {
		return inst;
	}

}

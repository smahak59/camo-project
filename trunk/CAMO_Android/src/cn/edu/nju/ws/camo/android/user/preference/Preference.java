package cn.edu.nju.ws.camo.android.user.preference;

import cn.edu.nju.ws.camo.android.rdf.UriInstance;
import cn.edu.nju.ws.camo.android.user.User;

/**
 * @author Hang Zhang
 *
 */
public abstract class Preference {

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

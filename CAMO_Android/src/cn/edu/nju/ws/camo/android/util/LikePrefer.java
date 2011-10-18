package cn.edu.nju.ws.camo.android.util;

import cn.edu.nju.ws.camo.android.rdf.UriInstance;

/**
 * @author Hang Zhang
 *
 */
public class LikePrefer extends Preference {

	private boolean subscribe = false;
	
	public LikePrefer(User user, UriInstance inst) {
		super(user, inst);
	}
	
	public boolean isSubscribed() {
		return subscribe;
	}
}

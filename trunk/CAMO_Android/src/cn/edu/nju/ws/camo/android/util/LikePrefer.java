package cn.edu.nju.ws.camo.android.util;

import cn.edu.nju.ws.camo.android.rdf.UriInstance;

/**
 * @author Hang Zhang
 *
 */
public class LikePrefer extends Preference {

	private boolean subscribe;
	
	public LikePrefer(User user, UriInstance inst, boolean sub) {
		super(user, inst);
		this.subscribe = sub;
	}
	
	public boolean isSubscribed() {
		return subscribe;
	}
}

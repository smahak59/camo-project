package cn.edu.nju.ws.camo.android.util;

/**
 * @author Hang Zhang
 *
 */
public class LikePrefer extends Preference {

	private boolean subscribe;
	
	LikePrefer(User user, RdfInstance inst, boolean sub) {
		super(user, inst);
		this.subscribe = sub;
	}
	
	public boolean isSubscribed() {
		return subscribe;
	}
}

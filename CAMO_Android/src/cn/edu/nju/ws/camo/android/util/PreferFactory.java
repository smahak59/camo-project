package cn.edu.nju.ws.camo.android.util;

/**
 * @author Hang Zhang
 *
 */
public class PreferFactory {

	private static PreferFactory instance = null;
	
	private PreferFactory() {
	}
	
	public PreferFactory getInstance() {
		if(instance == null)
			instance = new PreferFactory();
		return instance;
	}
	
	public Preference createLike(User user, RdfInstance inst, boolean sub) {
		return new LikePrefer(user, inst, sub);
	}
	
	public Preference createDislike(User user, RdfInstance inst) {
		return new DislikePrefer(user, inst);
	}
}
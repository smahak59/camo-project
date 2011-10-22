package cn.edu.nju.ws.camo.android.operate.command;

import cn.edu.nju.ws.camo.android.util.DislikePrefer;
import cn.edu.nju.ws.camo.android.util.LikePrefer;
import cn.edu.nju.ws.camo.android.util.Preference;

/**
 * @author Hang Zhang
 *
 */
public class CommandFactory {
	
	public static final int LIKE = 1;
	public static final int DISLIKE = 0;
	public static final int SUBSCRIBE = 1;
	public static final int DISSUBSCRIBE = 0;

	private static CommandFactory instance = null;
	
	private CommandFactory() {
	}
	
	public static CommandFactory getInstance() {
		if(instance == null)
			instance = new CommandFactory();
		return instance;
	}
	
	public Command createDislikeCmd(DislikePrefer prefer) {
		return new DislikeCommand(prefer);
	}
	
	public Command createLikeCmd(LikePrefer prefer) {
		return new LikeCommand(prefer);
	}
	
	public Command createCancelPreferCmd(Preference prefer) {
		return new DelPreferCommand(prefer);
	}
	
}

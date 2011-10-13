package cn.edu.nju.ws.camo.android.operate;

import cn.edu.nju.ws.camo.android.util.DislikePrefer;
import cn.edu.nju.ws.camo.android.util.LikePrefer;

/**
 * @author Hang Zhang
 *
 */
public class CommandFactory {

	private static CommandFactory instance = null;
	
	private CommandFactory() {
	}
	
	public CommandFactory getInstance() {
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
}

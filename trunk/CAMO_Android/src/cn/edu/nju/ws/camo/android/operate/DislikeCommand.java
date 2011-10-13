package cn.edu.nju.ws.camo.android.operate;

import cn.edu.nju.ws.camo.android.util.DislikePrefer;

/**
 * @author Hang Zhang
 *
 */
public class DislikeCommand implements Command {

	private DislikePrefer prefer;
	
	DislikeCommand(DislikePrefer prefer) {
		this.prefer = prefer;
	}
	
	public void execute() {
		// TODO Auto-generated method stub
	}
}

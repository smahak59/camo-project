package cn.edu.nju.ws.camo.android.operate;

import cn.edu.nju.ws.camo.android.util.LikePrefer;

/**
 * @author Hang Zhang
 *
 */
public class LikeCommand implements Command {
	
	private LikePrefer prefer;
	
	LikeCommand(LikePrefer prefer) {
		this.prefer = prefer;
	}

	public void execute(){
		// TODO Auto-generated method stub
	}
}

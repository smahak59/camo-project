package cn.edu.nju.ws.camo.android.user.preference;

import cn.edu.nju.ws.camo.android.connect.ServerParam;
import cn.edu.nju.ws.camo.android.connect.WebService;
import cn.edu.nju.ws.camo.android.util.Command;

/**
 * @author Hang Zhang
 * 
 */
public class LikeCommand implements Command {

	private LikePrefer prefer;

	LikeCommand(LikePrefer prefer) {
		this.prefer = prefer;
	}

	public void execute() {
		Object[] paramValues = { prefer.getUser().getId(),
				prefer.getInst().getUri(), prefer.getInst().getMediaType(),
				prefer.getInst().getClassType(), prefer.getInst().getName(),
				Command.LIKE, Command.DISSUBSCRIBE };
		WebService.getInstance().runFunction(ServerParam.USER_URL,
				"addPreference", paramValues);
	}
}

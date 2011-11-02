package cn.edu.nju.ws.camo.android.user.preference;

import cn.edu.nju.ws.camo.android.connect.ServerParam;
import cn.edu.nju.ws.camo.android.connect.WebService;
import cn.edu.nju.ws.camo.android.util.Command;

/**
 * @author Hang Zhang
 * 
 */
public class DelPreferCommand implements Command {

	private Preference prefer;

	DelPreferCommand(Preference prefer) {
		this.prefer = prefer;
	}

	public void execute() {
		Object[] paramValues = { prefer.getUser().getId(),
				prefer.getInst().getUri() };
		WebService.getInstance().runFunction(ServerParam.USER_URL,
				"delPreference", paramValues);
	}
}

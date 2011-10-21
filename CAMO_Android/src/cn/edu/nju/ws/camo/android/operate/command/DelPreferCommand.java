package cn.edu.nju.ws.camo.android.operate.command;

import cn.edu.nju.ws.camo.android.connect.server.ServerParam;
import cn.edu.nju.ws.camo.android.connect.server.WebService;
import cn.edu.nju.ws.camo.android.util.Preference;

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

package cn.edu.nju.ws.camo.android.operate;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParserException;


import cn.edu.nju.ws.camo.android.connect.server.ServerParam;
import cn.edu.nju.ws.camo.android.connect.server.WebService;
import cn.edu.nju.ws.camo.android.util.Preference;

public class DelPreferCommand implements Command {

	private Preference prefer;
	
	DelPreferCommand(Preference prefer) {
		this.prefer = prefer;
	}
	
	public void execute() {
		Object[] paramValues = {prefer.getUser().getId(), prefer.getInst().getUri()};
		try {
			WebService.getInstance().runFunction(ServerParam.USER_URL, "delPreference", paramValues);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} 
	}
}

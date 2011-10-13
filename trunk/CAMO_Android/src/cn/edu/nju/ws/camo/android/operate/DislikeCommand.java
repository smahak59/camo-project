package cn.edu.nju.ws.camo.android.operate;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParserException;

import cn.edu.nju.ws.camo.android.connect.server.ServerParam;
import cn.edu.nju.ws.camo.android.connect.server.WebService;
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
		Object[] paramValues = { prefer.getUser().getId(),
				prefer.getInst().getUri(), prefer.getInst().getMediaType(),
				prefer.getInst().getClassType(), prefer.getInst().getName(), CommandFactory.DISLIKE,
				CommandFactory.DISSUBSCRIBE };
		try {
			WebService.getInstance().runFunction(ServerParam.USER_URL, "addPreference", paramValues);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} 
	}
}

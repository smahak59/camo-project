package cn.edu.nju.ws.camo.android.interestgp;

import cn.edu.nju.ws.camo.android.connect.server.ServerParam;
import cn.edu.nju.ws.camo.android.connect.server.WebService;
import cn.edu.nju.ws.camo.android.operate.command.Command;
import cn.edu.nju.ws.camo.android.rdf.UriInstance;
import cn.edu.nju.ws.camo.android.util.User;

public class MediaArtistFavorCmd implements Command {

	private User user;
	private UriInstance mediaInst;
	private UriInstance artistInst; 
	MediaArtistFavorCmd(User user, UriInstance mediaInst, UriInstance artistInst) {
		this.user = user;
		this.mediaInst = mediaInst;
		this.artistInst = artistInst;
	}
	
	public void execute() {
		Object[] paramValues = {user.getId(),mediaInst.getUri(),artistInst.getUri()};
		WebService.getInstance().runFunction(ServerParam.INTERESET_GP_URL,
				"delFriend", paramValues);
	}
}

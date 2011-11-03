package cn.edu.nju.ws.camo.android.user.interestgp;

import cn.edu.nju.ws.camo.android.command.Command;
import cn.edu.nju.ws.camo.android.connect.ServerParam;
import cn.edu.nju.ws.camo.android.connect.WebService;
import cn.edu.nju.ws.camo.android.rdf.UriInstance;
import cn.edu.nju.ws.camo.android.user.User;

/**
 * ��class�����ڶ�ĳ��musicϲ������һ�£�
 * @author Warren
 *
 */
public class MediaInterest {

	protected User user;
	protected UriInstance mediaInst;
	
	public MediaInterest(User user, UriInstance mediaInst) {
		this.user = user;
		this.mediaInst = mediaInst;
	}

	public User getUser() {
		return user;
	}

	public UriInstance getMediaInst() {
		return mediaInst;
	}

	public Command getCreateCmd() {
		return new MediaFavorCmd(user, mediaInst);
	}
	
	public Command getDeleteCmd() {
		return new DelMediaFavorCmd(user, mediaInst);
	}
	
	public static boolean isFavoredMedia(User user, UriInstance mediaInst) {
		boolean isFavored = false;
		Object[] paramValues = { user.getId(), mediaInst.getUri()};
		String naiveResult = WebService.getInstance().runFunction(
				ServerParam.INTERESET_GP_URL, "isFavoredMedia", paramValues);
		if(naiveResult.equals(ServerParam.NETWORK_ERROR1))
			return false;
		if(naiveResult.equals("1"))
			isFavored = true;
		return isFavored;
	}
	
	class DelMediaFavorCmd implements Command {

		private User user;
		private UriInstance mediaInst;
		DelMediaFavorCmd(User user, UriInstance mediaInst) {
			this.user = user;
			this.mediaInst = mediaInst;
		}
		
		public void execute() {
			Object[] paramValues = {user.getId(),mediaInst.getUri(),""};
			WebService.getInstance().runFunction(ServerParam.INTERESET_GP_URL,
					"delInterest", paramValues);
		}
	}
	
	class MediaFavorCmd implements Command {

		private User user;
		private UriInstance mediaInst;
		MediaFavorCmd(User user, UriInstance mediaInst) {
			this.user = user;
			this.mediaInst = mediaInst;
		}
		
		public void execute() {
			int sex=0;
			if(user.getSex().equals("male"))
				sex=1;
			Object[] paramValues = {user.getId(),user.getName(),sex,mediaInst.getUri(),mediaInst.getMediaType(),""};
			WebService.getInstance().runFunction(ServerParam.INTERESET_GP_URL,
					"addInterest", paramValues);
		}
	}
}

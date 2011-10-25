package cn.edu.nju.ws.camo.android.interestgp;

import java.util.ArrayList;
import java.util.List;

import cn.edu.nju.ws.camo.android.connect.server.ServerParam;
import cn.edu.nju.ws.camo.android.connect.server.WebService;
import cn.edu.nju.ws.camo.android.operate.command.Command;
import cn.edu.nju.ws.camo.android.rdf.RdfFactory;
import cn.edu.nju.ws.camo.android.rdf.UriInstance;
import cn.edu.nju.ws.camo.android.util.SetSerialization;
import cn.edu.nju.ws.camo.android.util.User;

/**
 * 该class适用于对特定movie下的某个actor的喜欢（顶一下）
 * @author Warren
 *
 */
public class MediaArtistInterest extends MediaInterest  {

	protected UriInstance artistInst; 
	
	public MediaArtistInterest(User user, UriInstance mediaInst, UriInstance artistInst) {
		super(user, mediaInst);
		this.artistInst = artistInst;
	}
	
	@Override
	public Command getCreateCmd() {
		return new MediaArtistFavorCmd(user, mediaInst, artistInst);
	}
	
	@Override
	public Command getDeleteCmd() {
		return new DelMediaArtistFavorCmd(user, mediaInst, artistInst);
	}
	
	public static List<UriInstance> viewFavoredArtist(User user, UriInstance mediaInst) {
		List<UriInstance> artists = new ArrayList<UriInstance>();
		Object[] paramValues = { user.getId(), mediaInst.getUri()};
		String naiveAritsts = WebService.getInstance().runFunction(
				ServerParam.INTERESET_GP_URL, "getFavorArtist", paramValues);
		if(naiveAritsts.equals(ServerParam.NETWORK_ERROR1))
			return null;
		if(naiveAritsts.length()==0)
			return artists;
		List<String> naiveAritstList = SetSerialization.deserialize2(naiveAritsts);
		for(String naiveArtist : naiveAritstList) {
			List<String> naiveTermList = SetSerialization.deserialize1(naiveArtist);
			if(naiveTermList.size() == 3) {
				UriInstance inst = RdfFactory.getInstance().createInstance(naiveTermList.get(0), mediaInst.getMediaType(), naiveTermList.get(2), naiveTermList.get(1));
				artists.add(inst);
			}
		}
		return artists;
	}

	public UriInstance getArtistInst() {
		return artistInst;
	}
	
	class DelMediaArtistFavorCmd implements Command {

		private User user;
		private UriInstance mediaInst;
		private UriInstance artistInst; 
		DelMediaArtistFavorCmd(User user, UriInstance mediaInst, UriInstance artistInst) {
			this.user = user;
			this.mediaInst = mediaInst;
			this.artistInst = artistInst;
		}
		
		public void execute() {
			Object[] paramValues = {user.getId(),mediaInst.getUri(),artistInst.getUri()};
			WebService.getInstance().runFunction(ServerParam.INTERESET_GP_URL,
					"delInterest", paramValues);
		}
	}
	
	class MediaArtistFavorCmd implements Command {

		private User user;
		private UriInstance mediaInst;
		private UriInstance artistInst; 
		MediaArtistFavorCmd(User user, UriInstance mediaInst, UriInstance artistInst) {
			this.user = user;
			this.mediaInst = mediaInst;
			this.artistInst = artistInst;
		}
		
		public void execute() {
			Object[] paramValues = {user.getId(),user.getName(),mediaInst.getUri(),mediaInst.getMediaType(),artistInst.getUri()};
			WebService.getInstance().runFunction(ServerParam.INTERESET_GP_URL,
					"delFriend", paramValues);
		}
	}
	
}

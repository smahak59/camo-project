package cn.edu.nju.ws.camo.android.user.interestgp;

import java.util.ArrayList;
import java.util.List;

import cn.edu.nju.ws.camo.android.command.Command;
import cn.edu.nju.ws.camo.android.connect.ServerParam;
import cn.edu.nju.ws.camo.android.connect.WebService;
import cn.edu.nju.ws.camo.android.rdf.RdfFactory;
import cn.edu.nju.ws.camo.android.rdf.UriInstance;
import cn.edu.nju.ws.camo.android.user.User;
import cn.edu.nju.ws.camo.android.util.SetSerialization;

/**
 * 该class适用于对特定movie下的某个actor的喜欢（顶一下）
 * @author Warren
 *
 */
public class MediaArtistInterest extends MediaInterest  {

	protected UriInstance actorInst; 
	
	public MediaArtistInterest(User user, UriInstance mediaInst, UriInstance actorInst) {
		super(user, mediaInst);
		this.actorInst = actorInst;
	}
	
	@Override
	public Command getCreateCmd() {
		return new MediaArtistFavorCmd(user, mediaInst, actorInst);
	}
	
	@Override
	public Command getDeleteCmd() {
		return new DelMediaArtistFavorCmd(user, mediaInst, actorInst);
	}
	
	public static List<UriInstance> viewFavoredArtist(User user, UriInstance mediaInst) {
		List<UriInstance> artists = new ArrayList<UriInstance>();
		Object[] paramValues = { user.getId(), mediaInst.getUri()};
		String naiveAritsts = WebService.getInstance().runFunction(
				ServerParam.INTERESET_GP_URL, "getFavoredArtist", paramValues);
		if(naiveAritsts.equals(ServerParam.NETWORK_ERROR1))
			return artists;
		if(naiveAritsts.length()==0)
			return artists;
		List<String> naiveAritstList = SetSerialization.deserialize2(naiveAritsts);
		for(String naiveArtist : naiveAritstList) {
			List<String> naiveTermList = SetSerialization.deserialize1(naiveArtist);
			if(naiveTermList.size() == 3) {
				UriInstance inst = RdfFactory.getInstance().createInstance(naiveTermList.get(0), mediaInst.getMediaType(), naiveTermList.get(2), SetSerialization.instNameNomalize(naiveTermList.get(1)));
				artists.add(inst);
			}
		}
		return artists;
	}

	public UriInstance getArtistInst() {
		return actorInst;
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
			Object[] paramValues = {user.getId(),mediaInst.getUri(),mediaInst.getMediaType(),artistInst.getUri()};
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
			int sex=0;
			if(user.getSex().equals("male"))
				sex=1;
			Object[] paramValues = {user.getId(),user.getName(),sex,mediaInst.getUri(),mediaInst.getMediaType(),artistInst.getUri()};
			WebService.getInstance().runFunction(ServerParam.INTERESET_GP_URL,
					"addInterest", paramValues);
		}
	}
	
}

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

public class MediaArtistInterest  {

	private User user;
	private UriInstance mediaInst;
	private UriInstance artistInst; 
	
	public MediaArtistInterest(User user, UriInstance mediaInst, UriInstance artistInst) {
		this.user = user;
		this.mediaInst = mediaInst;
		this.artistInst = artistInst;
	}
	
	public Command getCreateCmd() {
		return new MediaArtistFavorCmd(user, mediaInst, artistInst);
	}
	
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

	public User getUser() {
		return user;
	}

	public UriInstance getMediaInst() {
		return mediaInst;
	}

	public UriInstance getArtistInst() {
		return artistInst;
	}
	
}

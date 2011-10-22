package cn.edu.nju.ws.camo.android.interestgp;

import cn.edu.nju.ws.camo.android.operate.command.Command;
import cn.edu.nju.ws.camo.android.rdf.UriInstance;
import cn.edu.nju.ws.camo.android.util.User;

public class MediaArtistInterest {

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
}

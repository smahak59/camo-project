package cn.edu.nju.ws.camo.android.interestgp;

import cn.edu.nju.ws.camo.android.rdf.UriInstance;
import cn.edu.nju.ws.camo.android.util.User;

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

}

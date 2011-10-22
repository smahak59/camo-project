package cn.edu.nju.ws.camo.android.interestgp;

import cn.edu.nju.ws.camo.android.operate.command.Command;
import cn.edu.nju.ws.camo.android.rdf.UriInstance;
import cn.edu.nju.ws.camo.android.util.User;

public class InterestFactory {

	private static InterestFactory instance = null;
	private InterestFactory(){}
	
	public static InterestFactory getInstance() {
		if(instance == null)
			instance = new InterestFactory();
		return instance;
	}
	
	public Command createAddInterestCmd(User user, UriInstance mediaInst, UriInstance artistInst) {
		return new MediaArtistFavorCmd(user, mediaInst, artistInst);
	}
}

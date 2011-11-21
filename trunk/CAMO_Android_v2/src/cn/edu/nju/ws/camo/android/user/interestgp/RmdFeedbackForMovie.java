package cn.edu.nju.ws.camo.android.user.interestgp;

import java.io.Serializable;

public class RmdFeedbackForMovie extends RmdFeedback implements Serializable {

	private MediaArtistInterest userInterest;
	
	public RmdFeedbackForMovie(MediaArtistInterest interest, int rule) {
		super(rule);
		this.userInterest = interest;
	}
	

	public MediaArtistInterest getUserInterest() {
		return userInterest;
	}
}

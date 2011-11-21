package cn.edu.nju.ws.camo.android.user.interestgp;

import java.io.Serializable;

public class RmdFeedbackForMusic extends RmdFeedback implements Serializable {

	private MediaInterest userInterest;
	
	public RmdFeedbackForMusic(MediaInterest interest, int rule) {
		super(rule);
		this.userInterest = interest;
	}
	

	public MediaInterest getUserInterest() {
		return userInterest;
	}

}

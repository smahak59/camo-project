package cn.edu.nju.ws.camo.android.interestgp;

public class RmdFeedbackForMusic extends RmdFeedback {

	private MediaInterest userInterest;
	
	public RmdFeedbackForMusic(MediaInterest interest, int rule) {
		super(rule);
		this.userInterest = interest;
	}
	

	public MediaInterest getUserInterest() {
		return userInterest;
	}

}

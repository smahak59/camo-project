package cn.edu.nju.ws.camo.android.user.interestgp;

public class RmdFeedbackForMovie extends RmdFeedback  {

	private MediaArtistInterest userInterest;
	
	public RmdFeedbackForMovie(MediaArtistInterest interest, int rule) {
		super(rule);
		this.userInterest = interest;
	}
	

	public MediaArtistInterest getUserInterest() {
		return userInterest;
	}
}

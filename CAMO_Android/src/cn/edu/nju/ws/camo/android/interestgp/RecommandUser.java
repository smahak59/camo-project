package cn.edu.nju.ws.camo.android.interestgp;

public class RecommandUser implements Comparable<RecommandUser> {

	private MediaArtistInterest userInterest;
	private int ruleId;
	private long createTime=0;
	
	public RecommandUser(MediaArtistInterest interest, int rule) {
		this.userInterest = interest;
		this.ruleId = rule;
	}
	

	public long getTime() {
		return createTime;
	}


	public void setTime(long createTime) {
		this.createTime = createTime;
	}

	public MediaArtistInterest getUserInterest() {
		return userInterest;
	}

	public int getRuleId() {
		return ruleId;
	}
	
	public int compareTo(RecommandUser another) {
		return Long.signum(this.createTime-another.createTime);
	}
}

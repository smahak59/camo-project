package cn.edu.nju.ws.camo.android.interestgp;

public class RmdFeedbackForMusic {

	private MediaInterest userInterest;
	private int ruleId;
	private long createTime=0;
	
	public RmdFeedbackForMusic(MediaInterest interest, int rule) {
		this.userInterest = interest;
		this.ruleId = rule;
	}
	
	public long getTime() {
		return createTime;
	}


	public void setTime(long createTime) {
		this.createTime = createTime;
	}

	public MediaInterest getUserInterest() {
		return userInterest;
	}

	public int getRuleId() {
		return ruleId;
	}
	
	public int compareTo(RmdFeedbackForMusic another) {
		return Long.signum(this.createTime-another.createTime);
	}
}

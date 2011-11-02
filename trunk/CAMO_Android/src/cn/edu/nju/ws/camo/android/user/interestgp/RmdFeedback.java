package cn.edu.nju.ws.camo.android.user.interestgp;

public abstract class RmdFeedback implements Comparable<RmdFeedback> {

	protected int ruleId;
	protected long createTime=0;
	
	RmdFeedback(int rule) {
		this.ruleId = rule;
	}

	public long getTime() {
		return createTime;
	}


	public void setTime(long createTime) {
		this.createTime = createTime;
	}
	
	public int getRuleId() {
		return ruleId;
	}
	
	public int compareTo(RmdFeedback another) {
		return Long.signum(this.createTime-another.createTime);
	}
	
	abstract public MediaInterest getUserInterest();		
	
}
package cn.edu.nju.ws.camo.android.user.interestgp;

import cn.edu.nju.ws.camo.android.user.interestgp.rule.RmdRule;
import cn.edu.nju.ws.camo.android.user.interestgp.rule.RmdRuleFactory;

public abstract class RmdFeedback implements Comparable<RmdFeedback> {

	protected RmdRule rule;
	protected long createTime=0;
	
	RmdFeedback(int rule) {
		this.rule = RmdRuleFactory.getInstance().generateRule(rule);
	}

	public long getTime() {
		return createTime;
	}


	public void setTime(long createTime) {
		this.createTime = createTime;
	}
	
	public RmdRule getRule() {
		return rule;
	}
	
	public int compareTo(RmdFeedback another) {
		return Long.signum(this.createTime-another.createTime);
	}
	
	abstract public MediaInterest getUserInterest();		
	
}
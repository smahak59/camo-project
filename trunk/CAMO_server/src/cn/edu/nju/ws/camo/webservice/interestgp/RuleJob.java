package cn.edu.nju.ws.camo.webservice.interestgp;

public class RuleJob extends Thread {
	
	public static final boolean COMMOM_ROLE_RULE = true;
	public static final boolean SPOUSE_RULE = true;
	public static final boolean COOPERATOR_RULE = true;

	private int ruleId = 0;
	RuleJob(int ruleId) {
		this.ruleId = ruleId;
	}
	
	public int getRuleId() {
		return ruleId;
	}
}

package cn.edu.nju.ws.camo.android.user.interestgp.rule;

public abstract class RmdRule {
	
	private int ruleId;
	private String rulesToSuggest;

	RmdRule(int ruleId, String suggest) {
		this.ruleId = ruleId;
		this.rulesToSuggest = suggest;
	}
	
	public int getRuleId() {
		return ruleId;
	}
	
	public String getSuggest() {
		return rulesToSuggest;
	}
}

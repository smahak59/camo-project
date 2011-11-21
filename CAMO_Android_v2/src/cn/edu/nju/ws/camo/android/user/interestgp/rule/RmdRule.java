package cn.edu.nju.ws.camo.android.user.interestgp.rule;

import java.io.Serializable;

public abstract class RmdRule implements Serializable{
	
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

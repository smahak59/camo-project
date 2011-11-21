package cn.edu.nju.ws.camo.android.user.interestgp.rule;

import java.io.Serializable;


public class SeriesMusicRule extends RmdRule implements Serializable{

	public static final int RULE_ID = 11;
	private static final String SUGGEST = "share CDs with each other";
	
	
	SeriesMusicRule() {
		super(RULE_ID, SUGGEST);
	}
	
}

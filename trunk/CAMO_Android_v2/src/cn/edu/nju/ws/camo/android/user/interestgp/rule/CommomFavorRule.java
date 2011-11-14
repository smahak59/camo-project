package cn.edu.nju.ws.camo.android.user.interestgp.rule;

public class CommomFavorRule extends RmdRule {

	public static final int RULE_ID = 0;
	private static final String SUGGEST = "chat to share other interests";
	
	CommomFavorRule() {
		super(RULE_ID, SUGGEST);
	}
	
}

package cn.edu.nju.ws.camo.android.user.interestgp.rule;

public class RmdRuleFactory {

	private static RmdRuleFactory instance = null;
	
	private RmdRuleFactory(){}
	
	public static RmdRuleFactory getInstance() {
		if(instance == null) {
			instance = new RmdRuleFactory();
		}
		return instance;
	}
	
	public RmdRule generateRule(int ruleId) {
		RmdRule rule = null;
		switch (ruleId) {
		case SeriesMusicRule.RULE_ID:
			rule = new SeriesMusicRule();
			break;
		case SpouseMovieRule.RULE_ID:
			rule = new SpouseMovieRule();
			break;
		case CooperatorMovieRule.RULE_ID:
			rule = new CooperatorMovieRule();
			break;
		case CommomFavorRule.RULE_ID:
			rule = new CooperatorMovieRule();
			break;
		default:
			break;
		}
		return rule;
	}
}

package cn.edu.nju.ws.camo.android.user.interestgp.rule;

import java.io.Serializable;

/**
 * @author Warren
 *
 * A-B <- A like movie_1+actor_1(music) and B like movie_1+actor_1(music)
 * 
 */
public class CommomFavorRule extends RmdRule implements Serializable{

	public static final int RULE_ID = 0;
	private static final String SUGGEST = "chat to share other interests";
	
	CommomFavorRule() {
		super(RULE_ID, SUGGEST);
	}
	
}

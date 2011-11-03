package cn.edu.nju.ws.camo.webservice.interestgp;

import cn.edu.nju.ws.camo.webservice.interestgp.rules.CooperatorMovieRuleJob;
import cn.edu.nju.ws.camo.webservice.interestgp.rules.SeriesMusicRuleJob;
import cn.edu.nju.ws.camo.webservice.interestgp.rules.SpouseMovieRuleJob;

public class RuleJobRunner {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SpouseMovieRuleJob job1 = new SpouseMovieRuleJob();
		job1.run();
		CooperatorMovieRuleJob job2 = new CooperatorMovieRuleJob();
		job2.run();
		SeriesMusicRuleJob job3 = new SeriesMusicRuleJob();
		job3.run();
	}

}

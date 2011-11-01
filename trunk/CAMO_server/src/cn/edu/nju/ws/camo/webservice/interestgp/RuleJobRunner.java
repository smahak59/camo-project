package cn.edu.nju.ws.camo.webservice.interestgp;

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

package cn.edu.nju.ws.camo.android.service.mining;

public class RulesFactory {

	private static RulesFactory instance = null;
	
	private RulesFactory(){}
	
	public static RulesFactory getInstance() {
		if(instance == null) {
			instance = new RulesFactory();
		}
		return instance;
	}
}

package cn.edu.nju.ws.camo.android.user.interestgp.rule;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import cn.edu.nju.ws.camo.android.util.UtilParam;

public class CooperatorMovieRule extends RmdRule {

	public static final int RULE_ID = 2;
	private static final String SUGGEST = "play games of double players";
	private static List<String> gameList = new ArrayList<String>();
	private String game = "";
	
	CooperatorMovieRule() {
		super(RULE_ID, SUGGEST);
		try {
			initGame();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void initGame() throws IOException {
		if(gameList.size()==0) {
			UtilParam.PROP_TO_NAME_DOWN = new HashMap<String, String>();
			InputStream in = UtilParam.ASSETS.open("game.list");
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String line = "";
			while((line = br.readLine()) != null) {
				line = line.trim();
				if(line.length()>0)
					gameList.add(line.trim());
			}
			br.close();
		}
		int randomIdx = new Random().nextInt(gameList.size());
		game = gameList.get(randomIdx);
	}
	
	public String getRmdGame() {
		return game;
	}
}

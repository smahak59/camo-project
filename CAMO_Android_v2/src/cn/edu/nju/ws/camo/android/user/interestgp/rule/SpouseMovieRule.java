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

public class SpouseMovieRule extends RmdRule {

	public static final int RULE_ID = 1;
	private static final String SUGGEST = "choose a day to date";
	private static List<String> locationList = new ArrayList<String>();
	private String location = "";
	
	SpouseMovieRule() {
		super(RULE_ID, SUGGEST);
		try {
			initDateLocation();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void initDateLocation() throws IOException {
		if(locationList.size()==0) {
			UtilParam.PROP_TO_NAME_DOWN = new HashMap<String, String>();
			InputStream in = UtilParam.ASSETS.open("date.list");
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String line = "";
			while((line = br.readLine()) != null) {
				line = line.trim();
				if(line.length()>0)
					locationList.add(line.trim());
			}
			br.close();
		}
		int randomIdx = new Random().nextInt(locationList.size());
		location = locationList.get(randomIdx);
	}
	
	public String getRmdLocation() {
		return location;
	}
}
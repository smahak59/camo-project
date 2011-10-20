package cn.edu.nju.ws.camo.android.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

import android.content.res.AssetManager;

public class UtilConfig {
	
	public static void setAssets(AssetManager assets) {
		UtilParam.assets = assets;
	}

	public static void initParam() {
		setPropToName();
	}
	
	private static void setPropToName() {
		try {
			UtilParam.propToNameDown = new HashMap<String, String>();
			InputStream in = UtilParam.assets.open("propNames.map");
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String line = "";
			while((line = br.readLine()) != null) {
				String[] tokens  = line.split("##");
				if(tokens.length == 2) {
					UtilParam.propToNameDown.put(tokens[0].trim(), tokens[1].trim());
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}

package cn.edu.nju.ws.camo.android.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;

import android.content.res.AssetManager;

public class UtilConfig {
	
	public static void setAssets(AssetManager assets) {
		UtilParam.ASSETS = assets;
	}

	public static void initParam() {
		setPropToName();
		setExcludedProps();
	}
	
	private static void setPropToName() {
		try {
			UtilParam.PROP_TO_NANME_DOWN = new HashMap<String, String>();
			InputStream in = UtilParam.ASSETS.open("propNames.map");
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String line = "";
			while((line = br.readLine()) != null) {
				String[] tokens  = line.split("##");
				if(tokens.length == 2) {
					UtilParam.PROP_TO_NANME_DOWN.put(tokens[0].trim(), tokens[1].trim());
				}
			}
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void setExcludedProps() {
		try {
			UtilParam.EXCLUDED_PROPS = new HashSet<String>();
			InputStream in = UtilParam.ASSETS.open("exclude.prop");
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String line = "";
			while((line = br.readLine()) != null) {
				if(line.trim().equals("") == false)
					UtilParam.EXCLUDED_PROPS.add(line.trim());
			}
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

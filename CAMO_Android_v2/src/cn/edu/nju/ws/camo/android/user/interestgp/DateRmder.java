package cn.edu.nju.ws.camo.android.user.interestgp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cn.edu.nju.ws.camo.android.util.UtilParam;

public class DateRmder {
	
	private List<String> restaurant;

	public DateRmder() {} {
		restaurant = new ArrayList<String>();
		try {
			InputStream in = UtilParam.ASSETS.open("date.list");
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String line = "";
			while((line = br.readLine()) != null) {
				if(line.length()>0)
					restaurant.add(line.trim());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String getRandomRest() {
		int size = restaurant.size();
		Random random = new Random();
		int randomIdx = random.nextInt(size);
		return restaurant.get(randomIdx);
	}
}

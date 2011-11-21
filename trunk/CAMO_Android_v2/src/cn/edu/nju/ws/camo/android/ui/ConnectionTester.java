package cn.edu.nju.ws.camo.android.ui;

import cn.edu.nju.ws.camo.android.connect.WebService;

public class ConnectionTester {
	private static ConnectionTester instance = null;
	private ConnectionTester() {}
	
	public static ConnectionTester getInstance() {
		if(instance == null) {
			instance = new ConnectionTester();
		}
			return instance;
	}
	
	public boolean testConnection() {
		try {
			return WebService.testConnection();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return false;
	}
}

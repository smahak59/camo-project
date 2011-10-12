package cn.edu.nju.ws.camo.webservice;

import javax.xml.ws.Endpoint;

import cn.edu.nju.ws.camo.webservice.connect.Config;
import cn.edu.nju.ws.camo.webservice.user.IUserService;
import cn.edu.nju.ws.camo.webservice.user.UserService;

public class Publisher {

	public static void main(String[] args) {
		Config.initParam();
		IUserService userService = new UserService();
		Endpoint.publish("http://114.212.87.172:8580/user", userService);
	}
	
}

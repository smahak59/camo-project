package cn.edu.nju.ws.camo.webservice;

import javax.xml.ws.Endpoint;

import cn.edu.nju.ws.camo.webservice.connect.Config;
import cn.edu.nju.ws.camo.webservice.connect.Param;

public class Publisher {

	public static void main(String[] args) {
		Config.initParam();
		IUserService userService = new UserService();
		IViewService viewService = new ViewService();
		IInterestGroupService interstGpService = new InterestGroupService();
		Endpoint.publish(Param.SERVER_ADDRE+"user", userService);
		Endpoint.publish(Param.SERVER_ADDRE+"view", viewService);
		Endpoint.publish(Param.SERVER_ADDRE+"interestGp", interstGpService);
	}
	
}

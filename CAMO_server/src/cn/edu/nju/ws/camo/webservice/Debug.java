package cn.edu.nju.ws.camo.webservice;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;


public class Debug {
	
	public static void main(String[] args) {
		JaxWsProxyFactoryBean soapFactoryBean = new JaxWsProxyFactoryBean();
		soapFactoryBean.setAddress("http://114.212.87.172:8580/user");
		soapFactoryBean.setServiceClass(IUserService.class);
		IUserService userService = (IUserService) soapFactoryBean.create();
		userService.addFriend(7, 8);
	}
}

package cn.edu.nju.ws.camo.webservice;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;


public class Debug {
	
	public static void main(String[] args) {
		JaxWsProxyFactoryBean soapFactoryBean = new JaxWsProxyFactoryBean();
		soapFactoryBean.setAddress("http://114.212.87.172:8580/user");
		soapFactoryBean.setServiceClass(IUserService.class);
		IUserService userService = (IUserService) soapFactoryBean.create();
		userService.addPreference(7, "http://dbpedia.org/resource/Norman_Granz", "music", "abc", "Norman Granz", 1, 0);
		userService.addPreference(7, "http://dbpedia.org/resource/Norman", "music", "abc", "Norman", 1, 0);
		System.out.println(userService.getPreference(7, "music", "", 1));
	}
}

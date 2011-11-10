package cn.edu.nju.ws.camo.webservice;

import java.util.Date;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;

import cn.edu.nju.ws.camo.webservice.connect.Config;
import cn.edu.nju.ws.camo.webservice.view.LabelAndTypeFinder;


public class Debug {
	
	public static void main(String[] args) {
//		JaxWsProxyFactoryBean soapFactoryBean = new JaxWsProxyFactoryBean();
//		soapFactoryBean.setAddress("http://114.212.87.172:8580/user");
//		soapFactoryBean.setServiceClass(IUserService.class);
//		IUserService service = (IUserService) soapFactoryBean.create();
//		service.addUser("abc", "pwd", "male", "male");
//		System.out.println(service.textView("avatar", "movie"));
		
		
		Config.initParam();
		IUserService service = new UserService();
		service.addUser("abc", "pwd", "email", "male");
	}
}

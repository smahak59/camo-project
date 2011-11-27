package cn.edu.nju.ws.camo.webservice;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Date;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;


import cn.edu.nju.ws.camo.webservice.connect.Config;
import cn.edu.nju.ws.camo.webservice.connect.DBConnFactory;
import cn.edu.nju.ws.camo.webservice.view.LabelAndTypeFinder;


public class Debug {
	
	public static void main(String[] args) throws Throwable {
//		JaxWsProxyFactoryBean soapFactoryBean = new JaxWsProxyFactoryBean();
//		soapFactoryBean.setAddress("http://114.212.87.172:8580/user");
//		soapFactoryBean.setServiceClass(IUserService.class);
//		IUserService service = (IUserService) soapFactoryBean.create();
//		service.addUser("abc", "pwd", "male", "male");
//		System.out.println(service.textView("avatar", "movie"));
		
		Long oldTime = new Date().getTime();
		Config.initParam();
		ViewService service = new ViewService();
		service.instViewDown("http://dbpedia.org/resource/Spider-Man_3");
		service.instViewUp("http://dbpedia.org/resource/Spider-Man_3");
		System.out.println(new Date().getTime()-oldTime);
	}
}

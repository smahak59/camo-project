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
//		for(int i=0; i<100; i++) {
//			System.out.println(i);
			System.out.println(service.instViewDown("http://dbpedia.org/resource/Island_of_Fire"));
			System.out.println(service.instViewUp("http://dbpedia.org/resource/Island_of_Fire"));
//		service.textView("avatar", "movie");
//		service.textView("avatar", "music");
			Connection sourceConn = DBConnFactory.getInstance().dbConnect(DBConnFactory.MYSQL_CONN);
			String sqlStr = "flush table";
			PreparedStatement stmt = sourceConn.prepareStatement(sqlStr);
			stmt.execute();
			stmt.close();
			sourceConn.close();
//		}
		System.out.println(new Date().getTime()-oldTime);
	}
}

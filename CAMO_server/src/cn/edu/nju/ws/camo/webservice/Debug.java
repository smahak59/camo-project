package cn.edu.nju.ws.camo.webservice;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;

import cn.edu.nju.ws.camo.webservice.connect.Config;
import cn.edu.nju.ws.camo.webservice.view.LabelAndTypeFinder;


public class Debug {
	
	public static void main(String[] args) {
//		JaxWsProxyFactoryBean soapFactoryBean = new JaxWsProxyFactoryBean();
//		soapFactoryBean.setAddress("http://114.212.87.172:8580/view");
//		soapFactoryBean.setServiceClass(IViewService.class);
//		IViewService userService = (IViewService) soapFactoryBean.create();
//		System.out.println(userService.textView("While Paris Sleeps", "movie"));
		
		Config.initParam();
		LabelAndTypeFinder newFinder = new LabelAndTypeFinder("http://dbpedia.org/resource/Azzurro%23Die_Toten_Hosen_cover");
		newFinder.run();
		System.out.println(newFinder.getResult());
	}
}

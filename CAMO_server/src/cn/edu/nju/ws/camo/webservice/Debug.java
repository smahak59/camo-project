package cn.edu.nju.ws.camo.webservice;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;


public class Debug {
	
	public static void main(String[] args) {
		JaxWsProxyFactoryBean soapFactoryBean = new JaxWsProxyFactoryBean();
		soapFactoryBean.setAddress("http://114.212.87.172:8580/view");
		soapFactoryBean.setServiceClass(IViewService.class);
		IViewService userService = (IViewService) soapFactoryBean.create();
		System.out.println(userService.textView("While Paris Sleeps", "movie"));
	}
}

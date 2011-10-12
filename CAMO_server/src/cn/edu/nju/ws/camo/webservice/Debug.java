package cn.edu.nju.ws.camo.webservice;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;

import cn.edu.nju.ws.camo.webservice.user.IUserService;

public class Debug {
	
	public static void main(String[] args) {
		JaxWsProxyFactoryBean soapFactoryBean = new JaxWsProxyFactoryBean();
		soapFactoryBean.setAddress("http://114.212.87.172:8580/textinject");
		soapFactoryBean.setServiceClass(IUserService.class);
		IUserService textService = (IUserService) soapFactoryBean.create();
//		System.out.println(textService.getUri2().get(0));
	}
}

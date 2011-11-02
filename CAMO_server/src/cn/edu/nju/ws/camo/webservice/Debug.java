package cn.edu.nju.ws.camo.webservice;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;

import cn.edu.nju.ws.camo.webservice.connect.Config;
import cn.edu.nju.ws.camo.webservice.view.LabelAndTypeFinder;


public class Debug {
	
	public static void main(String[] args) {
		JaxWsProxyFactoryBean soapFactoryBean = new JaxWsProxyFactoryBean();
		soapFactoryBean.setAddress("http://114.212.87.172:8580/interestGp");
		soapFactoryBean.setServiceClass(IInterestGroupService.class);
		IInterestGroupService gs = (IInterestGroupService) soapFactoryBean.create();
		System.out.println(gs.getFavoredArtist(8, "http://dbpedia.org/resource/Daughters_Who_Pay"));
		
	}
}

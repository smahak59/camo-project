package cn.edu.nju.ws.camo.webservice;

import java.util.Date;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;

import cn.edu.nju.ws.camo.webservice.connect.Config;
import cn.edu.nju.ws.camo.webservice.view.LabelAndTypeFinder;


public class Debug {
	
	public static void main(String[] args) {
		long oldTime = new Date().getTime();
		JaxWsProxyFactoryBean soapFactoryBean = new JaxWsProxyFactoryBean();
		soapFactoryBean.setAddress("http://114.212.87.172:8580/view");
		soapFactoryBean.setServiceClass(IViewService.class);
		IViewService service = (IViewService) soapFactoryBean.create();
//		System.out.println(service.textView("avatar", "movie"));
		System.out.println(service.instViewDown("http://dbpedia.org/resource/Aaj_Ka_M.L.A._Ram_Avtar"));
		System.out.println(service.instViewUp("http://dbpedia.org/resource/Aaj_Ka_M.L.A._Ram_Avtar"));
		
		
//		Config.initParam();
//		System.out.println(new ViewService().instViewDown("http://dbpedia.org/resource/Verve_Records"));
		System.out.println("Time Cost: " + ( new Date().getTime()-oldTime));
	}
}

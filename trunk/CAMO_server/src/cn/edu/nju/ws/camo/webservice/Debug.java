package cn.edu.nju.ws.camo.webservice;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;

import cn.edu.nju.ws.camo.webservice.connect.Config;


public class Debug {
	
	public static void main(String[] args) {
//		JaxWsProxyFactoryBean soapFactoryBean = new JaxWsProxyFactoryBean();
//		soapFactoryBean.setAddress("http://114.212.87.172:8580/view");
//		soapFactoryBean.setServiceClass(IViewService.class);
//		IViewService userService = (IViewService) soapFactoryBean.create();
//		System.out.println(userService.textView("While Paris Sleeps", "movie"));
		
		Config.initParam();
		IInterestGroupService service = new InterestGroupService();
//		service.addInterest(7, "cxjia", "http://dbpedia.org/resource/Alle_M%C3%A4dchen_wollen_k%C3%BCssen", "music", "");
//		System.out.println(service.getRecommandedMovieUser(7, "http://dbpedia.org/resource/Daughters_Who_Pay"));
		System.out.println(service.getRecommandedMusicUser(7, "http://dbpedia.org/resource/Alle_M%C3%A4dchen_wollen_k%C3%BCssen"));
	}
}

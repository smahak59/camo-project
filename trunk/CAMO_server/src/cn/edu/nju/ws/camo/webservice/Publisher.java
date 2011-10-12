package cn.edu.nju.ws.camo.webservice;

import javax.xml.ws.Endpoint;

public class Publisher {

	public static void main(String[] args) {
		IService service = new Service();
		Endpoint.publish("http://114.212.87.172:8580/camo", service);
	}
	
}

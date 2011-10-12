package cn.edu.nju.ws.camo.webservice;

import java.util.ArrayList;

import javax.jws.WebService;

@WebService(endpointInterface="cn.edu.nju.ws.camo.service.IService") 
public class Service implements IService {

	public String getUri(String uri) {
		// TODO Auto-generated method stub
		return uri;
	}
	
	public ArrayList<String> getUri2() {
		// TODO Auto-generated method stub
		ArrayList<String> list = new ArrayList<String>();
		list.add("Zhang");
		list.add("Hang");
		return list;
	}
}

package cn.edu.nju.ws.camo.webservice;

import java.util.ArrayList;

import javax.jws.WebService;

@WebService
public interface IService {

	public ArrayList<String> getUri2();
	public String getUri(String uri);
}

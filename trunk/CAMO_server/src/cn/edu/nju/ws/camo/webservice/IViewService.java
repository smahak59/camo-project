package cn.edu.nju.ws.camo.webservice;

import javax.jws.WebService;

@WebService
public interface IViewService {

	public String instViewDown(String inst);
	
	public String instViewUp(String inst);
	
	public String textView(String searchText, String mediaType);
	
	public String testConnection();
}

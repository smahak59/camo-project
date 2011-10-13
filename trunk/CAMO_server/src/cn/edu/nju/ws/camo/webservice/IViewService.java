package cn.edu.nju.ws.camo.webservice;

import javax.jws.WebService;

@WebService
public interface IViewService {

	public String instViewDown(String inst);
	
	public String instViewUp(String inst);
	
	public String textViewDown(String searchText, String mediaType);
	
	public String textViewUp(String searchText, String mediaType);
}

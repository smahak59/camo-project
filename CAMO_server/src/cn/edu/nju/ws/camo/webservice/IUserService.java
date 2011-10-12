package cn.edu.nju.ws.camo.webservice;


import javax.jws.WebService;

/**
 * @author Hang Zhang
 *
 */
@WebService
public interface IUserService {
	
	/**
	 * @param name
	 * @param email
	 * @param sex
	 */
	public void addUser(String name, String email, String sex);
	
	/**
	 * @param uid user id
	 * @return (u_id,name,email,sex): sex:male/female
	 */
	public String getUser(int uid);
	
	/**
	 * @param uid
	 * @param inst
	 * @param mediaType
	 * @param instType
	 * @param uAction
	 * @param subscribe
	 */
	public void addPreference(int uid, String inst, String mediaType, String instType, int uAction, int subscribe);
	
	/**
	 * @param uid
	 * @param mediaType
	 * @param instType
	 * @param uAction
	 * @return (uid,inst,media_type,inst_type,u_action,subscribe,u_time)
	 */
	public String getPreference(int uid, String mediaType, String instType, int uAction);
	
	
	/**
	 * @param uid
	 * @param mediaType
	 * @param instType
	 * @return (uid,inst,media_type,inst_type,u_action,u_time)
	 */
	public String getSubscribe(int uid, String mediaType, String instType);
}

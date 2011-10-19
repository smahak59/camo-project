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
	 * @return 0:error; 1:success
	 */
	public String addUser(String name, String pwd, String email, String sex);
	
	/**
	 * @param uid user id
	 * @return (u_id,name,email,sex): sex:male/female
	 */
	public String getUserById(int uid);
	
	/**
	 * @param email
	 * @return (u_id,name,email,sex): sex:male/female
	 */
	public String getUserByMail(String email);
	
	/**
	 * @param email
	 * @return
	 */
	public String getUserByPwd(String email, String pwd);
	
	/**
	 * @param uid
	 * @param inst
	 * @param mediaType
	 * @param instType
	 * @param uAction
	 * @param subscribe
	 * @return 0:error; 1:success
	 * 
	 */
	public String addPreference(int uid, String inst, String mediaType, String instType, String labelName, int uAction, int subscribe);
	
	
	/**
	 * @param uid
	 * @param inst
	 * @return
	 */
	public String delPreference(int uid, String inst);
	
	/**
	 * @param uid
	 * @param mediaType
	 * @param instType
	 * @param uAction
	 * @return (uid,inst,media_type,inst_type,u_action,subscribe,u_time)
	 */
	public String getPreference(int uid, String mediaType, String instType, int uAction);
	
	
	/**
	 * @param uid1
	 * @param uid2
	 * @return 0:error; 1:success
	 */
	public String addFriend(int uid1, int uid2);	
	
	/**
	 * @param uid1
	 * @param uid2
	 * @return
	 */
	public String delFriend(int uid1, int uid2);
	
	
	/**
	 * @param uid1
	 * @return
	 */
	public String getAllFriends(int uid1);
}

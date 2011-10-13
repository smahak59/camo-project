package cn.edu.nju.ws.camo.android.connect.server;

import java.io.IOException;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

import cn.edu.nju.ws.camo.android.util.SetSerialization;
import cn.edu.nju.ws.camo.android.util.User;

/**
 * @author Hang Zhang
 *
 */
public class UserManager {
	
	private static final String ADD_USER = "addUser";
	private static final String GET_USER_ID = "getUserById";
	private static final String GET_USER_MAIL = "getUserByMail";

	private static UserManager instance = null;
	private UserManager(){}
	
	public static UserManager getInstance() {
		if(instance == null)
			instance = new  UserManager();
		return instance;
	}
	
	
	/**
	 * @param name
	 * @param email
	 * @param sex male/female
	 * @return 0:error; 1:success
	 * @throws XmlPullParserException 
	 * @throws IOException 
	 */
	public int addUser(String name, String email, String sex) throws IOException, XmlPullParserException {
		Object[] paramValues = {name,email,sex};
		int result = Integer.valueOf(WebService.getInstance().runFunction(ServerParam.USER_URL, ADD_USER, paramValues));
		return result;
	}
	
	
	/**
	 * @param uid: user id
	 * @return null, if no result
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	public User getUser(int uid) throws IOException, XmlPullParserException {
		User user = null;
		Object[] paramValues = {uid};
		String naiveUser = WebService.getInstance().runFunction(ServerParam.USER_URL, GET_USER_ID, paramValues);
		if(naiveUser.equals(""))
			return null;
		List<String> userInfo = SetSerialization.deserialize1(naiveUser);
		user = new User(uid);
		user.setName(userInfo.get(1));
		user.setEmail(userInfo.get(2));
		user.setSex(userInfo.get(3));
		return user;
	}
	
	
	/**
	 * @param email
	 * @return null, if no result
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	public User getUser(String email) throws IOException, XmlPullParserException {
		User user = null;
		Object[] paramValues = {email};
		String naiveUser = WebService.getInstance().runFunction(ServerParam.USER_URL, GET_USER_MAIL, paramValues);
		if(naiveUser.equals(""))
			return null;
		List<String> userInfo = SetSerialization.deserialize1(naiveUser);
		user = new User(Integer.valueOf(userInfo.get(0)));
		user.setName(userInfo.get(1));
		user.setEmail(email);
		user.setSex(userInfo.get(3));
		return user;
	}
}

package cn.edu.nju.ws.camo.android.user;

import java.io.Serializable;


/**
 * @author Hang Zhang
 *
 */
public class User implements Serializable {
	
	private int id;
	private String email;
	private String name;
	private String sex;	//male or female
	
	public static final User BLANK_USER = new User(-10);
	
	public User(int id) {
		this.id = id;
	}
	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}
	
	public int getId() {
		return this.id;
	}
	
	public static boolean isBlankUser(User curUser) {
		return curUser == BLANK_USER;
	}
}

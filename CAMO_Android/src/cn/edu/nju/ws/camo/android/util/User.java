package cn.edu.nju.ws.camo.android.util;


/**
 * @author Hang Zhang
 *
 */
public class User {
	
	private int id;
	private String email;
	private String name;
	private String sex;	//male or female
	
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
}

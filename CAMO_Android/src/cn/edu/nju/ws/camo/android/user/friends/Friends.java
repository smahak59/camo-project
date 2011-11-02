package cn.edu.nju.ws.camo.android.user.friends;

import java.sql.Timestamp;

import cn.edu.nju.ws.camo.android.user.User;

public class Friends {

	private User user1;
	private User user2;
	private Timestamp fTime = null;
	
	public Friends(User user1, User user2) {
		this.user1 = user1;
		this.user2 = user2;
	}

	public User getUser1() {
		return user1;
	}

	public User getUser2() {
		return user2;
	}
	
	public void setTime(Long time) {
		fTime = new Timestamp(time);
	}
	
	public void setTime(Timestamp time) {
		this.fTime = time;
	}
	
	public Timestamp getTime() {
		return this.fTime;
	}
}

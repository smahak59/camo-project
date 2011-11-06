package cn.edu.nju.ws.camo.android.user.preference;

import java.util.List;

import android.os.AsyncTask;

import cn.edu.nju.ws.camo.android.user.User;
import cn.edu.nju.ws.camo.android.user.interestgp.InterestGroup;

public class IgnoredUserList {
	private List<User> ignoredUserList;
	private boolean loaded = false;
	private User currentUser;
	
	public IgnoredUserList(User currentUser) {
		this.currentUser = currentUser;
	}
	
	public void initIgnoredUserList() {
		class LoadFriendListTask extends AsyncTask<String,Void,String> {
			@Override
			protected String doInBackground(String... params) {
				ignoredUserList = InterestGroup.getIgnoredUsers(currentUser);
				return null;
			}
			
			protected void onPostExecute(String result) {				 
				loaded = true;
			}
		}
		new LoadFriendListTask().execute("");		
	}
	
	public List<User> getIgnoredUserList() {
		return ignoredUserList;
	}
	
	public boolean isLoaded() {
		return loaded;
	}
} 

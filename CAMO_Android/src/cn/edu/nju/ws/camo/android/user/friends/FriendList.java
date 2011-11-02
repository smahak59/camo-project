package cn.edu.nju.ws.camo.android.user.friends;

import java.util.List;

import android.os.AsyncTask;
import cn.edu.nju.ws.camo.android.user.User;

public class FriendList {
	private List<Friends> friendList;
	private User currentUser;
	private boolean loaded = false;
	
	public FriendList(User user) {
		currentUser = user;
	}
	
	public boolean isLoaded() {
		return loaded;
	}
	
	
	
	public List<Friends> getFriendList() {
		return friendList;
	}
	
	public void initFriendList() {
		class LoadFriendListTask extends AsyncTask<String,Void,String> {
			@Override
			protected String doInBackground(String... params) {
				friendList = FriendsFactory.getInstance().viewAllFriends(currentUser);
				return null;
			}
			
			protected void onPostExecute(String result) {				 
				loaded = true;
			}
		}
		new LoadFriendListTask().execute("");		
	}
}

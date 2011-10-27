package cn.edu.nju.ws.camo.android.ui;

import java.util.List;

import android.app.Application;
import cn.edu.nju.ws.camo.android.friends.Friends;
import cn.edu.nju.ws.camo.android.interestgp.RmdFeedback;
import cn.edu.nju.ws.camo.android.rdf.UriInstance;
import cn.edu.nju.ws.camo.android.util.DislikePrefer;
import cn.edu.nju.ws.camo.android.util.FriendList;
import cn.edu.nju.ws.camo.android.util.LikePrefer;
import cn.edu.nju.ws.camo.android.util.PreferList;
import cn.edu.nju.ws.camo.android.util.RmdFeedbackList;
import cn.edu.nju.ws.camo.android.util.User;

public class CAMO_Application extends Application {
	private User currentUser;
	private PreferList preferList;
	private FriendList friendList;
	private RmdFeedbackList rmdFeedbackList;
	
	public void initCurrentUser() {
		currentUser = new User(7);
	}
	
	public int getSignedType(UriInstance uri) {
		return preferList.getSignedType(uri);
	}
	
	public void initPreferList() {
		preferList = new PreferList(currentUser);
		preferList.initPreferLists();
	}
	
	public void initFriendList() {
		friendList = new FriendList(currentUser);
		friendList.initFriendList();
	}
	
	public List<Friends> getFriendList() {
		return friendList.getFriendList();
	}
	
	public List<RmdFeedback> getRmdFeedbackList() {
		return rmdFeedbackList.getRmdFeedbackList();
	}
	
	public User getCurrentUser() {
		return currentUser;
	}
	
	public boolean preferListIsLoaded() {
		return preferList.isLoaded();
	}
	
	public boolean friendListIsLoaded() {
		return friendList.isLoaded();
	}
	
	public List<LikePrefer> getLikePreferList(int type) {
		List<LikePrefer> likePreferList = null;
		switch(type) {
		case PreferList.ARTIST:
			likePreferList = preferList.getArtistLikePreferList();
			break;
		case PreferList.MUSIC:
			likePreferList = preferList.getMusicLikePreferList();
			break;
		case PreferList.MOVIE:
			likePreferList = preferList.getMovieLikePreferList();
			break;
		case PreferList.PHOTO:
			likePreferList = preferList.getPhotoLikePreferList();
			break;
		}
		return likePreferList;
	}
	
	public List<DislikePrefer> getDislikePreferList(int type) {
		List<DislikePrefer> dislikePreferList = null;
		switch(type) {
		case PreferList.ARTIST:
			dislikePreferList = preferList.getArtistDislikePreferList();
			break;
		case PreferList.MUSIC:
			dislikePreferList = preferList.getMusicDislikePreferList();
			break;
		case PreferList.MOVIE:
			dislikePreferList = preferList.getMovieDislikePreferList();
			break;
		case PreferList.PHOTO:
			dislikePreferList = preferList.getPhotoDislikePreferList();
			break;
		}
		return dislikePreferList;
	}
}
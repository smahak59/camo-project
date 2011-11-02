package cn.edu.nju.ws.camo.android.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.Application;
import android.content.Context;
import cn.edu.nju.ws.camo.android.mediaplayer.PlayList;
import cn.edu.nju.ws.camo.android.rdf.Triple;
import cn.edu.nju.ws.camo.android.rdf.UriInstance;
import cn.edu.nju.ws.camo.android.user.User;
import cn.edu.nju.ws.camo.android.user.friends.FriendList;
import cn.edu.nju.ws.camo.android.user.friends.Friends;
import cn.edu.nju.ws.camo.android.user.interestgp.RmdFeedback;
import cn.edu.nju.ws.camo.android.user.interestgp.RmdFeedbackList;
import cn.edu.nju.ws.camo.android.user.preference.DislikePrefer;
import cn.edu.nju.ws.camo.android.user.preference.LikePrefer;
import cn.edu.nju.ws.camo.android.user.preference.PreferList;

public class CAMO_Application extends Application {
	private User currentUser;
	private PreferList preferList;
	private FriendList friendList;
	private PlayList playList;
	private RmdFeedbackList rmdFeedbackList;
	private ArrayList<Triple> triplesDown;
	private ArrayList<Triple> triplesUp;
	
	public void setTriplesDown(ArrayList<Triple> triplesDown) {
		this.triplesDown = triplesDown;
	}
	public void setTriplesUp(ArrayList<Triple> triplesUp) {
		this.triplesUp = triplesUp;
	}
	public ArrayList<Triple> getTriplesDown() {
		return triplesDown;
	}
	
	public ArrayList<Triple> getTriplesUp() {
		return triplesUp;
	}
	
	public void initPlayList(Context context) {
		playList = new PlayList(context);
	}
	
	public PlayList getPlayList() {
		return playList;
	}
	
	public boolean rmdFeedbackNotEmpty() {
		return rmdFeedbackList.notEmpty();
	}
	
	public void initCurrentUser() {
		currentUser = new User(7);
		currentUser.setName("cxjia");
		currentUser.setEmail("ymr674@gmail.com");
		currentUser.setSex("b");
	}
	
	public int getSignedType(UriInstance uri) {
		return preferList.getSignedType(uri);
	}
	
	public void initRmdFeedbackList(int mediaType, UriInstance currentPlaying) {
		rmdFeedbackList = new RmdFeedbackList(currentUser, mediaType, currentPlaying);
		rmdFeedbackList.initRmdFeedbackList();
	}
	
	public boolean rmdFeedbackListIsLoaded() {
		return rmdFeedbackList.isLoaded();
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
	
	public void addToPlayList(UriInstance inst) {
		playList.add(inst);
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

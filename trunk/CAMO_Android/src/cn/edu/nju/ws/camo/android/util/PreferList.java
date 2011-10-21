package cn.edu.nju.ws.camo.android.util;

import java.io.IOException;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

import android.os.AsyncTask;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import cn.edu.nju.ws.camo.android.R;
import cn.edu.nju.ws.camo.android.operate.InstViewOperation;
import cn.edu.nju.ws.camo.android.operate.PreferViewOperation;
import cn.edu.nju.ws.camo.android.rdf.UriInstance;
import cn.edu.nju.ws.camo.android.ui.SearchViewer;

public class PreferList {
	public final static int ARTIST = 0;
	public final static int MUSIC = 1;
	public final static int MOVIE = 2;
	public final static int PHOTO = 3;
	
	public final static int UNSIGNED = 0;
	public final static int LIKED = 1;
	public final static int DISLIKED = 2;
	
	private User currentUser;
	private List<LikePrefer> artistLikePreferList;
	private List<LikePrefer> musicLikePreferList;
	private List<LikePrefer> movieLikePreferList;
	private List<LikePrefer> photoLikePreferList;
	private List<DislikePrefer> artistDislikePreferList;
	private List<DislikePrefer> musicDislikePreferList;
	private List<DislikePrefer> movieDislikePreferList;
	private List<DislikePrefer> photoDislikePreferList;
	
	public PreferList(User user) {
		currentUser = user;
	}
	
	public User getCurrentUser() {
		return currentUser;
	}
	
	public void initPreferLists() {		
		class LoadPreferListTask extends AsyncTask<String,Void,String> {
			@Override
			protected String doInBackground(String... params) {
				artistLikePreferList = PreferViewOperation.viewLike(currentUser, "unknow");
				musicLikePreferList = PreferViewOperation.viewLike(currentUser, "music");
				movieLikePreferList = PreferViewOperation.viewLike(currentUser, "movie");
				photoLikePreferList = PreferViewOperation.viewLike(currentUser, "photo");
				artistDislikePreferList = PreferViewOperation.viewDislike(currentUser, "unknow");
				musicDislikePreferList = PreferViewOperation.viewDislike(currentUser, "music");
				movieDislikePreferList = PreferViewOperation.viewDislike(currentUser, "movie");
				photoDislikePreferList = PreferViewOperation.viewDislike(currentUser, "photo");
				return null;
			}
			
			protected void onPostExecute(String result) {				 
				
			}
		}
		new LoadPreferListTask().execute("");
		
	}
	
	public int getSignedType(UriInstance uri) {
		if(inLikeList(artistLikePreferList, uri) || inLikeList(musicLikePreferList, uri) ||
				inLikeList(movieLikePreferList, uri) || inLikeList(photoLikePreferList, uri)) {
			return LIKED;
		}
		else if(inDislikeList(artistDislikePreferList, uri) || inDislikeList(musicDislikePreferList, uri) ||
				inDislikeList(movieDislikePreferList, uri) || inDislikeList(photoDislikePreferList, uri)) {
			return DISLIKED;
		}
		else {
			return UNSIGNED;
		}
	}
	
	private boolean inDislikeList(List<DislikePrefer> dislikePreferList, UriInstance uri) {
		for(int i = 0; i < dislikePreferList.size(); i++) {
			if(dislikePreferList.get(i).getInst().equals(uri))
				return true;
		}
		return false;
	}

	private boolean inLikeList(List<LikePrefer> likePreferList, UriInstance uri) {
		for(int i = 0; i < likePreferList.size(); i++) {
			if(likePreferList.get(i).getInst().equals(uri))
				return true;
		}
		return false;
	}

	public List<LikePrefer> getArtistLikePreferList() {
		return artistLikePreferList;
	}
	
	public List<LikePrefer> getMusicLikePreferList() {
		return musicLikePreferList;
	}
	
	public List<LikePrefer> getMovieLikePreferList() {
		return movieLikePreferList;
	}
	
	public List<LikePrefer> getPhotoLikePreferList() {
		return photoLikePreferList;
	}
	
	public List<DislikePrefer> getArtistDislikePreferList() {
		return artistDislikePreferList;
	}
	
	public List<DislikePrefer> getMusicDislikePreferList() {
		return musicDislikePreferList;
	}
	
	public List<DislikePrefer> getMovieDislikePreferList() {
		return movieDislikePreferList;
	}
	
	public List<DislikePrefer> getPhotoDislikePreferList() {
		return photoDislikePreferList;
	}
}

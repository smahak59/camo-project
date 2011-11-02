package cn.edu.nju.ws.camo.android.user.preference;

import java.util.List;

import android.os.AsyncTask;
import cn.edu.nju.ws.camo.android.rdf.UriInstance;
import cn.edu.nju.ws.camo.android.user.User;

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
	private boolean loaded = false;
	
	public PreferList(User user) {
		currentUser = user;
	}
	
	public static int getMediaTypeInt(String mediaType) {
		if(mediaType.equals("artist"))
			return ARTIST;
		else if(mediaType.equals("music"))
			return MUSIC;
		else if(mediaType.equals("movie"))
			return MOVIE;
		else if(mediaType.equals("photo"))
			return PHOTO;
		else
			return -1;
	}
	
	public User getCurrentUser() {
		return currentUser;
	}
	
	public void initPreferLists() {		
		class LoadPreferListTask extends AsyncTask<String,Void,String> {
			@Override
			protected String doInBackground(String... params) {
				artistLikePreferList = PreferManager.viewLike(currentUser, "unknow");
				musicLikePreferList = PreferManager.viewLike(currentUser, "music");
				movieLikePreferList = PreferManager.viewLike(currentUser, "movie");
				photoLikePreferList = PreferManager.viewLike(currentUser, "photo");
				artistDislikePreferList = PreferManager.viewDislike(currentUser, "unknow");
				musicDislikePreferList = PreferManager.viewDislike(currentUser, "music");
				movieDislikePreferList = PreferManager.viewDislike(currentUser, "movie");
				photoDislikePreferList = PreferManager.viewDislike(currentUser, "photo");
				return null;
			}
			
			protected void onPostExecute(String result) {				 
				loaded = true;
			}
		}
		new LoadPreferListTask().execute("");
		
	}
	
	public boolean isLoaded()  {
		return loaded;
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

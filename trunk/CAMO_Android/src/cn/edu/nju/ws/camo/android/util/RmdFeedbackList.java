package cn.edu.nju.ws.camo.android.util;

import java.util.List;

import android.os.AsyncTask;
import cn.edu.nju.ws.camo.android.interestgp.InterestGroup;
import cn.edu.nju.ws.camo.android.interestgp.RmdFeedback;
import cn.edu.nju.ws.camo.android.rdf.UriInstance;
import cn.edu.nju.ws.camo.android.ui.MediaPlayer;

public class RmdFeedbackList {
	private List<RmdFeedback> rmdFeedbackList;
	private InterestGroup interestGroup;
	private boolean loaded = false;
	private User currentUser;
	private UriInstance currentPlaying;
	private int mediaType;
	public final static int MUSIC = 0;
	public final static int MOVIE = 1;
	
	public RmdFeedbackList(User currentUser, int type, UriInstance currentPlaying) {
		this.currentUser = currentUser;
		this.mediaType = type;
		this.currentPlaying = currentPlaying;
		interestGroup = new InterestGroup(this.currentUser);		
	}
	
	public boolean isLoaded() {
		return loaded;
	}
	
	public boolean notEmpty() {
		if(rmdFeedbackList.size() != 0)
			return true;
		else
			return false;
	}
	
	public List<RmdFeedback> getRmdFeedbackList() {
		return rmdFeedbackList;
	}
	
	public void initRmdFeedbackList() {
		switch (mediaType) {
		case MUSIC:
			rmdFeedbackList = interestGroup.getRecommandedMusicUser(currentPlaying);
			break;
		case MOVIE:
			rmdFeedbackList = interestGroup.getRecommandedMovieUser(currentPlaying);
			break;					
		}
		/*
		class LoadRmdFeedbackListTask extends AsyncTask<String,Void,String> {
			@Override
			protected String doInBackground(String... params) {
				switch (mediaType) {
				case MUSIC:
					rmdFeedbackList = interestGroup.getRecommandedMusicUser(currentPlaying);
					break;
				case MOVIE:
					rmdFeedbackList = interestGroup.getRecommandedMovieUser(currentPlaying);
					break;					
				}
				return null;
			}
			
			protected void onPostExecute(String result) {				 
				if(rmdFeedbackList != null) {
					loaded = true;
					
				}
				
			}
		}
		new LoadRmdFeedbackListTask().execute("");		
		*/
	}
}

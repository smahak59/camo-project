package cn.edu.nju.ws.camo.android.user.interestgp;

import java.util.Iterator;
import java.util.List;

import cn.edu.nju.ws.camo.android.rdf.UriInstance;
import cn.edu.nju.ws.camo.android.user.User;

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
		if(rmdFeedbackList == null || rmdFeedbackList.size() == 0)
			return false;
		else
			return true;
	}
	
	public void remove(User rmdedUser) {
		Iterator<RmdFeedback> iter = rmdFeedbackList.iterator();
		while(iter.hasNext()) {
			RmdFeedback curFeedback = iter.next();
			if(curFeedback.getUserInterest().getUser().getId() == rmdedUser.getId()) {
				iter.remove();
			}				
		}
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

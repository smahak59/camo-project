package cn.edu.nju.ws.camo.android.util;

import java.util.List;

import android.os.AsyncTask;
import cn.edu.nju.ws.camo.android.interestgp.InterestGroup;
import cn.edu.nju.ws.camo.android.interestgp.RmdFeedback;

public class RmdFeedbackList {
	private List<RmdFeedback> rmdFeedbackList;
	private InterestGroup interestGroup;
	private boolean loaded = false;
	
	public RmdFeedbackList() {
		
	}
	
	public boolean isLoaded() {
		return loaded;
	}
	
	
	
	public List<RmdFeedback> getRmdFeedbackList() {
		return rmdFeedbackList;
	}
	
	public void initRmdFeedbackList() {
		class LoadRmdFeedbackListTask extends AsyncTask<String,Void,String> {
			@Override
			protected String doInBackground(String... params) {
				
				return null;
			}
			
			protected void onPostExecute(String result) {				 
				loaded = true;
			}
		}
		new LoadRmdFeedbackListTask().execute("");		
	}
}

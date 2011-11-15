package cn.edu.nju.ws.camo.android.mediaplayer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.util.Log;
import cn.edu.nju.ws.camo.android.rdf.UriInstance;
import cn.edu.nju.ws.camo.android.user.User;

public class PlayList {
	
	public final static int ADD_SUC = 0;
	public final static int EXISTED = 1;
	
	private PlayListDatabase db;
	private List<PlayListEntry> list = null;
	private int currentPlaying = 0;
	private int userID = 0;
	public PlayList(Context context,User user){
		list = new ArrayList<PlayListEntry>();
		db = new PlayListDatabase(context);
		userID = user.getId();
//		//db.create_table();
//		int length = db.length();
//		for(int i = 0; i < length; i++){
//			list.add(db.queryFromUserID(userID));
//		}
		list = db.queryFromUserID(userID);
		Log.v("&&&&&&&&&&&&&&&&&&&&&", "listSize:" + list.size());
	}
	
	public UriInstance getCurrentPlaying() {
		return list.get(currentPlaying).getUriInstance();
	}
	
	public int getCurrentPlayingIndex() {
		return currentPlaying;
	}
	
	public void playByIndex(int index) {
		currentPlaying = index;
	}
	
	public void prev() {
		//currentPlaying--;
		currentPlaying += list.size() - 1;
		currentPlaying %= list.size();
	}
	
	public void next() {
		currentPlaying++;
		currentPlaying %= list.size();		
	}
	
	public void remove(int position) {		
		db.delete(list.get(position).getId());		
		list.remove(position);
		if(list.size() == 0) {
			return;
		}
		if(position <= currentPlaying){
			currentPlaying += list.size() - 1;
			currentPlaying %= list.size();
		}
	}
	
	public void clear() {
		list.clear();
	}
	
	public int size(){
		return list.size();
	}
		
	public int add(UriInstance inst){
		Iterator<PlayListEntry> iter = list.iterator();
		while(iter.hasNext()) {
			if(iter.next().getUriInstance().equals(inst)) {
				return EXISTED;
			}
		}
		int id = db.insert(userID, inst.getUri(), inst.getClassType(), inst.getName(), inst.getMediaType());	
		list.add(new PlayListEntry(inst, id, userID));
		return ADD_SUC;
	}

	public UriInstance getInstance(int k){
		return list.get(k).getUriInstance();
	}
	
	public List<PlayListEntry> getPlayList() {
		return list;
	}	

	
}

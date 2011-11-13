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
	
	
	private List<UriInstance> list = null;
	private int currentPlaying = 0;
	private int userID = 0;
	public PlayList(Context context,User user){
		list = new ArrayList<UriInstance>();
		PlayListDatabase db = new PlayListDatabase(context);
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
		return list.get(currentPlaying);
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
		if(list.contains(inst))
			return EXISTED;
		else {
			list.add(inst);
			return ADD_SUC;
		}
	}

	public UriInstance getInstance(int k){
		return list.get(k);
	}
	
	public List<UriInstance> getPlayList() {
		return list;
	}
	
	public void close(Context context){
		PlayListDatabase db = new PlayListDatabase(context);
		int length = db.length();
		for(int i = 0; i < length; i++){
			db.delete(i);
		}
		Iterator<UriInstance> it = list.iterator();
		for(int i = 0; i < list.size(); i++){
			UriInstance ins = it.next();
			db.insert(i,userID, ins.getUri(),ins.getClassType(),ins.getName(),ins.getMediaType());
		}
		
	}
	
}

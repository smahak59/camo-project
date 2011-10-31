package cn.edu.nju.ws.camo.android.mediaplayer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import cn.edu.nju.ws.camo.android.rdf.UriInstance;

public class PlayList {
	
	private List<UriInstance> list = null;
	private int currentPlaying = 0;
	public PlayList(Context context){
		list = new ArrayList<UriInstance>();
		PlayListDatabase db = new PlayListDatabase(context);
		db.create_table();
		int length = db.length();
		for(int i = 0; i < length; i++){
			list.add(db.queryFromID(i));
		}
	}
	
	public UriInstance getCurrentPlaying() {
		return list.get(currentPlaying);
	}
	
	public void prev() {
		currentPlaying--;
		currentPlaying %= list.size();
	}
	
	public void next() {
		currentPlaying++;
		currentPlaying %= list.size();		
	}
	
	public int length(){
		return list.size();
	}
		
	public void add(UriInstance inst){
		list.add(inst);
	}

	public UriInstance getInstance(int k){
		return list.get(k);
	}
	
	public void exit(Context context){
		PlayListDatabase db = new PlayListDatabase(context);
		int length = db.length();
		for(int i = 0; i < length; i++){
			db.delete(i);
		}
		Iterator<UriInstance> it = list.iterator();
		for(int i = 0; i < list.size(); i++){
			UriInstance ins = it.next();
			db.insert(i, ins.getUri(),ins.getClassType(),ins.getName(),ins.getMediaType());
		}
		
	}
	
}

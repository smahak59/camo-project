package cn.edu.nju.ws.camo.android.mediaplayer;

import java.util.Iterator;
import java.util.List;

import android.content.Context;
import cn.edu.nju.ws.camo.android.rdf.UriInstance;

public class PlayList {
	
	private List<UriInstance>  list=null;
	public PlayList(Context context){
		PlayListDatabase db = new PlayListDatabase(context);
		db.create_table();
		int length=db.length();
		for(int i=0;i<length;i++){
			list.add(db.queryFromID(i));
		}
	}
	
	public int length(){
		return list.size();
	}
	
	//将列表中第location个元素移除
	public void delete(int location){
		Iterator<UriInstance> it=list.iterator();
		for(int i=0;i<location;i++){
			it.next();
			it.remove();
		}
	}
	
	//在列表中加入某个instance
	public void add(UriInstance inst ){
		list.add(inst);
	}

	public UriInstance getInstance(int k){
		Iterator<UriInstance> it=list.iterator();
		UriInstance ins = null;
		for(int i=0;i<k;i++){
			ins=it.next();
		}
		return ins;
	}
	
	public void exit(Context context){
		PlayListDatabase db = new PlayListDatabase(context);
		int length=db.length();
		for(int i=0;i<length;i++){
			db.delete(i);
		}
		Iterator<UriInstance> it = list.iterator();
		for(int i=0;i<list.size();i++){
			UriInstance ins=it.next();
			db.insert(i, ins.getUri(),ins.getClassType(),ins.getName(),ins.getMediaType());
		}
		
	}
	
}

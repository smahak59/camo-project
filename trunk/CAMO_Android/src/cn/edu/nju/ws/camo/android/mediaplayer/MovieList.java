package cn.edu.nju.ws.camo.android.mediaplayer;

import java.util.Iterator;
import java.util.List;

import android.content.Context;
import cn.edu.nju.ws.camo.android.rdf.UriInstance;

public class MovieList {
	
	private List<UriInstance>  list=null;
	public MovieList(Context context){
		MovieListDatabase db = new MovieListDatabase(context);
		db.create_table();
		int length=db.length();
		for(int i=0;i<length;i++){
			list.add(db.queryFromID(i));
		}
	}
	
	public int length(){
		return list.size();
	}
	
	//���б��е�location��Ԫ���Ƴ�
	public void delete(int location){
		Iterator<UriInstance> it=list.iterator();
		for(int i=0;i<location;i++){
			it.next();
			it.remove();
		}
	}
	
	//���б��м���ĳ��instance
	public void add(UriInstance inst ){
		list.add(inst);
	}

	public UriInstance getInstance(int k){
		Iterator<UriInstance> it=list.iterator();
		for(int i=0;i<k;i++){
			it.next();
		}
		return it.next();
	}
	
	public void exit(Context context){
		MovieListDatabase db = new MovieListDatabase(context);
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

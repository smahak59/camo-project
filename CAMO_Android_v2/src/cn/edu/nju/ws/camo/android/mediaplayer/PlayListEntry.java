package cn.edu.nju.ws.camo.android.mediaplayer;

import cn.edu.nju.ws.camo.android.rdf.UriInstance;

public class PlayListEntry {
	private UriInstance instance;
	private int id;
	private int userId;
	
	public PlayListEntry(UriInstance instance, int id, int userId) {
		this.instance = instance;
		this.id = id;
		this.userId = userId;
	}
	
	public UriInstance getUriInstance() {
		return instance;
	}
	
	public int getId() {
		return id;
	}

	public int getUserId() {
		return userId;
	}
}

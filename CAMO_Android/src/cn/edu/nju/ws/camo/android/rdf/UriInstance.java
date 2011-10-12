package cn.edu.nju.ws.camo.android.rdf;

import java.util.ArrayList;

/**
 * @author Hang Zhang
 *
 */
public class UriInstance extends Resource {
	
	
	private String uri;
	private String classType;
	private String name;
	private ArrayList<Triple> infosDown = new ArrayList<Triple>();
	private ArrayList<Triple> infosUp = new ArrayList<Triple>();
	
	UriInstance(String uri, String mediaType) {
		super(mediaType);
		this.uri = uri;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getClassType() {
		return classType;
	}

	public void setClassType(String classType) {
		this.classType = classType;
	}

	public String getUri() {
		return uri;
	}
	
	public void addInfoDown(Triple triple) {
		infosDown.add(triple);
	}
	
	public void addInfoUp(Triple triple) {
		infosUp.add(triple);
	}
	
	public ArrayList<Triple> getInfosDown() {
		return this.infosDown;
	}
	
	public ArrayList<Triple> getInfosUp() {
		return this.infosUp;
	}
	
	public boolean hasInfosDown() {
		if(infosDown.size() == 0)
			return false;
		return true;
	}
	
	public boolean hasInfosUp() {
		if(infosUp.size() == 0) 
			return false;
		return true;
	}
	
	@Override
	public int hashCode() {
		return this.uri.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof UriInstance) {
			UriInstance inst = (UriInstance) obj;
			if (this.uri.equals(inst.uri)) {
				return true;
			}
		}
		return false;
	}
}

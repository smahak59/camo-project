package cn.edu.nju.ws.camo.android.rdf;

import cn.edu.nju.ws.camo.android.util.UtilParam;
import java.io.Serializable;


/**
 * @author Hang Zhang
 *
 */
public class UriInstance extends Resource implements Serializable {
	
	
	private String uri = "";
	private String classType = "";
	private String name = "";
	
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
	
	public boolean canShowed() {
		if(classType.trim().length()>0) {
			return UtilParam.MEDIA_CLASS.contains(classType);
		}
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

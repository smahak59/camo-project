package cn.edu.nju.ws.camo.android.rdf;

/**
 * @author Hang Zhang
 *
 */
public class Property extends Resource {

	private String uri;
	private String name;
	
	Property(String uri, String mediaType) {
		super(mediaType);
		this.uri = uri;
		this.name = "";
	}
	
	

	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}

	public String getUri() {
		return uri;
	}
}

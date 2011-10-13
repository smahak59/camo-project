package cn.edu.nju.ws.camo.android.rdf;

/**
 * @author Hang Zhang
 *
 */
public class Property extends Resource {

	private String uri;
	
	Property(String uri, String mediaType) {
		super(mediaType);
		this.uri = uri;
	}

	public String getUri() {
		return uri;
	}
}

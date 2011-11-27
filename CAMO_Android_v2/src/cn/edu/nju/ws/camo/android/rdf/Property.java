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
	
	@Override
	public int hashCode() {
		return this.uri.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Property) {
			Property prop = (Property) obj;
			if (this.uri.equals(prop.uri)) {
				return true;
			}
		}
		return false;
	}
}

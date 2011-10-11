package cn.edu.nju.ws.camo.android.util;

/**
 * @author Hang Zhang
 *
 */
public class RdfInstance {
	
	public static final String MOVIE_TYPE ="movie";
	public static final String MUSIC_TYPE ="music";
	public static final String PHOTO_TYPE ="photo";
	public static final String UNKNOWN_MEDIA ="unknown";
	
	private String uri;
	private String name;
	private String mediaType;	// movie/music/photo
	private String classType;
	
	public RdfInstance(String uri) {
		this.uri = uri;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMediaType() {
		return mediaType;
	}

	public void setMediaType(String mediaType) {
		this.mediaType = mediaType;
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
	
	@Override
	public int hashCode() {
		return this.uri.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof RdfInstance) {
			RdfInstance inst = (RdfInstance) obj;
			if (this.uri.equals(inst.uri)) {
				return true;
			}
		}
		return false;
	}
}

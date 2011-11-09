package cn.edu.nju.ws.camo.android.rdf;

import java.io.Serializable;

/**
 * @author Hang Zhang
 *
 */
public abstract class Resource implements Serializable {

	public static final String MOVIE_TYPE ="movie";
	public static final String MUSIC_TYPE ="music";
	public static final String PHOTO_TYPE ="photo";
	public static final String UNKNOWN_MEDIA ="unknown";
	
	private String mediaType;	// movie/music/photo
	private String name;
	
	Resource(String mediaType) {
		this.mediaType = mediaType;
	}
	
	public String getMediaType() {
		return this.mediaType;
	}
	
	public String getName() {
		return name;
	}
}

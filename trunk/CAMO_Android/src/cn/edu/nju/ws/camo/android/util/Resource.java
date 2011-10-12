package cn.edu.nju.ws.camo.android.util;

/**
 * @author Hang Zhang
 *
 */
public class Resource {

	public static final String MOVIE_TYPE ="movie";
	public static final String MUSIC_TYPE ="music";
	public static final String PHOTO_TYPE ="photo";
	public static final String UNKNOWN_MEDIA ="unknown";
	
	private String mediaType;	// movie/music/photo
	
	Resource(String mediaType) {
		this.mediaType = mediaType;
	}
	
	public String getMediaType() {
		return this.mediaType;
	}
}

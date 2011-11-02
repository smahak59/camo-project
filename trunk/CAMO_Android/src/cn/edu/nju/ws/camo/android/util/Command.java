package cn.edu.nju.ws.camo.android.util;

/**
 * @author Hang Zhang
 *
 */
public interface Command {

	public static final int LIKE = 1;
	public static final int DISLIKE = 0;
	public static final int SUBSCRIBE = 1;
	public static final int DISSUBSCRIBE = 0;

	public void execute();
}

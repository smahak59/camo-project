package cn.edu.nju.ws.camo.android.operate.comand;


/**
 * @author Hang Zhang
 *
 */
public class View {
	
	private static View instace = null;
	
	private View() {}
	
	public View getInstace() {
		if(instace == null)
			instace = new View();
		return instace;
	}
	
	// interfaces called by applications
	public void instView(String uri, String mediaType) {
	}
	
}

package cn.edu.nju.ws.camo.android.util;

public class RdfFactory {

	private static RdfFactory instance = null;
	
	private RdfFactory() {}
	
	public static RdfFactory getInstance() {
		if(instance == null)
			instance = new RdfFactory();
		return instance;
	}
	
	public Literal createLiteral(String name, String mediaType) {
		return new Literal(name, mediaType);
	}
	
	public UriInstance createInstance(String uri, String mediaType) {
		return new UriInstance(uri, mediaType);
	}
	
	public UriInstance createInstance(String uri, String mediaType, String classType, String name) {
		UriInstance newInst = new UriInstance(uri, mediaType);
		newInst.setClassType(classType);
		newInst.setName(name);
		return newInst;
	}
}

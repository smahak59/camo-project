package cn.edu.nju.ws.camo.android.rdf;

import java.util.ArrayList;


/**
 * @author Hang Zhang
 *
 */
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
	
	public UriInstWithNeigh createInstWithNeigh(UriInstance inst) {
		return new UriInstWithNeigh(inst);
	}
	
	public UriInstWithNeigh createInstWithNeigh(UriInstance inst, ArrayList<Triple> infosDown, ArrayList<Triple> infosUp) {
		return new UriInstWithNeigh(inst, infosDown, infosUp);
	}
	
	public Property createProperty(String uri, String mediaType) {
		return new Property(uri, mediaType);
	}
	
	public Triple createTriple(UriInstance subj, Property pred, Resource obj) {
		return new Triple(subj, pred, obj);
	}
}

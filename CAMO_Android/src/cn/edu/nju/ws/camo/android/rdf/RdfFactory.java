package cn.edu.nju.ws.camo.android.rdf;

import java.util.ArrayList;
import java.util.HashSet;


/**
 * @author Hang Zhang
 *
 */
public class RdfFactory {

	private static RdfFactory instance = null;
	private static HashSet<String> movieClsSet = new HashSet<String>();
	private static HashSet<String> musicClsSet = new HashSet<String>();
	private static HashSet<String> actorClsSet = new HashSet<String>();
	private static HashSet<String> artistClsSet = new HashSet<String>();
	
	private RdfFactory() {}
	
	public static RdfFactory getInstance() {
		if(instance == null) {
			instance = new RdfFactory();
			movieClsSet.add("http://dbpedia.org/ontology/Film");
			movieClsSet.add("http://data.linkedmdb.org/resource/movie/Film");
			actorClsSet.add("http://dbpedia.org/ontology/VoiceActor");
			actorClsSet.add("http://dbpedia.org/ontology/AdultActor");
			actorClsSet.add("http://dbpedia.org/ontology/Actor");
			actorClsSet.add("http://xmlns.com/foaf/0.1/Person");
			actorClsSet.add("http://data.linkedmdb.org/resource/movie/Actor");
			actorClsSet.add("http://dbtropes.org/resource/Main/VoiceActors");
			musicClsSet.add("http://dbpedia.org/ontology/Single");
			musicClsSet.add("http://dbpedia.org/ontology/Album");
			musicClsSet.add("http://dbpedia.org/ontology/Song");
			musicClsSet.add("http://purl.org/ontology/mo/Track");
			artistClsSet.add("http://xmlns.com/foaf/0.1/Person");
			artistClsSet.add("http://dbpedia.org/ontology/Artist");
			artistClsSet.add("http://dbpedia.org/ontology/Band");
			artistClsSet.add("http://dbpedia.org/ontology/MusicalArtist");
			artistClsSet.add("http://purl.org/ontology/mo/MusicArtist");
			artistClsSet.add("http://dbtropes.org/resource/Main/VoiceActors");
		}
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
	
	public boolean isMovieCls(String inst) {
		return movieClsSet.contains(inst);
	}
	
	public boolean isMusicCls(String inst) {
		return musicClsSet.contains(inst);
	}
	
	public boolean isActorCls(String inst) {
		return actorClsSet.contains(inst);
	}
	
	public boolean isArtistCls(String inst) {
		return artistClsSet.contains(inst);
	}
}

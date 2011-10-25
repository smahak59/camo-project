package cn.edu.nju.ws.camo.webservice.interestgp;

import java.util.HashSet;
import java.util.Set;

public class MusicRuleJob extends RuleJob {

	private Set<String> musicClsList = new HashSet<String>();
	private Set<String> artistClsList = new HashSet<String>();
	
	MusicRuleJob(int ruleId) {
		super(ruleId);
		initMusics();
		initArtists();
	}
	
	private void initMusics() {
		musicClsList.add("http://dbpedia.org/ontology/Single");
		musicClsList.add("http://dbpedia.org/ontology/Album");
		musicClsList.add("http://dbpedia.org/ontology/Song");
		musicClsList.add("http://purl.org/ontology/mo/Track");
	}
	
	private void initArtists() {
		artistClsList.add("http://xmlns.com/foaf/0.1/Person");
		artistClsList.add("http://dbpedia.org/ontology/Artist");
		artistClsList.add("http://dbpedia.org/ontology/Band");
		artistClsList.add("http://dbpedia.org/ontology/MusicalArtist");
		artistClsList.add("http://purl.org/ontology/mo/MusicArtist");
		artistClsList.add("http://dbtropes.org/resource/Main/VoiceActors");
	}
	
	public boolean isMusic(String uri) {
		return musicClsList.contains(uri);
	}
	
	public boolean isArtist(String uri) {
		return artistClsList.contains(uri);
	}
}

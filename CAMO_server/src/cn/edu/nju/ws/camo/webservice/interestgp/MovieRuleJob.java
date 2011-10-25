package cn.edu.nju.ws.camo.webservice.interestgp;

import java.util.HashSet;
import java.util.Set;

public abstract class MovieRuleJob extends RuleJob {
	
	private Set<String> movieClsList = new HashSet<String>();
	private Set<String> actorClsList = new HashSet<String>();
	private Set<String> performPropList = new HashSet<String>();

	MovieRuleJob(int ruleId) {
		super(ruleId);
		initMovies();
		initActors();
		initPeforms();
	}
	
	private void initMovies() {
		movieClsList.add("http://dbpedia.org/ontology/Film");
		movieClsList.add("http://data.linkedmdb.org/resource/movie/Film");
		
	}
	
	private void initActors() {
		actorClsList.add("http://dbpedia.org/ontology/VoiceActor");
		actorClsList.add("http://dbpedia.org/ontology/AdultActor");
		actorClsList.add("http://dbpedia.org/ontology/Actor");
		actorClsList.add("http://xmlns.com/foaf/0.1/Person");
		actorClsList.add("http://data.linkedmdb.org/resource/movie/Actor");
		actorClsList.add("http://dbtropes.org/resource/Main/VoiceActors");
	}
	
	private void initPeforms() {
		performPropList.add("http://data.linkedmdb.org/resource/movie/actor");
		performPropList.add("http://dbpedia.org/ontology/starring");
	}
	
	public boolean isMovie(String uri) {
		return movieClsList.contains(uri);
	}
	
	public boolean isActor(String uri) {
		return actorClsList.contains(uri);
	}
}

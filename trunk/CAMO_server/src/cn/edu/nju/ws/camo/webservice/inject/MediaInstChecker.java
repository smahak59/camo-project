package cn.edu.nju.ws.camo.webservice.inject;

import java.util.*;

import cn.edu.nju.ws.camo.webservice.connect.*;

import com.hp.hpl.jena.ontology.*;
import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.sdb.sql.*;
import com.hp.hpl.jena.sdb.store.*;
import com.hp.hpl.jena.util.*;
import com.hp.hpl.jena.util.iterator.*;

public class MediaInstChecker implements Runnable 
{	
	private static Set<String> mediaClassList = new HashSet<String>();
	private static Set<String> movieClassList = new HashSet<String>();
	private static Set<String> musicClassList = new HashSet<String>();
	private static Set<String> photoClassList = new HashSet<String>();
	private String inst;
	private boolean media = false;
	private int movie = -1;	// -1:unknown, 0:no, 1:yes
	private int music = -1;
	private int photo = -1;

	public MediaInstChecker(String inst) 
	{
		if (mediaClassList.size() == 0)
			initMediaClasses();
		movieClassList.add("http://dbpedia.org/ontology/ComicsCreator");
		movieClassList.add("http://dbpedia.org/ontology/VoiceActor");
		movieClassList.add("http://dbpedia.org/ontology/AdultActor");
		movieClassList.add("http://dbpedia.org/ontology/FilmFestival");
		movieClassList.add("http://dbpedia.org/ontology/TelevisionEpisode");
		movieClassList.add("http://dbpedia.org/ontology/Film");
		movieClassList.add("http://dbpedia.org/ontology/TelevisionShow");
		movieClassList.add("http://dbpedia.org/ontology/Comedian");
		movieClassList.add("http://dbpedia.org/ontology/Actor");
		movieClassList.add("http://dbpedia.org/ontology/FictionalCharacter");
		movieClassList.add("http://dbpedia.org/ontology/ComicsCharacter");
		movieClassList.add("http://dbpedia.org/ontology/ComicsCharacter");
		musicClassList.add("http://dbpedia.org/ontology/Artist");
		musicClassList.add("http://dbpedia.org/ontology/Single");
		musicClassList.add("http://dbpedia.org/ontology/Instrument");
		musicClassList.add("http://dbpedia.org/ontology/Album");
		musicClassList.add("http://dbpedia.org/ontology/Band");
		musicClassList.add("http://dbpedia.org/ontology/MusicalArtist");
		musicClassList.add("http://dbpedia.org/ontology/MusicGenre");
		musicClassList.add("http://dbpedia.org/ontology/Song");
		this.inst = inst;
	}

	public void initMediaClasses() 
	{
		String filePath = "file:./schema/MediaOntology.owl";
		OntDocumentManager mgr = new OntDocumentManager();
		mgr.setProcessImports(false);
		OntModelSpec spec = new OntModelSpec(OntModelSpec.OWL_MEM);
		spec.setDocumentManager(mgr);
		OntModel model = ModelFactory.createOntologyModel(spec, null);
		model.read(filePath);
		
		ExtendedIterator<OntClass> iter = model.listClasses();
		while (iter.hasNext()) 
			mediaClassList.add(iter.next().getURI());
	}
	
	public void run() 
	{
		try {
			SDBConnection sdbc = SDBConnFactory.getInstance().sdbConnect(SDBConnFactory.DBP_CONN);
			String qstr = "PREFIX dbpedia: <http://dbpedia.org/ontology/> " 
						+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " 
						+ "SELECT ?y WHERE { <" + URIref.encode(inst) + "> rdf:type ?y }";
			QueryExecution qe = JenaSDBOp.query(sdbc, DatabaseType.MySQL, qstr);
			ResultSet rs = qe.execSelect();
			while (rs.hasNext()) {
				QuerySolution qs = rs.nextSolution();
				String uriType = qs.get("y").toString().trim();
				if (mediaClassList.contains(uriType)) {
					media = true;
					if(movieClassList.contains(uriType)) {
						movie = 1; music = 0; photo = 0;
					} else if(musicClassList.contains(uriType)) {
						movie = 0; music = 1; photo = 0;
					} else if(photoClassList.contains(uriType)) {
						movie = 0; music = 0; photo = 1;
					}
				}
			}
			sdbc.close();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	public String getInst() 
	{
		return inst;
	}
	
	public boolean isMedia() 
	{
		return media;
	}
	
	public int isMovie() {
		return movie;
	}
	
	public int isMusic() {
		return music;
	}
	
	public int isPhoto() {
		return photo;
	}
}

package cn.edu.nju.ws.camo.webservice.interestgp.rules;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import cn.edu.nju.ws.camo.webservice.connect.DBConnFactory;
import cn.edu.nju.ws.camo.webservice.connect.JenaSDBOp;
import cn.edu.nju.ws.camo.webservice.connect.SDBConnFactory;
import cn.edu.nju.ws.camo.webservice.view.CorefFinder;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.sdb.sql.SDBConnection;
import com.hp.hpl.jena.sdb.store.DatabaseType;

public class CooperatorMovieRuleJob extends MovieRuleJob {

	public static final int ruleId = 2;
	private static final int COMMOM_MOVIE_NUM = 4;
	
	public CooperatorMovieRuleJob() {
		super(ruleId);
	}
	
	class ArtistToMovieFinder extends Thread {
		
		private int connType; 
		private String actorProp;
		private Map<String,Set<String>> artistToMovie = new HashMap<String, Set<String>>();
		
		public ArtistToMovieFinder(int connType, String actorProp) {
			this.connType = connType;
			this.actorProp = actorProp;
		}
		
		@Override
		public void run() {
			try {
				SDBConnection sdbc = SDBConnFactory.getInstance().sdbConnect(connType);
				String qstr = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " + 
						"SELECT ?x ?y WHERE { ?x rdf:type <http://dbpedia.org/ontology/Film> . " +
						"?x <" + actorProp + "> ?y . }";
				QueryExecution qe = JenaSDBOp.query(sdbc, DatabaseType.MySQL, qstr);
				ResultSet rs = qe.execSelect();
				while (rs.hasNext()) {
					QuerySolution qs = rs.nextSolution();
					if(artistToMovie.containsKey(qs.get("y").toString().trim())) {
						artistToMovie.get(qs.get("y").toString().trim()).add(qs.get("x").toString().trim());
					} else {
						Set<String> movieSet = new HashSet<String>();
						movieSet.add(qs.get("x").toString().trim());
						artistToMovie.put(qs.get("y").toString().trim(), movieSet);
					}
//					System.out.println(qs.get("x").toString().trim());
//					System.out.println(qs.get("y").toString().trim());
				}
				sdbc.close();
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}

		public int getConnType() {
			return connType;
		}

		public Map<String, Set<String>> getArtistToMovie() {
			return artistToMovie;
		}
	}
	
	class CooperatorsFinder extends Thread {
		
		private Map<String,Set<String>> artistToMovie;
		Map<String, Map<String, String>> cooperators = new HashMap<String, Map<String,String>>();
		
		public CooperatorsFinder(Map<String,Set<String>> artistToMovie) {
			this.artistToMovie = artistToMovie;
		}
		
		public Map<String, Map<String, String>> getCooperators() {
			return cooperators;
		}

		@Override
		public void run() {
			Iterator<Entry<String, Set<String>>> itr1 = artistToMovie.entrySet().iterator();
			while(itr1.hasNext()) {
				Entry<String, Set<String>> entry1 = itr1.next();
				String artist1 = entry1.getKey();
				Iterator<Entry<String, Set<String>>> itr2 = artistToMovie.entrySet().iterator();
				while(itr2.next().getKey().equals(artist1)==false) {}
				while(itr2.hasNext()) {
					Entry<String, Set<String>> entry2 = itr2.next();
					String artist2 = entry2.getKey();
					Set<String> addedMovies = new HashSet<String>();
					for(String movie2 : entry2.getValue()) {
						if(entry1.getValue().contains(movie2)) {
							addedMovies.add(movie2);
						}
					}
					if(addedMovies.size()>=COMMOM_MOVIE_NUM) {
						for(String addedMovie : addedMovies) {
							if(cooperators.containsKey(addedMovie)) {
								cooperators.get(addedMovie).put(artist1, artist2);
//								System.out.println(addedMovie);
//								System.out.println(artist1);
//								System.out.println(artist2);
//								System.out.println("");
							} else {
								Map<String, String> artistPair = new HashMap<String, String>();
								artistPair.put(artist1, artist2);
								cooperators.put(addedMovie, artistPair);
//								System.out.println(addedMovie);
//								System.out.println(artist1);
//								System.out.println(artist2);
//								System.out.println("");
							}
						}
					}
				}
			}
		}
	}
	
	private void updateDatabase(Map<String, Map<String, String>> movieToArtistPairs) {
		try {
			Connection sourceConn = DBConnFactory.getInstance().dbConnect(DBConnFactory.ISTGP_CONN);
			String sqlStr = "insert into movie_artist_gp(movie,artist1,artist2,rule) values(?,?,?,?)";
			PreparedStatement stmt = sourceConn.prepareStatement(sqlStr);
			Iterator<Entry<String, Map<String, String>>> movieItr = movieToArtistPairs.entrySet().iterator();
			while(movieItr.hasNext()) {
				Entry<String, Map<String, String>> movieEntry = movieItr.next();
				String movie = movieEntry.getKey();
				CorefFinder corefFinder = new CorefFinder(movie, "movie");
				corefFinder.run();
//				Set<String> corefs = corefFinder.getCorefs().keySet();
				Iterator<Entry<String, String>> spouseItr = movieEntry.getValue().entrySet().iterator();
				while(spouseItr.hasNext()) {
					Entry<String, String> spouseEntry = spouseItr.next();
//					for(String corefMovie : corefs) {
//						stmt.setString(1, corefMovie);
						stmt.setString(1, movie);
						stmt.setString(2, spouseEntry.getKey());
						stmt.setString(3, spouseEntry.getValue());
						stmt.setInt(4, ruleId);
						stmt.addBatch();
//					}
				}
			}
			clearHistory();
			sourceConn.setAutoCommit(false);
			stmt.executeBatch();
			sourceConn.commit();
			stmt.clearBatch();
			sourceConn.setAutoCommit(true);
			stmt.close();
			sourceConn.close();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void clearHistory() {
		try {
			Connection sourceConn = DBConnFactory.getInstance().dbConnect(DBConnFactory.ISTGP_CONN);
			String sqlStr = "delete from movie_artist_gp where rule=?";
			PreparedStatement stmt = sourceConn.prepareStatement(sqlStr);
			stmt.setInt(1, ruleId);
			stmt.executeUpdate();
			stmt.close();
			sourceConn.close();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		ArtistToMovieFinder dbpFinder = new ArtistToMovieFinder(SDBConnFactory.DBP_CONN, "http://dbpedia.org/ontology/starring");
		dbpFinder.start();
		ArtistToMovieFinder lmdbFinder = new ArtistToMovieFinder(SDBConnFactory.LMDB_CONN, "http://data.linkedmdb.org/resource/movie/actor");
		lmdbFinder.start();
		try {
			dbpFinder.join();
			lmdbFinder.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Map<String,Set<String>> dbpArtistToMovie = dbpFinder.getArtistToMovie();
		Map<String,Set<String>> lmdbArtistToMovie = lmdbFinder.getArtistToMovie();
		
		//remove coreferent actors  
		List<CorefFinder> finderList = new ArrayList<CorefFinder>();
		BlockingQueue<Runnable> bkQueue = new LinkedBlockingQueue<Runnable>();
		ThreadPoolExecutor threadExec = new ThreadPoolExecutor(10, 12, 7, TimeUnit.DAYS, bkQueue);
		Iterator<Entry<String, Set<String>>> itr = lmdbArtistToMovie.entrySet().iterator();
		while(itr.hasNext()) {
			Entry<String, Set<String>> entry = itr.next();
			CorefFinder finder = new CorefFinder(entry.getKey(),"movie");
			threadExec.execute(finder);
			finderList.add(finder);
		}
		threadExec.shutdown();
		try {
			threadExec.awaitTermination(7, TimeUnit.DAYS);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(CorefFinder finder : finderList) {
			Set<String> actors = finder.getCorefs().keySet();
			if(actors.size() < 2)
				continue;
			for(String actor : actors) {
				if(dbpArtistToMovie.containsKey(actor)) {
					lmdbArtistToMovie.remove(actor);
				}
			}
		}
		
		CooperatorsFinder coFinder1 = new CooperatorsFinder(dbpArtistToMovie);
		CooperatorsFinder coFinder2 = new CooperatorsFinder(lmdbArtistToMovie);
		coFinder1.start();
		coFinder2.start();
		try {
			coFinder1.join();
			coFinder2.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Map<String, Map<String, String>> cooperators1 = coFinder1.getCooperators();
		Map<String, Map<String, String>> cooperators2 = coFinder2.getCooperators();
		cooperators1.putAll(cooperators2);
		
		updateDatabase(cooperators1);
	}
	
}

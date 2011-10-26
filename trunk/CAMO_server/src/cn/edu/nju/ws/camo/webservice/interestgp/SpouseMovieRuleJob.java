package cn.edu.nju.ws.camo.webservice.interestgp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import cn.edu.nju.ws.camo.webservice.connect.Config;
import cn.edu.nju.ws.camo.webservice.connect.DBConnFactory;
import cn.edu.nju.ws.camo.webservice.connect.JenaSDBOp;
import cn.edu.nju.ws.camo.webservice.connect.SDBConnFactory;
import cn.edu.nju.ws.camo.webservice.view.CorefFinder;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.sdb.sql.SDBConnection;
import com.hp.hpl.jena.sdb.store.DatabaseType;

public class SpouseMovieRuleJob extends MovieRuleJob {

	public static final int ruleId = 1;
	
	public SpouseMovieRuleJob() {
		super(ruleId);
	}
	
	private Map<String, Map<String, String>> mineDBPMovie() {
		Map<String, Map<String, String>> movieToSpouse = new HashMap<String, Map<String,String>>();
		try {
			SDBConnection sdbc = SDBConnFactory.getInstance().sdbConnect(SDBConnFactory.DBP_CONN);
			String qstr = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " + 
					"SELECT ?x ?y ?z WHERE { ?x rdf:type <http://dbpedia.org/ontology/Film> . " +
					"?x <http://dbpedia.org/ontology/starring> ?y . " +
					"?y <http://dbpedia.org/ontology/spouse> ?z . " +
					"?x <http://dbpedia.org/ontology/starring> ?z . }";
			QueryExecution qe = JenaSDBOp.query(sdbc, DatabaseType.MySQL, qstr);
			ResultSet rs = qe.execSelect();
			while (rs.hasNext()) {
				QuerySolution qs = rs.nextSolution();
				if(movieToSpouse.containsKey(qs.get("x").toString().trim())) {
					Map<String, String> spouses = movieToSpouse.get(qs.get("x").toString().trim());
					if(spouses.containsKey(qs.get("y").toString().trim())==false && spouses.containsKey(qs.get("z").toString().trim())==false)
						spouses.put(qs.get("y").toString().trim(), qs.get("z").toString().trim());
				} else {
					Map<String, String> spouses = new HashMap<String, String>();
					spouses.put(qs.get("y").toString().trim(), qs.get("z").toString().trim());
					movieToSpouse.put(qs.get("x").toString().trim(), spouses);
				}
//				System.out.println(qs.get("x").toString().trim());
//				System.out.println(qs.get("y").toString().trim());
//				System.out.println(qs.get("z").toString().trim());
			}
			sdbc.close();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return movieToSpouse;
	}
	
	private void updateDatabase(Map<String, Map<String, String>> movieToSpouses) {
		try {
			Connection sourceConn = DBConnFactory.getInstance().dbConnect(DBConnFactory.ISTGP_CONN);
			String sqlStr = "insert into movie_artist_gp(movie,artist1,artist2,rule) values(?,?,?,?)";
			PreparedStatement stmt = sourceConn.prepareStatement(sqlStr);
			Iterator<Entry<String, Map<String, String>>> movieItr = movieToSpouses.entrySet().iterator();
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
		Map<String, Map<String, String>> movieToSpouses = mineDBPMovie();
		updateDatabase(movieToSpouses);
	}
	
	public static void main(String[] args) throws Throwable {
		Config.initParam();
		SpouseMovieRuleJob job = new SpouseMovieRuleJob();
		job.run();
	}
}

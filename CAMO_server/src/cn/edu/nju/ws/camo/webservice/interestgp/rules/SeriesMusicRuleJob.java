package cn.edu.nju.ws.camo.webservice.interestgp.rules;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import cn.edu.nju.ws.camo.webservice.connect.DBConnFactory;
import cn.edu.nju.ws.camo.webservice.connect.JenaSDBOp;
import cn.edu.nju.ws.camo.webservice.connect.SDBConnFactory;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.sdb.sql.SDBConnection;
import com.hp.hpl.jena.sdb.store.DatabaseType;

public class SeriesMusicRuleJob extends MusicRuleJob {

	public static final int ruleId = 11;
	
	public SeriesMusicRuleJob() {
		super(ruleId);
	}
	
	private List<Set<String>> mineDBPMusic() {
		List<Set<String>> gpList = new ArrayList<Set<String>>();
		try {
			List<String> musics = new ArrayList<String>();
			SDBConnection sdbc = SDBConnFactory.getInstance().sdbConnect("DBP");
			String[] musicClasses = {"http://dbpedia.org/ontology/Single","http://dbpedia.org/ontology/Song","http://dbpedia.org/ontology/Album"};
			for(String musicClass : musicClasses) {
				String qstr = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " + 
						"SELECT distinct ?x WHERE { ?x rdf:type <" + musicClass + "> . ?x <http://dbpedia.org/ontology/subsequentWork> ?y .}";
				QueryExecution qe = JenaSDBOp.query(sdbc, DatabaseType.MySQL, qstr);
				ResultSet rs = qe.execSelect();
				while(rs.hasNext()) {
					QuerySolution qs = rs.nextSolution();
					musics.add(qs.get("x").toString().trim());
				}
			}
			sdbc.close();
			System.out.println("get insts over");
			
			BlockingQueue<Runnable> bkQueue = new LinkedBlockingQueue<Runnable>();
			ThreadPoolExecutor threadExec = new ThreadPoolExecutor(10, 12, 7, TimeUnit.DAYS, bkQueue);
			List<GpFinder> finderList = new ArrayList<GpFinder>();
			for(String music : musics) {
				GpFinder finder = new GpFinder(music);
				threadExec.execute(finder);
				finderList.add(finder);
			}
			threadExec.shutdown();
			threadExec.awaitTermination(7, TimeUnit.DAYS);
			System.out.println("get subsequences over");
			
			Map<String, Set<String>> musicToGp = new HashMap<String, Set<String>>();
			for(GpFinder finder : finderList) {
				Set<String> curGp = finder.getMusicGp();
				Set<String> preGp = null;
				for(String tmpMusic : curGp) {
					if(musicToGp.containsKey(tmpMusic)) {
						preGp = musicToGp.get(tmpMusic);
						break;
					} 
				}
				if(preGp == null) {
					for(String tmpMusic : curGp) {
						musicToGp.put(tmpMusic, curGp);
					}
					gpList.add(curGp);
				} else {
					preGp.addAll(curGp);
					for(String tmpMusic : curGp) {
						musicToGp.put(tmpMusic, preGp);
					}
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return gpList;
	}
	
	private void updateDatabase(List<Set<String>> gpList) {
		int maxId = getMaxGpId();
		try {
			Connection sourceConn = DBConnFactory.getInstance().dbConnect(DBConnFactory.ISTGP_CONN);
			String sqlStr = "insert into music_gp(gp_id,music,rule) values(?,?,?)";
			PreparedStatement stmt = sourceConn.prepareStatement(sqlStr);
			for(Set<String> gp : gpList) {
				maxId++;
				for(String music : gp) {
					stmt.setInt(1, maxId);
					stmt.setString(2, music);
					stmt.setInt(3, ruleId);
					stmt.addBatch();
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
			e.printStackTrace();
		}
	}
	
	private int getMaxGpId() {
		int max = 0;
		try {
			Connection sourceConn = DBConnFactory.getInstance().dbConnect(DBConnFactory.ISTGP_CONN);
			String sqlStr = "select max(gp_id) from music_gp";
			PreparedStatement stmt = sourceConn.prepareStatement(sqlStr);
			java.sql.ResultSet rs = stmt.executeQuery();
			if(rs.next()) {
				max = rs.getInt(1);
			}
			rs.close();
			stmt.close();
			sourceConn.close();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return max;
	}
	
	private void clearHistory() {
		try {
			Connection sourceConn = DBConnFactory.getInstance().dbConnect(DBConnFactory.ISTGP_CONN);
			String sqlStr = "delete from music_gp where rule=?";
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
	
	class GpFinder extends Thread {
		
		private String music;
		private Set<String> musicGp = new HashSet<String>();
		GpFinder(String music) {
			this.music = music;
		}
		
		@Override
		public void run() {
			String curMusic = music;
			musicGp.add(curMusic);
			try {
				SDBConnection sdbc = SDBConnFactory.getInstance().sdbConnect("DBP");
				while(curMusic != null) {
					String qstr = "SELECT distinct ?x WHERE { <" + curMusic + "> <http://dbpedia.org/ontology/subsequentWork> ?x }";
					QueryExecution qe = JenaSDBOp.query(sdbc, DatabaseType.MySQL, qstr);
					ResultSet rs = qe.execSelect();
					if(rs.hasNext()) {
						QuerySolution qs = rs.nextSolution();
						String newMusic = qs.get("x").toString().trim();
						if(musicGp.contains(newMusic))
							break;
						musicGp.add(newMusic);
						curMusic = newMusic;
					} else {
						curMusic = null;
					}
				}
				sdbc.close();
			} catch (Throwable e) {
				e.printStackTrace();
				System.err.println(music);
			}
		}

		public Set<String> getMusicGp() {
			return musicGp;
		}
	}
	
	@Override
	public void run() {
		updateDatabase(mineDBPMusic());
	}
}

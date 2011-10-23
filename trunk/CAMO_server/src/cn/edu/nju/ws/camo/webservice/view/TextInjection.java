package cn.edu.nju.ws.camo.webservice.view;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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

import cn.edu.nju.ws.camo.webservice.connect.Config;
import cn.edu.nju.ws.camo.webservice.connect.DBConnFactory;

public class TextInjection 
{
	private int queryMode;	//down, up, all
	public static final int MODE_DOWN=1;
	public static final int MODE_UP=2;
	public static final int MODE_ALL=0;
	
	private String initSearchWords(String searchStr)
	{
		String[] keywords = searchStr.split(" ");
		String searchWords = "";
		for (String keyword : keywords) 
			searchWords += "+" + keyword + " ";
		return searchWords;
	}

	public Map<String, Map<String, List<String[]>>> query(String searchStr) throws Throwable 
	{
		String[] mediaTypes = { "movie", "music", "photo" };
		// (mediaType, (instance, {property, value}))
		Map<String, Map<String, List<String[]>>> resultSet = new HashMap<String, Map<String, List<String[]>>>(); 

		for (String mediaType : mediaTypes) 
			resultSet.put(mediaType, query(searchStr, mediaType));
		
		return resultSet;
	}

	public Map<String, List<String[]>> query(String searchStr, String mediaType) throws Throwable 
	{
		String searchWords = initSearchWords(searchStr);
		Set<String> instSet = new HashSet<String>();
		Map<String, List<String[]>> ipvSet = new HashMap<String, List<String[]>>();
		List<MediaInstChecker> mediaCheckerList = new ArrayList<MediaInstChecker>();
		
		BlockingQueue<Runnable> bkQueue = new LinkedBlockingQueue<Runnable>();
		ThreadPoolExecutor threadExec = new ThreadPoolExecutor(8, 9, 7, TimeUnit.DAYS, bkQueue);
		if (searchWords.equals(""))
			return ipvSet;

		Connection sourceConn = DBConnFactory.getInstance().dbConnect(DBConnFactory.FUSE_CONN);

		String sqlStr1 = "SELECT uri FROM inst_" + mediaType
					   + " WHERE MATCH(uri) AGAINST (? IN BOOLEAN MODE) or MATCH(label) AGAINST (? IN BOOLEAN MODE) LIMIT 3";
		PreparedStatement stmt1 = sourceConn.prepareStatement(sqlStr1);
		stmt1.setString(1, searchWords);
		stmt1.setString(2, searchWords);
		ResultSet rs1 = stmt1.executeQuery();
		while (rs1.next()) 
			instSet.add(rs1.getString(1).trim());
		rs1.close();
		stmt1.close();

		String sqlStr3 = "SELECT uri FROM inst_dbpedia " 
					   + "WHERE MATCH(uri) AGAINST (? IN BOOLEAN MODE) or MATCH(label) AGAINST (? IN BOOLEAN MODE) LIMIT 3";
		PreparedStatement stmt3 = sourceConn.prepareStatement(sqlStr3);
		stmt3.setString(1, searchWords);
		stmt3.setString(2, searchWords);
		ResultSet rs3 = stmt3.executeQuery();
		while (rs3.next()) {
			String s = rs3.getString(1).trim();
			instSet.add(s);
			MediaInstChecker mediaChecker = new MediaInstChecker(s);
			threadExec.execute(mediaChecker);
			mediaCheckerList.add(mediaChecker);
		}
		rs3.close();
		stmt3.close();

		threadExec.shutdown();
		threadExec.awaitTermination(7, TimeUnit.DAYS);
//		while (!threadExec.isTerminated()) {
//			Thread.sleep(100);
//		}
		
		rmNotMedia(instSet, mediaCheckerList, mediaType);
		rmCorefs(instSet, mediaType);
		for (String inst : instSet) 
		{
			if(queryMode == MODE_DOWN) {
				List<String[]> downList = (new UriInjection(inst)).queryDown();
				ipvSet.put(inst, downList);
			} else if(queryMode == MODE_UP) {
				List<String[]> upList = (new UriInjection(inst)).queryUp();
				ipvSet.put(inst, upList);
			} else if(queryMode == MODE_ALL) {
				List<String[]> allList = new ArrayList<String[]>();
				UriInjection uriInject =  new UriInjection(inst);
				List<String[]> downList = uriInject.queryDown();
				List<String[]> upList = uriInject.queryUp();
				allList.addAll(downList);
				allList.addAll(upList);
				ipvSet.put(inst, allList);
			}
		}
		return ipvSet;
	}
	
	public Map<String, String[]> queryForUri(String searchStr, String mediaType) throws Throwable {
		String searchWords = initSearchWords(searchStr);
		Map<String, String[]> instSet = new HashMap<String, String[]>();
		List<MediaInstChecker> mediaCheckerList = new ArrayList<MediaInstChecker>();
		
		BlockingQueue<Runnable> bkQueue = new LinkedBlockingQueue<Runnable>();
		ThreadPoolExecutor threadExec = new ThreadPoolExecutor(8, 9, 7, TimeUnit.DAYS, bkQueue);
		if (searchWords.equals(""))
			return instSet;

		Connection sourceConn = DBConnFactory.getInstance().dbConnect(DBConnFactory.FUSE_CONN);

		String sqlStr1 = "SELECT uri FROM inst_" + mediaType
					   + " WHERE MATCH(uri) AGAINST (? IN BOOLEAN MODE) or MATCH(label) AGAINST (? IN BOOLEAN MODE) LIMIT 3";
		PreparedStatement stmt1 = sourceConn.prepareStatement(sqlStr1);
		stmt1.setString(1, searchWords);
		stmt1.setString(2, searchWords);
		ResultSet rs1 = stmt1.executeQuery();
		while (rs1.next()) {
			String[] value = {"",""};
			instSet.put(rs1.getString(1).trim(), value);
		}
		rs1.close();
		stmt1.close();

		String sqlStr3 = "SELECT uri FROM inst_dbpedia " 
					   + "WHERE MATCH(uri) AGAINST (? IN BOOLEAN MODE) or MATCH(label) AGAINST (? IN BOOLEAN MODE) LIMIT 3";
		PreparedStatement stmt3 = sourceConn.prepareStatement(sqlStr3);
		stmt3.setString(1, searchWords);
		stmt3.setString(2, searchWords);
		ResultSet rs3 = stmt3.executeQuery();
		while (rs3.next()) {
			String s = rs3.getString(1).trim();
			String[] value = {"",""};
			instSet.put(rs3.getString(1).trim(), value);
			MediaInstChecker mediaChecker = new MediaInstChecker(s);
			threadExec.execute(mediaChecker);
			mediaCheckerList.add(mediaChecker);
		}
		rs3.close();
		stmt3.close();

		threadExec.shutdown();
		threadExec.awaitTermination(7, TimeUnit.DAYS);
//		while (!threadExec.isTerminated()) {
//			Thread.sleep(100);
//		}
		
		rmNotMedia(instSet, mediaCheckerList, mediaType);
		rmCorefs(instSet, mediaType);
		UriInjection.initLabelAndType(instSet);
		
		return instSet;
	}
	
	
	
	private void rmNotMedia(Map<String, String[]> instSet, List<MediaInstChecker> mediaCheckerList, String mediaType) {
		for (MediaInstChecker checker : mediaCheckerList) {
			String inst = checker.getInst();
			if (checker.isMedia() == false) {
				instSet.remove(inst);
			} else {
				if(mediaType.equals("movie") && checker.isMovie() == 0){
					instSet.remove(inst);
				} else if(mediaType.equals("music") && checker.isMusic() == 0) {
					instSet.remove(inst);
				} else if(mediaType.equals("photo") && checker.isPhoto() == 0) {
					instSet.remove(inst);
				}
			}
		}
	}
	
	private void rmNotMedia(Set<String> instSet, List<MediaInstChecker> mediaCheckerList, String mediaType) 
	{
		for (MediaInstChecker checker : mediaCheckerList) {
			String inst = checker.getInst();
			if (checker.isMedia() == false) {
				instSet.remove(inst);
			} else {
				if(mediaType.equals("movie") && checker.isMovie() == 0){
					instSet.remove(inst);
				} else if(mediaType.equals("music") && checker.isMusic() == 0) {
					instSet.remove(inst);
				} else if(mediaType.equals("photo") && checker.isPhoto() == 0) {
					instSet.remove(inst);
				}
			}
		}
	}
	
	private void rmCorefs(Map<String, String[]> instSet, String mediaType) throws Throwable {
		List<CorefFinder> finderList = new ArrayList<CorefFinder>();
		for (String inst : instSet.keySet()) {
			CorefFinder coFinder = new CorefFinder(inst, mediaType);
			coFinder.start();
			finderList.add(coFinder);
		}
		for (CorefFinder tmpFinder : finderList) {
			tmpFinder.join();
			if (instSet.containsKey(tmpFinder.getURI())) {
				Map<String, Integer> corefs = tmpFinder.getCorefs();
				for (Entry<String, Integer> entry : corefs.entrySet()) {
					if (!tmpFinder.getURI().equals(entry.getKey()))
						instSet.remove(entry.getKey());
				}
			}
		}
	}

	private void rmCorefs(Set<String> instSet, String mediaType) throws Throwable 
	{
		List<CorefFinder> finderList = new ArrayList<CorefFinder>();
		for (String inst : instSet) {
			CorefFinder coFinder = new CorefFinder(inst, mediaType);
			coFinder.start();
			finderList.add(coFinder);
		}
		for (CorefFinder tmpFinder : finderList) {
			tmpFinder.join();
			if (instSet.contains(tmpFinder.getURI())) {
				Map<String, Integer> corefs = tmpFinder.getCorefs();
				for (Entry<String, Integer> entry : corefs.entrySet()) {
					if (!tmpFinder.getURI().equals(entry.getKey()))
						instSet.remove(entry.getKey());
				}
			}
		}
	}
	
	public void setQueryMode(int mode) {
		this.queryMode = mode;
	}
	
	public static void main(String[] args) throws Throwable 
	{
		Config.initParam(); 
		TextInjection query = new TextInjection();
		query.setQueryMode(TextInjection.MODE_DOWN);	//down, up, all
		
		Iterator<Entry<String, Map<String, List<String[]>>>> queryItr = query.query("Cannes Film Festival").entrySet().iterator();
		while(queryItr.hasNext()) {
			Entry<String, Map<String, List<String[]>>> queryEntry = queryItr.next();
			if(queryEntry.getValue().size()>0)
				System.out.println(queryEntry.getKey() + "\n");
			Iterator<Entry<String, List<String[]>>> instItr = queryEntry.getValue().entrySet().iterator();
			while(instItr.hasNext()) {
				Entry<String, List<String[]>> instEntry = instItr.next();
				System.out.println(instEntry.getKey() + "\n");
				for(String[] triple : instEntry.getValue()) {
					System.out.println(triple[0]);
					System.out.println(triple[1]);
					System.out.println(triple[2]);
					System.out.println("");
				}
				System.out.println("");
			}
			System.out.println("");
		}
	}
}

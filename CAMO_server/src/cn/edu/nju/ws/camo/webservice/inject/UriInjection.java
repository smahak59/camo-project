package cn.edu.nju.ws.camo.webservice.inject;

import java.sql.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import cn.edu.nju.ws.camo.webservice.connect.*;

public class UriInjection 
{
	public static final String DBP_PREFIX = "http://dbpedia.org";
	public static final String JMD_PREFIX = "http://dbtune.org/jamendo";
	public static final String MAG_PREFIX = "http://dbtune.org/magnatune";
	public static final String JHP_PREFIX = "http://dbtune.org/bbc/peel";
	public static final String LMDB_PREFIX = "http://data.linkedmdb.org";
	public static final String TROP_PREFIX = "http://dbtropes.org";
	public static final String WDF_PREFIX = "http://www.bbc.co.uk/nature";
	
	private static final String MUSIC = "music";
	private static final String MOVIE = "movie";
	private static final String PHOTO = "photo";
	
	private String[] mediaTypeSet = { MUSIC, MOVIE, PHOTO };
	private String inst;
	private Map<String, Integer> corefs;	// (inst, {prop, value})
	private String mediaType;
	
	public UriInjection(String uri) throws Throwable {
		int connType = getConnType(uri);
		this.inst = uri;
		corefs = new HashMap<String, Integer>();
		if (connType == -1)
			return;
		if (connType == SDBConnFactory.DBP_CONN) {
			List<CorefFinder> typeObtThread = new ArrayList<CorefFinder>();
			for (String tmpMedia : mediaTypeSet) {
				CorefFinder newThread = new CorefFinder(inst, tmpMedia);
				newThread.start();
				typeObtThread.add(newThread);
			}
			for (CorefFinder tmpTypeObt : typeObtThread) {
				if (corefs.size()>0)
					break;
				tmpTypeObt.join();
				if (corefs.size()==0) {
					corefs = tmpTypeObt.getCorefs();
					mediaType = tmpTypeObt.getMediaType();
				}
			}
		} else {
			mediaType = getMediaType(connType);
			if (mediaType.equals(""))
				return;
			CorefFinder newThread = new CorefFinder(inst, mediaType);
			newThread.start();
			newThread.join();
			corefs = newThread.getCorefs();
		}
		if (corefs.size() == 0)
			corefs.put(inst, connType);
		
		System.out.println(corefs + "\n");
	}
	
	private static String getMediaType(int connType) 
	{
		String mediaType = "";
		switch (connType) {
			case SDBConnFactory.JMD_CONN:
				mediaType = MUSIC;
				break;
			case SDBConnFactory.MAG_CONN:
				mediaType = MUSIC;
				break;
			case SDBConnFactory.JHP_CONN:
				mediaType = MUSIC;
				break;
			case SDBConnFactory.LMDB_CONN:
				mediaType = MOVIE;
				break;
			case SDBConnFactory.TROP_CONN:
				mediaType = MOVIE;
				break;
			case SDBConnFactory.WDF_CONN:
				mediaType = PHOTO;
				break;
		}
		return mediaType;
	}
	
	private int getConnType(String uri) throws Throwable 
	{
		uri = uri.trim();
		int connType = -1;
		if (uri.startsWith(DBP_PREFIX)) {
			connType = SDBConnFactory.DBP_CONN;
		} else if (uri.startsWith(JMD_PREFIX)) {
			connType = SDBConnFactory.JMD_CONN;
		} else if (uri.startsWith(MAG_PREFIX)) {
			connType = SDBConnFactory.MAG_CONN;
		} else if (uri.startsWith(JHP_PREFIX)) {
			connType = SDBConnFactory.JHP_CONN;
		} else if (uri.startsWith(LMDB_PREFIX)) {
			connType = SDBConnFactory.LMDB_CONN;
		} else if (uri.startsWith(TROP_PREFIX)) {
			connType = SDBConnFactory.TROP_CONN;
		} else if (uri.startsWith(WDF_PREFIX)) {
			connType = SDBConnFactory.WDF_CONN;
		}
		
		if (connType == -1) 
			connType = getConnTypeFromDB(uri, MUSIC);
		if (connType == -1) 
			connType = getConnTypeFromDB(uri, MOVIE);
		if (connType == -1) 
			connType = getConnTypeFromDB(uri, PHOTO);
		
		return connType;
	}
	
	private int getConnTypeFromDB(String uri, String mediaType) throws Throwable 
	{
		int connType = -1;
		Connection sourceConn = DBConnFactory.getInstance().dbConnect(DBConnFactory.FUSE_CONN);
		String sqlStr = "SELECT source FROM inst_" + mediaType + " WHERE uri=?";
		PreparedStatement stmt = sourceConn.prepareStatement(sqlStr);
		stmt.setString(1, uri);
		ResultSet rs = stmt.executeQuery();
		if (rs.next()) 
			connType = rs.getInt(1);
		
		rs.close();
		stmt.close();
		sourceConn.close();
		return connType;
	}
	
	private void rmProp(Map<String, List<String[]>> instPropList, String inst, String prop) 
	{
		if (instPropList.containsKey(inst)) {
			List<String[]> propList = instPropList.get(inst);
			Iterator<String[]> itr = propList.iterator();
			while (itr.hasNext()) {
				String[] pv = itr.next();
				if (pv[0].trim().equals(prop)) 
					itr.remove();
			}
			if (propList.size() == 0) {
				instPropList.remove(inst);
			}
		}
	}
	
	public List<String[]> queryDown() throws Throwable 
	{
		List<String[]> ipvList = new ArrayList<String[]>();		//{s,p,v}
		Map<String, List<String[]>> instPropList = new HashMap<String, List<String[]>>();
		if(mediaType.equals(""))
			return ipvList;
		Iterator<Entry<String, Integer>> itr1 = corefs.entrySet().iterator();
		List<DownPropFinder> propThedList = new ArrayList<DownPropFinder>();
		List<CoPropFinder> coPropThedList = new ArrayList<CoPropFinder>();
		Set<String> rmCoPropSet = new HashSet<String>();
		while (itr1.hasNext()) {
			Entry<String, Integer> entry = itr1.next();
			DownPropFinder newFinder = new DownPropFinder(entry.getKey(), entry.getValue());
			newFinder.start();
			propThedList.add(newFinder);
			CoPropFinder newCoFinder = new CoPropFinder(entry.getKey(), mediaType);
			newCoFinder.start();
			coPropThedList.add(newCoFinder);
		}
		for (DownPropFinder tmpFinder : propThedList) {
			tmpFinder.join();
			instPropList.put(tmpFinder.getInst(), tmpFinder.getPropList());
		}
		for (CoPropFinder tmpFinder : coPropThedList) {
			tmpFinder.join();
			// remove redundant properties
			String tmpInst = tmpFinder.getInst();
			Map<String, List<String[]>> instPropSet = tmpFinder.getCorefSet();
			Iterator<Entry<String, List<String[]>>> itr4 = instPropSet.entrySet().iterator();
			while (itr4.hasNext()) {
				Entry<String, List<String[]>> entry = itr4.next();
				String prop = entry.getKey();
				if (rmCoPropSet.contains(tmpInst + "::" + prop))
					continue;
				List<String[]> spList = entry.getValue();
				for (String[] sp : spList) {
					if (tmpInst.equals(sp[0].trim()) == false) {		//remove 
						rmProp(instPropList, sp[0].trim(), sp[1].trim());
						rmCoPropSet.add(sp[0].trim()+"::"+sp[1].trim());
					} else {
						rmProp(instPropList, tmpInst, prop);	//remove itself
						rmCoPropSet.add(tmpInst+"::"+prop);
					}
				}
			}
		}
		
		Iterator<Entry<String, List<String[]>>> itr2 = instPropList.entrySet().iterator();
		while (itr2.hasNext()) {
			Entry<String, List<String[]>> entry = itr2.next();
//			ipvList.addAll(entry.getValue());
			for (String[] pv : entry.getValue()) {
				ipvList.add(new String[]{inst,pv[0],pv[1]});
//				System.out.println(inst + "\n" + pv[0] + "\n" + pv[1] + "\n");
			}
		}
		return ipvList;
	}
	
	public List<String[]> queryUp() throws InterruptedException {
		List<String[]> ipvList = new ArrayList<String[]>();		//{s,p,o}
		if(mediaType.equals(""))
			return ipvList;
		BlockingQueue<Runnable> bkQueue1 = new LinkedBlockingQueue<Runnable>();
		ThreadPoolExecutor threadExec1 = new ThreadPoolExecutor(10, 12, 7, TimeUnit.DAYS, bkQueue1);
		List<UpPropFinder> propFinderList = new ArrayList<UpPropFinder>();
		Iterator<Entry<String, Integer>> itr1 = corefs.entrySet().iterator();
		while (itr1.hasNext()) {
			Entry<String, Integer> entry = itr1.next();
			UpPropFinder newFinder = new UpPropFinder(entry.getKey(), entry.getValue());
			propFinderList.add(newFinder);
			threadExec1.execute(newFinder);
		}
		threadExec1.shutdown();
		while (!threadExec1.isTerminated()) {
			Thread.sleep(20);
		}
		for(UpPropFinder finder : propFinderList) {
			ipvList.addAll(finder.getTripleList());
		}
		
		List<String[]> rmIpvList = new ArrayList<String[]>();	//{s,p}
		List<CoPropFinder> coPFList = new ArrayList<CoPropFinder>();
		BlockingQueue<Runnable> bkQueue2 = new LinkedBlockingQueue<Runnable>();
		ThreadPoolExecutor threadExec2 = new ThreadPoolExecutor(10, 12, 7, TimeUnit.DAYS, bkQueue2);
		for(String[] tmpIpv : ipvList) {
			CoPropFinder newCoFinder = new CoPropFinder(tmpIpv[0], tmpIpv[1], mediaType);
			coPFList.add(newCoFinder);
			threadExec2.execute(newCoFinder);
		}
		threadExec2.shutdown();
		while (!threadExec2.isTerminated()) {
			Thread.sleep(10);
		}
		for(CoPropFinder tmpFinder : coPFList) {
			String tmpInst = tmpFinder.getInst();
			String tmpProp = tmpFinder.getProp();
			boolean hasRemoved = false;
			for(String[] tmpRmSp : rmIpvList) {
				if(tmpRmSp[0].equals(tmpInst) && tmpRmSp[1].equals(tmpProp)) {
					hasRemoved = true;
					break;
				}
			}
			if(hasRemoved == false) {
				List<String[]> newList = tmpFinder.getCorefSet().get(tmpProp);
				if(newList.size()>0) {
					rmIpvList.addAll(newList);
				}
			} else {
				continue;
			}
		}
		for(String[] rmIpv : rmIpvList) {
			Iterator<String[]> itr = ipvList.iterator();
			while(itr.hasNext()) {
				String[] ipv = itr.next();
				if(ipv[0].equals(rmIpv[0]) && ipv[1].equals(rmIpv[1])) {
					itr.remove();
				}
			}
		}
		
		for(String[] ipv : ipvList) {
			ipv[2] = this.inst;
//			System.out.println(ipv[0] + "\n" + ipv[1] + "\n" + ipv[2] + "\n");
		}
		return ipvList;
	}
	
	public String getInst()
	{
		return getInst();
	}
	
	public static void main(String[] args) throws Throwable 
	{
		Config.initParam(); 
		UriInjection query = new UriInjection("http://dbpedia.org/resource/Norman_Granz");
		System.out.println("\n==========Query Down=========\n");
		List<String[]> triplesDown = query.queryDown();
		for(String[] triple : triplesDown) {
			System.out.println(triple[0] + "\n" + triple[1] + "\n" + triple[2] + "\n");
		}
		System.out.println("\n==========Query Up=========\n");
		List<String[]> triplesUp = query.queryUp();
		for(String[] triple : triplesUp) {
			System.out.println(triple[0] + "\n" + triple[1] + "\n" + triple[2] + "\n");
		}
	}
	
}

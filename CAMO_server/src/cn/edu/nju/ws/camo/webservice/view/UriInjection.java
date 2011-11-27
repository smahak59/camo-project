package cn.edu.nju.ws.camo.webservice.view;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
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
import cn.edu.nju.ws.camo.webservice.connect.SDBConnFactory;
import cn.edu.nju.ws.camo.webservice.util.ISub;

public class UriInjection 
{
	
	private String[] mediaTypeSet = { SDBConnFactory.MUSIC, SDBConnFactory.MOVIE };
	private String inst;
	private Map<String, Integer> corefs;	// (inst, {prop, value})
	private String mediaType;
	
	private static Map<String, String> propToDbpProp = new HashMap<String, String>();
	
	public UriInjection(String uri) throws Throwable {
		if(propToDbpProp.size()==0)
			initPropToDbp();
		String ontoName = SDBConnFactory.getInstance().getOntoName(uri);
		this.inst = uri;
		corefs = new HashMap<String, Integer>();
		if (ontoName == null)
			return;
		if (ontoName.equals("DBP")) {
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
			mediaType = SDBConnFactory.getInstance().getMediaType(ontoName);
			if (mediaType.equals(""))
				return;
			CorefFinder newThread = new CorefFinder(inst, mediaType);
			newThread.start();
			newThread.join();
			corefs = newThread.getCorefs();
		}
		if (corefs.size() == 0)
			corefs.put(inst, SDBConnFactory.getInstance().getSdbIdx(ontoName));
	}
	
	private void initPropToDbp() throws IOException {
		File file = new File("config/prop_dbp.map");
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String line = "";
		while((line=reader.readLine())!=null) {
			line = line.trim();
			if(line.length()==0)
				continue;
			String[] terms = line.split("###");
			if(terms.length==2) {
				propToDbpProp.put(terms[0].trim(), terms[1].trim());
			}
		}
	}
	
	public List<String[]> queryDown() throws Throwable 
	{
		List<String[]> ipvList = new ArrayList<String[]>();		//{s,p,v}
		Map<String, Map<String, Set<String>>> instPropList = new HashMap<String, Map<String, Set<String>>>();
		if(mediaType.equals(""))
			return ipvList;
		Iterator<Entry<String, Integer>> itr1 = corefs.entrySet().iterator();
		List<DownPropFinder> propThedList = new ArrayList<DownPropFinder>();
		List<CoPropFinder> coPropThedList = new ArrayList<CoPropFinder>();
//		Set<String> rmCoPropSet = new HashSet<String>();
		while (itr1.hasNext()) {
			Entry<String, Integer> entry = itr1.next();
			DownPropFinder newFinder = new DownPropFinder(entry.getKey(), entry.getValue());
			newFinder.start();
			propThedList.add(newFinder);
			if(corefs.size()>1 && entry.getKey().startsWith("http://dbpedia.org")) {	//only consider dbp
				CoPropFinder newCoFinder = new CoPropFinder(entry.getKey(), mediaType);
				newCoFinder.start();
				coPropThedList.add(newCoFinder);
			}
		}
		for (DownPropFinder tmpFinder : propThedList) {
			tmpFinder.join();
			instPropList.put(tmpFinder.getInst(), tmpFinder.getPropList());
		}
		for (CoPropFinder tmpFinder : coPropThedList) {		//only dbp(center)
			tmpFinder.join();
			// remove redundant properties
			String dbpInst1 = tmpFinder.getInst();
			Map<String, List<String[]>> instPropSet = tmpFinder.getCorefSet();
			Iterator<Entry<String, List<String[]>>> itr4 = instPropSet.entrySet().iterator();
			while (itr4.hasNext()) {
				Entry<String, List<String[]>> entry = itr4.next();
				String dbpProp = entry.getKey();
				List<String[]> spList = entry.getValue();
				for (String[] sp : spList) {
					String tmpInst2 = sp[0].trim();
					String prop2 = sp[1].trim();
					//combine
					if(instPropList.get(tmpInst2)==null) {
						continue;
					}
					combineValueCoref(instPropList.get(dbpInst1).get(dbpProp), instPropList.get(tmpInst2).get(prop2));
					instPropList.get(tmpInst2).remove(prop2);
				}
			}
		}
		
		Iterator<Entry<String, Map<String, Set<String>>>> itr2 = instPropList.entrySet().iterator();
		while (itr2.hasNext()) {
			Entry<String, Map<String, Set<String>>> entry = itr2.next();
			Iterator<Entry<String, Set<String>>> itr3 = entry.getValue().entrySet().iterator();
			while(itr3.hasNext()) {
				Entry<String, Set<String>> entry2 = itr3.next();
				String p = entry2.getKey();
				if(propToDbpProp.containsKey(p)) {
					p = propToDbpProp.get(p);
				}
				if(p.startsWith("http://dbpedia.org")==false)
					continue;
				for(String v : entry2.getValue()) {
					ipvList.add(new String[]{inst,p,v});
				}
			}
		}
		return ipvList;
	}
	
	private void combineValueCoref(Set<String> valueSet1, Set<String> valueSet2) throws InterruptedException {
		if(valueSet1.iterator().next().startsWith("http")==false && valueSet2.iterator().next().startsWith("http")==false) {
			combineSimilaryLiteralValue(valueSet1, valueSet2);
			return;
		}
		
		List<CorefFinder> finderList = new ArrayList<CorefFinder>();
		BlockingQueue<Runnable> bkQueue1 = new LinkedBlockingQueue<Runnable>();
		ThreadPoolExecutor threadExec1 = new ThreadPoolExecutor(8, 9, 7, TimeUnit.DAYS, bkQueue1);
		for(String value : valueSet1) {
			CorefFinder newThread = new CorefFinder(value, mediaType);
			threadExec1.execute(newThread);
			finderList.add(newThread);
		}
		threadExec1.shutdown();
		threadExec1.awaitTermination(7, TimeUnit.DAYS);
		for(CorefFinder finder : finderList) {
			for(String tmpU : finder.getCorefs().keySet()) {
				if(valueSet2.contains(tmpU))
					System.out.println("aaaa: " + tmpU);
			}
			valueSet2.removeAll(finder.getCorefs().keySet());
		}
		valueSet1.addAll(valueSet2);
	}
	
	private void combineSimilaryLiteralValue(Set<String> valueSet1, Set<String> valueSet2) {
		Set<String> removed = new HashSet<String>();
		for(String value1 : valueSet1) {
			for(String value2 : valueSet2) {
				if(ISub.getSimilarity(value1, value2)>=0.85)
					removed.add(value2);
			}
		} 
		valueSet2.removeAll(removed);
		valueSet1.addAll(valueSet2);
	}
	
	public List<String[]> queryUp() throws InterruptedException {
		List<String[]> ipvList = new ArrayList<String[]>();		//{s,p,o}
		if(mediaType.equals(""))
			return ipvList;
		BlockingQueue<Runnable> bkQueue1 = new LinkedBlockingQueue<Runnable>();
		ThreadPoolExecutor threadExec1 = new ThreadPoolExecutor(8, 9, 7, TimeUnit.DAYS, bkQueue1);
		List<UpPropFinder> propFinderList = new ArrayList<UpPropFinder>();
		Iterator<Entry<String, Integer>> itr1 = corefs.entrySet().iterator();
		while (itr1.hasNext()) {
			Entry<String, Integer> entry = itr1.next();
			UpPropFinder newFinder = new UpPropFinder(entry.getKey(), entry.getValue());
			propFinderList.add(newFinder);
			threadExec1.execute(newFinder);
		}
		threadExec1.shutdown();
		threadExec1.awaitTermination(7, TimeUnit.DAYS);
		for(UpPropFinder finder : propFinderList) {
			ipvList.addAll(finder.getTripleList());
		}
		
		if(corefs.size()>1) {
			List<String[]> rmIpvList = new ArrayList<String[]>();	//{s,p}
			List<CoPropFinder> coPFList = new ArrayList<CoPropFinder>();
			BlockingQueue<Runnable> bkQueue2 = new LinkedBlockingQueue<Runnable>();
			ThreadPoolExecutor threadExec2 = new ThreadPoolExecutor(8, 9, 7, TimeUnit.DAYS, bkQueue2);
			for(String[] tmpIpv : ipvList) {
				CoPropFinder newCoFinder = new CoPropFinder(tmpIpv[0], tmpIpv[1], mediaType);
				coPFList.add(newCoFinder);
				threadExec2.execute(newCoFinder);
			}
			threadExec2.shutdown();
			threadExec2.awaitTermination(7, TimeUnit.DAYS);
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
		}
		
		for(String[] ipv : ipvList) {
			ipv[2] = this.inst;
			if(propToDbpProp.containsKey(ipv[1]))
				ipv[1] = propToDbpProp.get(ipv[1]);
		}
		return ipvList;
	}
	
	public String getInst()
	{
		return getInst();
	}
	
	public static void initLabelAndType(Map<String, String[]> instSet) throws InterruptedException {
		BlockingQueue<Runnable> bkQueue = new LinkedBlockingQueue<Runnable>();
		ThreadPoolExecutor threadExec = new ThreadPoolExecutor(3, 5, 7, TimeUnit.DAYS, bkQueue);
		List<LabelAndTypeFinder> finderList = new ArrayList<LabelAndTypeFinder>();
		for(String inst : instSet.keySet()) {
			LabelAndTypeFinder finder = new LabelAndTypeFinder(inst);
			threadExec.execute(finder);
			finderList.add(finder);
		}
		threadExec.shutdown();
		threadExec.awaitTermination(7, TimeUnit.DAYS);
		for(LabelAndTypeFinder finder : finderList) {
			String[] value = instSet.get(finder.getUri());
			value[0] = finder.getLabel();
			value[1] = finder.getType();
		}
	}
	
	public static void main(String[] args) throws Throwable 
	{
		Config.initParam(); 
		Long oldTime = new Date().getTime();
		UriInjection query = new UriInjection("http://dbpedia.org/resource/Spider-Man_3");
		System.out.println("\n==========Query Down=========\n");
		List<String[]> triplesDown = query.queryDown();
		for(String[] triple : triplesDown) {
			System.out.println(triple[0] + "\n" + triple[1] + "\n" + triple[2] + "\n");
		}
		System.out.println(new Date().getTime()-oldTime);
		System.out.println("\n==========Query Up=========\n");
		List<String[]> triplesUp = query.queryUp();
		for(String[] triple : triplesUp) {
			System.out.println(triple[0] + "\n" + triple[1] + "\n" + triple[2] + "\n");
		}
		System.out.println(new Date().getTime()-oldTime);
	}
}

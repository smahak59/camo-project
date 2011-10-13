package cn.edu.nju.ws.camo.webservice;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.jws.WebService;

import cn.edu.nju.ws.camo.webservice.connect.Config;
import cn.edu.nju.ws.camo.webservice.util.SetSerialization;
import cn.edu.nju.ws.camo.webservice.view.LabelAndTypeFinder;
import cn.edu.nju.ws.camo.webservice.view.TextInjection;
import cn.edu.nju.ws.camo.webservice.view.UriInjection;

@WebService(endpointInterface="cn.edu.nju.ws.camo.webservice.IViewService") 
public class ViewService implements IViewService {

	public String instViewDown(String inst) {
		if(inst.startsWith("http://") == false)
			return null;
		String result = "";
		ArrayList<String> resultList = new ArrayList<String>();
		List<String[]> triples = null;
		try {
			UriInjection query = new UriInjection(inst);
			triples = query.queryDown();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		if(triples != null) {
			ArrayList<String> propList = new ArrayList<String>();
			ArrayList<LabelAndTypeFinder> finderList = new ArrayList<LabelAndTypeFinder>();
			BlockingQueue<Runnable> bkQueue = new LinkedBlockingQueue<Runnable>();
			ThreadPoolExecutor threadExec = new ThreadPoolExecutor(3, 5, 7, TimeUnit.DAYS, bkQueue);   
			for(String[] triple : triples) {
				try {
					LabelAndTypeFinder newFinder = new LabelAndTypeFinder(triple[2]);
					threadExec.execute(newFinder);
					propList.add(triple[1]);
					finderList.add(newFinder);
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
			threadExec.shutdown();
			while (!threadExec.isTerminated()) {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			for(int idx=0; idx<finderList.size(); idx++) {
				ArrayList<String> termSet = new ArrayList<String>();
				termSet.add(propList.get(idx));
				termSet.add(finderList.get(idx).getResult());
				resultList.add(SetSerialization.serialize2(termSet));
			}
		}
		result = SetSerialization.serialize3(resultList);
		return result;
	}

	public String instViewUp(String inst) {
		if(inst.startsWith("http://") == false)
			return null;
		String result = "";
		ArrayList<String> resultList = new ArrayList<String>();
		List<String[]> triples = null;
		try {
			UriInjection query = new UriInjection(inst);
			triples = query.queryUp();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		if(triples != null) {
			ArrayList<String> propList = new ArrayList<String>();
			ArrayList<LabelAndTypeFinder> finderList = new ArrayList<LabelAndTypeFinder>();
			BlockingQueue<Runnable> bkQueue = new LinkedBlockingQueue<Runnable>();
			ThreadPoolExecutor threadExec = new ThreadPoolExecutor(3, 5, 7, TimeUnit.DAYS, bkQueue);   
			for(String[] triple : triples) {
				try {
					LabelAndTypeFinder newFinder = new LabelAndTypeFinder(triple[0]);
					threadExec.execute(newFinder);
					propList.add(triple[1]);
					finderList.add(newFinder);
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
			threadExec.shutdown();
			while (!threadExec.isTerminated()) {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			for(int idx=0; idx<finderList.size(); idx++) {
				ArrayList<String> termSet = new ArrayList<String>();
				termSet.add(finderList.get(idx).getResult());
				termSet.add(propList.get(idx));
				resultList.add(SetSerialization.serialize2(termSet));
			}
		}
		result = SetSerialization.serialize3(resultList);
		return result;
	}

	public String textViewDown(String searchText, String mediaType) {
		// TODO Auto-generated method stub
		String result = "";
		ArrayList<String> resultList = new ArrayList<String>();
		try {
			TextInjection query = new TextInjection();
			query.setQueryMode(TextInjection.MODE_DOWN);	//down, up, all
			Map<String, List<String[]>> instToTriples = query.query(searchText, mediaType);
			Iterator<Entry<String, List<String[]>>> itr = instToTriples.entrySet().iterator();
			while(itr.hasNext()) {
				Entry<String, List<String[]>> entry = itr.next();
				ArrayList<String> instToTriplesResult = new ArrayList<String>();
				LabelAndTypeFinder finder = new LabelAndTypeFinder(entry.getKey());
				finder.start();
				ArrayList<String> triplesResult = new ArrayList<String>();
				
				ArrayList<String> propList = new ArrayList<String>();
				ArrayList<LabelAndTypeFinder> finderList = new ArrayList<LabelAndTypeFinder>();
				BlockingQueue<Runnable> bkQueue = new LinkedBlockingQueue<Runnable>();
				ThreadPoolExecutor threadExec = new ThreadPoolExecutor(3, 5, 7, TimeUnit.DAYS, bkQueue);  
				for(String[] triple : entry.getValue()) {
					try {
						LabelAndTypeFinder newFinder = new LabelAndTypeFinder(triple[2]);
						threadExec.execute(newFinder);
						propList.add(triple[1]);
						finderList.add(newFinder);
					} catch (Throwable e) {
						e.printStackTrace();
					}
				}
				threadExec.shutdown();
				while (!threadExec.isTerminated()) {
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				for(int idx=0; idx<finderList.size(); idx++) {
					ArrayList<String> termSet = new ArrayList<String>();
					termSet.add(propList.get(idx));
					termSet.add(finderList.get(idx).getResult());
					triplesResult.add(SetSerialization.serialize2(termSet));
				}
				
				finder.join();
				instToTriplesResult.add(finder.getResult());	// L3 -> L1
				instToTriplesResult.add(SetSerialization.serialize3(triplesResult));
				resultList.add(SetSerialization.serialize4(instToTriplesResult));
			}
			result = SetSerialization.serialize5(resultList);
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	public String textViewUp(String searchText, String mediaType) {
		String result = "";
		ArrayList<String> resultList = new ArrayList<String>();
		try {
			TextInjection query = new TextInjection();
			query.setQueryMode(TextInjection.MODE_UP);	//down, up, all
			Map<String, List<String[]>> instToTriples = query.query(searchText, mediaType);
			Iterator<Entry<String, List<String[]>>> itr = instToTriples.entrySet().iterator();
			while(itr.hasNext()) {
				Entry<String, List<String[]>> entry = itr.next();
				ArrayList<String> instToTriplesResult = new ArrayList<String>();
				LabelAndTypeFinder finder = new LabelAndTypeFinder(entry.getKey());
				finder.start();
				ArrayList<String> triplesResult = new ArrayList<String>();

				ArrayList<String> propList = new ArrayList<String>();
				ArrayList<LabelAndTypeFinder> finderList = new ArrayList<LabelAndTypeFinder>();
				BlockingQueue<Runnable> bkQueue = new LinkedBlockingQueue<Runnable>();
				ThreadPoolExecutor threadExec = new ThreadPoolExecutor(3, 5, 7, TimeUnit.DAYS, bkQueue);  
				for(String[] triple : entry.getValue()) {
					try {
						LabelAndTypeFinder newFinder = new LabelAndTypeFinder(triple[0]);
						threadExec.execute(newFinder);
						propList.add(triple[1]);
						finderList.add(newFinder);
					} catch (Throwable e) {
						e.printStackTrace();
					}
				}
				threadExec.shutdown();
				while (!threadExec.isTerminated()) {
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				for(int idx=0; idx<finderList.size(); idx++) {
					ArrayList<String> termSet = new ArrayList<String>();
					termSet.add(finderList.get(idx).getResult());
					termSet.add(propList.get(idx));
					triplesResult.add(SetSerialization.serialize2(termSet));
				}
				
				finder.join();
				instToTriplesResult.add(finder.getResult());	// L3 -> L1
				instToTriplesResult.add(SetSerialization.serialize3(triplesResult));
				resultList.add(SetSerialization.serialize4(instToTriplesResult));
			}
			result = SetSerialization.serialize5(resultList);
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	
	public static void main(String[] args) {
		Config.initParam();
		System.out.println(new ViewService().textViewDown("While Paris Sleeps", "movie"));
	}
}

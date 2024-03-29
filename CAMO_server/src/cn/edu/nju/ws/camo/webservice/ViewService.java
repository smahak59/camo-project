package cn.edu.nju.ws.camo.webservice;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.jws.WebService;

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
			query.close();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		if(triples != null) {
			ArrayList<String> propList = new ArrayList<String>();
			ArrayList<LabelAndTypeFinder> finderList = new ArrayList<LabelAndTypeFinder>();
			BlockingQueue<Runnable> bkQueue = new LinkedBlockingQueue<Runnable>();
			ThreadPoolExecutor threadExec = new ThreadPoolExecutor(8, 10, 7, TimeUnit.DAYS, bkQueue);   
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
			query.close();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		if(triples != null) {
			ArrayList<String> propList = new ArrayList<String>();
			ArrayList<LabelAndTypeFinder> finderList = new ArrayList<LabelAndTypeFinder>();
			BlockingQueue<Runnable> bkQueue = new LinkedBlockingQueue<Runnable>();
			ThreadPoolExecutor threadExec = new ThreadPoolExecutor(8, 10, 7, TimeUnit.DAYS, bkQueue);   
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
	
	public String textView(String searchText, String mediaType) {
		String result = "";
		ArrayList<String> resultList = new ArrayList<String>();
		try {
			TextInjection query = new TextInjection();
			query.setQueryMode(TextInjection.MODE_DOWN);	//down, up, all
			List<String[]> instToTriples = query.queryForUri(searchText, mediaType);
			for(String[] instInfo : instToTriples) {
				List<String> termList = new ArrayList<String>();
				String inst = instInfo[0];
				String label = instInfo[1];
				String clsType = instInfo[2];
				termList.add(inst);
				termList.add(label);
				termList.add(clsType);
				resultList.add(SetSerialization.serialize1(termList));
			}
			result = SetSerialization.serialize2(resultList);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return result;
	}

	
	public String testConnection() {
		return "1";
	}
}

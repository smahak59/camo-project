package cn.edu.nju.ws.camo.webservice.view;

import java.util.*;

import cn.edu.nju.ws.camo.webservice.connect.*;

import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.sdb.sql.*;

public class DownPropFinder extends Thread 
{	
	private String instance = "";
	private int connType = -1;
	private Map<String, Set<String>> propertyList = new HashMap<String, Set<String>>();

	public DownPropFinder(String instance, int connType)
	{
		this.instance = instance;
		this.connType = connType;
	}
	
	@Override
	public void run() 
	{
		try {
			String qstrForProp = "SELECT ?p ?o WHERE { <" + instance + "> ?p ?o }";
			SDBConnection conn = SDBConnFactory.getInstance().sdbConnect(connType);
			QueryExecution qe = JenaSDBOp.query(conn, SDBConnFactory.getInstance().sdbConnectType(connType), qstrForProp);
			ResultSet rs = qe.execSelect();
			while (rs.hasNext()) {
				QuerySolution qs = rs.nextSolution();
				String p = qs.get("p").toString();
				String o = qs.get("o").toString();
				if(propertyList.containsKey(p)) {
					Set<String> valueList = propertyList.get(p);
					valueList.add(o);
				} else {
					Set<String> valueList = new HashSet<String>();
					valueList.add(o);
					propertyList.put(p, valueList);
				}
			}
			conn.close();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	public String getInst() 
	{
		return instance;
	}
	
	public Map<String, Set<String>> getPropList() 
	{
		return propertyList;
	}
}

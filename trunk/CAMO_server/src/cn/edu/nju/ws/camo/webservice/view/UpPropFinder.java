package cn.edu.nju.ws.camo.webservice.view;

import java.util.ArrayList;
import java.util.List;

import cn.edu.nju.ws.camo.webservice.connect.JenaSDBOp;
import cn.edu.nju.ws.camo.webservice.connect.SDBConnFactory;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.sdb.sql.SDBConnection;

public class UpPropFinder implements Runnable  {

	private String uri = "";
	private int connType = -1;
	private List<String[]> tripleList = new ArrayList<String[]>();
	
	public UpPropFinder(String uri, int connType) {
		this.uri = uri;
		this.connType = connType;
	}
	
	public void run() {
		try {
			String qstrForProp = "SELECT ?s ?p WHERE { ?s ?p <" + uri + "> }";
			SDBConnection conn = SDBConnFactory.getInstance().sdbConnect(connType);
			QueryExecution qe = JenaSDBOp.query(conn, SDBConnFactory.getInstance().sdbConnectType(connType), qstrForProp);
			ResultSet rs = qe.execSelect();
			while (rs.hasNext()) {
				QuerySolution qs = rs.nextSolution();
				String s = qs.get("s").toString();
				String p = qs.get("p").toString();
				tripleList.add(new String[]{s, p, uri});
			}
			conn.close();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
	}
	
	public String getObj() 
	{
		return this.uri;
	}
	
	public List<String[]> getTripleList() 
	{
		return tripleList;
	}
}

package cn.edu.nju.ws.camo.webservice.view;

import java.util.ArrayList;

import cn.edu.nju.ws.camo.webservice.connect.Config;
import cn.edu.nju.ws.camo.webservice.connect.JenaSDBOp;
import cn.edu.nju.ws.camo.webservice.connect.SDBConnFactory;
import cn.edu.nju.ws.camo.webservice.util.SetSerialization;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.sdb.sql.SDBConnection;
import com.hp.hpl.jena.util.URIref;
import com.hp.hpl.jena.vocabulary.RDF;

public class LabelAndTypeFinder extends Thread {

	private String uri;
	private String result;
	
	private static String[] NAME_PROPS = {
		"http://xmlns.com/foaf/0.1/name",
		"http://purl.org/dc/elements/1.1/title", 
		"http://dbpedia.org/ontology/title", 
		"http://www.w3.org/2000/01/rdf-schema#label",
		"http://purl.org/ontology/mo/label" 
	};
	
	public LabelAndTypeFinder(String uri) {
		this.uri = uri;
	}
	
	private static boolean isNameProp(String prop) 
	{
		for (String tmpProp : NAME_PROPS) {
			if (tmpProp.equals(prop))
				return true;
		}
		return false;
	}
	
	private static String getSpecialLocalName(String url)
	{
		String localName = url;
		int jIndex = localName.lastIndexOf("#");
		if (jIndex>=0)
			localName = localName.substring(jIndex+1);
		int xIndex = localName.lastIndexOf("/");
		if (xIndex >= 0)
			localName = localName.substring(xIndex+1);
		return localName;
	}
	
	@Override
	public void run() {
		String labelName = "";
		String type = "";
		ArrayList<String> labelAndType = new ArrayList<String>();
		int connType = -1;
		if(uri.startsWith("http://")==false) {
			result = uri;
			return;
		}
		try {
			connType = SDBConnFactory.getConnType(uri);
			
			if(connType == -1) {
				result = uri;
				return;
			}
			SDBConnection sdbc = SDBConnFactory.getInstance().sdbConnect(connType);
			String queryStr = "SELECT ?p ?o WHERE { <" + URIref.encode(uri) + "> ?p ?o }";
			QueryExecution qe = JenaSDBOp.query(sdbc, SDBConnFactory.getInstance().sdbConnectType(connType), queryStr);
			ResultSet rs = qe.execSelect();
			while (rs.hasNext()) {
				QuerySolution qs = rs.nextSolution();
				String tmpProp = qs.get("p").toString().trim();
				if (labelName.length() == 0 && isNameProp(tmpProp)) {
					labelName = qs.get("o").toString().trim();
					int endIdx = labelName.indexOf("^^");
					if (endIdx > 0)
						labelName = labelName.substring(0, endIdx);
					endIdx = labelName.indexOf("@");
					if (endIdx > 0)
						labelName = labelName.substring(0, endIdx);
				}
				if(type.length() == 0 && tmpProp.equals(RDF.type.getURI())) {
					if(qs.get("o").toString().trim().equals("http://www.w3.org/2002/07/owl#Thing") || qs.get("o").toString().trim().equals("http://dbpedia.org/ontology/Person"))
						continue;
					type = qs.get("o").toString().trim();
				}
				if(labelName.length()>0 && type.length()>0)
					break;
			}
			qe.close();
			sdbc.close();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		if(type.length()==0) {
			result = uri;
			return;
		}
		if(labelName.length()==0 && connType == SDBConnFactory.DBP_CONN) {
			labelName = getSpecialLocalName(uri).replaceAll("_", " ");
		}
		labelAndType.add(uri);
		labelAndType.add(labelName);
		labelAndType.add(type);
		this.result = SetSerialization.serialize1(labelAndType);
	}
	
	public String getResult() {
		return this.result;
	}
	
	public static void main(String[] args) throws Throwable  {
		Config.initParam();
		LabelAndTypeFinder finder = new LabelAndTypeFinder("http://dbpedia.org/resource/Verve_Records");
		finder.run();
		System.out.println(finder.getResult());
	}
}

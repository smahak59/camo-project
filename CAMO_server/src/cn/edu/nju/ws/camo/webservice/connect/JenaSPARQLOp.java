package cn.edu.nju.ws.camo.webservice.connect;

import com.hp.hpl.jena.query.*;

public class JenaSPARQLOp 
{
	public synchronized static QueryExecution query(String sparqlURI, String qstr) throws Throwable
	{
		Query query = QueryFactory.create(qstr);
		QueryExecution qe = QueryExecutionFactory.sparqlService(sparqlURI, query);
		return qe;
	}
}

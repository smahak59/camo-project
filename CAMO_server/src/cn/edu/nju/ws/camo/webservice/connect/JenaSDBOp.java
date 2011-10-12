package cn.edu.nju.ws.camo.webservice.connect;

import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.sdb.*;
import com.hp.hpl.jena.sdb.sql.*;
import com.hp.hpl.jena.sdb.store.*;

public class JenaSDBOp 
{
	// DatabaseType.MySQL, DatabaseType.PostgreSQL
	private synchronized static void loadDriver(DatabaseType dbType)
	{
		if (dbType.getName().equals("MySQL")) {
			JDBC.loadDriverMySQL();
		} else if (dbType.getName().equals("PostgreSQL")) {
			JDBC.loadDriverPGSQL();
		} else System.err.println("Unknown database type?");
	}
	
	// DatabaseType.MySQL, DatabaseType.PostgreSQL
	private synchronized static SDBConnection getConn(String dbURL, DatabaseType dbType)
	{
		SDBConnection sdbc = null;
		
		if (dbType.getName().equals("MySQL")) {
			sdbc = new SDBConnection(dbURL, Param.MYSQL_DB_USR, Param.MYSQL_DB_PWD);
		} else if (dbType.getName().equals("PostgreSQL")) {
			sdbc = new SDBConnection(dbURL, Param.PGSQL_DB_USR, Param.PGSQL_DB_PWD);
		} else System.err.println("Unknown database type?");
		
		return sdbc;
	}
	
	public synchronized static void install(String dbURL, DatabaseType dbType)
	{
		System.out.println("Please make sure the database has been created beforehand.");
		
		StoreDesc storeDesc = new StoreDesc(LayoutType.LayoutTripleNodesHash, dbType);
		
		loadDriver(dbType);
		SDBConnection sdbc = getConn(dbURL, dbType);
		if (sdbc == null) return;
		
		Store store = SDBFactory.connectStore(sdbc, storeDesc) ;
		store.getTableFormatter().create();
		sdbc.close(); // Don't forget to close
		store.close();
	}
	
	public synchronized static void load(String dbURL, DatabaseType dbType, String file, String fmat)
	{
		StoreDesc desc = new StoreDesc(LayoutType.LayoutTripleNodesHash, dbType);
		
		loadDriver(dbType);
		SDBConnection sdbc = getConn(dbURL, dbType);
		if (sdbc == null) return;
		
		Store store = SDBFactory.connectStore(sdbc, desc);
		
		Model model = SDBFactory.connectDefaultModel(store);
		model.read(file, fmat);
		model.close(); // Is it necessary to close Model
		
		sdbc.close(); // Don't forget to close
		store.close();
	}
	
	public synchronized static void truncate(String dbURL, DatabaseType dbType)
	{
		StoreDesc desc = new StoreDesc(LayoutType.LayoutTripleNodesHash, dbType);
		
		loadDriver(dbType);
		SDBConnection sdbc = getConn(dbURL, dbType);
		if (sdbc == null) return;
		
		Store store = SDBFactory.connectStore(sdbc, desc);
		store.getTableFormatter().truncate();
		
		sdbc.close(); // Don't forget to close
		store.close();
	}
	
	public synchronized static QueryExecution query(SDBConnection sdbc, DatabaseType dbType, String qstr)
	{
		StoreDesc desc = new StoreDesc(LayoutType.LayoutTripleNodesHash, dbType);
		Query query = QueryFactory.create(qstr, Syntax.syntaxARQ); // Extend SPARQL
		Store store = SDBFactory.connectStore(sdbc, desc);
		
		Dataset ds = SDBFactory.connectDataset(store);
		QueryExecution qe = QueryExecutionFactory.create(query, ds);
		
		store.close(); // Don't forget to close SDBConnection outside! 
		return qe;
	}
}

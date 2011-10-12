package cn.edu.nju.ws.camo.webservice.connect;

import java.util.*;

import cn.edu.nju.ws.camo.webservice.connect.impl.*;

import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.sdb.sql.*;
import com.hp.hpl.jena.sdb.store.*;

public class SDBConnFactory 
{
	public static final int DBP_CONN = 1;
	
	public static final int WDF_CONN = 2;
	
	public static final int LMDB_CONN = 4;
	public static final int TROP_CONN = 5;

	public static final int JMD_CONN = 8;
	public static final int JHP_CONN = 9;
	public static final int MAG_CONN = 10;
	

	private static Map<Model, SDBConnection> connMap;
	private static Map<Model, DatabaseType> typeMap;

	private static SDBConnFactory sdbcFact = null;
	
	private SDBConnFactory() 
	{ 
		connMap = new HashMap<Model, SDBConnection>(); 
		typeMap = new HashMap<Model, DatabaseType>();
	}
	
	public static synchronized SDBConnFactory getInstance()
	{
		if (sdbcFact == null)
			sdbcFact = new SDBConnFactory();
		return SDBConnFactory.sdbcFact;
	}
	
	public SDBConnection sdbConnect(int sdbcType) throws Throwable
	{
		SDBConnection conn = null;
		
		switch (sdbcType) {
			case SDBConnFactory.DBP_CONN:
				conn = new DBP_SDBConn().sdbConnect();
				break;
			case SDBConnFactory.WDF_CONN:
				conn = new WDF_SDBConn().sdbConnect();
				break;
			case SDBConnFactory.LMDB_CONN:
				conn = new LMDB_SDBConn().sdbConnect();
				break;
			case SDBConnFactory.TROP_CONN:
				conn = new TROP_SDBConn().sdbConnect();
				break;
			case SDBConnFactory.JMD_CONN:
				conn = new JMD_SDBConn().sdbConnect();
				break;
			case SDBConnFactory.MAG_CONN:
				conn = new MAG_SDBConn().sdbConnect();
				break;
			case SDBConnFactory.JHP_CONN:
				conn = new JHP_SDBConn().sdbConnect();
				break;
			default:
				break;
		}
		return conn;
	}
	
	public DatabaseType sdbConnectType(int sdbcType) throws Throwable
	{
		DatabaseType dbType = null;
		
		switch (sdbcType) {
			case SDBConnFactory.DBP_CONN:
				dbType = DatabaseType.PostgreSQL;
				break;
			default:
				dbType = DatabaseType.MySQL;
				break;
		}
		return dbType;
	}
	
	public String sdbConnectName(int sdbcType) throws Throwable
	{
		String dsName = null;
		
		switch (sdbcType) {
			case SDBConnFactory.DBP_CONN: // dbpedia36?
				dsName = "dbpedia";
				break;
			case SDBConnFactory.WDF_CONN:
				dsName = "wildlife";
				break;
			case SDBConnFactory.LMDB_CONN: // lmdb_v2?
				dsName = "lmdb";
				break;
			case SDBConnFactory.TROP_CONN:
				dsName = "tropes";
				break;
			case SDBConnFactory.JMD_CONN:
				dsName = "jamendo";
				break;
			case SDBConnFactory.MAG_CONN:
				dsName = "magnatune";
				break;
			case SDBConnFactory.JHP_CONN:
				dsName = "johnpeel";
				break;
			default:
				break;
		}
		return dsName;
	}
	
	public void shutdown(int sdbcType) throws Throwable
	{
		switch (sdbcType) {
			case SDBConnFactory.DBP_CONN:
				new DBP_SDBConn().shutdown();
				break;
			case SDBConnFactory.WDF_CONN:
				new WDF_SDBConn().shutdown();
				break;
			case SDBConnFactory.LMDB_CONN:
				new LMDB_SDBConn().shutdown();
				break;
			case SDBConnFactory.TROP_CONN:
				new TROP_SDBConn().shutdown();
				break;
			case SDBConnFactory.JMD_CONN:
				new JMD_SDBConn().shutdown();
				break;
			case SDBConnFactory.MAG_CONN:
				new MAG_SDBConn().shutdown();
				break;
			case SDBConnFactory.JHP_CONN:
				new JHP_SDBConn().shutdown();
				break;
			default:
				break;
		}
	}
	
	public void register(Model model, SDBConnection sdbc, DatabaseType dbType)
	{
		synchronized (connMap) {
			if (model != null && sdbc != null) 
				connMap.put(model, sdbc);
		}
		synchronized (typeMap) {
			if (model != null && dbType != null)
				typeMap.put(model, dbType);
		}
	}
	
	public SDBConnection getSDBConn(Model model)
	{
		synchronized (connMap) {
			return connMap.get(model);
		}
	}
	
	public DatabaseType getSDBType(Model model)
	{
		synchronized (typeMap) {
			return typeMap.get(model);
		}
	}

	public void close(Model model)
	{
		synchronized (connMap) {
			SDBConnection sdbc = connMap.get(model);
			if (sdbc != null) {
				sdbc.close();
				connMap.remove(model);
			}
		}
	}
}

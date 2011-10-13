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
	
	public static final String MUSIC = "music";
	public static final String MOVIE = "movie";
	public static final String PHOTO = "photo";
	public static final String DBP_PREFIX = "http://dbpedia.org";
	public static final String JMD_PREFIX = "http://dbtune.org/jamendo";
	public static final String MAG_PREFIX = "http://dbtune.org/magnatune";
	public static final String JHP_PREFIX = "http://dbtune.org/bbc/peel";
	public static final String LMDB_PREFIX = "http://data.linkedmdb.org";
	public static final String TROP_PREFIX = "http://dbtropes.org";
	public static final String WDF_PREFIX = "http://www.bbc.co.uk/nature";
	

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
	
	public static String getMediaType(int connType) 
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
	
	public static int getConnType(String uri) throws Throwable 
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
		
		return connType;
	}
}

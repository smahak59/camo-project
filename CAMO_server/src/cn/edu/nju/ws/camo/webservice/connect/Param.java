package cn.edu.nju.ws.camo.webservice.connect;

public class Param
{	
	// Config
	public static final String CONFIG = "./config/camo.properties";
	
	// MySQL
	public static String MYSQL_DB_DRV = null;
	public static String MYSQL_DB_USR = null;
	public static String MYSQL_DB_PWD = null;
	public static String MYSQL_DB_URL = null;
	
	// PostgreSQL
	public static String PGSQL_DB_DRV = null;
	public static String PGSQL_DB_USR = null;
	public static String PGSQL_DB_PWD = null;
	public static String PGSQL_DB_URL = null;
	
	
	public String[] DB_DRVS = { MYSQL_DB_DRV, PGSQL_DB_DRV };
	public String[] DB_USRS = { MYSQL_DB_USR, PGSQL_DB_USR };
	public String[] DB_PWDS = { MYSQL_DB_PWD, PGSQL_DB_PWD };
	public String[] DB_URLS = { MYSQL_DB_URL, PGSQL_DB_URL };
	
	// dbpedia
	public static String DBP_URL = null;
	public static String DBP_SQL = null;
	
	// jamendo
	public static String JMD_URL = null;
	public static String JMD_SQL = null;
	
	// magnatune
	public static String MAG_URL = null;
	public static String MAG_SQL = null;
	
	// johnPeel
	public static String JHP_URL = null;
	public static String JHP_SQL = null;
	
	public String[] MUSIC_URLS = { DBP_URL, JMD_URL, MAG_URL, JHP_URL };
	public String[] MUSIC_SQLS = { DBP_SQL, JMD_SQL, MAG_SQL, JHP_SQL };
	
	// lmdb
	public static String LMDB_URL = null;
	public static String LMDB_SQL = null;
	
	// tropes
	public static String TROP_URL = null;
	public static String TROP_SQL = null;
	
	public String[] MOVIE_URLS = { DBP_URL, LMDB_URL, TROP_URL };
	public String[] MOVIE_SQLS = { DBP_SQL, LMDB_SQL, TROP_SQL };
	
	// wildlife
	public static String WDF_URL = null;
	public static String WDF_SQL = null;
	
	public String[] IMAGE_URLS = { DBP_URL, WDF_URL };
	public String[] IMAGE_SQLS = { DBP_SQL, WDF_SQL };
	
	public static String FUSE_URL = null;
	public static String FUSE_SQL = null;
	
	public static String USER_URL = null;
	public static String USER_SQL = null;
	
	public static final String DBP = "DBP";
	public static final String JMD = "JMD";
	public static final String MAG = "MAG";
	public static final String JHP = "JHP";
	public static final String LMDB = "LMDB";
	public static final String TROP = "TROP";
	public static final String WDF = "WDF";
}

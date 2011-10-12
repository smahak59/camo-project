package cn.edu.nju.ws.camo.webservice.connect;

import java.io.*;
import java.util.*;

public class Config 
{
	private static Properties getProperties()
	{
		try {
			File file = new File(Param.CONFIG);
			FileInputStream fis = new FileInputStream(file);
			Properties prop = new Properties();
			prop.load(fis);
			fis.close();
			return prop;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static void initParam()
	{
		Properties prop = getProperties();
		
		setMySQLDBConn(prop);
		setPGSQLDBConn(prop);
		
		setJMD(prop);
		setMAG(prop);
		setJHP(prop);
		setLMDB(prop);
		setTROP(prop);
		setWDF(prop);
		
		setDBP(prop);
		setFUSE(prop);
		setUSER(prop);
	}
	
	public static void echoParam()
	{
		Param param = new Param();
		
		for (int i = 0; i < param.DB_DRVS.length; ++i)
			System.out.println("DB_DRV" + i + "=" + param.DB_DRVS[i]);
		for (int j = 0; j < param.DB_USRS.length; ++j)
			System.out.println("DB_USR" + j + "=" + param.DB_USRS[j]);	
		for (int k = 0; k < param.DB_PWDS.length; ++k)
			System.out.println("DB_PWD" + k + "=" + param.DB_PWDS[k]);
		for (int l = 0; l < param.DB_URLS.length; ++l)
			System.out.println("DB_URL" + l + "=" + param.DB_URLS[l]);
		
		for (int i = 0; i < param.MUSIC_URLS.length; ++i)
			System.out.println("MUSIC_URL" + i + "=" + param.MUSIC_URLS[i]);
		for (int j = 0; j < param.MUSIC_SQLS.length; ++j)
			System.out.println("MUSIC_SQL" + j + "=" + param.MUSIC_SQLS[j]);
		
		for (int i = 0; i < param.MOVIE_URLS.length; ++i)
			System.out.println("MOVIE_URL" + i + "=" + param.MOVIE_URLS[i]);
		for (int j = 0; j < param.MOVIE_SQLS.length; ++j)
			System.out.println("MOVIE_URL" + j + "=" + param.MOVIE_SQLS[j]);
		
		for (int i = 0; i < param.IMAGE_URLS.length; ++i)
			System.out.println("IMAGE_URL" + i + "=" + param.IMAGE_URLS[i]);
		for (int j = 0; j < param.IMAGE_SQLS.length; ++j)
			System.out.println("IMAGE_SQL" + j + "=" + param.IMAGE_SQLS[j]);
		
		System.out.println("FUSE_URL=" + Param.FUSE_URL);
		System.out.println("FUSE_SQL=" + Param.FUSE_SQL);
	}

	private static void setMySQLDBConn(Properties prop)
	{
		String s1 = prop.getProperty("mysql_db_drv");
		if (s1 != null && s1.trim().length() != 0)
			Param.MYSQL_DB_DRV = s1;
		
		String s2 = prop.getProperty("mysql_db_usr");
		if (s2 != null && s2.trim().length() != 0)
			Param.MYSQL_DB_USR = s2;
		
		String s3 = prop.getProperty("mysql_db_pwd");
		if (s3 != null && s3.trim().length() != 0)
			Param.MYSQL_DB_PWD = s3;
		
		String s4 = prop.getProperty("mysql_db_url");
		if (s4 != null && s4.trim().length() != 0)
			Param.MYSQL_DB_URL = s4;
	}
	
	private static void setPGSQLDBConn(Properties prop) 
	{
		String s1 = prop.getProperty("pgsql_db_drv");
		if (s1 != null && s1.trim().length() != 0)
			Param.PGSQL_DB_DRV = s1;
		
		String s2 = prop.getProperty("pgsql_db_usr");
		if (s2 != null && s2.trim().length() != 0)
			Param.PGSQL_DB_USR = s2;
		
		String s3 = prop.getProperty("pgsql_db_pwd");
		if (s3 != null && s3.trim().length() != 0)
			Param.PGSQL_DB_PWD = s3;
		
		String s4 = prop.getProperty("pgsql_db_url");
		if (s4 != null && s4.trim().length() != 0)
			Param.PGSQL_DB_URL = s4;
	}

	private static void setJMD(Properties prop)
	{
		String s1 = prop.getProperty("jmd_url");
		if (s1 != null && s1.trim().length() != 0)
			Param.JMD_URL = s1;
		
		String s2 = prop.getProperty("jmd_sql");
		if (s2 != null && s2.trim().length() != 0)
			Param.JMD_SQL = s2;
	}
	
	private static void setMAG(Properties prop)
	{
		String s1 = prop.getProperty("mag_url");
		if (s1 != null && s1.trim().length() != 0)
			Param.MAG_URL = s1;
		
		String s2 = prop.getProperty("mag_sql");
		if (s2 != null && s2.trim().length() != 0)
			Param.MAG_SQL = s2;
	}
	
	private static void setJHP(Properties prop)
	{
		String s1 = prop.getProperty("jhp_url");
		if (s1 != null && s1.trim().length() != 0)
			Param.JHP_URL = s1;
		
		String s2 = prop.getProperty("jhp_sql");
		if (s2 != null && s2.trim().length() != 0)
			Param.JHP_SQL = s2;
	}
	
	private static void setLMDB(Properties prop)
	{
		String s1 = prop.getProperty("lmdb_url");
		if (s1 != null && s1.trim().length() != 0)
			Param.LMDB_URL = s1;
		
		String s2 = prop.getProperty("lmdb_sql");
		if (s2 != null && s2.trim().length() != 0)
			Param.LMDB_SQL = s2;
	}
	
	private static void setTROP(Properties prop) 
	{
		String s1 = prop.getProperty("trop_url");
		if (s1 != null && s1.trim().length() != 0)
			Param.TROP_URL = s1;
		
		String s2 = prop.getProperty("trop_sql");
		if (s2 != null && s2.trim().length() != 0)
			Param.TROP_SQL = s2;
	}
	
	private static void setWDF(Properties prop)
	{
		String s1 = prop.getProperty("wdf_url");
		if (s1 != null && s1.trim().length() != 0)
			Param.WDF_URL = s1;
		
		String s2 = prop.getProperty("wdf_sql");
		if (s2 != null && s2.trim().length() != 0)
			Param.WDF_SQL = s2;
	}

	private static void setDBP(Properties prop)
	{
		String s1 = prop.getProperty("dbp_url");
		if (s1 != null && s1.trim().length() != 0)
			Param.DBP_URL = s1;
		
		String s2 = prop.getProperty("dbp_sql");
		if (s2 != null && s2.trim().length() != 0)
			Param.DBP_SQL = s2;
	}
	
	private static void setFUSE(Properties prop)
	{
		String s1 = prop.getProperty("fuse_url");
		if (s1 != null && s1.trim().length() != 0)
			Param.FUSE_URL = s1;
		
		String s2 = prop.getProperty("fuse_sql");
		if (s2 != null && s2.trim().length() != 0)
			Param.FUSE_SQL = s2;
	}
	
	private static void setUSER(Properties prop)
	{
		String s1 = prop.getProperty("user_url");
		if (s1 != null && s1.trim().length() != 0)
			Param.USER_URL = s1;
		
		String s2 = prop.getProperty("user_sql");
		if (s2 != null && s2.trim().length() != 0)
			Param.USER_SQL = s2;
	}
	
	public static void main(String args[])
	{
		Config.initParam();
		Config.echoParam();
	}
}

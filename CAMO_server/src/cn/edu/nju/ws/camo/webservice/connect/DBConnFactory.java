package cn.edu.nju.ws.camo.webservice.connect;

import java.sql.*;
import java.util.Date;

import cn.edu.nju.ws.camo.webservice.connect.impl.*;

public class DBConnFactory 
{
	public static final int FUSE_CONN = 16;
	public static final int MYSQL_CONN = 17;
	public static final int PGSQL_CONN = 18;
	public static final int USER_CONN = 19;
	public static final int ISTGP_CONN = 20;
	
	private DBConnFactory(){}
	
	private static DBConnFactory INSTANCE = null;
	
	public static synchronized DBConnFactory getInstance() 
	{
		if (DBConnFactory.INSTANCE == null) {
			DBConnFactory.INSTANCE = new DBConnFactory();
		}
		return DBConnFactory.INSTANCE;
	}
	
	public Connection dbConnect(int sourceFrom) throws Throwable
	{	
//		Long oldTime = new Date().getTime();
		Connection conn = null;
		switch (sourceFrom) {
			case DBConnFactory.FUSE_CONN:
				conn = new Fusion_DBConn().connect();
				break;
			case DBConnFactory.USER_CONN:
				conn = new User_DBConn().connect();
				break;
			case DBConnFactory.ISTGP_CONN:
				conn = new IstGp_DBConn().connect();
				break;
			case DBConnFactory.MYSQL_CONN:
				conn = new MySQL_DBConn().connect();
				break;
			case DBConnFactory.PGSQL_CONN:
				conn = new PostgreSQL_DBConn().connect();
				break;
			default:
				conn = null;
				break;
		}
//		System.out.print("Connection:");
//		System.out.println(new Date().getTime()-oldTime);
		return conn;
	}
}

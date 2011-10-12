package cn.edu.nju.ws.camo.webservice.connect.impl;

import java.sql.Connection;

import org.apache.commons.dbcp.BasicDataSource;

import cn.edu.nju.ws.camo.webservice.connect.AbsDBConn;
import cn.edu.nju.ws.camo.webservice.connect.Config;
import cn.edu.nju.ws.camo.webservice.connect.Param;

public class PostgreSQL_DBConn extends AbsDBConn {

private static BasicDataSource ds = null;
	
	@Override
	synchronized protected void init() 
	{
		Config.initParam();
		ds = new BasicDataSource();
		
		ds.setDriverClassName("com.mysql.jdbc.Driver");
		ds.setUrl(Param.PGSQL_DB_URL);
		ds.setUsername(Param.PGSQL_DB_USR);
		ds.setPassword(Param.PGSQL_DB_PWD);
		
		ds.setMaxActive(30);
		ds.setMinIdle(1);
		ds.setDefaultTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
		
		ds.setTestOnBorrow(true);
	}

	@Override
	synchronized public Connection connect() throws Throwable
	{
		if (ds == null)
			init();
		
		return ds.getConnection();
	}
	
	@Override
	synchronized public void shutdown() throws Throwable
	{
		if (ds != null) {
			ds.close();
			ds = null;
		}
	}
	
}

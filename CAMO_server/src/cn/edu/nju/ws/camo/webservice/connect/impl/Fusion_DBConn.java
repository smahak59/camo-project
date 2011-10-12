package cn.edu.nju.ws.camo.webservice.connect.impl;

import java.sql.Connection;

import org.apache.commons.dbcp.BasicDataSource;

import cn.edu.nju.ws.camo.webservice.connect.AbsDBConn;
import cn.edu.nju.ws.camo.webservice.connect.Param;

public class Fusion_DBConn extends AbsDBConn 
{
	private static BasicDataSource dataSrc = null;
	
	@Override
	synchronized protected void init() 
	{
		dataSrc = new BasicDataSource();
		
		dataSrc.setDriverClassName(Param.MYSQL_DB_DRV);
		dataSrc.setUrl(Param.FUSE_URL);
		dataSrc.setUsername(Param.MYSQL_DB_USR);
		dataSrc.setPassword(Param.MYSQL_DB_PWD);
		
		dataSrc.setMaxActive(30);
		dataSrc.setMinIdle(1);
		dataSrc.setDefaultTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
		
		dataSrc.setTestOnBorrow(true);
		dataSrc.setValidationQuery(Param.FUSE_SQL);
	}

	@Override
	synchronized public Connection connect() throws Throwable
	{
		if (dataSrc == null)
			init();
		return dataSrc.getConnection();
	}
	
	@Override
	synchronized public void shutdown() throws Throwable
	{
		if (dataSrc != null)
			dataSrc.close();
	}
}

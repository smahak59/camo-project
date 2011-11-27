package cn.edu.nju.ws.camo.webservice.connect.impl;

import java.sql.*;
import org.apache.commons.dbcp.*;

import cn.edu.nju.ws.camo.webservice.connect.*;

import com.hp.hpl.jena.sdb.*;
import com.hp.hpl.jena.sdb.sql.*;

public class TROP_SDBConn extends AbsDBConn
{
	private static BasicDataSource dataSrc = null;
	
	@Override
	synchronized protected void init() 
	{
		dataSrc = new BasicDataSource();
		
		dataSrc.setDriverClassName(Param.MYSQL_DB_DRV);
		dataSrc.setUrl(Param.TROP_URL);
		dataSrc.setUsername(Param.MYSQL_DB_USR);
		dataSrc.setPassword(Param.MYSQL_DB_PWD);
		
		dataSrc.setMaxActive(30);
		dataSrc.setMinIdle(3);
		dataSrc.setDefaultTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
		
		dataSrc.setTestOnBorrow(true);
		dataSrc.setValidationQuery(Param.TROP_SQL);
	}

	@Override
	synchronized public Connection connect() throws Throwable
	{
		if (dataSrc == null)
			init();
		return dataSrc.getConnection();
	}
	
	@Override
	synchronized public SDBConnection sdbConnect() throws Throwable
	{
		if (dataSrc == null)
			init();

		Connection conn = dataSrc.getConnection();
		return SDBFactory.createConnection(conn);
	}

	@Override
	synchronized public void shutdown() throws Throwable
	{
		if (dataSrc != null)
			dataSrc.close();
	}
}

package cn.edu.nju.ws.camo.webservice.connect.impl;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.dbcp.BasicDataSource;

import cn.edu.nju.ws.camo.webservice.connect.AbsDBConn;
import cn.edu.nju.ws.camo.webservice.connect.Param;

import com.hp.hpl.jena.sdb.SDBFactory;
import com.hp.hpl.jena.sdb.sql.SDBConnection;


public class Extended_SDBConn extends AbsDBConn {

	private static Map<String, BasicDataSource> dataSrcMap = null;
	private String ontoName;
	
	public Extended_SDBConn(String ontoName) {
		this.ontoName = ontoName;
		if(dataSrcMap == null)
			dataSrcMap = new HashMap<String, BasicDataSource>();
	}

	public Connection connect() throws Throwable {
		if(dataSrcMap.containsKey(ontoName)==false)
			init();
		return dataSrcMap.get(ontoName).getConnection();
	}

	protected void init() {
		BasicDataSource dataSrc = new BasicDataSource();
		
		dataSrc.setDriverClassName(Param.MYSQL_DB_DRV);
		dataSrc.setUrl(new Param().EXTENDED_URL(ontoName));
		dataSrc.setUsername(Param.MYSQL_DB_USR);
		dataSrc.setPassword(Param.MYSQL_DB_PWD);
		
		dataSrc.setMaxActive(30);
		dataSrc.setMinIdle(1);
		dataSrc.setDefaultTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
		
		dataSrc.setTestOnBorrow(true);
		dataSrc.setValidationQuery(new Param().EXTENDED_SQL(ontoName));
		
		dataSrcMap.put(ontoName, dataSrc);
	}
	
	synchronized public SDBConnection sdbConnect() throws Throwable
	{
		Connection conn = connect();
		return SDBFactory.createConnection(conn);
	}

	public void shutdown() throws Throwable {
		if(dataSrcMap.containsKey(ontoName))
			dataSrcMap.get(ontoName).close();
	}
}

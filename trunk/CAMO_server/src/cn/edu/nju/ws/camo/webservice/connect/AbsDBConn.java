package cn.edu.nju.ws.camo.webservice.connect;

import java.sql.*;
import com.hp.hpl.jena.sdb.sql.*;

public abstract class AbsDBConn 
{
	protected abstract void init();
	
	public abstract Connection connect() throws Throwable;
	
	public SDBConnection sdbConnect() throws Throwable { return null; }
	
	public abstract void shutdown() throws Throwable;
}

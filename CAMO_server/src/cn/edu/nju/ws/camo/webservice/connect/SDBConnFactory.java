package cn.edu.nju.ws.camo.webservice.connect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import cn.edu.nju.ws.camo.webservice.connect.impl.DBP_SDBConn;
import cn.edu.nju.ws.camo.webservice.connect.impl.Extended_SDBConn;
import cn.edu.nju.ws.camo.webservice.connect.impl.JHP_SDBConn;
import cn.edu.nju.ws.camo.webservice.connect.impl.JMD_SDBConn;
import cn.edu.nju.ws.camo.webservice.connect.impl.LMDB_SDBConn;
import cn.edu.nju.ws.camo.webservice.connect.impl.MAG_SDBConn;
import cn.edu.nju.ws.camo.webservice.connect.impl.TROP_SDBConn;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.sdb.sql.SDBConnection;
import com.hp.hpl.jena.sdb.store.DatabaseType;

public class SDBConnFactory {

//	public static final int DBP_CONN = 1;
//	
//	public static final int WDF_CONN = 2;
//	
//	public static final int LMDB_CONN = 4;
//	public static final int TROP_CONN = 5;
//
//	public static final int JMD_CONN = 8;
//	public static final int JHP_CONN = 9;
//	public static final int MAG_CONN = 10;
	
	public static final String MUSIC = "music";
	public static final String MOVIE = "movie";
	public static final String PHOTO = "photo";
	public static final String DBP = "dbpedia";
	

	private static Map<Model, SDBConnection> connMap;
	private static Map<Model, DatabaseType> typeMap;

	private static SDBConnFactory sdbcFact = null;
	private static OntologyDBSet ontoSet;
	
	private SDBConnFactory() throws Throwable 
	{ 
		connMap = new HashMap<Model, SDBConnection>(); 
		typeMap = new HashMap<Model, DatabaseType>();
		if (ontoSet == null)
			ontoSet = new OntologyDBSet();
	}
	
	public static synchronized SDBConnFactory getInstance() throws Throwable 
	{
		if (sdbcFact == null)
			sdbcFact = new SDBConnFactory();
		
		return SDBConnFactory.sdbcFact;
	}
	
	
	
	public SDBConnection sdbConnect(String sdbcType) throws Throwable
	{
		SDBConnection conn = null;
		if(sdbcType.equals("DBP"))
			conn = new DBP_SDBConn().sdbConnect();
		else if(sdbcType.equals("LMDB"))
			conn = new LMDB_SDBConn().sdbConnect();
		else if(sdbcType.equals("TROP"))
			conn = new TROP_SDBConn().sdbConnect();
		else if(sdbcType.equals("JMD"))
			conn = new JMD_SDBConn().sdbConnect();
		else if(sdbcType.equals("JHP"))
			conn = new JHP_SDBConn().sdbConnect();
		else if(sdbcType.equals("MAG"))
			conn = new MAG_SDBConn().sdbConnect();
		else {
			conn = new Extended_SDBConn(sdbcType).sdbConnect();
		}
		return conn;
	}
	
	public SDBConnection sdbConnect(int sdbIdx) throws Throwable {
		return sdbConnect(ontoSet.getOntoName(sdbIdx));
	}
	
	public DatabaseType sdbConnectType(String sdbcType) throws Throwable
	{
		DatabaseType dbType = null;
		if(sdbcType.equals("DBP"))
			dbType = DatabaseType.PostgreSQL;
		else 
			dbType = DatabaseType.MySQL;
		
		return dbType;
	}
	
	public DatabaseType sdbConnectType(int sdbIdx) throws Throwable
	{
		return sdbConnectType(ontoSet.getOntoName(sdbIdx));
	}
	
	public String getDBName(String ontoName) throws Throwable
	{
		String dsName = ontoSet.getDBName(ontoName);
		return dsName;
	}
	
	public int getSdbIdx(String ontoName) {
		return ontoSet.getSdbIdx(ontoName);
	}
	
	public void shutdown(String sdbcType) throws Throwable
	{
		if(sdbcType.equals("DBP"))
			new DBP_SDBConn().shutdown();
		else if(sdbcType.equals("LMDB"))
			new LMDB_SDBConn().shutdown();
		else if(sdbcType.equals("TROP"))
			new TROP_SDBConn().shutdown();
		else if(sdbcType.equals("JMD"))
			new JMD_SDBConn().shutdown();
		else if(sdbcType.equals("JHP"))
			new JHP_SDBConn().shutdown();
		else if(sdbcType.equals("MAG"))
			new MAG_SDBConn().shutdown();
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
	
	public String getOntoName(String uri) throws Throwable 
	{
		uri = uri.trim();
		String ontoName = ontoSet.getOntoNameMatchPrefix(uri);
		return ontoName;
	}
	
	public String getMediaType(String sdbcType) 
	{
		String mediaType = ontoSet.getMediaType(sdbcType);
		return mediaType;
	}
	
	public static boolean isLegalMediaType(String mediaType) {
		if(mediaType.equals(MOVIE))
			return true;
		else if(mediaType.equals(MUSIC))
			return true;
		else if(mediaType.equals(PHOTO))
			return true;
		return false;
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
	
	public boolean addNewOntoTerm(String ontoName, String dbName, String mediaType, String prefix) throws Throwable {
		boolean success = true;
		if(prefix.startsWith("http://")==false || isLegalMediaType(mediaType)==false) {
			success = false;
			return success;
		}
		Connection sourceConn = DBConnFactory.getInstance().dbConnect(DBConnFactory.FUSE_CONN);
		
		String getOntoSqlStr = "select * from onto where onto_name=? or db_name=? or prefix like ?";
		PreparedStatement getOntoStmt = sourceConn.prepareStatement(getOntoSqlStr);
		getOntoStmt.setString(1, ontoName);
		getOntoStmt.setString(2, dbName);
		getOntoStmt.setString(3, prefix+"%");
		ResultSet getOntoRs = getOntoStmt.executeQuery();
		if(getOntoRs.next()) {
			success = false;
		}
		getOntoRs.close();
		getOntoStmt.close();
		if(success == false) {
			sourceConn.close();
			return success;
		}
		
		String addNewOntoSqlStr = "insert into onto(onto_name,db_name,media_type,prefix) values(?,?,?,?)";
		PreparedStatement addNewOntoStmt = sourceConn.prepareStatement(addNewOntoSqlStr);
		addNewOntoStmt.setString(1, ontoName);
		addNewOntoStmt.setString(2, dbName);
		addNewOntoStmt.setString(3, mediaType);
		addNewOntoStmt.setString(4, prefix);
		if(addNewOntoStmt.executeUpdate()>0)
			success = true;
		else
			success = false;
		addNewOntoStmt.close();
		sourceConn.close();
		
		return success;
	}
	
	class OntologyDBSet {
		private Map<String, OntologyDB> ontos = new HashMap<String, OntologyDB>();;
		OntologyDBSet() {
			if(ontos.size()==0)
				try {
					init();
				} catch (Throwable e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		public void init() throws Throwable {
			Connection sourceConn = DBConnFactory.getInstance().dbConnect(DBConnFactory.FUSE_CONN);
			String sqlStr = "select sdb_idx,onto_name,db_name,media_type,prefix from onto";
			PreparedStatement stmt = sourceConn.prepareStatement(sqlStr);
			ResultSet rs = stmt.executeQuery();
			while(rs.next()) {
				OntologyDB newOnto = new OntologyDB(rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getInt(1));
				ontos.put(rs.getString(2), newOnto);
			}
			rs.close();
			stmt.close();
			sourceConn.close();
		}
		public String getOntoNameMatchPrefix(String uri) {
			Iterator<OntologyDB> itr = ontos.values().iterator();
			while(itr.hasNext()) {
				OntologyDB onto = itr.next();
				if(uri.startsWith(onto.getPrefix()))
					return onto.getOntoName();
			}
			return null;
		}
		public String getMediaType(String ontoName) {
			if(ontos.containsKey(ontoName)) {
				return ontos.get(ontoName).getMediaType();
			} else 
				return null;
		}
		public String getDBName(String ontoName) {
			if(ontos.containsKey(ontoName)) {
				return ontos.get(ontoName).getDbName();
			} else 
				return null;
		}
		public String getOntoName(int sdbIdx) {
			Iterator<OntologyDB> itr = ontos.values().iterator();
			while(itr.hasNext()) {
				OntologyDB onto = itr.next();
				if(onto.getSdbIdx()==sdbIdx)
					return onto.getOntoName();
			}
			return null;
		}
		public int getSdbIdx(String ontoName) {
			if(ontos.containsKey(ontoName)) {
				return ontos.get(ontoName).getSdbIdx();
			} else 
				return -1;
		}
	}
	
	class OntologyDB {
		private String ontoName;
		private String dbName;
		private String mediaType;
		private String prefix;
		private int sdbIdx;
		
		public OntologyDB(String ontoName, String dbName, String mediaType, String prefix, int sdbIdx) {
			this.ontoName = ontoName;
			this.dbName = dbName;
			this.mediaType = mediaType;
			this.prefix = prefix;
			this.sdbIdx = sdbIdx;
		}

		public String getOntoName() {
			return ontoName;
		}

		public String getDbName() {
			return dbName;
		}

		public String getMediaType() {
			return mediaType;
		}

		public String getPrefix() {
			return prefix;
		}

		public int getSdbIdx() {
			return sdbIdx;
		}
		
		@Override
		public int hashCode() {
			return this.sdbIdx;
		}
		@Override
		public boolean equals(Object obj) {
			if (obj instanceof OntologyDB) {
				if (this.sdbIdx==((OntologyDB)obj).sdbIdx) {
					return true;
				}
			}
			return false;
		}
	}
}

package cn.edu.nju.ws.camo.webservice.view;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import cn.edu.nju.ws.camo.webservice.connect.DBConnFactory;
import cn.edu.nju.ws.camo.webservice.connect.SDBConnFactory;

public class CorefFinder extends Thread
{
	private String uri = "";
	private String mediaType = "";
	private Map<String, Integer> corefs = new HashMap<String, Integer>();
	
	public CorefFinder(String uri, String mediaType) 
	{
		this.uri = uri;
		this.mediaType = mediaType;
	}
	
	@Override
	public void run() 
	{
		try {
			Connection sourceConn = DBConnFactory.getInstance().dbConnect(DBConnFactory.FUSE_CONN);
			String sqlStr = "SELECT uri, source FROM coref_inst_" + mediaType 
						  + " WHERE group_id = " 
						  + "(SELECT DISTINCT group_id FROM coref_inst_" + mediaType + " WHERE uri=?)";
			PreparedStatement stmt = sourceConn.prepareStatement(sqlStr);
			stmt.setString(1, uri);
			ResultSet rs = stmt.executeQuery();
			
			while(rs.next()) 
				corefs.put(rs.getString(1), rs.getInt(2));
			
			rs.close();
			stmt.close();
			sourceConn.close();
			
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	public Map<String, Integer> getCorefs() 
	{
		return corefs;
	}
	
	public boolean isCoref(String uri) {
		return corefs.containsKey(uri);
	}
	
	public String getDBPCoref() throws Throwable {
		Iterator<Entry<String, Integer>> itr = corefs.entrySet().iterator();
		while(itr.hasNext()) {
			Entry<String, Integer> entry = itr.next();
			if(SDBConnFactory.getInstance().getOntoName(entry.getKey()).equals("DBP"))
				return entry.getKey();
		}
		return null;
	}
	
	public String getMediaType()
	{
		return mediaType;
	}
	
	public String getURI() 
	{
		return uri;
	}
}

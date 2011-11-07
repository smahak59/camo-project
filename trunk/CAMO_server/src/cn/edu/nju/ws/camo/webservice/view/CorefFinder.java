package cn.edu.nju.ws.camo.webservice.view;

import java.sql.*;
import java.util.*;

import cn.edu.nju.ws.camo.webservice.connect.*;

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
	
	public String getMediaType()
	{
		return mediaType;
	}
	
	public String getURI() 
	{
		return uri;
	}
}

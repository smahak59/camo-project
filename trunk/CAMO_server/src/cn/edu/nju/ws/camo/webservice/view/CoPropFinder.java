package cn.edu.nju.ws.camo.webservice.view;

import java.sql.*;
import java.util.*;

import cn.edu.nju.ws.camo.webservice.connect.*;

public class CoPropFinder extends Thread 
{
	private String inst = "";
	private String prop = "";
	private String mediaType = "";
	private Map<String, List<String[]>> instPropSet; // (sourceProp, List(inst,prop))

	public CoPropFinder(String uri, String mediaType)
	{
		this.inst = uri;
		this.prop = "";
		this.mediaType = mediaType;
		this.instPropSet = new HashMap<String, List<String[]>>(); 
	}
	
	public CoPropFinder(String uri, String prop, String mediaType)
	{
		this.inst = uri;
		this.prop = prop;
		this.mediaType = mediaType;
		this.instPropSet = new HashMap<String, List<String[]>>(); 
	}

	@Override
	public void run() 
	{
		if(this.prop.equals(""))
			findCoProp1();
		else
			findCoProp2();
	}
	
	private void findCoProp1() {
		try {
			Connection sourceConn = DBConnFactory.getInstance().dbConnect(DBConnFactory.FUSE_CONN);
			String sqlStr = "SELECT prop1.uri_inst, prop1.uri_prop, prop2.uri_prop "
						  + "FROM coref_inst_prop_" + mediaType + " AS prop1 " 
						  + "JOIN(coref_inst_prop_" + mediaType + " AS prop2) " 
						  + "ON(prop1.group_id=prop2.group_id) " 
						  + "WHERE prop2.uri_inst=?";
			PreparedStatement stmt = sourceConn.prepareStatement(sqlStr);
			stmt.setString(1, inst);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				if (rs.getString(1).trim().equals(inst))
					continue;
				if (instPropSet.containsKey(rs.getString(3).trim())) {
					List<String[]> spList = instPropSet.get(rs.getString(3).trim());
					spList.add(new String[]{rs.getString(1).trim(), rs.getString(2).trim()});
					instPropSet.put(rs.getString(3).trim(), spList);
				} else {
					List<String[]> spList = new ArrayList<String[]>();
					spList.add(new String[]{rs.getString(1).trim(), rs.getString(2).trim()});
					instPropSet.put(rs.getString(3).trim(), spList);
				}
			}
			rs.close();
			stmt.close();
			sourceConn.close();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	private void findCoProp2() {
		try {
			List<String[]> spList = new ArrayList<String[]>();
			Connection sourceConn = DBConnFactory.getInstance().dbConnect(DBConnFactory.FUSE_CONN);
			String sqlStr = "SELECT prop1.uri_inst, prop1.uri_prop "
				  + "FROM coref_inst_prop_" + mediaType + " AS prop1 " 
				  + "JOIN(coref_inst_prop_" + mediaType + " AS prop2) " 
				  + "ON(prop1.group_id=prop2.group_id) " 
				  + "WHERE prop2.uri_inst=? and prop2.uri_prop=?";
			PreparedStatement stmt = sourceConn.prepareStatement(sqlStr);
			stmt.setString(1, inst);
			stmt.setString(2, prop);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				if (rs.getString(1).trim().equals(inst) && rs.getString(2).trim().equals(prop))
					continue;
				spList.add(new String[]{rs.getString(1).trim(), rs.getString(2).trim()});
			}
			instPropSet.put(prop, spList);
			rs.close();
			stmt.close();
			sourceConn.close();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	public String getInst()
	{
		return inst;
	}
	
	public String getProp() {
		return prop;
	}

	public Map<String, List<String[]>> getCorefSet() 
	{
		return instPropSet;
	}
}

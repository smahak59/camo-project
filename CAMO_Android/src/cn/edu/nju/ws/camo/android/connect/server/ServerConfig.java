package cn.edu.nju.ws.camo.android.connect.server;

import java.io.*;
import java.util.*;

public class ServerConfig 
{
	private static Properties getProperties()
	{
		try {
			File file = new File(ServerParam.CONFIG);
			FileInputStream fis = new FileInputStream(file);
			Properties prop = new Properties();
			prop.load(fis);
			fis.close();
			return prop;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static void initParam()
	{
		Properties prop = getProperties();
		
		setServer(prop);
	}
	
	private static void setServer(Properties prop)
	{
		String s1 = prop.getProperty("server_user_url");
		if (s1 != null && s1.trim().length() != 0)
			ServerParam.USER_URL = s1;
		
		String s2 = prop.getProperty("server_view_url");
		if (s2 != null && s2.trim().length() != 0)
			ServerParam.VIEW_URL = s2;
		
		String s3 = prop.getProperty("server_nspace");
		if (s3 != null && s3.trim().length() != 0)
			ServerParam.NAMESPACE = s3;
		
	}
	
	public static void main(String args[])
	{
		ServerConfig.initParam();
	}
}

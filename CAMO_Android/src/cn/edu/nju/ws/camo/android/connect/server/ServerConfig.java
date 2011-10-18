package cn.edu.nju.ws.camo.android.connect.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import cn.edu.nju.ws.camo.android.util.UtilParam;

/**
 * @author Hang Zhang
 *
 */
public class ServerConfig 
{
	private static Properties getProperties()
	{
		Properties prop = new Properties();
		try {
			InputStream in = UtilParam.assets.open("camo.properties");
			prop.load(in);
//			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
//			String line;
//			while((line = reader.readLine()) != null) {
//				String[] tokens = line.split("=", 2);
//				if(tokens.length == 2) {
//					prop.setProperty(tokens[0], tokens[1]);
//				}
//			}
			in.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		return prop;
	}
	
	public static void initParam()
	{
		Properties prop = getProperties();
		
		setServer(prop);
	}
	
	private static void setServer(Properties prop)
	{
		String s1 = prop.getProperty("service_user_url");
		if (s1 != null && s1.trim().length() != 0)
			ServerParam.USER_URL = s1;
		
		String s2 = prop.getProperty("service_view_url");
		if (s2 != null && s2.trim().length() != 0)
			ServerParam.VIEW_URL = s2;
		
		String s3 = prop.getProperty("service_nspace");
		if (s3 != null && s3.trim().length() != 0)
			ServerParam.NAMESPACE = s3;
		
	}
	
	public static void main(String args[])
	{
		ServerConfig.initParam();
	}
}

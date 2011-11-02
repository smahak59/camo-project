package cn.edu.nju.ws.camo.android.connect;

import java.io.IOException;
import java.io.InputStream;
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
			InputStream in = UtilParam.ASSETS.open("camo.properties");
			prop.load(in);
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
		
		String s3 = prop.getProperty("service_interest_gp_url");
		if (s3 != null && s3.trim().length() != 0)
			ServerParam.INTERESET_GP_URL = s3;
		
		String s4 = prop.getProperty("service_nspace");
		if (s4 != null && s4.trim().length() != 0)
			ServerParam.NAMESPACE = s4;
	}
	
	public static void main(String args[])
	{
		ServerConfig.initParam();
	}
}

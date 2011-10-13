package cn.edu.nju.ws.camo.android.util;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Hang Zhang
 *
 */
public class SetSerialization {
	
	private static final String LEVEL1 = "#:#";
	private static final String LEVEL2 = "#::#";
	private static final String LEVEL3 = "#:::#";
	private static final String LEVEL4 = "#::::#";
	private static final String LEVEL5 = "#:::::#";

	private SetSerialization(){}
	
	public static String serialize1(Collection<String> stringSet) {
		String result = "";
		for(String string : stringSet) {
			result += LEVEL1;
			result += string;
		}
		if(result.length()>0) {
			result = result.substring(LEVEL1.length());
		}
		return result;
	}
	
	public static String serialize2(Collection<String> stringSet) {
		String result = "";
		for(String string : stringSet) {
			result += LEVEL2;
			result += string;
		}
		if(result.length()>0) {
			result = result.substring(LEVEL2.length());
		}
		return result;
	}
	
	public static String serialize3(Collection<String> stringSet) {
		String result = "";
		for(String string : stringSet) {
			result += LEVEL3;
			result += string;
		}
		if(result.length()>0) {
			result = result.substring(LEVEL3.length());
		}
		return result;
	}
	
	public static String serialize4(Collection<String> stringSet) {
		String result = "";
		for(String string : stringSet) {
			result += LEVEL4;
			result += string;
		}
		if(result.length()>0) {
			result = result.substring(LEVEL4.length());
		}
		return result;
	}
	
	public static String serialize5(Collection<String> stringSet) {
		String result = "";
		for(String string : stringSet) {
			result += LEVEL5;
			result += string;
		}
		if(result.length()>0) {
			result = result.substring(LEVEL5.length());
		}
		return result;
	}
	
	public static ArrayList<String> deserialize1(String se) {
		ArrayList<String> result = new ArrayList<String>();
		String[] set = se.split(LEVEL1);
		for(String string: set) {
			result.add(string);
		}
		return result;
	}
	
	public static ArrayList<String> deserialize2(String se) {
		ArrayList<String> result = new ArrayList<String>();
		String[] set = se.split(LEVEL2);
		for(String string: set) {
			result.add(string);
		}
		return result;
	}
	
	public static ArrayList<String> deserialize3(String se) {
		ArrayList<String> result = new ArrayList<String>();
		String[] set = se.split(LEVEL3);
		for(String string: set) {
			result.add(string);
		}
		return result;
	}
	
	public static ArrayList<String> deserialize4(String se) {
		ArrayList<String> result = new ArrayList<String>();
		String[] set = se.split(LEVEL4);
		for(String string: set) {
			result.add(string);
		}
		return result;
	}
	
	public static ArrayList<String> deserialize5(String se) {
		ArrayList<String> result = new ArrayList<String>();
		String[] set = se.split(LEVEL5);
		for(String string: set) {
			result.add(string);
		}
		return result;
	}
	
	public static String rmIllegal(String str) {
		String newStr = str.replaceAll("\"", "").replaceAll("'", "");
		return newStr;
	}
}

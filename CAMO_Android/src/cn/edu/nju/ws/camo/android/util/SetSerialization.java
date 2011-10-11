package cn.edu.nju.ws.camo.android.util;

import java.util.ArrayList;
import java.util.Collection;

public class SetSerialization {
	
	private static final String LEVEL1 = ":::";
	private static final String LEVEL2 = "###";
	private static final String LEVEL3 = "#::#";

	private SetSerialization(){}
	
	public static String serialize1(Collection<String> stringSet) {
		String result = "";
		for(String string : stringSet) {
			result += LEVEL1;
			result += string;
		}
		if(result.length()>0) {
			result = result.substring(1);
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
			result = result.substring(1);
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
			result = result.substring(1);
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
}

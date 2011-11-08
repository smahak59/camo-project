package cn.edu.nju.ws.camo.webservice.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class MediaType {

	private static Set<String> movieSet = new HashSet<String>();
	private static Set<String> musicSet = new HashSet<String>();
	
	private static void initSet() {
		File file1 = new File("config/music.cls");
		BufferedReader reader1;
		try {
			reader1 = new BufferedReader(new FileReader(file1));
			String line1 = "";
			while((line1=reader1.readLine())!=null) {
				musicSet.add(line1.trim());
			}
			reader1.close();
			File file2 = new File("config/movie.cls");
			BufferedReader reader2 = new BufferedReader(new FileReader(file2));
			String line2 = "";
			while((line2=reader2.readLine())!=null) {
				movieSet.add(line2.trim());
			}
			reader2.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static boolean isMovie(String movie) {
		if(movieSet.size()==0)
			initSet();
		return movieSet.contains(movie);
	}
	
	public static boolean isMusic(String music) {
		if(musicSet.size()==0)
			initSet();
		return musicSet.contains(music);
	}
}

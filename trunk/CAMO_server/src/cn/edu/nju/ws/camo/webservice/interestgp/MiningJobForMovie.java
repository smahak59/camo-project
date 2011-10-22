package cn.edu.nju.ws.camo.webservice.interestgp;

public class MiningJobForMovie extends Thread  {
	
	private int uid;
	private String movie;
	private String artist;

	public MiningJobForMovie(int uid, String movie, String artist) {
		this.uid = uid;
		this.movie = movie;
		this.artist = artist;
	}
	
	@Override
	public void run() {
		
	}
}

package cn.edu.nju.ws.camo.webservice.interestgp;

import java.sql.Connection;
import java.sql.PreparedStatement;

import cn.edu.nju.ws.camo.webservice.connect.DBConnFactory;

public class DelJobForMovie extends Thread  {
	
	private int uid;
	private String movie;
	private String artist;

	public DelJobForMovie(int uid, String movie, String artist) {
		this.uid = uid;
		this.movie = movie;
		this.artist = artist;
	}
	
	@Override
	public void run() {
		try {
			Connection sourceConn = DBConnFactory.getInstance().dbConnect(DBConnFactory.USER_CONN);
			String sqlStr = "delete from interest_gp where (u_id1=? and media=? and artist1=? and status=1) or (u_id2=? and media=? and artist2=? and status=1)";
			PreparedStatement stmt = sourceConn.prepareStatement(sqlStr);
			stmt.setInt(1, uid);
			stmt.setString(2, movie);
			stmt.setString(3, artist);
			stmt.setInt(4, uid);
			stmt.setString(5, movie);
			stmt.setString(6, artist);
			stmt.executeUpdate();
			stmt.close();
			sourceConn.close();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
}

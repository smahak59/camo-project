package cn.edu.nju.ws.camo.webservice;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.jws.WebService;

import cn.edu.nju.ws.camo.webservice.connect.DBConnFactory;
import cn.edu.nju.ws.camo.webservice.util.SetSerialization;
import cn.edu.nju.ws.camo.webservice.view.UriInjection;

@WebService(endpointInterface="cn.edu.nju.ws.camo.webservice.InterestGroupService") 
public class InterestGroupService implements IInterestGroupService {

	public String addInterest(int uid, String media, String mediaType, String artist) {
		media = SetSerialization.rmIllegal(media);
		artist = SetSerialization.rmIllegal(artist);
		try {
			Connection sourceConn = DBConnFactory.getInstance().dbConnect(DBConnFactory.USER_CONN);
			String sqlStr = "insert into media_favor(u_id,media,media_type,artist,in_time) values(?,?,?,?,?)";
			PreparedStatement stmt = sourceConn.prepareStatement(sqlStr);
			Timestamp curTime = new Timestamp(System.currentTimeMillis());
			stmt.setInt(1, uid);
			stmt.setString(2, media);
			stmt.setString(3, mediaType);
			stmt.setString(4, artist);
			stmt.setTimestamp(5, curTime);
			stmt.executeUpdate();
			stmt.close();
			sourceConn.close();
		} catch (Throwable e) {
			e.printStackTrace();
			return "0";
		}
		return "1";
	}

	public String delInterest(int uid, String media, String artist) {
		media = SetSerialization.rmIllegal(media);
		artist = SetSerialization.rmIllegal(artist);
		try {
			Connection sourceConn = DBConnFactory.getInstance().dbConnect(DBConnFactory.USER_CONN);
			String sqlStr = "delete from media_favor where u_id=? and media=? and artist=?";
			PreparedStatement stmt = sourceConn.prepareStatement(sqlStr);
			stmt.setInt(1, uid);
			stmt.setString(2, media);
			stmt.setString(3, artist);
			stmt.executeUpdate();
			stmt.close();
			sourceConn.close();
		} catch (Throwable e) {
			e.printStackTrace();
			return "0";
		}
		return "1";
	}

	public String getFavorArtist(int uid, String media) {
		String result = "";
		List<String> artistList = new ArrayList<String>();
		Map<String, String[]> artistSet = new HashMap<String, String[]>();
		media = SetSerialization.rmIllegal(media);
		try {
			Connection sourceConn = DBConnFactory.getInstance().dbConnect(DBConnFactory.USER_CONN);
			String sqlStr = "select artist from media_favor where u_id=? and media=? order by in_time desc";
			PreparedStatement stmt = sourceConn.prepareStatement(sqlStr);
			stmt.setInt(1, uid);
			stmt.setString(2, media);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				String[] value = {"",""};
				artistSet.put(rs.getString(1), value);
			}
			rs.close();
			stmt.close();
			sourceConn.close();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		try {
			UriInjection.initLabelAndType(artistSet);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		Iterator<Entry<String, String[]>>  itr = artistSet.entrySet().iterator();
		while(itr.hasNext()) {
			Entry<String, String[]> entry = itr.next();
			List<String> termList = new ArrayList<String>();
			termList.add(entry.getKey());
			termList.add(entry.getValue()[0]);
			termList.add(entry.getValue()[1]);
			artistList.add(SetSerialization.serialize1(termList));
		}
		
		result = SetSerialization.serialize2(artistList);
		return result;
	}

}

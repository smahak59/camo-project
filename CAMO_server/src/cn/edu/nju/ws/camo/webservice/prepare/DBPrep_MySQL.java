package cn.edu.nju.ws.camo.webservice.prepare;

import java.sql.*;

import cn.edu.nju.ws.camo.webservice.connect.*;

public class DBPrep_MySQL {
	public void install() {
		String sqlStr1 = "CREATE DATABASE camo_user";
		String sqlStr2 = "CREATE DATABASE camo_istgp";
		Connection conn = null;
		try {
			conn = DBConnFactory.getInstance().dbConnect(
					DBConnFactory.MYSQL_CONN);
			Statement stmt = conn.createStatement();
			stmt.executeUpdate(sqlStr1);
			stmt.executeUpdate(sqlStr2);
			stmt.close();
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void loadUserSchema() {
		String sqlTabStr1 = "CREATE TABLE user (id int NOT NULL AUTO_INCREMENT, pwd varchar(255) NOT NULL, name varchar(255) NOT NULL, email varchar(255) NOT NULL, sex bit(1) NOT NULL,"
				+ " PRIMARY KEY (id), KEY email_idx (email), KEY name_idx (name))"
				+ " ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin";
		String sqlTabStr2 = "CREATE TABLE preference (uid int NOT NULL, inst text NOT NULL, media_type varchar(255) NOT NULL, inst_type text NOT NULL, label_name text NOT NULL, u_action int(11) NOT NULL, subscribe bit(1) DEFAULT NULL, u_time timestamp NOT NULL, KEY uid_idx (uid), KEY inst_idx (inst(200)))"
				+ " ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin";
		String sqlTabStr3 = "CREATE TABLE friends (u_id1 int NOT NULL, u_id2 int NOT NULL, f_time timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' ON UPDATE CURRENT_TIMESTAMP, KEY id1_idx (u_id1), KEY id2_idx (u_id2))"
				+ " ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin";
		String sqlTabStr4 = "CREATE TABLE friend_rq (u_from int NOT NULL, u_to int NOT NULL, status int NOT NULL, in_time timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' ON UPDATE CURRENT_TIMESTAMP, KEY from_idx (u_from), KEY to_idx (u_to))"
				+ " ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin";
		Connection conn = null;
		try {
			conn = DBConnFactory.getInstance().dbConnect(
					DBConnFactory.USER_CONN);
			Statement stmt = conn.createStatement();
			stmt.executeUpdate(sqlTabStr1);
			stmt.executeUpdate(sqlTabStr2);
			stmt.executeUpdate(sqlTabStr3);
			stmt.executeUpdate(sqlTabStr4);
			stmt.close();
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void loadIstSchema() {
		String sqlTabStr1 = "CREATE TABLE ignore_rmd_request (u_from int NOT NULL, u_to int NOT NULL, in_time timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' ON UPDATE CURRENT_TIMESTAMP,"
				+ " KEY uid1_idx (u_from), KEY uid2_idx (u_to))"
				+ " ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin";
		String sqlTabStr2 = "CREATE TABLE media_favor (r_id int NOT NULL AUTO_INCREMENT, u_id int NOT NULL, u_name varchar(100) NOT NULL, u_sex bit NOT NULL, media text NOT NULL, media_type varchar(255) DEFAULT NULL, artist text, in_time timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' ON UPDATE CURRENT_TIMESTAMP, PRIMARY KEY (r_id), KEY u_id_idx (u_id), KEY media_idx (media(200)))"
				+ " ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin";
		String sqlTabStr3 = "CREATE TABLE movie_artist_gp (movie text NOT NULL, artist1 text NOT NULL, artist2 text NOT NULL, rule int NOT NULL, KEY movie_idx (movie(200)), KEY artist1_idx (artist1(200)), KEY artist2_idx (artist2(200)))"
				+ " ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin";
		String sqlTabStr4 = "CREATE TABLE music_gp (gp_id bigint NOT NULL, music text NOT NULL, rule int NOT NULL, KEY gp_idx (gp_id), KEY music_idx (music(200)))"
				+ " ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin";
		Connection conn = null;
		try {
			conn = DBConnFactory.getInstance().dbConnect(
					DBConnFactory.ISTGP_CONN);
			Statement stmt = conn.createStatement();
			stmt.executeUpdate(sqlTabStr1);
			stmt.executeUpdate(sqlTabStr2);
			stmt.executeUpdate(sqlTabStr3);
			stmt.executeUpdate(sqlTabStr4);
			stmt.close();
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void main(String[] args) {
		Config.initParam();
		new DBPrep_MySQL().install();
		new DBPrep_MySQL().loadUserSchema();
		new DBPrep_MySQL().loadIstSchema();
	}
}

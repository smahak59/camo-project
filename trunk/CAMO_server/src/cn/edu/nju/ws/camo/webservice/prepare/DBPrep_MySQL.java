package cn.edu.nju.ws.camo.webservice.prepare;

import java.sql.*;

import cn.edu.nju.ws.camo.webservice.connect.*;

public class DBPrep_MySQL 
{
	public void install() 
	{
		String sqlStr1 = "CREATE DATABASE camo_user";
		Connection conn = null;
		try {
			conn = DBConnFactory.getInstance().dbConnect(DBConnFactory.MYSQL_CONN);
			Statement stmt = conn.createStatement();
			stmt.executeUpdate(sqlStr1);
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

	public void loadFuseSchema() 
	{
		String sqlTabStr1 = "CREATE TABLE user (id int NOT NULL AUTO_INCREMENT, name varchar(255) NOT NULL, email varchar(255) DEFAULT NULL, sex bit(1) DEFAULT NULL,"
						  + " PRIMARY KEY (id))" 
						  + " ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin";
		String sqlTabStr2 = "CREATE TABLE preference (uid int NOT NULL, inst text , media_type varchar(255) DEFAULT NULL, inst_type text, label_name text, u_action int(11) DEFAULT NULL, subscribe bit(1) DEFAULT NULL, u_time timestamp DEFAULT NULL, KEY uid_idx (uid))"
						  + " ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin";
		Connection conn = null;
		try {
			conn = DBConnFactory.getInstance().dbConnect(DBConnFactory.USER_CONN);
			Statement stmt = conn.createStatement();
			stmt.executeUpdate(sqlTabStr1);
			stmt.executeUpdate(sqlTabStr2);
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

	public static void main(String[] args)
	{
		Config.initParam();
//		new DBPrep_MySQL().install();
		new DBPrep_MySQL().loadFuseSchema();
	}
}

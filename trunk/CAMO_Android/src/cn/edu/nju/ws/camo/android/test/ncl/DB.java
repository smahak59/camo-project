package cn.edu.nju.ws.camo.android.test.ncl;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;


public class DB {
	private static final String uri = "jdbc:mysql://114.212.81.80/android";
	private static final String user="root";
	private static final String password = "mysql";

	public static void main(String args[])
	{
		try{
			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection(uri,user,password);
			System.out.println("Success!");
			Statement st=con.createStatement();
			ResultSet rs = st.executeQuery("SELECT * FROM user");
			while(rs.next())
			{
				System.out.println("ID"+"    "+rs.getInt(1));
				System.out.println("Data"+"    "+rs.getString(2));
			}
		}catch (Exception e){
			e.printStackTrace();
		}
	}
}

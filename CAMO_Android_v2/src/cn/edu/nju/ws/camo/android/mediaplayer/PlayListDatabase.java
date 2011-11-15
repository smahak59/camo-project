package cn.edu.nju.ws.camo.android.mediaplayer;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import cn.edu.nju.ws.camo.android.rdf.RdfFactory;
import cn.edu.nju.ws.camo.android.rdf.UriInstance;

public class PlayListDatabase {
	
	private Context context = null;
	
	public PlayListDatabase(Context context){
		this.context = context;
	}

//	public void create_table(){
//		DatabaseHelper dbHelper = new DatabaseHelper(context,"CAMO_db1");//"name" is the name of the table to be created!
//		SQLiteDatabase db = dbHelper.getWritableDatabase();
//		if(isNeedCreateNewTable())db.execSQL("CREATE TABLE movieList(id INT PRIMAARY KEY,uri VARCHAR(50),classType VARCHAR(20),name VARCHAR(30),mediaType VARCHAR(20) )");
//		dbHelper.close();
//	}
//	
//	private boolean isNeedCreateNewTable()
//	{
//		DatabaseHelper dbHelper = new DatabaseHelper(context,"CAMO_db1");
//		SQLiteDatabase db= dbHelper.getWritableDatabase();
//		Cursor cursor = db.query("sqlite_master", new String[]{"name"},"name=?",new String[]{"movieList"},null,null,null);
//		if(cursor.moveToNext())
//		{
//			dbHelper.close();
//			return false;
//		}
//		else
//			{
//				dbHelper.close();
//				return true;
//			}
//	}
	
	public int insert(int userID,String uri,String classType,String name,String mediaType){
		ContentValues value = new ContentValues();
		
		value.put("userID", userID);
		value.put("uri",uri);
		value.put("classType",classType);
		value.put("name",name);
		value.put("mediaType", mediaType);
		DatabaseHelper dbHelper = new DatabaseHelper(context,"CAMO_db");
		SQLiteDatabase db= dbHelper.getWritableDatabase();
		db.insert("playList", null, value);
		int id = -1;
		Cursor cursor = db.query("playList", new String[]{"id"}, "userID=? AND uri=?", new String[]{Integer.toString(userID), uri}, null, null, null);
		while(cursor.moveToNext()){
			id = cursor.getInt(cursor.getColumnIndex("id"));	
		}		
		dbHelper.close();
		
		return id;
	}
	
	public void delete(int id)
	{
		DatabaseHelper dbHelper = new DatabaseHelper(context,"CAMO_db");
		SQLiteDatabase db= dbHelper.getWritableDatabase();
		db.delete("playList", "id=?", new String[]{Integer.toString(id)});
		dbHelper.close();
	}
	
//	public void update(int u_id,String nickname,String trigger_inst1,String trigger_inst2){
//		ContentValues value = new ContentValues();
//		//value.put("u_id",u_id);
//		if(nickname!=null)value.put("nickname",nickname);
//		if(trigger_inst1!=null)value.put("trigger_inst1",trigger_inst1);
//		if(trigger_inst2!=null)value.put("trigger_inst2",trigger_inst2);
//		DatabaseHelper dbHelper = new DatabaseHelper(context,"CAMO_db");
//		SQLiteDatabase db= dbHelper.getWritableDatabase();
//		db.update("u_recommend", value, "u_id=?",new String[]{Integer.toString(u_id)});
//		dbHelper.close();
//	}
//	public void update(int u_id,String nickname,String trigger_inst1,String trigger_inst2,int rule){
//		ContentValues value = new ContentValues();
//		//value.put("u_id",u_id);
//		if(nickname!=null)value.put("nickname",nickname);
//		if(trigger_inst1!=null)value.put("trigger_inst1",trigger_inst1);
//		if(trigger_inst2!=null)value.put("trigger_inst2",trigger_inst2);
//		value.put("rule",rule);
//		DatabaseHelper dbHelper = new DatabaseHelper(context,"CAMO_db");
//		SQLiteDatabase db= dbHelper.getWritableDatabase();
//		db.update("u_recommend", value, "u_id=?",new String[]{Integer.toString(u_id)});
//		dbHelper.close();
//	}
	
//	public boolean isEmpty()
//	{
//		DatabaseHelper dbHelper = new DatabaseHelper(context,"CAMO_db1");
//		SQLiteDatabase db= dbHelper.getWritableDatabase();
//		Cursor cursor = db.query("playList", new String[]{"id","uri","classType","name","mediaType"}, "id=?", new String[]{Integer.toString(0)}, null, null, null);
//		if(cursor.moveToNext())
//			{
//				dbHelper.close();
//				return false;
//			}
//		else
//			{
//				dbHelper.close();
//				return true;
//			}
//	}
//	
	public boolean isEmpty(){
		int len= length();
		if(len == 0)return true;
		else return false;
	}
	public List<PlayListEntry> queryFromUserID(int userID){
		DatabaseHelper dbHelper = new DatabaseHelper(context,"CAMO_db");
		SQLiteDatabase db= dbHelper.getWritableDatabase();
		Cursor cursor = db.query("playList", new String[]{"id","userID","uri","classType","name","mediaType"}, "userID=?", new String[]{Integer.toString(userID)}, null, null, null);
		//UriInstance item = new UriInstance();
		String uri=null;
		String classType=null;
		String name=null;
		String mediaType=null;
		int id = 0;
		List<PlayListEntry>  list = new ArrayList<PlayListEntry>();
		while(cursor.moveToNext()){
			id = cursor.getInt(cursor.getColumnIndex("id"));
			uri = cursor.getString(cursor.getColumnIndex("uri"));
			classType = cursor.getString(cursor.getColumnIndex("classType"));
			name = cursor.getString(cursor.getColumnIndex("name"));
			mediaType = cursor.getString(cursor.getColumnIndex("mediaType"));
			RdfFactory factory = RdfFactory.getInstance();
			UriInstance item= factory.createInstance(uri,mediaType,classType,name);
			PlayListEntry entry = new PlayListEntry(item, id, userID);
			list.add(entry);
		}
		dbHelper.close();
		return list;
	}
	
	public int length(){
		DatabaseHelper dbHelper = new DatabaseHelper(context,"CAMO_db");
		SQLiteDatabase db= dbHelper.getWritableDatabase();
		Cursor cursor = db.query("playList", new String[]{"COUNT(*)"}, null,null, null, null, null);
		int length=0;
		if(cursor.moveToNext()){
			dbHelper.close();
			length= cursor.getInt(0);
		}
		//dbHelper.close();
		return length;
	}

}

package cn.edu.nju.ws.camo.android.connect.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class LocalDatabase {
	
	private Context context = null;
	
	public LocalDatabase(Context context)
	{
		this.context = context;
	}
	public void create_table(){
		DatabaseHelper dbHelper = new DatabaseHelper(context,"CAMO_db");//"name" is the name of table to be created!
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.execSQL("CREATE TABLE u_recommend(u_id INT PRIMAARY KEY,nickname VARCHAR(30),triger_inst1 TEXT,tringer_inst2 TEXT,rule INT)");
	}
	
	public void insert(int u_id,String nickname,String trigger_inst1,String trigger_inst2,int rule){
		ContentValues value = new ContentValues();
		value.put("u_id",u_id);
		value.put("nichname",nickname);
		value.put("trigger_inst1",trigger_inst1);
		value.put("trigger_inst2",trigger_inst2);
		value.put("rule",rule);
		DatabaseHelper dbHelper = new DatabaseHelper(context,"CAMO_db");
		SQLiteDatabase db= dbHelper.getWritableDatabase();
		db.insert("u_recommend", null, value);
	}
	
	public void delete(int u_id)
	{
		DatabaseHelper dbHelper = new DatabaseHelper(context,"CAMO_db");
		SQLiteDatabase db= dbHelper.getWritableDatabase();
		db.delete("u_recommend", "u_id=?", new String[]{Integer.toString(u_id)});
	}
	
	public void update(int u_id,String nickname,String trigger_inst1,String trigger_inst2){
		ContentValues value = new ContentValues();
		//value.put("u_id",u_id);
		if(nickname!=null)value.put("nichname",nickname);
		if(trigger_inst1!=null)value.put("trigger_inst1",trigger_inst1);
		if(trigger_inst2!=null)value.put("trigger_inst2",trigger_inst2);
		DatabaseHelper dbHelper = new DatabaseHelper(context,"CAMO_db");
		SQLiteDatabase db= dbHelper.getWritableDatabase();
		db.update("u_recommend", value, "u_id=?",new String[]{Integer.toString(u_id)});
	}
	public void update(int u_id,String nickname,String trigger_inst1,String trigger_inst2,int rule){
		ContentValues value = new ContentValues();
		//value.put("u_id",u_id);
		if(nickname!=null)value.put("nichname",nickname);
		if(trigger_inst1!=null)value.put("trigger_inst1",trigger_inst1);
		if(trigger_inst2!=null)value.put("trigger_inst2",trigger_inst2);
		value.put("rule",rule);
		DatabaseHelper dbHelper = new DatabaseHelper(context,"CAMO_db");
		SQLiteDatabase db= dbHelper.getWritableDatabase();
		db.update("u_recommend", value, "u_id=?",new String[]{Integer.toString(u_id)});
	}
	
	public Cursor queryFromu_id(int u_id){
		DatabaseHelper dbHelper = new DatabaseHelper(context,"CAMO_db");
		SQLiteDatabase db= dbHelper.getWritableDatabase();
		Cursor cursor = db.query("u_recommend", new String[]{"u_id","nickname","trigger_inst1","trigger_inst2","rule"}, "u_id=?", new String[]{Integer.toString(u_id)}, null, null, null);
		return cursor;
	}
	
	public Cursor queryFromNickname(String nickname){
		DatabaseHelper dbHelper = new DatabaseHelper(context,"CAMO_db");
		SQLiteDatabase db= dbHelper.getWritableDatabase();
		Cursor cursor = db.query("u_recommend", new String[]{"u_id","nickname","trigger_inst1","trigger_inst2","rule"}, "nickname=?", new String[]{nickname}, null, null, null);
		return cursor;
	}
}


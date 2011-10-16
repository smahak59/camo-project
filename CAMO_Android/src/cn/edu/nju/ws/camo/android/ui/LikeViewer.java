package cn.edu.nju.ws.camo.android.ui;

import android.app.Activity;
import android.app.TabActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.TabHost;
import cn.edu.nju.ws.camo.android.R;

public class LikeViewer extends TabActivity{
	  @Override 
	    protected void onCreate(Bundle savedInstanceState) { 
	        super.onCreate(savedInstanceState); 
	        TabHost tabHost = getTabHost(); 
	         
	        LayoutInflater.from(this).inflate(R.layout.like_viewer, tabHost.getTabContentView(), true); 
	 
	        tabHost.addTab(tabHost.newTabSpec("All") 
	                .setIndicator("All") 
	                .setContent(R.id.listView_likeViewer_All)); 
	        tabHost.addTab(tabHost.newTabSpec("Music") 
	                .setIndicator("Music") 
	                .setContent(R.id.listView_likeViewer_Music)); 
	        tabHost.addTab(tabHost.newTabSpec("Movie") 
	                .setIndicator("Movie") 
	                .setContent(R.id.listView_likeViewer_Movie)); 
	        tabHost.addTab(tabHost.newTabSpec("Photo") 
	                .setIndicator("Photo") 
	                .setContent(R.id.listView_likeViewer_Photo));
	        
	    } 
}

package cn.edu.nju.ws.camo.android.ui;

import java.util.List;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import cn.edu.nju.ws.camo.android.R;
import cn.edu.nju.ws.camo.android.operate.PreferViewOperation;
import cn.edu.nju.ws.camo.android.rdf.UriInstance;
import cn.edu.nju.ws.camo.android.util.DislikePrefer;
import cn.edu.nju.ws.camo.android.util.PreferList;
import cn.edu.nju.ws.camo.android.util.Preference;
import cn.edu.nju.ws.camo.android.util.SerKeys;
import cn.edu.nju.ws.camo.android.util.User;


public class DislikeListViewer extends TabActivity implements OnItemClickListener{
	
	private User currentUser;
	
	private List<DislikePrefer> artistPreferList;
	private List<DislikePrefer> musicPreferList;
	private List<DislikePrefer> moviePreferList;
	private List<DislikePrefer> photoPreferList;
	
	private ListView listView_artist;
	private ListView listView_music;
	private ListView listView_movie;
	private ListView listView_photo;
	  
	
    protected void onCreate(Bundle savedInstanceState) { 
        super.onCreate(savedInstanceState);
        setTitle("My Dislikes");
        initTabs();
        initUser();	        
   
        
    }
    
    protected void onStart() {
    	super.onStart();
        initPreferLists();
        initListViews(); 
    }
	  
	  
	private void initUser() {
		currentUser = ((CAMO_Application)getApplication()).getCurrentUser();		
	}

	private void initPreferLists() {
		artistPreferList = ((CAMO_Application)getApplication()).getDislikePreferList(PreferList.ARTIST);
		musicPreferList = ((CAMO_Application)getApplication()).getDislikePreferList(PreferList.MUSIC);
		moviePreferList = ((CAMO_Application)getApplication()).getDislikePreferList(PreferList.MOVIE);
		photoPreferList = ((CAMO_Application)getApplication()).getDislikePreferList(PreferList.PHOTO);
	}
	private void initTabs() {
			TabHost tabHost = getTabHost(); 
         
	        LayoutInflater.from(this).inflate(R.layout.dislike_list_viewer, tabHost.getTabContentView(), true); 
	 
	        tabHost.addTab(tabHost.newTabSpec("Artist") 
	                .setIndicator("Artist", getResources().getDrawable(R.drawable.tab_artist)) 
	                .setContent(R.id.listView_dislikeViewer_Artist)); 
	        tabHost.addTab(tabHost.newTabSpec("Music") 
	                .setIndicator("Music", getResources().getDrawable(R.drawable.tab_music)) 
	                .setContent(R.id.listView_dislikeViewer_Music)); 
	        tabHost.addTab(tabHost.newTabSpec("Movie") 
	                .setIndicator("Movie", getResources().getDrawable(R.drawable.tab_movie)) 
	                .setContent(R.id.listView_dislikeViewer_Movie)); 
	        tabHost.addTab(tabHost.newTabSpec("Photo") 
	                .setIndicator("Photo", getResources().getDrawable(R.drawable.tab_photo)) 
	                .setContent(R.id.listView_dislikeViewer_Photo));
		
	}
	private void initListViews() {
		listView_artist = (ListView) findViewById(R.id.listView_dislikeViewer_Artist);
		listView_music = (ListView) findViewById(R.id.listView_dislikeViewer_Music);
		listView_movie = (ListView) findViewById(R.id.listView_dislikeViewer_Movie);
		listView_photo = (ListView) findViewById(R.id.listView_dislikeViewer_Photo);
		
		ListViewAdapter artistAdapter = new ListViewAdapter(ListViewAdapter.ARTIST);
		ListViewAdapter musicAdapter = new ListViewAdapter(ListViewAdapter.MUSIC);
		ListViewAdapter movieAdapter = new ListViewAdapter(ListViewAdapter.MOVIE);
		ListViewAdapter photoAdapter = new ListViewAdapter(ListViewAdapter.PHOTO);		
		
		listView_artist.setAdapter(artistAdapter);
		listView_music.setAdapter(musicAdapter);
		listView_movie.setAdapter(movieAdapter);
		listView_photo.setAdapter(photoAdapter);		
		
		listView_artist.setOnItemClickListener(this);
		listView_music.setOnItemClickListener(this);
		listView_movie.setOnItemClickListener(this);
		listView_photo.setOnItemClickListener(this);
	}
	

	private class ListViewAdapter extends BaseAdapter {
		View[] itemViews;

		public final static int ARTIST = 0;
		public final static int MUSIC = 1;
		public final static int MOVIE = 2;
		public final static int PHOTO = 3;
		
		public ListViewAdapter(int category) {
			switch (category) {
			case ARTIST:
				itemViews = new View[artistPreferList.size()];
				for(int i = 0; i < artistPreferList.size(); i++) {
					itemViews[i] = makeItemView(artistPreferList.get(i));
				}
				break;
			case MUSIC:
				itemViews = new View[musicPreferList.size()];
				for(int i = 0; i < musicPreferList.size(); i++) {
					itemViews[i] = makeItemView(musicPreferList.get(i));
				}
				break;
			case MOVIE:
				itemViews = new View[moviePreferList.size()];
				for(int i = 0; i < moviePreferList.size(); i++) {
					itemViews[i] = makeItemView(moviePreferList.get(i));
				}
				break;
			case PHOTO:				
				itemViews = new View[photoPreferList.size()];
				for(int i = 0; i < photoPreferList.size(); i++) {
					itemViews[i] = makeItemView(artistPreferList.get(i));
				}
				break;
			}
		}	

		private View makeItemView(Preference preference) {
			LayoutInflater inflator = (LayoutInflater) DislikeListViewer.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View itemView = inflator.inflate(R.layout.preference_list_item, null);
			TextView textView_preferName = (TextView) itemView.findViewById(R.id.textView_preferName);
			String preferNameString = preference.getInst().getName();
			textView_preferName.setText(preferNameString);
			return itemView;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return itemViews.length;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return itemViews[position];
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub		
				return itemViews[position];
		
		}
		
	
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		int currentTab = getTabHost().getCurrentTab();
		UriInstance targetUri;
		switch (currentTab) {
		case 0:targetUri = artistPreferList.get(arg2).getInst();break;
		case 1:targetUri = musicPreferList.get(arg2).getInst();break;
		case 2:targetUri = moviePreferList.get(arg2).getInst();break;
		case 3:targetUri = photoPreferList.get(arg2).getInst();break;
		default: targetUri = null;
		}
		new RdfInstanceLoader(DislikeListViewer.this, targetUri).loadRdfInstance();		
	}
}

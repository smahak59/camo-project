package cn.edu.nju.ws.camo.android.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.edu.nju.ws.camo.android.R;
import cn.edu.nju.ws.camo.android.interestgp.MediaArtistInterest;
import cn.edu.nju.ws.camo.android.interestgp.MediaInterest;
import cn.edu.nju.ws.camo.android.mediaplayer.PlayList;
import cn.edu.nju.ws.camo.android.operate.InstViewOperation;
import cn.edu.nju.ws.camo.android.rdf.RdfFactory;
import cn.edu.nju.ws.camo.android.rdf.Triple;
import cn.edu.nju.ws.camo.android.rdf.UriInstWithNeigh;
import cn.edu.nju.ws.camo.android.rdf.UriInstance;
import cn.edu.nju.ws.camo.android.util.RmdFeedbackList;
import cn.edu.nju.ws.camo.android.util.User;


public class MediaPlayer extends Activity implements OnClickListener {
	private PlayList playList;
	private TextView textView_mediaPlayerTitle;
	private ListView listView_actorList;
	private ArrayList<UriInstance> actorList;
	private List<UriInstance> favoredActorList;
	private UriInstance currentPlaying;
	private User currentUser;
	private Button button_recommandedUser;
	private ImageButton imageButton_detailInfo;
	private ImageButton imageButton_playList;
	private ImageButton imageButton_prev;
	private ImageButton imageButton_next;
	private ImageView imageView_current;
	private RelativeLayout relativeLayout_music;
	private RelativeLayout relativeLayout_movie;
	private ImageButton imageButton_favMusic;
	private boolean isFavoredMedia;
	
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.media_player); 
        initPlayList();
        initComponents();
        initCurrentPlaying();
        
    }
    
    private void initCurrentPlaying() {
		relativeLayout_movie.setVisibility(View.GONE);
		relativeLayout_music.setVisibility(View.GONE);
		button_recommandedUser.setVisibility(View.GONE);
    	currentPlaying = playList.getCurrentPlaying();
		String mediaType = currentPlaying.getMediaType();
		textView_mediaPlayerTitle.setText(currentPlaying.getName());
		actorList.clear();

		if(mediaType.equals("music")) {
			imageView_current.setImageDrawable(getResources().getDrawable(R.drawable.music));
			if(MediaInterest.isFavoredMedia(currentUser, currentPlaying)) {
				isFavoredMedia = true;
				imageButton_favMusic.setImageDrawable(getResources().getDrawable(R.drawable.fav_on));
				getRecommandedUser();
			}
			else {
				isFavoredMedia = false;
				imageButton_favMusic.setImageDrawable(getResources().getDrawable(R.drawable.fav_off));
			}
			relativeLayout_music.setVisibility(View.VISIBLE);
		}
		else if(mediaType.equals("movie")) {
			imageView_current.setImageDrawable(getResources().getDrawable(R.drawable.movie));
			loadActorList();
			relativeLayout_movie.setVisibility(View.VISIBLE);
		}
		
	}

	private void initPlayList() {
        playList = ((CAMO_Application)getApplication()).getPlayList();
    }
    

    
    private void initComponents() {
		currentUser = ((CAMO_Application)getApplication()).getCurrentUser();    	
        actorList = new ArrayList<UriInstance>();
        textView_mediaPlayerTitle = (TextView) findViewById(R.id.textView_mediaPlayerTitle);        
        button_recommandedUser = (Button) findViewById(R.id.button_recommandedUser);
        imageButton_detailInfo = (ImageButton) findViewById(R.id.imageButton_detailInfo);
        imageButton_playList = (ImageButton) findViewById(R.id.imageButton_playList);
        imageButton_prev = (ImageButton) findViewById(R.id.imageButton_prev);
        imageButton_next = (ImageButton) findViewById(R.id.imageButton_next);
        imageView_current = (ImageView) findViewById(R.id.imageView_current);
        relativeLayout_music = (RelativeLayout) findViewById(R.id.relativeLayout_music);
        relativeLayout_movie = (RelativeLayout) findViewById(R.id.relativeLayout_movie);
        imageButton_favMusic = (ImageButton) findViewById(R.id.imageButton_favMusic);        
        this.registerForContextMenu(findViewById(R.id.imageButton_playList));
        
        imageButton_detailInfo.setOnClickListener(this);
        imageButton_playList.setOnClickListener(this);
        imageButton_prev.setOnClickListener(this);
        imageButton_next.setOnClickListener(this);
        button_recommandedUser.setOnClickListener(this);
        imageButton_favMusic.setOnClickListener(this);
        
        UriInstance currentPlayingUri1 = RdfFactory.getInstance().createInstance("http://dbpedia.org/resource/Daughters_Who_Pay", "movie");
        currentPlayingUri1.setName("Daughters Who Pay");
        UriInstance currentPlayingUri2 = RdfFactory.getInstance().createInstance("http://dbpedia.org/resource/Azzurro%23Die_Toten_Hosen_cover", "music");
        currentPlayingUri2.setName("Die Toten Hosen cover");
        UriInstance currentPlayingUri3 = RdfFactory.getInstance().createInstance("http://dbpedia.org/resource/The_Woodsman", "movie");
        currentPlayingUri3.setName("The Woodsman");


        playList.clear();
        playList.add(currentPlayingUri1);
        playList.add(currentPlayingUri2); 
        playList.add(currentPlayingUri3); 
        
	}
    
    public void onCreateContextMenu(ContextMenu menu, View v,  
            ContextMenuInfo menuInfo) {
    	menu.setHeaderTitle("Playlist");
        menu.setHeaderIcon(R.drawable.playlist_big);
        for(int i = 0; i < playList.length(); i++) {        	
        	menu.add(0, i, 0, playList.getInstance(i).getName());
        }
    }  
    
    public boolean onContextItemSelected(MenuItem item) {  
    	playList.playByIndex(item.getItemId());
    	initCurrentPlaying();
    	return true;
    }




	private void loadActorList() {
    	
    	LinearLayout linearLayout_loading = (LinearLayout)findViewById(R.id.linearLayout_loading);
    	relativeLayout_movie.setVisibility(View.GONE);
		linearLayout_loading.setVisibility(View.VISIBLE);
		
    	class LoadActorListTask extends AsyncTask <String,Void,String>{
			@Override
			protected String doInBackground(String... params) {
				try {
					UriInstWithNeigh triples = InstViewOperation.viewInstDown(currentPlaying);
					ArrayList<Triple> triplesDown = triples.getTriplesDown();
					Iterator<Triple> iter = triplesDown.iterator();
					while(iter.hasNext()) {
						Triple curTriple = iter.next();
						if(//curTriple.getPredicate().getName().equals("Actor") ||
							curTriple.getPredicate().getName().equals("Starring") &&
							!curTriple.getObject().getName().equals("")) {
							actorList.add((UriInstance) curTriple.getObject());
						}
					}
					favoredActorList = MediaArtistInterest.viewFavoredArtist(currentUser, currentPlaying);
					if(!favoredActorList.isEmpty()) {
						getRecommandedUser();
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (XmlPullParserException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
			}
			
			protected void onPostExecute(String result) {
				LinearLayout linearLayout_loading = (LinearLayout)findViewById(R.id.linearLayout_loading);
				linearLayout_loading.setVisibility(View.GONE);
				relativeLayout_movie.setVisibility(View.VISIBLE);
				initActorListView();
			} 		
    	}
    	new LoadActorListTask().execute("");
    	
    }
    
    private void initActorListView() {
    	listView_actorList = (ListView) findViewById(R.id.listView_actorList);
    	ActorListViewAdapter adapter = new ActorListViewAdapter();
    	listView_actorList.setAdapter(adapter);
    	listView_actorList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Toast.makeText(MediaPlayer.this, "clicked: " + arg2, Toast.LENGTH_SHORT).show();
				new RdfInstanceLoader(MediaPlayer.this, actorList.get(arg2)).loadRdfInstance();
			}
		});
    }
    
    private class ActorListViewAdapter extends BaseAdapter {
    	View[] itemViews;
    	ImageButton[] likeActorButtons;
    	boolean[] likeActorButtonsOn;
    	
    	public ActorListViewAdapter() {    		
    		itemViews = new View[actorList.size()];
    		likeActorButtonsOn = new boolean[actorList.size()];
    		likeActorButtons = new ImageButton[actorList.size()];
    		for(int i = 0; i < actorList.size(); i++) {
    			if(favoredActorList.contains(actorList.get(i))) {
    				likeActorButtonsOn[i] = true;
    			}
    			else {
    				likeActorButtonsOn[i] = false;
    			}
    			itemViews[i] = makeItemView(i);    			
    		}
    	}
    	
		private View makeItemView(final int position) {
			UriInstance uriInstance = actorList.get(position);
			LayoutInflater inflater = (LayoutInflater)MediaPlayer.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View item = inflater.inflate(R.layout.actor_list_item, null);
			TextView textView_actorName = (TextView) item.findViewById(R.id.textView_actorName);
			likeActorButtons[position] = (ImageButton) item.findViewById(R.id.imageButton_likeActor);
			if(likeActorButtonsOn[position]) {
				likeActorButtons[position].setImageDrawable(getResources().getDrawable(R.drawable.fav_on));
			}
			likeActorButtons[position].setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if(likeActorButtonsOn[position]) {
						likeActorButtonsOn[position] = false;
						likeActorButtons[position].setImageDrawable(getResources().getDrawable(R.drawable.fav_off));
						MediaArtistInterest mediaArtistInterest = new MediaArtistInterest(currentUser, currentPlaying, actorList.get(position));
						mediaArtistInterest.getDeleteCmd().execute();
					}
					else {
						likeActorButtonsOn[position] = true;
						likeActorButtons[position].setImageDrawable(getResources().getDrawable(R.drawable.fav_on));
						MediaArtistInterest mediaArtistInterest = new MediaArtistInterest(currentUser, currentPlaying, actorList.get(position));
						mediaArtistInterest.getCreateCmd().execute();												
					}
					getRecommandedUser();
				}
				
			});
			textView_actorName.setText(uriInstance.getName());
			return item;
		}
		
		@Override
		public int getCount() {
			return itemViews.length;
		}

		@Override
		public Object getItem(int arg0) {
			return itemViews[arg0];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			return itemViews[position];
		}
    	
    }

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.imageButton_detailInfo:			
			new RdfInstanceLoader(MediaPlayer.this, currentPlaying).loadRdfInstance();
			break;
		case R.id.imageButton_next:
			playList.next();
			initCurrentPlaying();
			break;
		case R.id.imageButton_prev:
			playList.prev();
			initCurrentPlaying();
			break;
		case R.id.button_recommandedUser:			
			Intent recommandedUserIntent = new Intent(MediaPlayer.this, RecommandedUserListViewer.class);
			startActivity(recommandedUserIntent);			
			break;
		case R.id.imageButton_favMusic:
			toggleFavorMusicButton();
			break;
		case R.id.imageButton_playList:
			imageButton_playList.showContextMenu();
			break;
		}
		
	}

	private void toggleFavorMusicButton() {
		MediaInterest mediaInterest = new MediaInterest(currentUser, currentPlaying);
		if(isFavoredMedia) {
			mediaInterest.getDeleteCmd().execute();
			imageButton_favMusic.setImageDrawable(getResources().getDrawable(R.drawable.fav_off));
			isFavoredMedia = false;
		}
		else {
			mediaInterest.getCreateCmd().execute();
			imageButton_favMusic.setImageDrawable(getResources().getDrawable(R.drawable.fav_on));
			isFavoredMedia = true;
		}
		getRecommandedUser();
	}
	
	private void getRecommandedUser() {
		button_recommandedUser.setVisibility(View.GONE);
		class getRecommandedUserTask extends AsyncTask <String, Void, String>{

			@Override
			protected String doInBackground(String... params) {
				if(params[0].equals("music"))
					((CAMO_Application)getApplication()).initRmdFeedbackList(RmdFeedbackList.MUSIC, currentPlaying);
				else if(params[0].equals("movie")) {
					((CAMO_Application)getApplication()).initRmdFeedbackList(RmdFeedbackList.MOVIE, currentPlaying);
				}
				return null;
			}
			protected void onPostExecute(String result) {
				if(((CAMO_Application)getApplication()).rmdFeedbackNotEmpty())
					button_recommandedUser.setVisibility(View.VISIBLE);
				else {
					button_recommandedUser.setVisibility(View.GONE);
				}
			}
		}		
		new getRecommandedUserTask().execute(currentPlaying.getMediaType());
	}
}

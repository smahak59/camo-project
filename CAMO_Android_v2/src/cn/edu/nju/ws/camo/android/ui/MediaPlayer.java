package cn.edu.nju.ws.camo.android.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.xmlpull.v1.XmlPullParserException;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import cn.edu.nju.ws.camo.android.R;
import cn.edu.nju.ws.camo.android.mediaplayer.PlayList;
import cn.edu.nju.ws.camo.android.rdf.InstViewManager;
import cn.edu.nju.ws.camo.android.rdf.Resource;
import cn.edu.nju.ws.camo.android.rdf.Triple;
import cn.edu.nju.ws.camo.android.rdf.UriInstWithNeigh;
import cn.edu.nju.ws.camo.android.rdf.UriInstance;
import cn.edu.nju.ws.camo.android.user.User;
import cn.edu.nju.ws.camo.android.user.interestgp.MediaArtistInterest;
import cn.edu.nju.ws.camo.android.user.interestgp.MediaInterest;
import cn.edu.nju.ws.camo.android.user.interestgp.RmdFeedback;
import cn.edu.nju.ws.camo.android.user.interestgp.RmdFeedbackList;
import cn.edu.nju.ws.camo.android.util.SerKeys;

public class MediaPlayer extends Activity implements OnClickListener {

	private TabHost tabHost;
	private ListView listView_playList;
	private ListView listView_mediaInfo;
	private ListView listView_actorList;
	private ArrayList<Triple> mediaInfo;
	private PlayListViewAdapter adapter;
	private User currentUser;
	private PlayList playList;
	private ArrayList<UriInstance> actorList;
	private List<UriInstance> favoredActorList;
	private List<RmdFeedback> rmdFeedbackList;
	private ImageView imageView_current;
	private UriInstance currentPlaying;
	private ImageButton imageButton_prev;
	private ImageButton imageButton_play;
	private ImageButton imageButton_next;
	private ImageButton imageButton_favMusic;
	private TextView textView_actorListTitle;
	private Gallery gallery_recommended;
	private RelativeLayout relativeLayout_music;
	private RelativeLayout relativeLayout_hint;
	private boolean playButtonStatus;
	private boolean isFavoredMedia;
	private boolean PNButtonsStatus;
	private ImageButton imageButton_recPrev;
	private ImageButton imageButton_recNext;
	
	private android.media.MediaPlayer mPlayer;
	private Timer mTimer;
	private TimerTask mTimerTask;
	private SeekBar seekBar;
	private boolean isChanging = false;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.media_player);
		setTitle("Media Player");
		initTabs();
		initComponents();
		initMPlayer();
		initPlayList();
		initCurrentPlaying();
		
	}
	
	public void onDestroy() {
		mPlayer.reset();
		super.onDestroy();		
	}
	private void setMusicPlayer() {
		try {
			if(currentPlaying.getName().contains("Only"))
				mPlayer.setDataSource("/mnt/sdcard/only.mp3");
			else if(currentPlaying.getName().contains("Secret"))
				mPlayer.setDataSource("/mnt/sdcard/secret.mp3");
			else if(currentPlaying.getName().contains("Good"))
				mPlayer.setDataSource("/mnt/sdcard/good.mp3");
			else
				return;				
			mPlayer.prepare();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		seekBar.setMax(mPlayer.getDuration());
	}
	
	private void initMPlayer() {
		mPlayer = new android.media.MediaPlayer();
		seekBar = (SeekBar) findViewById(R.id.seekBar1);		
		seekBar.setOnSeekBarChangeListener(new SeekBarChangeEvent());
		seekBar.setMax(mPlayer.getDuration());
        mTimer = new Timer();    
        mTimerTask = new TimerTask() {    
            @Override    
            public void run() {     
                if(isChanging==true) {   
                    return;    
                }  
                seekBar.setProgress(mPlayer.getCurrentPosition());  
            }    
        };   
        mTimer.schedule(mTimerTask, 0, 10);
	}
	
	class SeekBarChangeEvent implements SeekBar.OnSeekBarChangeListener{

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
	    	isChanging=true;
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			mPlayer.seekTo(seekBar.getProgress());
	    	isChanging=false;	
		}
		  
	  }

	private void initTabs() {
		tabHost = (TabHost) findViewById(R.id.tabHost_mediaPlayer);
		tabHost.setup();
		tabHost.addTab(tabHost.newTabSpec("MediaInfo")
				.setIndicator("Media Info").setContent(R.id.tab_mediaInfo));
		tabHost.addTab(tabHost.newTabSpec("Interest").setIndicator("Interest")
				.setContent(R.id.tab_interest));
		tabHost.addTab(tabHost.newTabSpec("PlayList").setIndicator("Play List")
				.setContent(R.id.tab_playList));
		TabWidget tabWidget = tabHost.getTabWidget();
		for (int i = 0; i < tabWidget.getChildCount(); i++) {
			TextView tv = (TextView) tabWidget.getChildAt(i).findViewById(
					android.R.id.title);
			tv.setTextSize(15);
		}
	}

	private void initComponents() {
		playButtonStatus = true;
		PNButtonsStatus = true;
		currentUser = ((CAMO_Application) getApplication()).getCurrentUser();
		actorList = new ArrayList<UriInstance>();
		mediaInfo = new ArrayList<Triple>();
		rmdFeedbackList = new ArrayList<RmdFeedback>();
		imageButton_prev = (ImageButton) findViewById(R.id.imageButton_prev);
		imageButton_next = (ImageButton) findViewById(R.id.imageButton_next);
		imageButton_play = (ImageButton) findViewById(R.id.imageButton_play);
		imageButton_favMusic = (ImageButton) findViewById(R.id.imageButton_favMusic);
		listView_actorList = (ListView) findViewById(R.id.listView_actorList);
		listView_playList = (ListView) findViewById(R.id.listView_playList);
		listView_mediaInfo = (ListView) findViewById(R.id.listView_mediaInfo);
		gallery_recommended = (Gallery) findViewById(R.id.gallery_recommended);
		relativeLayout_music = (RelativeLayout) findViewById(R.id.relativeLayout_music);
		relativeLayout_hint = (RelativeLayout) findViewById(R.id.relativeLayout_hint);
		textView_actorListTitle = (TextView) findViewById(R.id.textView_actorListTitle);
		
		imageButton_recNext = (ImageButton) findViewById(R.id.button_recNext);
		imageButton_recPrev = (ImageButton) findViewById(R.id.button_recPrev);

		imageView_current = (ImageView) findViewById(R.id.imageView_current);

		imageButton_prev.setOnClickListener(this);
		imageButton_next.setOnClickListener(this);
		imageButton_play.setOnClickListener(this);
		imageButton_favMusic.setOnClickListener(this);
		imageButton_recNext.setOnClickListener(this);
		imageButton_recPrev.setOnClickListener(this);

		imageButton_prev.setOnTouchListener(CAMO_AndroidActivity.touchListener);
		imageButton_next.setOnTouchListener(CAMO_AndroidActivity.touchListener);
		imageButton_play.setOnTouchListener(CAMO_AndroidActivity.touchListener);
		imageButton_recNext.setOnTouchListener(CAMO_AndroidActivity.touchListener);
		imageButton_recPrev.setOnTouchListener(CAMO_AndroidActivity.touchListener);
		
	}

	private void initPlayList() {
		playList = ((CAMO_Application) getApplication()).getPlayList();

		adapter = new PlayListViewAdapter();
		listView_playList.setAdapter(adapter);
		this.registerForContextMenu(listView_playList);
		listView_playList.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if(PNButtonsStatus == false)
					return;
				disablePNButtons();
				playList.playByIndex(arg2);
				adapter.setCurrentPlaying();
				initCurrentPlaying();
			}
		});
	}

	private class PlayListViewAdapter extends BaseAdapter {

		private View[] itemViews;
		private TextView[] textView_mediaName;

		public PlayListViewAdapter() {
			itemViews = new View[playList.size()];
			textView_mediaName = new TextView[playList.size()];
			for (int i = 0; i < itemViews.length; i++) {
				itemViews[i] = makeListItemView(i);
			}
			setCurrentPlaying();
		}

		private View makeListItemView(int i) {
			LayoutInflater inflater = (LayoutInflater) MediaPlayer.this
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View item = inflater.inflate(R.layout.play_list_item, null);
			textView_mediaName[i] = (TextView) item
					.findViewById(R.id.textView_mediaName);
			textView_mediaName[i].setText(playList.getInstance(i).getName());

			return item;
		}

		public void setCurrentPlaying() {
			if (playList.size() == 0) {
				return;
			}
			for (int i = 0; i < textView_mediaName.length; i++)
				textView_mediaName[i].setTextColor(0xE0FFFFFF);
			TextView current = textView_mediaName[playList
					.getCurrentPlayingIndex()];
			current.setTextColor(0xFFEF7100);
		}

		public int getCount() {
			return itemViews.length;
		}

		public Object getItem(int position) {
			return itemViews[position];
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			return itemViews[position];
		}

	}

	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		menu.add(0, 0, 0, "Delete");
	}

	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) item
				.getMenuInfo();
		int position = menuInfo.position;
		playList.remove(position);
		adapter = new PlayListViewAdapter();
		listView_playList.setAdapter(adapter);

		return true;
	}

	private void initCurrentPlaying() {
		if(playList.size() == 0)
			return;
		currentPlaying = playList.getCurrentPlaying();
		String mediaType = currentPlaying.getMediaType();
		this.setTitle(currentPlaying.getName());
		actorList.clear();
		mediaInfo.clear();
		if(rmdFeedbackList != null)
			rmdFeedbackList.clear();
		imageButton_recNext.setVisibility(View.INVISIBLE);
		imageButton_recPrev.setVisibility(View.INVISIBLE);
		gallery_recommended.setVisibility(View.INVISIBLE);
		relativeLayout_music.setVisibility(View.INVISIBLE);
		listView_actorList.setVisibility(View.INVISIBLE);
		textView_actorListTitle.setVisibility(View.INVISIBLE);
		relativeLayout_hint.setVisibility(View.VISIBLE);
		loadMediaInfo();
		
		
		
		mPlayer.seekTo(0);
		mPlayer.pause();
		mPlayer.reset();
		
		if (playButtonStatus == false) {
			imageButton_play.setImageDrawable(getResources().getDrawable(
					R.drawable.play));
			playButtonStatus = true;
		}
		
//		mPlayer.seekTo(0);
//		seekBar.setProgress(0);
//		mPlayer.reset();
		
		
		//seekBar.setMax(0);
				
		if (mediaType.equals("music")) {
			imageView_current.setImageDrawable(getResources().getDrawable(
					R.drawable.music));
			if (MediaInterest.isFavoredMedia(currentUser, currentPlaying)) {
				isFavoredMedia = true;
				imageButton_favMusic.setImageDrawable(getResources()
						.getDrawable(R.drawable.fav_on));
				getRecommendedUser();
			} else {
				isFavoredMedia = false;
				imageButton_favMusic.setImageDrawable(getResources()
						.getDrawable(R.drawable.fav_off));
			}
			relativeLayout_music.setVisibility(View.VISIBLE);
			if(true) {
				setMusicPlayer();
			}
		} else if (mediaType.equals("movie")) {
			imageView_current.setImageDrawable(getResources().getDrawable(
					R.drawable.movie));
			// relativeLayout_movie.setVisibility(View.VISIBLE);
			loadActorList();
		}
	}
	
	

	private void loadMediaInfo() {
		listView_mediaInfo.setVisibility(View.INVISIBLE);
		new LoadMediaInfoTask().execute("");
	}

	class LoadMediaInfoTask extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... params) {
			UriInstWithNeigh neigh;
			try {
				neigh = InstViewManager.viewInstDown(currentPlaying);
				if (neigh == null)
					mediaInfo = new ArrayList<Triple>();
				else
					mediaInfo = neigh.getTriplesDown();
				trimTriples(mediaInfo);
				mergeDuplicatesDown(mediaInfo);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (XmlPullParserException e) {
				e.printStackTrace();
			}
			return null;
		}

		protected void onPostExecute(String result) {
			initMediaInfoListView();
			listView_mediaInfo.setVisibility(View.VISIBLE);
			enablePNButtons();
		}

		private void mergeDuplicatesDown(ArrayList<Triple> triplesDown) {
			Map<Resource, Triple> triplesDownMap = new HashMap<Resource, Triple>();
			Iterator<Triple> iter = triplesDown.iterator();
			while (iter.hasNext()) {
				Triple curTriple = iter.next();
				if (curTriple.getPredicate().getName().equals(""))
					continue;
				if (triplesDownMap.get(curTriple.getObject()) != null) {
					Triple preTriple = triplesDownMap
							.get(curTriple.getObject());
					String predicateString = preTriple.getPredicate().getName();
					predicateString += ", "
							+ curTriple.getPredicate().getName();
					preTriple.getPredicate().setName(predicateString);
					iter.remove();
				} else {
					triplesDownMap.put(curTriple.getObject(), curTriple);
				}
			}
		}

		public void trimTriples(ArrayList<Triple> triples) {
			Iterator<Triple> iter = triples.iterator();
			while (iter.hasNext()) {
				Triple triple = iter.next();
				if (triple.getSubject().getName().equals("")
						|| triple.getObject().getName().equals("")) {// ||
					// !triple.getSubject().canShowed()||
					// ((triple.getObject() instanceof UriInstance) &&
					// !((UriInstance)triple.getObject()).canShowed())) {
					iter.remove();
				}
			}

		}
	}

	private void loadActorList() {

		LinearLayout linearLayout_loading = (LinearLayout) findViewById(R.id.linearLayout_loading);

		linearLayout_loading.setVisibility(View.VISIBLE);

		new LoadActorListTask().execute("");

	}

	private void getRecommendedUser() {
		new GetRecommendedUserTask().execute(currentPlaying.getMediaType());
	}

	class GetRecommendedUserTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			if (isCancelled())
				return null;
			if (params[0].equals("music"))
				((CAMO_Application) getApplication()).initRmdFeedbackList(
						RmdFeedbackList.MUSIC, currentPlaying);
			else if (params[0].equals("movie")) {
				((CAMO_Application) getApplication()).initRmdFeedbackList(
						RmdFeedbackList.MOVIE, currentPlaying);
			}
			return null;
		}

		protected void onPostExecute(String result) {
			rmdFeedbackList = ((CAMO_Application) getApplication())
					.getRmdFeedbackList();
			if(rmdFeedbackList != null && !rmdFeedbackList.isEmpty()) {
				initRecommendedGallery();
			}
			else {
				gallery_recommended.setVisibility(View.INVISIBLE);
				relativeLayout_hint.setVisibility(View.VISIBLE);
				imageButton_recNext.setVisibility(View.INVISIBLE);
				imageButton_recPrev.setVisibility(View.INVISIBLE);
			}
		}
	}

	private void initRecommendedGallery() {
		final GalleryAdapter adapter = new GalleryAdapter();
		gallery_recommended.setAdapter(adapter);
		gallery_recommended.setVisibility(View.VISIBLE);
		relativeLayout_hint.setVisibility(View.INVISIBLE);
		gallery_recommended.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				RmdFeedback selectedFeedback = rmdFeedbackList.get(arg2);
				Intent viewUserIntent = new Intent(MediaPlayer.this, UserInfoViewer.class);
				Bundle viewUserBundle = new Bundle();
				viewUserBundle.putSerializable(SerKeys.SER_RMD_FEEDBACK, selectedFeedback);
				viewUserIntent.putExtras(viewUserBundle);
				startActivity(viewUserIntent);
				
			}
		});
		gallery_recommended.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				if(adapter.getCount() < 2) {
					imageButton_recPrev.setVisibility(View.INVISIBLE);
					imageButton_recNext.setVisibility(View.INVISIBLE);
				}
				else if(arg2 == adapter.getCount() - 1) {
					imageButton_recPrev.setVisibility(View.VISIBLE);
					imageButton_recNext.setVisibility(View.INVISIBLE);
					
				}
				else if(arg2 == 0) {
					imageButton_recPrev.setVisibility(View.INVISIBLE);
					imageButton_recNext.setVisibility(View.VISIBLE);
					
				}
				else {
					imageButton_recPrev.setVisibility(View.VISIBLE);
					imageButton_recNext.setVisibility(View.VISIBLE);
				}
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				
			}
		
		});
		
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.menu, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.item_viewIgnoredList:
			Intent ignoredIntend = new Intent(MediaPlayer.this,
					IgnoredListViewer.class);
			startActivity(ignoredIntend);
			break;
		}
		return true;
	}

	private void initActorListView() {

		ActorListViewAdapter adapter = new ActorListViewAdapter();
		listView_actorList.setAdapter(adapter);
	}

	private class ActorListViewAdapter extends BaseAdapter {
		View[] itemViews;
		boolean[] likeActorButtonsOn;

		public ActorListViewAdapter() {
			itemViews = new View[actorList.size()];
			likeActorButtonsOn = new boolean[actorList.size()];
			for (int i = 0; i < actorList.size(); i++) {
				if (favoredActorList.contains(actorList.get(i))) {
					likeActorButtonsOn[i] = true;
				} else {
					likeActorButtonsOn[i] = false;
				}
				itemViews[i] = makeItemView(i);
			}
		}

		private View makeItemView(int position) {
			UriInstance uriInstance = actorList.get(position);
			LayoutInflater inflater = (LayoutInflater) MediaPlayer.this
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View item = inflater.inflate(R.layout.actor_list_item, null);

			TextView textView_actorName = (TextView) item
					.findViewById(R.id.textView_actorName);
			ImageButton imageButton_likeActor = (ImageButton) item
					.findViewById(R.id.imageButton_likeActor);
			ImageButton imageButton_actorDetail = (ImageButton) item
					.findViewById(R.id.imageButton_actorDetail);
			imageButton_likeActor.setTag(position);
			imageButton_actorDetail.setTag(position);
			if (likeActorButtonsOn[position]) {
				imageButton_likeActor.setImageDrawable(getResources()
						.getDrawable(R.drawable.fav_on));
			}
			imageButton_likeActor
					.setOnClickListener(new ButtonOnClickListener());
			imageButton_actorDetail
					.setOnClickListener(new ButtonOnClickListener());
			imageButton_actorDetail
					.setOnTouchListener(CAMO_AndroidActivity.touchListener);
			textView_actorName.setText(uriInstance.getName());
			return item;
		}

		class ButtonOnClickListener implements OnClickListener {
			private int position;

			public void onClick(View v) {

				ImageButton imageButton = (ImageButton) v;
				position = (Integer) imageButton.getTag();
				if (actorList.size() == 0)
					return;
				switch (imageButton.getId()) {
				case R.id.imageButton_actorDetail:
					new RdfInstanceLoader(MediaPlayer.this,
							actorList.get(position)).loadRdfInstance();
					break;
				case R.id.imageButton_likeActor:
					if (likeActorButtonsOn[position]) {
						likeActorButtonsOn[position] = false;
						imageButton.setImageDrawable(getResources()
								.getDrawable(R.drawable.fav_off));
						MediaArtistInterest mediaArtistInterest = new MediaArtistInterest(
								currentUser, currentPlaying,
								actorList.get(position));
						mediaArtistInterest.getDeleteCmd().execute();
					} else {
						likeActorButtonsOn[position] = true;
						imageButton.setImageDrawable(getResources()
								.getDrawable(R.drawable.fav_on));
						MediaArtistInterest mediaArtistInterest = new MediaArtistInterest(
								currentUser, currentPlaying,
								actorList.get(position));
						mediaArtistInterest.getCreateCmd().execute();
					}
					getRecommendedUser();
					break;
				}

			}
		};

		public int getCount() {
			return itemViews.length;
		}

		public Object getItem(int arg0) {
			return itemViews[arg0];
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			return itemViews[position];
		}

	}

	class LoadActorListTask extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... params) {
			if (isCancelled())
				return null;
			try {
				UriInstWithNeigh triples = InstViewManager
						.viewInstDown(currentPlaying);
				if (triples == null) {
					return null;
				}
				ArrayList<Triple> triplesDown = triples.getTriplesDown();
				Iterator<Triple> iter = triplesDown.iterator();
				while (iter.hasNext()) {
					Triple curTriple = iter.next();
					if (curTriple.getPredicate().getName().equals("Actor")
							|| curTriple.getPredicate().getName()
									.equals("Starring")
							&& !curTriple.getObject().getName().equals("")) {
						actorList.add((UriInstance) curTriple.getObject());
					}
				}
				trimActorList(actorList);
				favoredActorList = MediaArtistInterest.viewFavoredArtist(
						currentUser, currentPlaying);
				if (!favoredActorList.isEmpty()) {
					getRecommendedUser();
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (XmlPullParserException e) {
				e.printStackTrace();
			}
			return null;
		}

		protected void onPostExecute(String result) {
			LinearLayout linearLayout_loading = (LinearLayout) findViewById(R.id.linearLayout_loading);
			linearLayout_loading.setVisibility(View.GONE);
			initActorListView();
			listView_actorList.setVisibility(View.VISIBLE);
			textView_actorListTitle.setVisibility(View.VISIBLE);
			// relativeLayout_movie.setVisibility(View.VISIBLE);
		}
	}

	private void trimActorList(ArrayList<UriInstance> actorList) {
		Map<String, UriInstance> actorMap = new HashMap<String, UriInstance>();
		for (int i = 0; i < actorList.size(); i++) {
			UriInstance curActor = actorList.get(i);
			if (actorMap.get(curActor.getName().trim()) != null
					&& actorMap.get(curActor.getName().trim()).getUri()
							.startsWith("http://dbpedia.org")) {
				curActor = actorMap.get(curActor.getName().trim());
			}
			actorMap.put(curActor.getName().trim(), curActor);
		}
		actorList.clear();
		Set<String> nameSet = actorMap.keySet();
		Iterator<String> nameIter = nameSet.iterator();
		while (nameIter.hasNext()) {
			actorList.add(actorMap.get(nameIter.next()));
		}
	}

	private void initMediaInfoListView() {
		final MediaInfoListViewAdapter adapter = new MediaInfoListViewAdapter();
		listView_mediaInfo.setAdapter(adapter);
		listView_mediaInfo.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if (arg2 == adapter.getCount() - 1) {
					new RdfInstanceLoader(MediaPlayer.this, currentPlaying)
							.loadRdfInstance();
				}

			}
		});
	}

	private class GalleryAdapter extends BaseAdapter {
		View[] itemViews;

		public GalleryAdapter() {
			itemViews = new View[rmdFeedbackList.size()];
			for (int i = 0; i < itemViews.length; i++) {
				itemViews[i] = makeItemView(i);
			}
		}

		private View makeItemView(int i) {
			LayoutInflater inflater = (LayoutInflater) MediaPlayer.this
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View item = inflater.inflate(R.layout.gallery_item, null);
			ImageView imageView_recoPotrait = (ImageView) item
					.findViewById(R.id.imageView_reco_portrait);
			TextView textView_recoUser = (TextView) item
					.findViewById(R.id.textView_reco_user);
			TextView textView_recoRule = (TextView) item
					.findViewById(R.id.textView_reco_rule);

			String gender = rmdFeedbackList.get(i).getUserInterest().getUser()
					.getSex();
			if (gender.equals("male")) {
				imageView_recoPotrait.setImageResource(R.drawable.male_s);
			} else if (gender.equals("female")) {
				imageView_recoPotrait.setImageResource(R.drawable.female_s);
			}
			String userName = rmdFeedbackList.get(i).getUserInterest().getUser().getName();
			String rule = rmdFeedbackList.get(i).getRule().getSuggest();
			textView_recoUser.setText(userName);
			textView_recoRule.setText(rule);
			return item;
		}

		@Override
		public int getCount() {
			return itemViews.length;
		}

		@Override
		public Object getItem(int position) {
			return itemViews[position];
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

	private class MediaInfoListViewAdapter extends BaseAdapter {
		private View[] itemViews;

		public MediaInfoListViewAdapter() {
			itemViews = new View[mediaInfo.size() + 1];
			for (int i = 0; i < itemViews.length; i++) {
				itemViews[i] = makeItemViews(i);
			}
		}

		private View makeItemViews(int i) {
			LayoutInflater inflater = (LayoutInflater) MediaPlayer.this
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View item = inflater.inflate(R.layout.media_info_list_item, null);
			TextView textView_pre = (TextView) item
					.findViewById(R.id.textView_mediaInfoPredicate);
			TextView textView_obj = (TextView) item
					.findViewById(R.id.textView_mediaInfoObject);
			
			if (i == itemViews.length - 1) {
				textView_pre.setText("");
				textView_obj.setText("More Information...");
			} else {
				String preString = mediaInfo.get(i).getPredicate().getName();
				String objString = mediaInfo.get(i).getObject().getName();
				textView_pre.setText(preString);
				textView_obj.setText(objString);
			}
			return item;
		}

		@Override
		public int getCount() {
			return itemViews.length;
		}

		@Override
		public Object getItem(int position) {
			return itemViews[position];
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
		switch (v.getId()) {
		case R.id.imageButton_play:
			if (playButtonStatus) {
				imageButton_play.setImageDrawable(getResources().getDrawable(
						R.drawable.pause));
				playButtonStatus = false;
				mPlayer.start();
			} else {
				imageButton_play.setImageDrawable(getResources().getDrawable(
						R.drawable.play));
				playButtonStatus = true;
				mPlayer.pause();
			}
			break;
		case R.id.imageButton_next:
			if (playList.size() < 2 || PNButtonsStatus == false)
				break;
			disablePNButtons();
			playList.next();
			adapter.setCurrentPlaying();
			initCurrentPlaying();
			break;
		case R.id.imageButton_prev:
			if (playList.size() < 2 || PNButtonsStatus == false)
				break;
			disablePNButtons();
			playList.prev();
			adapter.setCurrentPlaying();
			initCurrentPlaying();
			break;
		case R.id.imageButton_favMusic:
			toggleFavorMusicButton();
			break;
		case R.id.button_recNext:
			int selected_n = gallery_recommended.getSelectedItemPosition();
			gallery_recommended.setSelection(selected_n + 1, true);
			break;
		case R.id.button_recPrev:
			int selected_p = gallery_recommended.getSelectedItemPosition();
			gallery_recommended.setSelection(selected_p - 1, true);			
			break;
		}
	}
	private void enablePNButtons() {
		PNButtonsStatus = true;
	}
	
	private void disablePNButtons() {
		PNButtonsStatus = false;
	}

	private void toggleFavorMusicButton() {
		MediaInterest mediaInterest = new MediaInterest(currentUser,
				currentPlaying);
		if (isFavoredMedia) {
			mediaInterest.getDeleteCmd().execute();
			imageButton_favMusic.setImageDrawable(getResources().getDrawable(
					R.drawable.fav_off));
			gallery_recommended.setVisibility(View.INVISIBLE);

			isFavoredMedia = false;
		} else {
			mediaInterest.getCreateCmd().execute();
			imageButton_favMusic.setImageDrawable(getResources().getDrawable(
					R.drawable.fav_on));
			getRecommendedUser();
			isFavoredMedia = true;
		}

	}

}

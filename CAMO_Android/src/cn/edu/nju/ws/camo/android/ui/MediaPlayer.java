package cn.edu.nju.ws.camo.android.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.edu.nju.ws.camo.android.R;
import cn.edu.nju.ws.camo.android.mediaplayer.PlayList;
import cn.edu.nju.ws.camo.android.rdf.InstViewManager;
import cn.edu.nju.ws.camo.android.rdf.RdfFactory;
import cn.edu.nju.ws.camo.android.rdf.Triple;
import cn.edu.nju.ws.camo.android.rdf.UriInstWithNeigh;
import cn.edu.nju.ws.camo.android.rdf.UriInstance;
import cn.edu.nju.ws.camo.android.user.User;
import cn.edu.nju.ws.camo.android.user.interestgp.MediaArtistInterest;
import cn.edu.nju.ws.camo.android.user.interestgp.MediaInterest;
import cn.edu.nju.ws.camo.android.user.interestgp.RmdFeedbackList;

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
	private ImageButton imageButton_play;
	private ImageButton imageButton_next;
	private ImageView imageView_current;
	private RelativeLayout relativeLayout_music;
	private RelativeLayout relativeLayout_movie;
	private ImageButton imageButton_favMusic;
	private boolean isFavoredMedia;
	private boolean playButtonStatus;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.media_player);
		initComponents();
	}

	public void onStart() {
		super.onStart();
		initPlayList();
		initCurrentPlaying();
	}

	private void initCurrentPlaying() {
		relativeLayout_movie.setVisibility(View.GONE);
		relativeLayout_music.setVisibility(View.GONE);
		button_recommandedUser.setVisibility(View.INVISIBLE);
		if (playList.size() == 0) {
			imageButton_detailInfo.setEnabled(false);
			imageButton_next.setEnabled(false);
			imageButton_prev.setEnabled(false);
			imageButton_play.setEnabled(false);
			textView_mediaPlayerTitle.setText("Media Player");
			return;
		}
		imageButton_detailInfo.setEnabled(true);
		imageButton_next.setEnabled(true);
		imageButton_prev.setEnabled(true);
		imageButton_play.setEnabled(true);
		currentPlaying = playList.getCurrentPlaying();
		String mediaType = currentPlaying.getMediaType();
		textView_mediaPlayerTitle.setText(currentPlaying.getName());
		actorList.clear();

		if (mediaType.equals("music")) {
			imageView_current.setImageDrawable(getResources().getDrawable(
					R.drawable.music));
			if (MediaInterest.isFavoredMedia(currentUser, currentPlaying)) {
				isFavoredMedia = true;
				imageButton_favMusic.setImageDrawable(getResources()
						.getDrawable(R.drawable.fav_on));
				getRecommandedUser();
			} else {
				isFavoredMedia = false;
				imageButton_favMusic.setImageDrawable(getResources()
						.getDrawable(R.drawable.fav_off));
			}
			relativeLayout_music.setVisibility(View.VISIBLE);
		} else if (mediaType.equals("movie")) {
			imageView_current.setImageDrawable(getResources().getDrawable(
					R.drawable.movie));
			relativeLayout_movie.setVisibility(View.VISIBLE);
			loadActorList();
		}

	}

	private void initPlayList() {
		playList = ((CAMO_Application) getApplication()).getPlayList();

//		UriInstance currentPlayingUri1 = RdfFactory.getInstance()
//				.createInstance(
//						"http://dbpedia.org/resource/Daughters_Who_Pay",
//						"movie");
//		currentPlayingUri1.setName("Daughters Who Pay");
//		UriInstance currentPlayingUri2 = RdfFactory
//				.getInstance()
//				.createInstance(
//						"http://dbpedia.org/resource/Azzurro%23Die_Toten_Hosen_cover",
//						"music");
//		currentPlayingUri2.setName("Die Toten Hosen cover");
//		UriInstance currentPlayingUri3 = RdfFactory.getInstance()
//				.createInstance("http://dbpedia.org/resource/The_Woodsman",
//						"movie");
//		currentPlayingUri3.setName("The Woodsman");
//
//		playList.add(currentPlayingUri1);
//		playList.add(currentPlayingUri2);
//		playList.add(currentPlayingUri3);
	}

	private void initComponents() {
		setTitle("Media Player");
		currentUser = ((CAMO_Application) getApplication()).getCurrentUser();
		actorList = new ArrayList<UriInstance>();
		playButtonStatus = true;
		textView_mediaPlayerTitle = (TextView) findViewById(R.id.textView_mediaPlayerTitle);
		button_recommandedUser = (Button) findViewById(R.id.button_recommandedUser);
		imageButton_detailInfo = (ImageButton) findViewById(R.id.imageButton_detailInfo);
		imageButton_playList = (ImageButton) findViewById(R.id.imageButton_playList);
		imageButton_prev = (ImageButton) findViewById(R.id.imageButton_prev);
		imageButton_next = (ImageButton) findViewById(R.id.imageButton_next);
		imageButton_play = (ImageButton) findViewById(R.id.imageButton_play);
		imageView_current = (ImageView) findViewById(R.id.imageView_current);
		relativeLayout_music = (RelativeLayout) findViewById(R.id.relativeLayout_music);
		relativeLayout_movie = (RelativeLayout) findViewById(R.id.relativeLayout_movie);
		imageButton_favMusic = (ImageButton) findViewById(R.id.imageButton_favMusic);

		imageButton_detailInfo
				.setOnTouchListener(CAMO_AndroidActivity.touchListener);
		imageButton_playList
				.setOnTouchListener(CAMO_AndroidActivity.touchListener);

		imageButton_detailInfo.setOnClickListener(this);
		imageButton_playList.setOnClickListener(this);
		imageButton_prev.setOnClickListener(this);
		imageButton_next.setOnClickListener(this);
		imageButton_play.setOnClickListener(this);

		imageButton_prev.setOnTouchListener(CAMO_AndroidActivity.touchListener);
		imageButton_next.setOnTouchListener(CAMO_AndroidActivity.touchListener);
		imageButton_play.setOnTouchListener(CAMO_AndroidActivity.touchListener);

		button_recommandedUser.setOnClickListener(this);
		imageButton_favMusic.setOnClickListener(this);

	}

	private void loadActorList() {

		LinearLayout linearLayout_loading = (LinearLayout) findViewById(R.id.linearLayout_loading);
		relativeLayout_movie.setVisibility(View.GONE);
		linearLayout_loading.setVisibility(View.VISIBLE);

		new LoadActorListTask().execute("");

	}

	private void initActorListView() {
		listView_actorList = (ListView) findViewById(R.id.listView_actorList);
		ActorListViewAdapter adapter = new ActorListViewAdapter();
		listView_actorList.setAdapter(adapter);
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
					getRecommandedUser();
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

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.imageButton_detailInfo:
			new RdfInstanceLoader(MediaPlayer.this, currentPlaying)
					.loadRdfInstance();
			break;
		case R.id.imageButton_play:
			if (playButtonStatus) {
				imageButton_play.setImageDrawable(getResources().getDrawable(
						R.drawable.pause));
				playButtonStatus = false;
			} else {
				imageButton_play.setImageDrawable(getResources().getDrawable(
						R.drawable.play));
				playButtonStatus = true;
			}
			break;
		case R.id.imageButton_next:
			if (playList.size() < 2)
				break;
			playList.next();
			initCurrentPlaying();
			break;
		case R.id.imageButton_prev:
			if (playList.size() < 2)
				break;
			playList.prev();
			initCurrentPlaying();
			break;
		case R.id.button_recommandedUser:
			Intent recommandedUserIntent = new Intent(MediaPlayer.this,
					RecommandedUserListViewer.class);
			startActivity(recommandedUserIntent);
			break;
		case R.id.imageButton_favMusic:
			toggleFavorMusicButton();
			break;
		case R.id.imageButton_playList:
			Intent playListIntent = new Intent(MediaPlayer.this,
					PlayListViewer.class);
			startActivity(playListIntent);
			break;
		}

	}

	private void toggleFavorMusicButton() {
		MediaInterest mediaInterest = new MediaInterest(currentUser,
				currentPlaying);
		if (isFavoredMedia) {
			mediaInterest.getDeleteCmd().execute();
			imageButton_favMusic.setImageDrawable(getResources().getDrawable(
					R.drawable.fav_off));
			button_recommandedUser.setVisibility(View.INVISIBLE);
			
			isFavoredMedia = false;
		} else {
			mediaInterest.getCreateCmd().execute();
			imageButton_favMusic.setImageDrawable(getResources().getDrawable(
					R.drawable.fav_on));
			getRecommandedUser();
			isFavoredMedia = true;
		}

	}

	private void getRecommandedUser() {
		new GetRecommandedUserTask().execute(currentPlaying.getMediaType());
	}

	class GetRecommandedUserTask extends AsyncTask<String, Void, String> {

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
			changeVisibility();
		}
	}

	private void changeVisibility() {
		if (((CAMO_Application) getApplication()).rmdFeedbackNotEmpty()) {
			button_recommandedUser.setVisibility(View.VISIBLE);
		} else {
			button_recommandedUser.setVisibility(View.INVISIBLE);
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
					getRecommandedUser();
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
			relativeLayout_movie.setVisibility(View.VISIBLE);
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

}

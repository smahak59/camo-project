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
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import cn.edu.nju.ws.camo.android.R;
import cn.edu.nju.ws.camo.android.interestgp.MediaArtistInterest;
import cn.edu.nju.ws.camo.android.operate.InstViewOperation;
import cn.edu.nju.ws.camo.android.rdf.RdfFactory;
import cn.edu.nju.ws.camo.android.rdf.Triple;
import cn.edu.nju.ws.camo.android.rdf.UriInstWithNeigh;
import cn.edu.nju.ws.camo.android.rdf.UriInstance;
import cn.edu.nju.ws.camo.android.util.RmdFeedbackList;
import cn.edu.nju.ws.camo.android.util.User;


public class MediaPlayer extends Activity {
	private TextView textView_mediaPlayerTitle;
	private ListView listView_ActorList;
	private ArrayList<UriInstance> actorList;
	private List<UriInstance> favoredActorList;
	private UriInstance currentPlaying;
	private User currentUser;
	private Button button_recommandedUser;
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.media_player);
        initComponents();
        loadActorList();
        
    }
    
    public void showRecommandedButton() {
    	button_recommandedUser.setVisibility(View.VISIBLE);
    }
    
    
    private void initComponents() {
		currentUser = ((CAMO_Application)getApplication()).getCurrentUser();    	
        actorList = new ArrayList<UriInstance>();
        currentPlaying = RdfFactory.getInstance().createInstance("http://data.linkedmdb.org/resource/film/55601", "movie");
        currentPlaying.setName("Three Idiots");
        currentPlaying.setClassType("movie");
        textView_mediaPlayerTitle = (TextView) findViewById(R.id.textView_mediaPlayerTitle);
        textView_mediaPlayerTitle.setText(currentPlaying.getName());
        button_recommandedUser = (Button) findViewById(R.id.button_recommandedUser);
        button_recommandedUser.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (((CAMO_Application)getApplication()).rmdFeedbackListIsLoaded()) {
					Intent recommandedUserIntent = new Intent(MediaPlayer.this, RecommandedUserListViewer.class);
					startActivity(recommandedUserIntent);
				}								
			}        	
        });
	}




	private void loadActorList() {
    	
    	LinearLayout linearLayout_loading=(LinearLayout)findViewById(R.id.linearLayout_loading);
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
						if(curTriple.getPredicate().getName().equals("Actor") ||
							curTriple.getPredicate().getName().equals("Starring")) {
							actorList.add((UriInstance) curTriple.getObject());
						}
					}
					favoredActorList = MediaArtistInterest.viewFavoredArtist(currentUser, currentPlaying);
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
				initActorListView();
			} 		
    	}
    	new LoadActorListTask().execute("");
    	
    }
    
    private void initActorListView() {
    	listView_ActorList = (ListView) findViewById(R.id.listView_actorList);
    	ActorListViewAdapter adapter = new ActorListViewAdapter();
    	listView_ActorList.setAdapter(adapter);
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
						((CAMO_Application)getApplication()).initRmdFeedbackList(RmdFeedbackList.MOVIE, currentPlaying);
						
					}
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
}

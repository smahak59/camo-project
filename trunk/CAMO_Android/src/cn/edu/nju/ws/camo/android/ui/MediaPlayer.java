package cn.edu.nju.ws.camo.android.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.xmlpull.v1.XmlPullParserException;

import cn.edu.nju.ws.camo.android.R;
import cn.edu.nju.ws.camo.android.operate.InstViewOperation;
import cn.edu.nju.ws.camo.android.rdf.Triple;
import cn.edu.nju.ws.camo.android.rdf.UriInstWithNeigh;
import cn.edu.nju.ws.camo.android.rdf.UriInstance;
import cn.edu.nju.ws.camo.android.rdf.RdfFactory;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;


public class MediaPlayer extends Activity {
	private ListView listView_ActorList;
	private ArrayList<UriInstance> actorList;
	private UriInstance currentPlaying;
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.media_player);
        actorList = new ArrayList<UriInstance>();
        loadActorList();
        initActorList();
    }
    
    private void loadActorList() {
    	currentPlaying = RdfFactory.getInstance().createInstance("http://dbpedia.org/resource/Shine_a_Light_%28film%29", "movie");
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
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }
    
    private void initActorList() {
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
    			itemViews[i] = makeItemView(i);
    			
    		}
    	}
    	
    	
    	
		private View makeItemView(final int position) {
			UriInstance uriInstance = actorList.get(position);
			LayoutInflater inflater = (LayoutInflater)MediaPlayer.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View item = inflater.inflate(R.layout.actor_list_item, null);
			TextView textView_actorName = (TextView) item.findViewById(R.id.textView_actorName);
			likeActorButtons[position] = (ImageButton) item.findViewById(R.id.imageButton_likeActor);
			likeActorButtons[position].setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if(likeActorButtonsOn[position]) {
						likeActorButtonsOn[position] = false;
						likeActorButtons[position].setImageDrawable(getResources().getDrawable(R.drawable.like_off));
					}
					else {
						likeActorButtonsOn[position] = true;
						likeActorButtons[position].setImageDrawable(getResources().getDrawable(R.drawable.like_on));
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
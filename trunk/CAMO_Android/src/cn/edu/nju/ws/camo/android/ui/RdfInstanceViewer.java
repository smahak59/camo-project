package cn.edu.nju.ws.camo.android.ui;
/**
 * @author Cunxin Jia
 *
 */
import java.io.IOException;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParserException;

import cn.edu.nju.ws.camo.android.R;
import cn.edu.nju.ws.camo.android.operate.CommandFactory;
import cn.edu.nju.ws.camo.android.operate.InstViewOperation;
import cn.edu.nju.ws.camo.android.rdf.Property;
import cn.edu.nju.ws.camo.android.rdf.RdfFactory;
import cn.edu.nju.ws.camo.android.rdf.Resource;
import cn.edu.nju.ws.camo.android.rdf.Triple;
import cn.edu.nju.ws.camo.android.rdf.UriInstWithNeigh;
import cn.edu.nju.ws.camo.android.rdf.UriInstance;
import cn.edu.nju.ws.camo.android.util.DislikePrefer;
import cn.edu.nju.ws.camo.android.util.LikePrefer;
import cn.edu.nju.ws.camo.android.util.User;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.Toast;

public class RdfInstanceViewer extends Activity implements OnClickListener{
	public final static String SER_URI = "SER_URI";
	public final static String SER_USER = "SER_USER";
	private User currentUser;
	private UriInstance currentUri;
	private TabHost tabHost;
	private ListView listView_Down;
	private ListView listView_Up;
	private TextView textView_UriTitle;
	private Button button_like;
	private Button button_dislike;
	private ArrayList<Triple> triplesDown;
	private ArrayList<Triple> triplesUp;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rdf_instance_viewer);
		setTitle("RDF Instance Viwer");
		Intent intent = getIntent();
		currentUri = (UriInstance) intent.getSerializableExtra(SER_URI);
		currentUser = (User) intent.getSerializableExtra(SER_USER);
		setTitle(currentUri.getUri());
		
		new LoadTask().execute("");
		
		
	}
	class LoadTask extends AsyncTask<String,Void,String> {

		@Override
		protected String doInBackground(String... params) {
			initTriplesDown();
			initTriplesUp();
			return null;
		}
		
		@Override
		protected void onPostExecute(String result) {
			initLists();
			initButtons();		
			initTabs();
		}
		
	}
	
	private void initTriplesDown() {
		try {
			UriInstWithNeigh neigh = InstViewOperation.viewInstDown(currentUri);
			triplesDown = neigh.getTriplesDown();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private void initTriplesUp() {
		UriInstWithNeigh neigh;
		try {
			neigh = InstViewOperation.viewInstUp(currentUri);
			triplesUp = neigh.getTriplesUp();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	private void initLists() {		
		listView_Down = (ListView) findViewById(R.id.listView_Down);
		listView_Up = (ListView) findViewById(R.id.listView_Up);
		ListViewAdapter downAdapter = new ListViewAdapter(ListViewAdapter.VIEW_DOWN_ADAPTER);
		ListViewAdapter upAdapter = new ListViewAdapter(ListViewAdapter.VIEW_UP_ADAPTER);
		listView_Down.setAdapter(downAdapter);
		listView_Up.setAdapter(upAdapter);
		listView_Down.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub								
				Resource selected = triplesDown.get(arg2).getObject();
				if(selected instanceof UriInstance) {
					Intent newUriIntent = new Intent(RdfInstanceViewer.this,RdfInstanceViewer.class);
					Bundle newUriBundle = new Bundle();
					newUriBundle.putSerializable(SER_URI, selected);
					newUriBundle.putSerializable(SER_USER, currentUser);
					newUriIntent.putExtras(newUriBundle);
					startActivity(newUriIntent);
				}
			}			
		});
		listView_Up.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub				
				Resource selected = triplesUp.get(arg2).getSubject();
				if(selected instanceof UriInstance) {
					Intent newUriIntent = new Intent(RdfInstanceViewer.this,RdfInstanceViewer.class);
					Bundle newUriBundle = new Bundle();
					newUriBundle.putSerializable(SER_URI, selected);
					newUriBundle.putSerializable(SER_USER, currentUser);
					newUriIntent.putExtras(newUriBundle);
					startActivity(newUriIntent);
				}
			}			
		});
		
	}
	private void initButtons() {
		button_like = (Button)findViewById(R.id.button_like);
		button_dislike = (Button)findViewById(R.id.button_dislike);
		textView_UriTitle = (TextView) findViewById(R.id.textView_uriTitle);
		textView_UriTitle.setText(currentUri.getName());
		button_like.setOnClickListener(this);
		button_dislike.setOnClickListener(this);
	}
	private void initTabs() {
		tabHost = (TabHost) findViewById(R.id.tab_host);
		tabHost.setup();
		//View Down Tab
		TabSpec tabSpec = tabHost.newTabSpec("tab_viewDown");
		tabSpec.setContent(R.id.listView_Down);
		tabSpec.setIndicator("Attributes");
		tabHost.addTab(tabSpec);
		//View Up Tab
		tabSpec = tabHost.newTabSpec("tab_viewUp");
		tabSpec.setContent(R.id.listView_Up);
		tabSpec.setIndicator("Media Info");
		tabHost.addTab(tabSpec);
		
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()) {
		case R.id.button_dislike:
			button_dislike.setClickable(false);
			button_dislike.setText("Disliked");
			dislikeCurrentInstance();
			Toast.makeText(RdfInstanceViewer.this, "Dislike!", Toast.LENGTH_SHORT).show();
			break;
		case R.id.button_like:
			button_like.setClickable(false);
			button_like.setText("Liked");
			likeCurrentInstance();
			Toast.makeText(RdfInstanceViewer.this, "Like!", Toast.LENGTH_SHORT).show();
			break;
		}
	}
	
	private void likeCurrentInstance() {
		CommandFactory.getInstance().createLikeCmd(new LikePrefer(currentUser, currentUri)).execute();		
	}

	private void dislikeCurrentInstance() {
		CommandFactory.getInstance().createDislikeCmd(new DislikePrefer(currentUser, currentUri)).execute();
		
	}

	private class ListViewAdapter extends BaseAdapter {
		final static int VIEW_DOWN_ADAPTER = 1;
		final static int VIEW_UP_ADAPTER = 2;
		View[] itemViews;
		
		public ListViewAdapter(int adapterType) {
			switch(adapterType) {
			case VIEW_DOWN_ADAPTER:
				initViewDownAdapter();
				break;
			case VIEW_UP_ADAPTER:
				initViewUpAdapter();
				break;
			}
		}
		
		private void initViewUpAdapter() {						
			itemViews = new View[triplesUp.size()];
			for(int i = 0; i < triplesUp.size(); i++) {
				itemViews[i] = makeItemViewUp(triplesUp.get(i));
			}	
			
		}
		
		private void initViewDownAdapter() {				
			itemViews = new View[triplesDown.size()];
			for(int i = 0; i < triplesDown.size(); i++) {
				itemViews[i] = makeItemViewDown(triplesDown.get(i));
			}		
		}

		private View makeItemViewDown(Triple triple) {
			LayoutInflater inflater = (LayoutInflater)RdfInstanceViewer.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View itemView = inflater.inflate(R.layout.list_item, null);			
			String subjectString = triple.getSubject().getUri();
			String predicateString = triple.getPredicate().getName();
			String objectString = triple.getObject().getName();			
			TextView textView_predicate = (TextView) itemView.findViewById(R.id.textView_predicate);
			TextView textView_object = (TextView) itemView.findViewById(R.id.textView_object);			
			textView_predicate.setText(predicateString);
			textView_object.setText(objectString);			
			return itemView;
		}
		
		private View makeItemViewUp(Triple triple) {
			LayoutInflater inflater = (LayoutInflater)RdfInstanceViewer.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View itemView = inflater.inflate(R.layout.list_item, null);			
			String subjectString = triple.getSubject().getName();
			String predicateString = triple.getPredicate().getName();//triple.getPredicate().getUri();
			String objectString = triple.getObject().getName();			
			TextView textView_predicate = (TextView) itemView.findViewById(R.id.textView_predicate);
			TextView textView_object = (TextView) itemView.findViewById(R.id.textView_object);			
			textView_predicate.setText(predicateString);
			textView_object.setText(subjectString);			
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
	
	public static class SampleTriples {
		public static ArrayList<Triple> getSampleTriplesV1() {
			ArrayList<Triple> triples = new ArrayList<Triple>();
			for(int i = 0; i < 5; i++) {
				UriInstance subject = RdfFactory.getInstance().createInstance("subject" + i, "");
				Property predicate = RdfFactory.getInstance().createProperty(pred[i], "");
				Resource object = RdfFactory.getInstance().createInstance("object" + i, "", "", obj[i]);				
				Triple triple = RdfFactory.getInstance().createTriple(subject, predicate, object);
				triples.add(triple);
			}
			return triples;
		}
		public static ArrayList<Triple> getSampleTriplesV2() {
			ArrayList<Triple> triples = new ArrayList<Triple>();
			for(int i = 0; i < 5; i++) {
				UriInstance subject = RdfFactory.getInstance().createInstance("subject", "");
				Property predicate = RdfFactory.getInstance().createProperty(pred[9 - i], "");
				Resource object = RdfFactory.getInstance().createInstance("object", "", "", obj[9 - i]);				
				Triple triple = RdfFactory.getInstance().createTriple(subject, predicate, object);
				triples.add(triple);
			}
			return triples;
		}
		public final static String[] pred = {
									"http://www.w3.org/1999/02/22-rdf-syntax-ns#type",
									"http://dbpedia.org/ontology/genre",
									"http://dbpedia.org/ontology/birthPlace",
									"http://dbpedia.org/ontology/activeYearsStartYear",
									"http://dbpedia.org/ontology/birthDate",
									"http://dbpedia.org/ontology/artist",
									"http://dbpedia.org/ontology/artist",
									"http://dbpedia.org/ontology/artist",
									"http://dbpedia.org/ontology/starring",
									"http://dbpedia.org/ontology/writer"
									
		};
		public final static String[] obj = {
									"http://dbpedia.org/ontology/MusicalArtist",
									"http://dbpedia.org/resource/Soul_music",
									"http://dbpedia.org/resource/Staten_Island",
									"1998^^http://www.w3.org/2001/XMLSchema#gYear",
									"1980-12-18^^http://www.w3.org/2001/XMLSchema#date",
									"http://dbpedia.org/resource/Mi_Reflejo",
									"http://dbpedia.org/resource/Just_Be_Free",
									"http://dbpedia.org/resource/Stripped_Live_in_the_U.K.",
									"http://dbpedia.org/resource/Shine_a_Light_%28film%29",
									"http://dbpedia.org/resource/Glam_%28song%29"
		};
	}
}

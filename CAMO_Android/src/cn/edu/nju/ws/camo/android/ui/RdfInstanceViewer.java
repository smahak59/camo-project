package cn.edu.nju.ws.camo.android.ui;
/**
 * @author Cunxin Jia
 *
 */
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.Toast;
import cn.edu.nju.ws.camo.android.R;
import cn.edu.nju.ws.camo.android.operate.command.CommandFactory;
import cn.edu.nju.ws.camo.android.rdf.Property;
import cn.edu.nju.ws.camo.android.rdf.RdfFactory;
import cn.edu.nju.ws.camo.android.rdf.Resource;
import cn.edu.nju.ws.camo.android.rdf.Triple;
import cn.edu.nju.ws.camo.android.rdf.UriInstance;
import cn.edu.nju.ws.camo.android.util.DislikePrefer;
import cn.edu.nju.ws.camo.android.util.LikePrefer;
import cn.edu.nju.ws.camo.android.util.PreferList;
import cn.edu.nju.ws.camo.android.util.SerKeys;
import cn.edu.nju.ws.camo.android.util.User;

public class RdfInstanceViewer extends Activity implements OnClickListener{
	private User currentUser;
	private UriInstance currentUri;
	private TabHost tabHost;
	private ListView listView_Down;
	private ListView listView_Up;
	private TextView textView_UriTitle;
	private ImageButton button_like;
	private ImageButton button_dislike;
	private boolean likeButtonStatus = false;
	private boolean dislikeButtonStatus = false;
	private ArrayList<Triple> triplesDown;
	private ArrayList<Triple> triplesUp;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rdf_instance_viewer);
		setTitle("RDF Instance Viwer");
		Intent intent = getIntent();
		currentUri = (UriInstance) intent.getSerializableExtra(SerKeys.SER_URI);
		triplesDown = (ArrayList<Triple>) intent.getSerializableExtra(SerKeys.SER_TRIPLES_DOWN);
		triplesUp = (ArrayList<Triple>) intent.getSerializableExtra(SerKeys.SER_TRIPLES_UP);
		currentUser = ((CAMO_Application)getApplication()).getCurrentUser();
		setTitle("View " + currentUri.getMediaType());
		
		//new LoadTask().execute("");
		
		initLists();
		initButtons();		
		initTabs();
		
	}
	/*
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
			for(int i = 0; i < triplesDown.size(); i++) {
				Resource obj = triplesDown.get(i).getObject();
				if(obj instanceof UriInstance) {
					if(!((UriInstance) obj).canShowed()) {
						triplesDown.remove(i);
					}
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
	private void initTriplesUp() {
		UriInstWithNeigh neigh;
		try {
			neigh = InstViewOperation.viewInstUp(currentUri);
			triplesUp = neigh.getTriplesUp();
			for(int i = 0; i < triplesUp.size(); i++) {
				UriInstance inst = triplesUp.get(i).getSubject();
				if(!inst.canShowed()) {
					triplesUp.remove(i);
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
	*/
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
				if(selected instanceof UriInstance && ((UriInstance) selected).canShowed()) {
					new RdfInstanceLoader(RdfInstanceViewer.this, (UriInstance) selected).loadRdfInstance();
				}
				
				else if (triplesDown.get(arg2).getPredicate().getName().equals("Homepage") || 
						triplesDown.get(arg2).getPredicate().getName().equals("Photo Collection") ||
						triplesDown.get(arg2).getPredicate().getName().equals("Page")) {
					Uri uri = Uri.parse(selected.getName()); 
					Intent homepageIntent = new Intent(Intent.ACTION_VIEW, uri);
					startActivity(homepageIntent);
				}
				
			}			
		});
		listView_Up.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub				
				UriInstance selected = triplesUp.get(arg2).getSubject();
				if(selected.canShowed()) {
					new RdfInstanceLoader(RdfInstanceViewer.this, (UriInstance) selected).loadRdfInstance();
				}
			}			
		});
		
	}
	
	private void initButtons() {
		button_like = (ImageButton)findViewById(R.id.button_like);
		button_dislike = (ImageButton)findViewById(R.id.button_dislike);
		int signedType = ((CAMO_Application)getApplication()).getSignedType(currentUri);
		switch(signedType) {
		case PreferList.LIKED:
			likeButtonStatus = true;
			button_like.setImageDrawable(getResources().getDrawable(R.drawable.like_on));
			break;
		case PreferList.DISLIKED:
			dislikeButtonStatus = true;
			button_dislike.setImageDrawable(getResources().getDrawable(R.drawable.dislike_on));
			break;
		case PreferList.UNSIGNED:
			break;
		}
		textView_UriTitle = (TextView) findViewById(R.id.textView_instTitle);
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
		tabSpec.setIndicator("View Objects");
		tabHost.addTab(tabSpec);
		//View Up Tab
		tabSpec = tabHost.newTabSpec("tab_viewUp");
		tabSpec.setContent(R.id.listView_Up);
		tabSpec.setIndicator("View Subjects");
		tabHost.addTab(tabSpec);
		
	}
	
	@Override
	public void onClick(View v) {
		
		// TODO Auto-generated method stub
		switch(v.getId()) {
		case R.id.button_dislike:			
			toggleDislikeButton();			
			break;
		case R.id.button_like:			
			toggleLikeButton();			
			break;
		}
		
	}
	
	private void toggleLikeButton() {
		if(likeButtonStatus == true) {
			likeButtonStatus = false;
			button_like.setImageDrawable(getResources().getDrawable(R.drawable.like_off));
			deleteLikePrefer();
			Toast.makeText(RdfInstanceViewer.this, "\"Like " + currentUri.getName() + "\" canceled!", Toast.LENGTH_SHORT).show();
		}
		else {
			if(dislikeButtonStatus == true) {
				dislikeButtonStatus = false;
				button_dislike.setImageDrawable(getResources().getDrawable(R.drawable.dislike_off));
			}
			likeButtonStatus = true;
			button_like.setImageDrawable(getResources().getDrawable(R.drawable.like_on));
			likeCurrentInstance();
			Toast.makeText(RdfInstanceViewer.this, "Liked: " + currentUri.getName() + " !", Toast.LENGTH_SHORT).show();		
		}
	}
	
	private void toggleDislikeButton() {
		if(dislikeButtonStatus == true) {
			dislikeButtonStatus = false;
			button_dislike.setImageDrawable(getResources().getDrawable(R.drawable.dislike_off));
			deleteDislikePrefer();
			Toast.makeText(RdfInstanceViewer.this, "\"Dislike " + currentUri.getName() + "\" canceled!", Toast.LENGTH_SHORT).show();
		}
		else {
			if(likeButtonStatus == true) {
				likeButtonStatus = false;
				button_like.setImageDrawable(getResources().getDrawable(R.drawable.like_off));
			}
			dislikeButtonStatus = true;
			button_dislike.setImageDrawable(getResources().getDrawable(R.drawable.dislike_on));
			dislikeCurrentInstance();
			Toast.makeText(RdfInstanceViewer.this, "Disliked: " + currentUri.getName() + " !", Toast.LENGTH_SHORT).show();
		}
	}
	
	
	private void deleteLikePrefer() {
		CommandFactory.getInstance().createCancelPreferCmd(new LikePrefer(currentUser, currentUri)).execute();		
	}
	
	private void deleteDislikePrefer() {
		CommandFactory.getInstance().createCancelPreferCmd(new DislikePrefer(currentUser, currentUri)).execute();
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
			View itemView = inflater.inflate(R.layout.rdf_instance_viewer_list_item, null);			
			String predicateString = triple.getPredicate().getName();
			Resource objectResource = triple.getObject();
			String objectString = objectResource.getName();
			TextView textView_predicate = (TextView) itemView.findViewById(R.id.textView_predicate);
			TextView textView_object = (TextView) itemView.findViewById(R.id.textView_object);	
			TextView textView_accessable = (TextView) itemView.findViewById(R.id.textView_accessable);
			if(objectResource instanceof UriInstance && ((UriInstance) objectResource).canShowed()) {
				textView_accessable.setVisibility(View.VISIBLE);
			}			
			textView_predicate.setText(predicateString);
			textView_object.setText(objectString);	
			if(predicateString.equals("Homepage") || 
			   predicateString.equals("Photo Collection") ||
			   predicateString.equals("Page")) {
				String homepageUrl = "<a href = \"" + objectString + "\">" + objectString + "</a>";
				textView_object.setText(Html.fromHtml(homepageUrl)); 
			}
			return itemView;
		}
		
		private View makeItemViewUp(Triple triple) {
			LayoutInflater inflater = (LayoutInflater)RdfInstanceViewer.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View itemView = inflater.inflate(R.layout.rdf_instance_viewer_list_item, null);			
			String subjectString = triple.getSubject().getName();
			String predicateString = triple.getPredicate().getName();//.getUri();//.getSubject().getClassType();//triple.getPredicate().getName();//triple.getPredicate().getUri();
			TextView textView_predicate = (TextView) itemView.findViewById(R.id.textView_predicate);
			TextView textView_object = (TextView) itemView.findViewById(R.id.textView_object);
			TextView textView_accessable = (TextView) itemView.findViewById(R.id.textView_accessable);
			if(triple.getSubject().canShowed()) {
				textView_accessable.setVisibility(View.VISIBLE);
			}			
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

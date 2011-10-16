package cn.edu.nju.ws.camo.android.ui;
/**
 * @author Cunxin Jia
 *
 */
import java.io.IOException;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParserException;

import cn.edu.nju.ws.camo.android.R;
import cn.edu.nju.ws.camo.android.operate.ViewOperation;
import cn.edu.nju.ws.camo.android.rdf.Property;
import cn.edu.nju.ws.camo.android.rdf.RdfFactory;
import cn.edu.nju.ws.camo.android.rdf.Resource;
import cn.edu.nju.ws.camo.android.rdf.Triple;
import cn.edu.nju.ws.camo.android.rdf.UriInstWithNeigh;
import cn.edu.nju.ws.camo.android.rdf.UriInstance;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
	public final static String SER_KEY = "SER_URI";
	private UriInstance currentUri;
	private TabHost tabHost;
	private ListView listView_Down;
	private ListView listView_Up;
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
		currentUri = (UriInstance) intent.getSerializableExtra(SER_KEY);
		setTitle(currentUri.getUri());
		triplesDown = SampleTriples.getSampleTriplesV1();
		triplesUp = SampleTriples.getSampleTriplesV2();
		initLists();
		initButtons();		
		initTabs();
	}
	
	private void initTriplesDown() {
		try {
			UriInstWithNeigh neigh = ViewOperation.viewInstDown(currentUri);
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
			neigh = ViewOperation.viewInstUp(currentUri);
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
					newUriBundle.putSerializable(SER_KEY, selected);
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
				Resource selected = triplesUp.get(arg2).getObject();
				if(selected instanceof UriInstance) {
					Intent newUriIntent = new Intent(RdfInstanceViewer.this,RdfInstanceViewer.class);
					Bundle newUriBundle = new Bundle();
					newUriBundle.putSerializable(SER_KEY, selected);
					newUriIntent.putExtras(newUriBundle);
					startActivity(newUriIntent);
				}
			}			
		});
		
	}
	private void initButtons() {
		button_like = (Button)findViewById(R.id.button_like);
		button_dislike = (Button)findViewById(R.id.button_dislike);
		button_like.setOnClickListener(this);
		button_dislike.setOnClickListener(this);
	}
	private void initTabs() {
		tabHost = (TabHost) findViewById(R.id.tab_host);
		tabHost.setup();
		//View Down Tab
		TabSpec tabSpec = tabHost.newTabSpec("tab_viewDown");
		tabSpec.setContent(R.id.listView_Down);
		tabSpec.setIndicator("View Attributes");
		tabHost.addTab(tabSpec);
		//View Up Tab
		tabSpec = tabHost.newTabSpec("tab_viewUp");
		tabSpec.setContent(R.id.listView_Up);
		tabSpec.setIndicator("View Media Info");
		tabHost.addTab(tabSpec);
		
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()) {
		case R.id.button_dislike:
			button_dislike.setClickable(false);
			button_dislike.setText("Disliked");
			Toast.makeText(RdfInstanceViewer.this, "Dislike!", Toast.LENGTH_SHORT).show();
			break;
		case R.id.button_like:
			button_like.setClickable(false);
			button_like.setText("Liked");
			Toast.makeText(RdfInstanceViewer.this, "Like!", Toast.LENGTH_SHORT).show();
			break;
		}
	}
	
	public class ListViewAdapter extends BaseAdapter {
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
				itemViews[i] = makeItemView(triplesUp.get(i));
			}	
			
		}
		
		private void initViewDownAdapter() {				
			itemViews = new View[triplesDown.size()];
			for(int i = 0; i < triplesDown.size(); i++) {
				itemViews[i] = makeItemView(triplesDown.get(i));
			}		
		}

		private View makeItemView(Triple triple) {
			LayoutInflater inflater = (LayoutInflater)RdfInstanceViewer.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View itemView = inflater.inflate(R.layout.list_item, null);
			
			String subjectString = triple.getSubject().getUri();
			String predicateString = triple.getPredicate().getUri();
			String objectString = triple.getObject().getName();
			TextView textView_subject = (TextView) itemView.findViewById(R.id.textView_subject);
			TextView textView_predicate = (TextView) itemView.findViewById(R.id.textView_predicate);
			TextView textView_object = (TextView) itemView.findViewById(R.id.textView_object);
			textView_subject.setText(subjectString);
			textView_predicate.setText(predicateString);
			textView_object.setText(objectString);
			
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
			for(int i = 0; i < 10; i++) {
				UriInstance subject = RdfFactory.getInstance().createInstance("subject" + i, "");
				Property predicate = RdfFactory.getInstance().createProperty("predicate" + i, "");
				Resource object = RdfFactory.getInstance().createInstance("object" + i, "", "", "object" + i);				
				Triple triple = RdfFactory.getInstance().createTriple(subject, predicate, object);
				triples.add(triple);
			}
			return triples;
		}
		public static ArrayList<Triple> getSampleTriplesV2() {
			ArrayList<Triple> triples = new ArrayList<Triple>();
			for(int i = 0; i < 10; i++) {
				UriInstance subject = RdfFactory.getInstance().createInstance("subject", "");
				Property predicate = RdfFactory.getInstance().createProperty("predicate", "");
				Resource object = RdfFactory.getInstance().createInstance("object", "", "", "object");				
				Triple triple = RdfFactory.getInstance().createTriple(subject, predicate, object);
				triples.add(triple);
			}
			return triples;
		}
	}
}

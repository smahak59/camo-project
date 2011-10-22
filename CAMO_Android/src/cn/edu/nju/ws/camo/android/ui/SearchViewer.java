package cn.edu.nju.ws.camo.android.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.xmlpull.v1.XmlPullParserException;

import cn.edu.nju.ws.camo.android.R;
import cn.edu.nju.ws.camo.android.operate.InstViewOperation;
import cn.edu.nju.ws.camo.android.rdf.UriInstWithNeigh;
import cn.edu.nju.ws.camo.android.rdf.UriInstance;
import cn.edu.nju.ws.camo.android.util.SerKeys;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SearchViewer extends Activity {
	private ImageButton imageButton_search;
	private EditText editText_searchKey;
	private List<UriInstance> searchResultMusic;
	private List<UriInstance> searchResultMovie;
	private List<UriInstance> searchResultPhoto;
	private ExpandableListView expandableListView_searchResult;
	private ExpandableListViewAdapter expandableListViewAdapter;
    public void onCreate(Bundle savedInstanceState) {    
    	super.onCreate(savedInstanceState);
    	setTitle("Search");
        setContentView(R.layout.search_viewer);
    	initComponents();
    }
	private void initComponents() {
		expandableListView_searchResult = (ExpandableListView) findViewById(R.id.expandableListView_searchResult);
		imageButton_search = (ImageButton) findViewById(R.id.button_search);
		editText_searchKey = (EditText) findViewById(R.id.editText_searchKey);
		imageButton_search.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				LinearLayout linearLayout_loading=(LinearLayout)findViewById(R.id.linearLayout_loading);
				linearLayout_loading.setVisibility(View.VISIBLE);
				expandableListView_searchResult.setVisibility(View.GONE);
				class SearchTask extends AsyncTask<String,Void,String> {
					@Override
					protected String doInBackground(String... params) {
						String searchKey = editText_searchKey.getText().toString();
						try {
							searchResultMusic = InstViewOperation.searchInst(searchKey, "music");						
							searchResultMovie = InstViewOperation.searchInst(searchKey, "movie");
							searchResultPhoto = InstViewOperation.searchInst(searchKey, "photo");
							trimResult(searchResultMusic);
							trimResult(searchResultMovie);
							trimResult(searchResultPhoto);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (XmlPullParserException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						return null;
					}
					
					
					private void trimResult(List<UriInstance> searchResult) {
						Iterator<UriInstance> iter = searchResult.iterator();
						while(iter.hasNext()) {
							if(iter.next().getName().equals("")) {
								iter.remove();
							}
						}						
					}

					protected void onPostExecute(String result) {
						LinearLayout linearLayout_loading = (LinearLayout)findViewById(R.id.linearLayout_loading);
						linearLayout_loading.setVisibility(View.GONE);
						
						expandableListViewAdapter = new ExpandableListViewAdapter();
						if(expandableListViewAdapter.getGroupCount() == 0) {
							String errInfo = "Sorry! \"" + editText_searchKey.getText().toString() + "\" not found!";
							Toast.makeText(SearchViewer.this, errInfo, Toast.LENGTH_SHORT).show();
							return;
						}
						else {
							expandableListView_searchResult.setOnChildClickListener(new OnChildClickListener() {
	
								@Override
								public boolean onChildClick(
										ExpandableListView parent, View v,
										int groupPosition, int childPosition,
										long id) {
									UriInstance targetUri = (UriInstance) expandableListViewAdapter.getChild(groupPosition, childPosition);								
									new RdfInstanceLoader(SearchViewer.this, targetUri).loadRdfInstance();
									return false;
								}
							});						
							expandableListView_searchResult.setAdapter(expandableListViewAdapter);
							expandableListView_searchResult.setVisibility(View.VISIBLE);
							for(int i = 0; i < expandableListViewAdapter.getGroupCount(); i++) {
								expandableListView_searchResult.expandGroup(i);
							}
						}
					}
				}
				new SearchTask().execute("");						
			}			
		});		
	}
	
	private class ExpandableListViewAdapter extends BaseExpandableListAdapter {
		List<String> group;
		List<List<UriInstance>> resultLists;
		
		public ExpandableListViewAdapter() {
			resultLists = new ArrayList<List<UriInstance>>();
			group = new ArrayList<String>();
			if(searchResultMusic.size() != 0) { 
				group.add("Music");
				resultLists.add(searchResultMusic);
			}
			if(searchResultMovie.size() != 0) { 
				group.add("Movie");
				resultLists.add(searchResultMovie);
			}
			if(searchResultPhoto.size() != 0) { 
				group.add("Photo");
				resultLists.add(searchResultPhoto);
			}					
		}
		
		@Override
		public Object getChild(int groupPosition, int childPosition) {
			return resultLists.get(groupPosition).get(childPosition);
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
            LayoutInflater inflator = (LayoutInflater) SearchViewer.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View itemView = inflator.inflate(R.layout.search_result_item, null);
			TextView textView_preferName = (TextView) itemView.findViewById(R.id.textView_searchResultItemName);
			textView_preferName.setText(((UriInstance)getChild(groupPosition, childPosition)).getName());
			return itemView;
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			return resultLists.get(groupPosition).size();
		}

		@Override
		public Object getGroup(int groupPosition) {
			return group.get(groupPosition);
		}

		@Override
		public int getGroupCount() {
			return group.size();
		}

		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {			
			LayoutInflater inflator = (LayoutInflater) SearchViewer.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View itemView = inflator.inflate(R.layout.search_result_group, null);
			TextView textView_preferName = (TextView) itemView.findViewById(R.id.textView_searchResultGroupName);
			textView_preferName.setText(getGroup(groupPosition).toString());
			return itemView;
		}
		
		@Override
		public boolean hasStableIds() {
			return true;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}
		
	}
    
}

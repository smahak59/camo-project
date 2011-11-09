package cn.edu.nju.ws.camo.android.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.edu.nju.ws.camo.android.R;
import cn.edu.nju.ws.camo.android.rdf.InstViewManager;
import cn.edu.nju.ws.camo.android.rdf.RdfFactory;
import cn.edu.nju.ws.camo.android.rdf.UriInstance;

public class SearchViewer extends Activity {
	private ImageButton imageButton_search;
	private EditText editText_searchKey;
	private String searchKey;
	private Set<UriInstance> searchResultMusic;
	private Set<UriInstance> searchResultMovie;
	private Set<UriInstance> searchResultPhoto;
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
		imageButton_search.setOnTouchListener(CAMO_AndroidActivity.touchListener);
		imageButton_search.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				searchKey = editText_searchKey.getText().toString().trim();
				if(searchKey.equals(""))
					return;
				LinearLayout linearLayout_loading=(LinearLayout)findViewById(R.id.linearLayout_loading);
				linearLayout_loading.setVisibility(View.VISIBLE);
				expandableListView_searchResult.setVisibility(View.GONE);
				class SearchTask extends AsyncTask<String,Void,String> {
					@Override
					protected String doInBackground(String... params) {
						
						try {
							searchResultMusic = InstViewManager.searchInst(params[0], "music");						
							searchResultMovie = InstViewManager.searchInst(params[0], "movie");
							//searchResultPhoto = InstViewManager.searchInst(params[0], "photo");
							trimResult(searchResultMusic);
							trimResult(searchResultMovie);
							//trimResult(searchResultPhoto);
						} catch (IOException e) {
							e.printStackTrace();
						} catch (XmlPullParserException e) {
							e.printStackTrace();
						}
						return null;
					}
					
					
					private void trimResult(Set<UriInstance> searchResult) {
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
				new SearchTask().execute(searchKey);						
			}			
		});		
	}
	
	private class ExpandableListViewAdapter extends BaseExpandableListAdapter {
		List<String> group;
		ArrayList<ArrayList<UriInstance>> resultLists;
		
		public ExpandableListViewAdapter() {
			resultLists = new ArrayList<ArrayList<UriInstance>>();
			group = new ArrayList<String>();
			if(searchResultMusic.size() != 0) { 
				group.add("Music");
				resultLists.add(setToArrayList(searchResultMusic));
			}
			if(searchResultMovie.size() != 0) { 
				group.add("Movie");
				resultLists.add(setToArrayList(searchResultMovie));
			}
//			if(searchResultPhoto.size() != 0) { 
//				group.add("Photo");
//				resultLists.add(setToArrayList(searchResultPhoto));
//			}					
		}
		
		private ArrayList<UriInstance> setToArrayList(
				Set<UriInstance> set) {
			ArrayList<UriInstance> arrayList = new ArrayList<UriInstance>();
			Iterator<UriInstance> iter = set.iterator();
			while(iter.hasNext()) {
				arrayList.add(iter.next());
			}
			return arrayList;
		}

		public Object getChild(int groupPosition, int childPosition) {
			return resultLists.get(groupPosition).get(childPosition);
		}

		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			UriInstance curInst = (UriInstance)getChild(groupPosition, childPosition);
            LayoutInflater inflator = (LayoutInflater) SearchViewer.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View itemView = inflator.inflate(R.layout.search_result_item, null);
			TextView textView_searchResult = (TextView) itemView.findViewById(R.id.textView_searchResultItemName);
			ImageView imageView_instType = (ImageView) itemView.findViewById(R.id.imageView_instType);
			String classType = curInst.getClassType();
			if(RdfFactory.getInstance().isActorCls(classType) || RdfFactory.getInstance().isArtistCls(classType))
				imageView_instType.setImageDrawable(getResources().getDrawable(R.drawable.type_artist));
			else if(RdfFactory.getInstance().isMovieCls(classType) || curInst.getMediaType().equals("movie"))
				imageView_instType.setImageDrawable(getResources().getDrawable(R.drawable.type_movie));
			else if(RdfFactory.getInstance().isMusicCls(classType) || curInst.getMediaType().equals("music"))
				imageView_instType.setImageDrawable(getResources().getDrawable(R.drawable.type_music));
			textView_searchResult.setText(curInst.getName());
			
			return itemView;
		}

		public int getChildrenCount(int groupPosition) {
			return resultLists.get(groupPosition).size();
		}

		public Object getGroup(int groupPosition) {
			return group.get(groupPosition);
		}

		public int getGroupCount() {
			return group.size();
		}

		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {			
			LayoutInflater inflator = (LayoutInflater) SearchViewer.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View itemView = inflator.inflate(R.layout.search_result_group, null);
			TextView textView_preferName = (TextView) itemView.findViewById(R.id.textView_searchResultGroupName);
			textView_preferName.setText(getGroup(groupPosition).toString());
			return itemView;
		}
		
		public boolean hasStableIds() {
			return true;
		}

		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}
		
	}
    
}

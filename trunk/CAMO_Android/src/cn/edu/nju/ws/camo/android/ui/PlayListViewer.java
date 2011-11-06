package cn.edu.nju.ws.camo.android.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import cn.edu.nju.ws.camo.android.R;
import cn.edu.nju.ws.camo.android.mediaplayer.PlayList;

public class PlayListViewer extends Activity{
	private ListView listView_playList;
	private PlayList playList;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.play_list_viewer);
        setTitle("PlayList");
        initPlayList();
        initListView();
    }

	private void initPlayList() {
		playList = ((CAMO_Application)getApplication()).getPlayList();		
	}

	private void initListView() {
		listView_playList = (ListView) findViewById(R.id.listView_playList);
		final ListViewAdapter adapter = new ListViewAdapter();
		listView_playList.setAdapter(adapter);
		this.registerForContextMenu(listView_playList);
		listView_playList.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				playList.playByIndex(arg2);
				adapter.setCurrentPlaying();				
			}
		});
 	}
	
	private class ListViewAdapter extends BaseAdapter {
		
		private View[] itemViews;
		private TextView[] textView_mediaName;
		
		public ListViewAdapter() {
			itemViews = new View[playList.size()];
			textView_mediaName = new TextView[playList.size()];
			for(int i = 0; i < itemViews.length; i++) {
				itemViews[i] = makeListItemView(i);
			}
			setCurrentPlaying();
		}
		
		private View makeListItemView(int i) {
			LayoutInflater inflater = (LayoutInflater) PlayListViewer.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View item = inflater.inflate(R.layout.play_list_item, null);
			textView_mediaName[i] = (TextView) item.findViewById(R.id.textView_mediaName);
			textView_mediaName[i].setText(playList.getInstance(i).getName());
			
			return item;
		}
		
		public void setCurrentPlaying() {
			if(playList.size() == 0) {
				return;
			}
			for(int i = 0; i < textView_mediaName.length; i++)
				textView_mediaName[i].setTextColor(0xE0FFFFFF);
			TextView current = textView_mediaName[playList.getCurrentPlayingIndex()];
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
    	AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) item.getMenuInfo();
    	int position = menuInfo.position;
    	playList.remove(position);
    	listView_playList.setAdapter(new ListViewAdapter());
    	
    	return true;
    }
}

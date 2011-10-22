package cn.edu.nju.ws.camo.android.ui;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import cn.edu.nju.ws.camo.android.R;
import cn.edu.nju.ws.camo.android.util.Friends;

public class FriendListViewer extends Activity {
	private ListView listView_friendList;
	private List<Friends> friendList;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friend_list_viewer);
        initFriendList();
        initListView();
    }

	private void initFriendList() {
		friendList = ((CAMO_Application)getApplication()).getFriendList();		
	}

	private void initListView() {
		listView_friendList = (ListView) findViewById(R.id.listViewer_friendList);
		ListViewAdapter adapter = new ListViewAdapter();
		listView_friendList.setAdapter(adapter);
		listView_friendList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {				
				
			}
			
		});
	}
	
	private class ListViewAdapter extends BaseAdapter {
		private View[] itemViews;
		
		public ListViewAdapter() {
			itemViews = new View[friendList.size()];
			for(int i = 0; i < friendList.size(); i++) {
				itemViews[i] = makeItemView(friendList.get(i));
			}
		}
		
		private View makeItemView(Friends friends) {
			LayoutInflater inflater = (LayoutInflater) FriendListViewer.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View itemView = inflater.inflate(R.layout.friend_list_item, null);
			TextView textView_friendName = (TextView) itemView.findViewById(R.id.textView_friendName);
			textView_friendName.setText(friends.getUser2().getName());
			return itemView;
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


}

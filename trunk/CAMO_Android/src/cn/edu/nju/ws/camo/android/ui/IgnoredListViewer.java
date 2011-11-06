package cn.edu.nju.ws.camo.android.ui;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Toast;
import cn.edu.nju.ws.camo.android.R;
import cn.edu.nju.ws.camo.android.user.User;
import cn.edu.nju.ws.camo.android.user.interestgp.InterestGroup;

public class IgnoredListViewer extends Activity{
	private List<User> ignoredUserList;
	private ListView listView_ignoredUserList;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ignored_list_viewer);
        setTitle("Ignored Users");
        initIgnoredList();
        initListView();
    }
    
    private void initIgnoredList() {
    	ignoredUserList = ((CAMO_Application)getApplication()).getIgnoredUserList();
    }
    
    private void initListView() {
    	listView_ignoredUserList = (ListView) findViewById(R.id.listView_ignoredList);
    	ListViewAdapter adapter = new ListViewAdapter();
    	listView_ignoredUserList.setAdapter(adapter);
    	this.registerForContextMenu(listView_ignoredUserList);
    }

    private class ListViewAdapter extends BaseAdapter {
    	View[] itemViews;
    	
    	public ListViewAdapter() {
    		itemViews = new View[ignoredUserList.size()];
    		for(int i = 0; i < itemViews.length; i++) {
    			itemViews[i] = makeItemView(i);
    		}
    	}
    	
		private View makeItemView(int i) {
			LayoutInflater inflater = (LayoutInflater) IgnoredListViewer.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View itemView = inflater.inflate(R.layout.ignored_list_item, null);
			TextView textView_ignoredUserName = (TextView) itemView.findViewById(R.id.textView_ignoredUserName);
			textView_ignoredUserName.setText(ignoredUserList.get(i).getName());
			return itemView;
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
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			return itemViews[arg0];
		}
    }
    public void onCreateContextMenu(ContextMenu menu, View v,  
            ContextMenuInfo menuInfo) {             	
        menu.add(0, 0, 0, "Delete");        
    }  
    
    public boolean onContextItemSelected(MenuItem item) {  
    	AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) item.getMenuInfo();
    	int position = menuInfo.position;
    	User curUser = ((CAMO_Application)getApplication()).getCurrentUser();
    	new InterestGroup(curUser).getUserRecommandedCmd(ignoredUserList.get(position)).execute();
    	ignoredUserList.remove(position);
    	Toast.makeText(IgnoredListViewer.this, "deleted", Toast.LENGTH_SHORT).show();
    	listView_ignoredUserList.setAdapter(new ListViewAdapter());
    	
    	return true;
    }
}

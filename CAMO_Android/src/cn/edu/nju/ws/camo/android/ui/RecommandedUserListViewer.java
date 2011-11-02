package cn.edu.nju.ws.camo.android.ui;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import cn.edu.nju.ws.camo.android.user.User;
import cn.edu.nju.ws.camo.android.user.interestgp.InterestGroup;
import cn.edu.nju.ws.camo.android.user.interestgp.RmdFeedback;
import cn.edu.nju.ws.camo.android.util.SerKeys;

public class RecommandedUserListViewer extends Activity{
	
	private ListView listView_rmdUser;
	private List<RmdFeedback> rmdFeedbackList;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rmd_user_list_viewer);
        initRmdFeedbackList();
        initListView();
    }

	private void initRmdFeedbackList() {
		rmdFeedbackList = ((CAMO_Application)getApplication()).getRmdFeedbackList();		
	}

	private void initListView() {
		listView_rmdUser = (ListView)findViewById(R.id.listView_rmdUser);
		ListViewAdapter adapter = new ListViewAdapter();
		listView_rmdUser.setAdapter(adapter);
		listView_rmdUser.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				User selectedUser = rmdFeedbackList.get(arg2).getUserInterest().getUser();
				Intent viewUserIntent = new Intent(RecommandedUserListViewer.this, UserInfoViewer.class);
				Bundle viewUserBundle = new Bundle();
				viewUserBundle.putSerializable(SerKeys.SER_USER, selectedUser);
				viewUserIntent.putExtras(viewUserBundle);
				startActivity(viewUserIntent);				
			}
			
		});
	}
	
	private class ListViewAdapter extends BaseAdapter {
		View[] itemViews;
		
		public ListViewAdapter() {
			itemViews = new View[rmdFeedbackList.size()];
			for(int i = 0; i < rmdFeedbackList.size(); i++) {
				itemViews[i] = makeItemView(rmdFeedbackList.get(i));
			}
		}
		
		
		
		private View makeItemView(RmdFeedback rmdFeedback) {
			LayoutInflater inflater = (LayoutInflater) RecommandedUserListViewer.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View itemView = inflater.inflate(R.layout.rmd_user_list_item, null);
			TextView textView_rmdUserName = (TextView) itemView.findViewById(R.id.textView_rmdUserName);
			TextView textView_rmdRule = (TextView) itemView.findViewById(R.id.textView_rmdRule);
			textView_rmdUserName.setText(rmdFeedback.getUserInterest().getUser().getName());
			textView_rmdRule.setText(InterestGroup.getRuleSuggestion(rmdFeedback.getRuleId()));
			return itemView;
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
}

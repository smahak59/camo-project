//package cn.edu.nju.ws.camo.android.ui;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//
//import android.app.Activity;
//import android.content.Context;
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.Menu;
//import android.view.MenuInflater;
//import android.view.MenuItem;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.AdapterView;
//import android.widget.AdapterView.OnItemClickListener;
//import android.widget.BaseAdapter;
//import android.widget.ListView;
//import android.widget.TextView;
//import android.widget.Toast;
//import cn.edu.nju.ws.camo.android.R;
//import cn.edu.nju.ws.camo.android.user.User;
//import cn.edu.nju.ws.camo.android.user.interestgp.InterestGroup;
//import cn.edu.nju.ws.camo.android.user.interestgp.RmdFeedback;
//import cn.edu.nju.ws.camo.android.util.SerKeys;
//
//public class RecommandedUserListViewer extends Activity{
//	
//	private ListView listView_rmdUser;
//	private List<RmdFeedback> rmdFeedbackList;
//	private Map<Integer, List<RmdFeedback>> rmdFeedbackMap;
//	private List<Integer> itemPos;
//
//	
//    /** Called when the activity is first created. */
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.rmd_user_list_viewer);
//        setTitle("Recommended Users");
//    }
//    
//    public void onStart() {
//    	super.onStart();
//       // initRmdFeedbackList();
//        initListView();
//    }
//    
//    public boolean onCreateOptionsMenu(Menu menu) {
//    	super.onCreateOptionsMenu(menu);
//    	MenuInflater menuInflater = getMenuInflater();
//    	menuInflater.inflate(R.menu.menu, menu);
//    	return true;
//    }
//    
//    public boolean onOptionsItemSelected(MenuItem item) {
//    	switch(item.getItemId()) {
//    	case R.id.item_viewIgnoredList:
//			Intent ignoredIntend = new Intent(RecommandedUserListViewer.this, IgnoredListViewer.class);
//			startActivity(ignoredIntend);
//    		break;
//    	}
//    	return true;
//    }
// 
//
//
////	private void initRmdFeedbackList() {
////		rmdFeedbackList = ((CAMO_Application)getApplication()).getRmdFeedbackList();
////		rmdFeedbackMap = new HashMap<Integer, List<RmdFeedback>>();
////		itemPos = new ArrayList<Integer>();
////		for(int i = 0; i < rmdFeedbackList.size(); i++) {
////			RmdFeedback curFeedback = rmdFeedbackList.get(i);
////			int curKey = curFeedback.getRuleId();
////			List<RmdFeedback> curList = rmdFeedbackMap.get(curKey);
////			if(curList == null) {
////				curList = new ArrayList<RmdFeedback>();
////			}
////			curList.add(curFeedback);
////			rmdFeedbackMap.put(curKey, curList);
////		}
////	}
//
//	private void initListView() {
//		listView_rmdUser = (ListView)findViewById(R.id.listView_rmdUser);
//		ListViewAdapter adapter = new ListViewAdapter();
//		listView_rmdUser.setAdapter(adapter);
//		listView_rmdUser.setOnItemClickListener(new OnItemClickListener() {
//
//			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
//					long arg3) {
//				
//				int index = itemPos.get(arg2);
//				if(index == -1)
//					return;
//				RmdFeedback selectedFeedback = rmdFeedbackList.get(index);
//
//				User selectedUser = selectedFeedback.getUserInterest().getUser();
//				
//				Intent viewUserIntent = new Intent(RecommandedUserListViewer.this, UserInfoViewer.class);
//				Bundle viewUserBundle = new Bundle();
//				viewUserBundle.putSerializable(SerKeys.SER_USER, selectedUser);
//				viewUserIntent.putExtras(viewUserBundle);
//				startActivity(viewUserIntent);				
//			}
//			
//		});
//	}
//	
//	
//	private class ListViewAdapter extends BaseAdapter {
//		View[] itemViews;
//		
//		public ListViewAdapter() {
//			itemViews = new View[rmdFeedbackList.size() + rmdFeedbackMap.keySet().size()];
//			Set<Integer> ruldIdSet = rmdFeedbackMap.keySet();
//			Iterator<Integer> iter = ruldIdSet.iterator();
//			int curItemPos = 0;
//			int itemIndex = 0;
//			while(iter.hasNext()) {
//				int curRuleId = iter.next();
//				itemViews[curItemPos++] = makeRuleTitle(curRuleId);
//				itemPos.add(-1);
//				List<RmdFeedback> curList = rmdFeedbackMap.get(curRuleId);
//				for(int i = 0; i < curList.size(); i++) {
//					itemViews[curItemPos++] = makeItemView(curList.get(i));
//					itemPos.add(indexOfList(curList.get(i)));
//				}
//			}
//		}
//		
//		private int indexOfList(RmdFeedback rmdFeedback) {
//			for(int i = 0; i < rmdFeedbackList.size(); i++) {
//				if(rmdFeedbackList.get(i).equals(rmdFeedback)) {
//					return i;
//				}
//			}
//			return -1;
//		}
//		
//		
//		
//		private View makeRuleTitle(int curRuleId) {
//			LayoutInflater inflater = (LayoutInflater) RecommandedUserListViewer.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//			View itemView = inflater.inflate(R.layout.rmd_user_list_group_title, null);
//			TextView textView_rmdRule = (TextView) itemView.findViewById(R.id.textView_rmdRule);
//			textView_rmdRule.setText(InterestGroup.getRuleSuggestion(curRuleId));
//			return itemView;
//		}
//
//
//
//		private View makeItemView(RmdFeedback rmdFeedback) {
//			LayoutInflater inflater = (LayoutInflater) RecommandedUserListViewer.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//			View itemView = inflater.inflate(R.layout.rmd_user_list_item, null);
//			TextView textView_rmdUserName = (TextView) itemView.findViewById(R.id.textView_rmdUserName);
//			textView_rmdUserName.setText(rmdFeedback.getUserInterest().getUser().getName());
//			return itemView;
//		} 
//
//
//
//		public int getCount() {
//			return itemViews.length;
//		}
//
//		public Object getItem(int position) {
//			return itemViews[position];
//		}
//
//		public long getItemId(int position) {
//			return position;
//		}
//
//		public View getView(int position, View convertView, ViewGroup parent) {
//			return itemViews[position];
//		}
//		
//	}
//}

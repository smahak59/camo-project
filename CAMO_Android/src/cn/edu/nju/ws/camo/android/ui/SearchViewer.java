package cn.edu.nju.ws.camo.android.ui;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.xmlpull.v1.XmlPullParserException;

import cn.edu.nju.ws.camo.android.R;
import cn.edu.nju.ws.camo.android.operate.InstViewOperation;
import cn.edu.nju.ws.camo.android.rdf.UriInstWithNeigh;
import cn.edu.nju.ws.camo.android.rdf.UriInstance;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

public class SearchViewer extends Activity {
	private ImageButton imageButton_search;
	private EditText editText_searchKey;
	private Map<UriInstance, UriInstWithNeigh> searchResult;
    public void onCreate(Bundle savedInstanceState) {    
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.search_viewer);
    	initComponents();
    }
	private void initComponents() {
		imageButton_search = (ImageButton) findViewById(R.id.button_search);
		editText_searchKey = (EditText) findViewById(R.id.editText_searchKey);
		imageButton_search.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				LinearLayout linearLayout_loading=(LinearLayout)findViewById(R.id.linearLayout_loading);
				linearLayout_loading.setVisibility(View.VISIBLE);
				class SearchTask extends AsyncTask<String,Void,String> {
					@Override
					protected String doInBackground(String... params) {
						String searchKey = editText_searchKey.getText().toString();
						try {
							searchResult = InstViewOperation.searchInstDown(searchKey, "movie");
							
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (XmlPullParserException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						return null;
					}
					
					protected void onPostExecute(String result) {
						LinearLayout linearLayout_loading=(LinearLayout)findViewById(R.id.linearLayout_loading);
						linearLayout_loading.setVisibility(View.GONE);
						for(Iterator<UriInstance> iter = searchResult.keySet().iterator();iter.hasNext();) {
								Toast.makeText(SearchViewer.this, iter.next().getName(), Toast.LENGTH_SHORT).show();
							}   
				        } 
				
				}
				new SearchTask().execute("");
						
			}
			
		});
		
	}
    
}

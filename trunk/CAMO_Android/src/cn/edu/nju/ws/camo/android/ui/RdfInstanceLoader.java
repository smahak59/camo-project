package cn.edu.nju.ws.camo.android.ui;

import java.io.IOException;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParserException;

import cn.edu.nju.ws.camo.android.operate.InstViewOperation;
import cn.edu.nju.ws.camo.android.rdf.Resource;
import cn.edu.nju.ws.camo.android.rdf.Triple;
import cn.edu.nju.ws.camo.android.rdf.UriInstWithNeigh;
import cn.edu.nju.ws.camo.android.rdf.UriInstance;
import cn.edu.nju.ws.camo.android.util.SerKeys;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

public class RdfInstanceLoader {
	private ProgressDialog progressDialog;
	private UriInstance currentUri;
	private ArrayList<Triple> triplesDown;
	private ArrayList<Triple> triplesUp;
	private Context context;
	
	public RdfInstanceLoader(Context con, UriInstance inst) {
		currentUri = inst;
		context = con;
		progressDialog = new ProgressDialog(context);			  
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);  			
		progressDialog.setMessage("Loading...");  
		progressDialog.setCancelable(false);		
	}
	public void loadRdfInstance() {
		progressDialog.show();
		class LoadTask extends AsyncTask<String,Void,String> {
			
			@Override
			protected String doInBackground(String... params) {
				initTriplesDown();
				initTriplesUp();
				return null;
			}
			
			protected void onPostExecute(String result) {
				
				if(triplesDown.size() == 0 || triplesUp.size() == 0 ) {
					Toast.makeText(context, "Sorry, content not found.", Toast.LENGTH_SHORT).show();
					progressDialog.dismiss();
					return;
				}
				else {
					Intent newUriIntent = new Intent(context,RdfInstanceViewer.class);
					Bundle newUriBundle = new Bundle();
					newUriBundle.putSerializable(SerKeys.SER_URI, currentUri);
					newUriBundle.putSerializable(SerKeys.SER_TRIPLES_DOWN, triplesDown);
					newUriBundle.putSerializable(SerKeys.SER_TRIPLES_UP, triplesUp);
					newUriIntent.putExtras(newUriBundle);
					context.startActivity(newUriIntent);
					progressDialog.dismiss();
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
			
		}
		new LoadTask().execute("");
	}

}

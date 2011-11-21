package cn.edu.nju.ws.camo.android.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.xmlpull.v1.XmlPullParserException;

import cn.edu.nju.ws.camo.android.rdf.InstViewManager;
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
import android.util.Log;
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
				Log.v("****************", "in Loader");
				if(triplesDown.size() == 0 && triplesUp.size() == 0 ) {
					Toast.makeText(context, "Sorry, content not found.", Toast.LENGTH_SHORT).show();
					progressDialog.dismiss();
					return;
				}
				else {
					Intent newUriIntent = new Intent(context,RdfInstanceViewer.class);
					Bundle newUriBundle = new Bundle();
					newUriBundle.putSerializable(SerKeys.SER_URI, currentUri);
					((CAMO_Application)(context.getApplicationContext())).setTriplesDown(triplesDown);
					((CAMO_Application)(context.getApplicationContext())).setTriplesUp(triplesUp);
					newUriIntent.putExtras(newUriBundle);
					context.startActivity(newUriIntent);
					progressDialog.dismiss();
				}
				
			}
			
			private void initTriplesDown() {
				try {
					UriInstWithNeigh neigh = InstViewManager.viewInstDown(currentUri);
					if(neigh == null)
						triplesDown = new ArrayList<Triple>();
					else
						triplesDown = neigh.getTriplesDown();
					trimTriples(triplesDown);
					mergeDuplicatesDown(triplesDown);
				} catch (IOException e) {
					e.printStackTrace();
				} catch (XmlPullParserException e) {
					e.printStackTrace();
				}
			}


			private void initTriplesUp() {
				UriInstWithNeigh neigh;
				try {
					neigh = InstViewManager.viewInstUp(currentUri);
					if(neigh == null)
						triplesUp = new ArrayList<Triple>();
					else
						triplesUp = neigh.getTriplesUp();
					trimTriples(triplesUp);
					mergeDuplicatesUp(triplesUp);
				} catch (IOException e) {
					e.printStackTrace();
				} catch (XmlPullParserException e) {
					e.printStackTrace();
				}		
				
			}
			
			private void mergeDuplicatesUp(ArrayList<Triple> triplesUp) {
				Map<UriInstance,Triple> triplesUpMap = new HashMap<UriInstance,Triple>();
				Iterator<Triple> iter = triplesUp.iterator();
				while(iter.hasNext()) {
					Triple curTriple = iter.next();
					if(curTriple.getPredicate().getName().equals(""))
						continue;
					if(triplesUpMap.get(curTriple.getSubject()) != null) {
						String predicateString = triplesUpMap.get(curTriple.getSubject()).getPredicate().getName();
						predicateString += ", " + curTriple.getPredicate().getName();
						curTriple.getPredicate().setName(predicateString);
					}
					triplesUpMap.put(curTriple.getSubject(), curTriple);				
				}
				triplesUp.clear();
				Set<UriInstance> keySet = triplesUpMap.keySet();
				Iterator<UriInstance> keyIter = keySet.iterator();
				while(keyIter.hasNext()) {
					triplesUp.add(triplesUpMap.get(keyIter.next()));
				}
			}
			
			private void mergeDuplicatesDown(ArrayList<Triple> triplesDown) {
				Map<Resource,Triple> triplesDownMap = new HashMap<Resource,Triple>();
				Iterator<Triple> iter = triplesDown.iterator();
				while(iter.hasNext()) {
					Triple curTriple = iter.next();
					if(curTriple.getPredicate().getName().equals(""))
						continue;
					if(triplesDownMap.get(curTriple.getObject()) != null) {
						Triple preTriple = triplesDownMap.get(curTriple.getObject());
						String predicateString = preTriple.getPredicate().getName();
						predicateString += ", " + curTriple.getPredicate().getName();						
						preTriple.getPredicate().setName(predicateString);
						iter.remove();
					} else {
						triplesDownMap.put(curTriple.getObject(), curTriple);
					}
				}
			}
			
		}
		new LoadTask().execute("");
	}
	public void trimTriples(ArrayList<Triple> triples) {
		Iterator<Triple> iter = triples.iterator();
		while(iter.hasNext()) {
			Triple triple = iter.next();
			if(triple.getSubject().getName().equals("") || 
				triple.getObject().getName().equals("")){// ||
				//!triple.getSubject().canShowed()||
				//((triple.getObject() instanceof UriInstance) && !((UriInstance)triple.getObject()).canShowed())) {
				iter.remove();
			}
		}
		
	}

}

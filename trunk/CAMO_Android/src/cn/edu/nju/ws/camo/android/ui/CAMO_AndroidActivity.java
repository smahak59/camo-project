package cn.edu.nju.ws.camo.android.ui;
/**
 * @author Cunxin Jia
 *
 */
import cn.edu.nju.ws.camo.android.R;
import cn.edu.nju.ws.camo.android.connect.server.ServerConfig;
import cn.edu.nju.ws.camo.android.rdf.RdfFactory;
import cn.edu.nju.ws.camo.android.rdf.UriInstance;
import cn.edu.nju.ws.camo.android.util.User;
import cn.edu.nju.ws.camo.android.util.UtilParam;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class CAMO_AndroidActivity extends Activity implements OnClickListener {
	private Button button_viewLike;
	private Button button_viewDislike;
	private Button button_viewInstance;
	private Button button_viewSearch;
	private User currentUser;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	initCurrentUser();
    	initServerParams();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        initViewComponents();
    }
    
    
    
    private void initCurrentUser() {
		currentUser = new User(7);		
	}

	private void initServerParams() {
    	UtilParam.assets = getAssets();
    	ServerConfig.initParam();
		
	}


	private void initViewComponents() {
    	button_viewLike = (Button) findViewById(R.id.button_viewLike);
    	button_viewDislike = (Button) findViewById(R.id.button_viewDislike);
    	button_viewInstance = (Button) findViewById(R.id.button_viewInstance);
    	button_viewSearch = (Button) findViewById(R.id.button_viewSearch);
    	button_viewLike.setOnClickListener(this);	
    	button_viewDislike.setOnClickListener(this);
    	button_viewInstance.setOnClickListener(this);
    	button_viewSearch.setOnClickListener(this);
	}


	/**onClick Listener*/
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()) {			
		case R.id.button_viewLike:
			Intent likeIntent = new Intent(this, LikeViewer.class);
			Bundle likeBundle = new Bundle();
			likeBundle.putSerializable(RdfInstanceViewer.SER_USER, currentUser);
			likeIntent.putExtras(likeBundle);
			startActivity(likeIntent);
			break;
		case R.id.button_viewDislike:
			Intent dislikeIntent = new Intent(this, DislikeViewer.class);
			Bundle dislikeBundle = new Bundle();
			dislikeBundle.putSerializable(RdfInstanceViewer.SER_USER, currentUser);
			dislikeIntent.putExtras(dislikeBundle);
			startActivity(dislikeIntent);
			break;
		case R.id.button_viewInstance:
			Intent viewInstanceIntent = new Intent(this, RdfInstanceViewer.class);
			Bundle viewInstanceBundle = new Bundle();
			UriInstance uri = RdfFactory.getInstance().createInstance("http://dbpedia.org/resource/Eminem", "music");
			viewInstanceBundle.putSerializable(RdfInstanceViewer.SER_URI, uri);
			viewInstanceBundle.putSerializable(RdfInstanceViewer.SER_USER, currentUser);
			viewInstanceIntent.putExtras(viewInstanceBundle);
			startActivity(viewInstanceIntent);
			break;
		case R.id.button_viewSearch:
			Intent viewSearchIntent = new Intent(this, SearchViewer.class);
			Bundle searchBundle = new Bundle();
			searchBundle.putSerializable(RdfInstanceViewer.SER_USER, currentUser);
			viewSearchIntent.putExtras(searchBundle);
			startActivity(viewSearchIntent);
			break;
		}
	}
}
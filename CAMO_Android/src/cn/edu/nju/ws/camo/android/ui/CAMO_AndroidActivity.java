package cn.edu.nju.ws.camo.android.ui;
/**
 * @author Cunxin Jia
 *
 */
import cn.edu.nju.ws.camo.android.R;
import cn.edu.nju.ws.camo.android.connect.server.ServerConfig;
import cn.edu.nju.ws.camo.android.rdf.RdfFactory;
import cn.edu.nju.ws.camo.android.rdf.UriInstance;
import cn.edu.nju.ws.camo.android.util.SerKeys;
import cn.edu.nju.ws.camo.android.util.UtilConfig;
import cn.edu.nju.ws.camo.android.util.UtilParam;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class CAMO_AndroidActivity extends Activity implements OnClickListener {
	private CAMO_Application CAMO_app;
	private Button button_viewLike;
	private Button button_viewDislike;
	private Button button_viewInstance;
	private Button button_viewSearch;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	CAMO_app = (CAMO_Application) getApplication();    	
    	initServerParams();
    	initUserPreferList();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        initViewComponents();
    }
    

    
    
    
    private void initUserPreferList() {
    	CAMO_app.initCurrentUser();
    	CAMO_app.initPreferList();		
	}

	private void initServerParams() {
    	UtilParam.ASSETS = getAssets();
    	UtilConfig.initParam();
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
			if(!CAMO_app.preferListIsLoaded()) {
				Toast.makeText(CAMO_AndroidActivity.this, "My Like is loading...", Toast.LENGTH_SHORT).show();
			}
			else {
				Intent likeIntent = new Intent(this, LikeViewer.class);			
				startActivity(likeIntent);
			}
			break;
		case R.id.button_viewDislike:
			if(!CAMO_app.preferListIsLoaded()) {
				Toast.makeText(CAMO_AndroidActivity.this, "My Dislike is loading...", Toast.LENGTH_SHORT).show();
			}
			else {
				Intent dislikeIntent = new Intent(this, DislikeViewer.class);			
				startActivity(dislikeIntent);
			}
			break;
		case R.id.button_viewInstance:		
			UriInstance uri = RdfFactory.getInstance().createInstance("http://dbpedia.org/resource/Eminem", "music");
			new RdfInstanceLoader(CAMO_AndroidActivity.this, uri).loadRdfInstance();
			break;
		case R.id.button_viewSearch:
			Intent viewSearchIntent = new Intent(this, SearchViewer.class);			
			startActivity(viewSearchIntent);
			break;
		}
	}
}
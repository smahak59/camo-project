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
	private Button button_viewMediaPlayer;
	private Button button_viewSearch;
	private Button button_viewFriendList;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	CAMO_app = (CAMO_Application) getApplication();    	
    	initServerParams();
    	initUserData();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        initViewComponents();
    }   
    
    private void initUserData() {
    	CAMO_app.initCurrentUser();
    	CAMO_app.initPreferList();	
    	CAMO_app.initFriendList();
    	CAMO_app.InitPlayList(this);
	}

	private void initServerParams() {
    	UtilParam.ASSETS = getAssets();
    	UtilConfig.initParam();
    	ServerConfig.initParam();		
	}


	private void initViewComponents() {
    	button_viewLike = (Button) findViewById(R.id.button_viewLike);
    	button_viewDislike = (Button) findViewById(R.id.button_viewDislike);
    	button_viewMediaPlayer = (Button) findViewById(R.id.button_viewMediaPlayer);
    	button_viewSearch = (Button) findViewById(R.id.button_viewSearch);
    	button_viewFriendList = (Button) findViewById(R.id.button_viewFriendList);
    	button_viewLike.setOnClickListener(this);	
    	button_viewDislike.setOnClickListener(this);
    	button_viewMediaPlayer.setOnClickListener(this);
    	button_viewSearch.setOnClickListener(this);
    	button_viewFriendList.setOnClickListener(this);
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
				Intent likeIntent = new Intent(this, LikeListViewer.class);			
				startActivity(likeIntent);
			}
			break;
		case R.id.button_viewDislike:
			if(!CAMO_app.preferListIsLoaded()) {
				Toast.makeText(CAMO_AndroidActivity.this, "My Dislike is loading...", Toast.LENGTH_SHORT).show();
			}
			else {
				Intent dislikeIntent = new Intent(this, DislikeListViewer.class);			
				startActivity(dislikeIntent);
			}
			break;
		case R.id.button_viewMediaPlayer:		
			Intent mediaPlayerIntent = new Intent(this, MediaPlayer.class);			
			startActivity(mediaPlayerIntent);
			break;
		case R.id.button_viewSearch:
			Intent viewSearchIntent = new Intent(this, SearchViewer.class);			
			startActivity(viewSearchIntent);
			break;
		case R.id.button_viewFriendList:
			if(!CAMO_app.preferListIsLoaded()) {
				Toast.makeText(CAMO_AndroidActivity.this, "My Friends is loading...", Toast.LENGTH_SHORT).show();
			}
			else {
				Intent viewFriendListIntent = new Intent(this, FriendListViewer.class);
				startActivity(viewFriendListIntent);
			}
			break;
		}
	}
}
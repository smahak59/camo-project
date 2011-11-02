package cn.edu.nju.ws.camo.android.ui;
/**
 * @author Cunxin Jia
 *
 */
import android.app.Activity;
import android.content.Intent;
import android.graphics.ColorMatrixColorFilter;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;
import android.widget.Toast;
import cn.edu.nju.ws.camo.android.R;
import cn.edu.nju.ws.camo.android.connect.server.ServerConfig;
import cn.edu.nju.ws.camo.android.util.UtilConfig;
import cn.edu.nju.ws.camo.android.util.UtilParam;

public class CAMO_AndroidActivity extends Activity implements OnClickListener {
	private CAMO_Application CAMO_app;
	private ImageButton imageButton_viewLike;
	private ImageButton imageButton_viewDislike;
	private ImageButton imageButton_viewMediaPlayer;
	private ImageButton imageButton_viewSearch;
	//private ImageButton imageButton_viewFriendList;	


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
    	imageButton_viewLike =  (ImageButton) findViewById(R.id.imageButton_viewLike);
    	imageButton_viewDislike = (ImageButton) findViewById(R.id.imageButton_viewDislike);
    	imageButton_viewMediaPlayer = (ImageButton) findViewById(R.id.imageButton_viewMediaPlayer);
    	imageButton_viewSearch = (ImageButton) findViewById(R.id.imageButton_viewSearch);

    	

    	
    	imageButton_viewLike.setOnClickListener(this);	
    	imageButton_viewDislike.setOnClickListener(this);
    	imageButton_viewMediaPlayer.setOnClickListener(this);
    	imageButton_viewSearch.setOnClickListener(this);

    	imageButton_viewLike.setOnTouchListener(touchListener);
    	imageButton_viewDislike.setOnTouchListener(touchListener);
    	imageButton_viewMediaPlayer.setOnTouchListener(touchListener);
    	imageButton_viewSearch.setOnTouchListener(touchListener);
    	
	}


	/**onClick Listener*/
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()) {			
		case R.id.imageButton_viewLike:
			if(!CAMO_app.preferListIsLoaded()) {
				Toast.makeText(CAMO_AndroidActivity.this, "My Like is loading...", Toast.LENGTH_SHORT).show();
			}
			else {
				Intent likeIntent = new Intent(this, LikeListViewer.class);			
				startActivity(likeIntent);
			}
			break;
		case R.id.imageButton_viewDislike:
			if(!CAMO_app.preferListIsLoaded()) {
				Toast.makeText(CAMO_AndroidActivity.this, "My Dislike is loading...", Toast.LENGTH_SHORT).show();
			}
			else {
				Intent dislikeIntent = new Intent(this, DislikeListViewer.class);			
				startActivity(dislikeIntent);
			}
			break;
		case R.id.imageButton_viewMediaPlayer:		
			Intent mediaPlayerIntent = new Intent(this, MediaPlayer.class);			
			startActivity(mediaPlayerIntent);
			break;
		case R.id.imageButton_viewSearch:
			Intent viewSearchIntent = new Intent(this, SearchViewer.class);			
			startActivity(viewSearchIntent);
			break;
//		case R.id.button_viewFriendList:
//			if(!CAMO_app.preferListIsLoaded()) {
//				Toast.makeText(CAMO_AndroidActivity.this, "My Friends is loading...", Toast.LENGTH_SHORT).show();
//			}
//			else {
//				Intent viewFriendListIntent = new Intent(this, FriendListViewer.class);
//				startActivity(viewFriendListIntent);
//			}
//			break;
		}
	}

	public static final OnTouchListener touchListener = new OnTouchListener() { 
		  
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if(event.getAction()== MotionEvent.ACTION_DOWN){
				((ImageButton)v).getDrawable().setAlpha(150);
				v.invalidate();
			}
			else {
				((ImageButton)v).getDrawable().setAlpha(255);
				v.invalidate();
			}
			return false;
		} 

	};
 
}
package cn.edu.nju.ws.camo.android.ui;
/**
 * @author Cunxin Jia
 *
 */
import java.io.IOException;

import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.Toast;
import cn.edu.nju.ws.camo.android.R;
import cn.edu.nju.ws.camo.android.connect.ServerConfig;
import cn.edu.nju.ws.camo.android.user.User;
import cn.edu.nju.ws.camo.android.user.UserManager;
import cn.edu.nju.ws.camo.android.util.UtilConfig;
import cn.edu.nju.ws.camo.android.util.UtilParam;

public class CAMO_AndroidActivity extends Activity implements OnClickListener {
	private CAMO_Application CAMO_app;
	private ImageButton imageButton_viewLike;
	private ImageButton imageButton_viewDislike;
	private ImageButton imageButton_viewMediaPlayer;
	private ImageButton imageButton_viewSearch;
	private ProgressDialog progressDialog;
	private EditText editText_email;
	private EditText editText_password;
	private Button button_login;
	private Button button_register;
	private TableLayout loginPanel;
	private TableLayout mainPanel;

	
	
	//private ImageButton imageButton_viewFriendList;	


    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    	CAMO_app = (CAMO_Application) getApplication();    	
    	initServerParams();
    	initViewComponents();
    	testConnection();
    	       
    }
    
    public void onStart() {
    	super.onStart();
    	if(CAMO_app.getCurrentUser() == null) {
    		loginPanel.setVisibility(View.VISIBLE);
    		mainPanel.setVisibility(View.GONE);
    	}
    	else {
    		loginPanel.setVisibility(View.GONE);
    		mainPanel.setVisibility(View.VISIBLE);
    	}
    }
    
	
	public void onDestroy() {
		super.onDestroy();
		CAMO_app.closePlayList(CAMO_AndroidActivity.this);
	}
    
    private void testConnection() {
		class TestTask extends AsyncTask<Void, Void, Boolean[]> {

			@Override
			protected Boolean[] doInBackground(Void... params) {
				Boolean[] status = new Boolean[1];
				status[0] = ConnectionTester.getInstance().testConnection();
				return status;
			}
			protected void onPostExecute(Boolean[] status) {
				progressDialog.dismiss();
				if(status[0]) {
					button_login.setEnabled(true);
					button_register.setEnabled(true);
			    	imageButton_viewLike.setEnabled(true);
			    	imageButton_viewDislike.setEnabled(true);
			    	imageButton_viewMediaPlayer.setEnabled(true);
			    	imageButton_viewSearch.setEnabled(true);
				}
				else {
					Toast.makeText(CAMO_AndroidActivity.this, "Network Error!", Toast.LENGTH_LONG).show();
				}
			}			
		}
		progressDialog = new ProgressDialog(this);			  
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);  			
		progressDialog.setMessage("Testing network...");  
		progressDialog.setCancelable(false);
		progressDialog.show();
		new TestTask().execute();
		
	}

	private void initUserData() {
    	CAMO_app.initPreferList();	
    	CAMO_app.initFriendList();
    	CAMO_app.initIgnoredList();
    	CAMO_app.initPlayList(this);
	}

	private void initServerParams() {
    	UtilParam.ASSETS = getAssets();
    	UtilConfig.initParam();
    	ServerConfig.initParam();		
	}
	



	private void initViewComponents() {
		loginPanel = (TableLayout) findViewById(R.id.tableLayout_loginPanel);
		mainPanel = (TableLayout) findViewById(R.id.tableLayout_mainPanel);
    	imageButton_viewLike =  (ImageButton) findViewById(R.id.imageButton_viewLike);
    	imageButton_viewDislike = (ImageButton) findViewById(R.id.imageButton_viewDislike);
    	imageButton_viewMediaPlayer = (ImageButton) findViewById(R.id.imageButton_viewMediaPlayer);
    	imageButton_viewSearch = (ImageButton) findViewById(R.id.imageButton_viewSearch);
    	button_login = (Button) findViewById(R.id.button_login);
    	button_register = (Button) findViewById(R.id.button_register);
    	editText_email = (EditText) findViewById(R.id.editText_loginEmail);
    	editText_password = (EditText) findViewById(R.id.editText_loginPassword);

    	                   
    	button_login.setEnabled(false);
    	button_register.setEnabled(false);
    	imageButton_viewLike.setEnabled(false);
    	imageButton_viewDislike.setEnabled(false);
    	imageButton_viewMediaPlayer.setEnabled(false);
    	imageButton_viewSearch.setEnabled(false);
    	
    	imageButton_viewLike.setOnClickListener(this);	
    	imageButton_viewDislike.setOnClickListener(this);
    	imageButton_viewMediaPlayer.setOnClickListener(this);
    	imageButton_viewSearch.setOnClickListener(this);
    	button_login.setOnClickListener(this);
    	button_register.setOnClickListener(this);

    	imageButton_viewLike.setOnTouchListener(touchListener);
    	imageButton_viewDislike.setOnTouchListener(touchListener);
    	imageButton_viewMediaPlayer.setOnTouchListener(touchListener);
    	imageButton_viewSearch.setOnTouchListener(touchListener);
    	
	}


	/**onClick Listener*/
	public void onClick(View v) {
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
		case R.id.button_login:
			loginProcess();
			break;
		case R.id.button_register:
			Intent registerIntent = new Intent(this, RegisterViewer.class);
			startActivity(registerIntent);
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
	
	private void loginProcess() {
		String email = editText_email.getText().toString().trim();
		String password = editText_password.getText().toString().trim();
		if(email.equals("") || password.equals("")) {
			Toast.makeText(CAMO_AndroidActivity.this, "Please enter email and password!s", Toast.LENGTH_SHORT).show();
			return;
		}
		String[] params = {email, password};
		
		progressDialog = new ProgressDialog(this);			  
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);  			
		progressDialog.setMessage("Login...");  
		progressDialog.setCancelable(false);
		progressDialog.show();
		
		class LoginTask extends AsyncTask<String,Void,String> {
			private User loginUser;
			@Override
			protected String doInBackground(String... arg0) {
				try {
					loginUser = UserManager.getInstance().getUser(arg0[0], arg0[1]);
				} catch (IOException e) {
					e.printStackTrace();
				} catch (XmlPullParserException e) {
					e.printStackTrace();
				}
				return null;
			}
			
			protected void onPostExecute(String result) {
				progressDialog.dismiss();
				if(loginUser != null) {
					CAMO_app.setUser(loginUser);
					initUserData();
					Toast.makeText(CAMO_AndroidActivity.this, "Login successfully!", Toast.LENGTH_SHORT).show();
					loginSuc();
				}
				else {
					Toast.makeText(CAMO_AndroidActivity.this, "Email or Password error!", Toast.LENGTH_SHORT).show();
				}					
			}			
		}
		
		new LoginTask().execute(params);
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.main_menu, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.item_logout:
			CAMO_app.closePlayList(CAMO_AndroidActivity.this);
			mainPanel.setVisibility(View.GONE);
			loginPanel.setVisibility(View.VISIBLE);
			CAMO_app.logout();
			break;
		case R.id.item_exit:
			android.os.Process.killProcess(android.os.Process.myPid());
			break;
		}
		return true;
	}
	
	private void loginSuc() {
		editText_email.setText("");
		editText_password.setText("");		
		loginPanel.setVisibility(View.GONE);
		mainPanel.setVisibility(View.VISIBLE);
		
	}

	public static final OnTouchListener touchListener = new OnTouchListener() { 
		  
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
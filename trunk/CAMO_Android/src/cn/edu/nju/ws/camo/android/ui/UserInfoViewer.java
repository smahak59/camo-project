package cn.edu.nju.ws.camo.android.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import cn.edu.nju.ws.camo.android.R;
import cn.edu.nju.ws.camo.android.user.User;
import cn.edu.nju.ws.camo.android.user.interestgp.InterestGroup;
import cn.edu.nju.ws.camo.android.util.SerKeys;

public class UserInfoViewer extends Activity implements OnClickListener{
	private User user;
	private TextView textView_userInfoTitle;
	private TextView textView_userName;
	private TextView textView_userSex;
	private TextView textView_userEmail;
	private ImageButton imageButton_contactUser;
	private ImageButton imageButton_ignoreUser;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_info_viewer);
        initUserData();
        initComponents();
    }

	private void initUserData() {
		Intent intent = getIntent();
		user = (User) intent.getSerializableExtra(SerKeys.SER_USER);		
	}

	private void initComponents() {
		textView_userInfoTitle = (TextView) findViewById(R.id.textView_userInfoTitle);
		textView_userName = (TextView) findViewById(R.id.textView_userName);
		textView_userSex = (TextView) findViewById(R.id.textView_userSex);
		textView_userEmail = (TextView) findViewById(R.id.textView_userEmail);
		imageButton_contactUser = (ImageButton) findViewById(R.id.imageButton_contactUser);
		imageButton_ignoreUser = (ImageButton) findViewById(R.id.imageButton_ignoreUser);
		
		imageButton_contactUser.setOnTouchListener(CAMO_AndroidActivity.touchListener);
		imageButton_ignoreUser.setOnTouchListener(CAMO_AndroidActivity.touchListener);
		
		imageButton_contactUser.setOnClickListener(this);
		imageButton_ignoreUser.setOnClickListener(this);
		
		textView_userInfoTitle.setText(user.getName());
		textView_userName.setText("Name: " + user.getName());
		textView_userSex.setText("Sex: " + user.getSex());
		textView_userEmail.setText("Email: " + user.getEmail());
		
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.imageButton_contactUser:
			break;
		case R.id.imageButton_ignoreUser:
			User curUser = ((CAMO_Application) getApplication()).getCurrentUser();
			new InterestGroup(curUser).getUserIgnoreCmd(user).execute();
			break;
		}
		
	}
}

package cn.edu.nju.ws.camo.android.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import cn.edu.nju.ws.camo.android.R;
import cn.edu.nju.ws.camo.android.util.SerKeys;
import cn.edu.nju.ws.camo.android.util.User;

public class UserInfoViewer extends Activity{
	private User user;
	private TextView textView_userInfoTitle;
	private TextView textView_userName;
	private TextView textView_userSex;
	private TextView textView_userEmail;
	
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
		textView_userInfoTitle.setText(user.getName());
		textView_userName.setText("Name: " + user.getName());
		textView_userSex.setText("Sex: " + user.getSex());
		textView_userEmail.setText("Email: " + user.getEmail());
		
	}
}

package cn.edu.nju.ws.camo.android.ui;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import cn.edu.nju.ws.camo.android.R;
import cn.edu.nju.ws.camo.android.user.User;
import cn.edu.nju.ws.camo.android.user.UserManager;

public class RegisterViewer extends Activity implements OnClickListener {

	private Button button_confirm;
	private Button button_cancel;
	private EditText editText_name;
	private EditText editText_password;
	private EditText editText_passwordConfirm;
	private EditText editText_email;
	private Spinner spinner_gender;
	private ProgressDialog progressDialog;


	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);
		setTitle("Register");
		initViewComponents();
	}

	private void initViewComponents() {
		button_confirm = (Button) findViewById(R.id.button_regConfirm);
		button_cancel = (Button) findViewById(R.id.button_regCancel);
		editText_name = (EditText) findViewById(R.id.editText_regName);
		editText_password = (EditText) findViewById(R.id.editText_regPassword);
		editText_passwordConfirm = (EditText) findViewById(R.id.editText_regPasswordConfirm);
		editText_email = (EditText) findViewById(R.id.editText_regEmail);
		spinner_gender = (Spinner) findViewById(R.id.spinner_regGender);

		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.genders, R.layout.spinner_item);
		adapter.setDropDownViewResource(R.layout.spinner_item_dropdown);
		spinner_gender.setAdapter(adapter);

		button_confirm.setOnClickListener(this);
		button_cancel.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button_regConfirm:
			registerProcess();
			break;
		case R.id.button_regCancel:
			this.finish();
			break;
		}
	}
	
	private void initUserData() {
		CAMO_Application CAMO_app = (CAMO_Application)getApplication();
    	CAMO_app.initPreferList();	
    	CAMO_app.initFriendList();
    	CAMO_app.initIgnoredList();
    	CAMO_app.initPlayList(this);
	}

	private void registerProcess() {
		String email = editText_email.getText().toString().trim();
		String name = editText_name.getText().toString().trim();
		String password = editText_password.getText().toString().trim();
		String passwordConfirm = editText_passwordConfirm.getText().toString()
				.trim();
		String sex = spinner_gender.getSelectedItem().toString();

		if (!password.equals(passwordConfirm)) {
			Toast.makeText(RegisterViewer.this, "Please check password!", Toast.LENGTH_SHORT).show();
			return;
		}

		String[] params = { name, password, email, sex };

		progressDialog = new ProgressDialog(this);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setMessage("Loading...");
		progressDialog.setCancelable(false);
		progressDialog.show();

		class RegTask extends AsyncTask<String, Void, String> {
			int regStatus;

			@Override
			protected String doInBackground(String... params) {
				try {
					regStatus = UserManager.getInstance().addUser(params[0],
							params[1], params[2], params[3]);
					User newUser = UserManager.getInstance().getUser(params[2]);					
					if(newUser != null) {
						((CAMO_Application)getApplication()).setUser(newUser);
						initUserData();
					}
				} catch (IOException e) {
					e.printStackTrace();
				} catch (XmlPullParserException e) {
					e.printStackTrace();
				}
				return null;
			}

			protected void onPostExecute(String result) {
				progressDialog.dismiss();
				if (regStatus == 0) {
					Toast.makeText(RegisterViewer.this, "Registration failed!",
							Toast.LENGTH_SHORT).show();
				} else if (regStatus == 1) {
					Toast.makeText(RegisterViewer.this,
							"Registration successfully!", Toast.LENGTH_SHORT)
							.show();
					RegisterViewer.this.finish();
				}
			}

		}

		new RegTask().execute(params);
	}
}

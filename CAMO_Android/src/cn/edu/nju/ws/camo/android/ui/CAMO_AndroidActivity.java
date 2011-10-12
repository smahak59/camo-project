package cn.edu.nju.ws.camo.android.ui;

import cn.edu.nju.ws.camo.android.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class CAMO_AndroidActivity extends Activity implements OnClickListener {
	private Button button_viewRdfInstance;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        button_viewRdfInstance = (Button) findViewById(R.id.button_viewRdfInstance);
        button_viewRdfInstance.setOnClickListener(this);
    }
    
    
    /**onClick Listener*/
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()) {
		case R.id.button_viewRdfInstance:
			Intent i = new Intent(this, RdfInstanceViewer.class);
			startActivity(i);
			break;
		}
	}
}
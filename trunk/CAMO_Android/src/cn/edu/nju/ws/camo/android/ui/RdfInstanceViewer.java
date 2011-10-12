package cn.edu.nju.ws.camo.android.ui;

import cn.edu.nju.ws.camo.android.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class RdfInstanceViewer extends Activity implements OnClickListener{
	Button button_like;
	Button button_dislike;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rdf_instance_viewer);
		setTitle("RDF Instance Viwer");
		initButtons();
	}
	private void initButtons() {
		button_like = (Button)findViewById(R.id.button_like);
		button_dislike = (Button)findViewById(R.id.button_dislike);
		button_like.setOnClickListener(this);
		button_dislike.setOnClickListener(this);
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()) {
		case R.id.button_dislike:
			Toast.makeText(RdfInstanceViewer.this, "Dislike!", 500).show();
			break;
		case R.id.button_like:
			Toast.makeText(RdfInstanceViewer.this, "Like!", 500).show();
			break;
		}
	}
}

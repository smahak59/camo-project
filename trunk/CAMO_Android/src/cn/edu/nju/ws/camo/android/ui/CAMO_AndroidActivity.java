package cn.edu.nju.ws.camo.android.ui;
/**
 * @author Cunxin Jia
 *
 */
import cn.edu.nju.ws.camo.android.R;
import cn.edu.nju.ws.camo.android.rdf.RdfFactory;
import cn.edu.nju.ws.camo.android.rdf.UriInstance;
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
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        initViewComponents();
    }
    
    
    private void initViewComponents() {
    	button_viewLike = (Button) findViewById(R.id.button_viewLike);
    	button_viewDislike = (Button) findViewById(R.id.button_viewDislike);
    	button_viewInstance = (Button) findViewById(R.id.button_viewInstance);
    	button_viewLike.setOnClickListener(this);	
    	button_viewDislike.setOnClickListener(this);
    	button_viewInstance.setOnClickListener(this);
	}


	/**onClick Listener*/
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()) {			
		case R.id.button_viewLike:
			Intent likeIntent = new Intent(this, LikeViewer.class);
			startActivity(likeIntent);
			break;
		case R.id.button_viewDislike:
			Intent dislikeIntent = new Intent(this, DislikeViewer.class);
			startActivity(dislikeIntent);
			break;
		case R.id.button_viewInstance:
			Intent viewInstanceIntent = new Intent(this, RdfInstanceViewer.class);
			Bundle newBundle = new Bundle();
			UriInstance uri = RdfFactory.getInstance().createInstance("prime", "");
			newBundle.putSerializable(RdfInstanceViewer.SER_KEY, uri);
			viewInstanceIntent.putExtras(newBundle);
			startActivity(viewInstanceIntent);
			break;
		}
	}
}
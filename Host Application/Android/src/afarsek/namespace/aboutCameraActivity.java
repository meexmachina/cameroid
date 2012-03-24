package afarsek.namespace;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.TextView;

public class aboutCameraActivity extends Activity
{
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		Log.d("About Camera Activity", "Created a new 'aboutCameraActivity'");
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// Setup the window
		setContentView(R.layout.about_camera_panel);
		
		Intent infoIntent = getIntent();
		Bundle infoBundle = infoIntent.getExtras();

		// the camera's available property list
		String man = infoBundle.getString("Manufacturer");
		String model = infoBundle.getString("Model");
		String ver = infoBundle.getString("Version");
		String serNum = infoBundle.getString("SerialNumber");
	
		TextView manTV = (TextView) findViewById(R.id.manTextView);
		TextView modelTV = (TextView) findViewById(R.id.modelTextView);
		TextView verTV = (TextView) findViewById(R.id.verTextView);
		TextView serNumTV = (TextView) findViewById(R.id.serNumTextView);
		
		manTV.setText(man);
		modelTV.setText(model);
		verTV.setText(ver);
		serNumTV.setText(serNum);
		
	}
}

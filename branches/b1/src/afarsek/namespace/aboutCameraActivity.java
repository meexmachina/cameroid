package afarsek.namespace;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

public class aboutCameraActivity extends Activity
{
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// Setup the window
		setContentView(R.layout.about_camera_panel);
	}
}
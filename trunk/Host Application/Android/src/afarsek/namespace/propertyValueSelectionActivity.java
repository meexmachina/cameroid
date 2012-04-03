package afarsek.namespace;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

public class propertyValueSelectionActivity extends Activity
{
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		Log.d("Camera Property Value Selection", "Created a new 'propertyValueSelectionActivity'");
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// Setup the window
		setContentView(R.layout.camera_value_selection_panel);
	}
}

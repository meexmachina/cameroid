package afarsek.namespace;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

public class galleryTabPanelActivity extends Activity
{
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		Log.d("Gallery Activity", "Created a new 'galleryTabPanelActivity'");
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		// Setup the window
		setContentView(R.layout.gallery_tab_panel);
	}

	@Override
	public void onBackPressed()
	{
		Log.d("Gallery Activity", "Back button was pressed. going back to device selection activity.");
		Intent discoveryPanel = new Intent("afarsek.namespace.DEVICESELECTIONACTIVITY");
		startActivity(discoveryPanel);
		finish();
	};
}

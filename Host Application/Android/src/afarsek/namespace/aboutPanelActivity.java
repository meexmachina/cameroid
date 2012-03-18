package afarsek.namespace;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

public class aboutPanelActivity extends Activity
{
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		// Setup the window
		setContentView(R.layout.about_panel);
	}
}

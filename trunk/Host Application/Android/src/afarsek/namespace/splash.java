package afarsek.namespace;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

public class splash extends Activity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.splash);

		Thread splashTimer = new Thread()
		{
			public void run()
			{
				try
				{
					sleep(1000);
				} catch (InterruptedException e)
				{
					e.printStackTrace();
				} finally
				{
					Intent startProgram = new Intent("afarsek.namespace.DEVICESELECTIONACTIVITY");
					startProgram.putExtra(deviceSelectionActivity.EXTRA_SELECTION_INTENT_NAME, "afarsek.namespace.MAINPANELACTIVITY");
					startProgram.putExtra(deviceSelectionActivity.EXTRA_ABOUT_INTENT_NAME, "afarsek.namespace.ABOUTPANELACTIVITY");
					//startProgram.putExtra(deviceSelectionActivity.EXTRA_PREFERENCES_INTENT_NAME, "");
					//startProgram.putExtra(deviceSelectionActivity.EXTRA_HELP_INTENT_NAME, "");
					startProgram.putExtra(deviceSelectionActivity.EXTRA_DRAWABLE_ID, R.drawable.ic_camera);
					startActivity(startProgram);
				}
			}
		};

		splashTimer.start();
	}

	@Override
	protected void onPause()
	{
		// TODO Auto-generated method stub
		super.onPause();
		finish();
	}

}

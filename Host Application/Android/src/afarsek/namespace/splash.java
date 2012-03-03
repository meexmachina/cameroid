package afarsek.namespace;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class splash extends Activity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);

		Thread splashTimer = new Thread()
		{
			public void run()
			{
				try
				{
					sleep(2000);
				} catch (InterruptedException e)
				{
					e.printStackTrace();
				} finally
				{
					Intent startProgram = new Intent("afarsek.namespace.DEVICESELECTIONACTIVITY");
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

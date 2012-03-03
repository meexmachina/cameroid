package afarsek.namespace;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class mainPanelActivity extends Activity
{

	/**
	 * Internal properties
	 */
	private String			macAddress;
	private hardwareFacade	hwFacade				= null;

	/**
	 * Constants
	 */
	public static String	EXTRA_DEVICE_ADDRESS	= "device_address";

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// obtain the sent bundle
		Bundle extras = getIntent().getExtras();
		if (extras != null)
		{
			setMacAddress(extras.getString(EXTRA_DEVICE_ADDRESS));
		}

		hwFacade = new hardwareFacade(macAddress);
		if ( hwFacade == null)
		{
			
		}
	}

	@Override
	protected void onDestroy()
	{

	};

	@Override
	public void onBackPressed()
	{
		Intent discoveryPanel = new Intent("afarsek.namespace.DEVICESELECTIONACTIVITY");
		startActivity(discoveryPanel);
		finish();
	};

	/**
	 * Getters & Setters
	 */
	public String getMacAddress()
	{
		return macAddress;
	}

	public void setMacAddress(String macAddress)
	{
		this.macAddress = macAddress;
	};
}

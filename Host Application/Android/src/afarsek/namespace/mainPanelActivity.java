package afarsek.namespace;

import widget.ActionBar;
import widget.ActionBar.AbstractAction;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

public class mainPanelActivity extends TabActivity
{

	private String mConnectedDeviceName = null; // Name of the connected device
	private cameraControl mCameraControl = null;
	private ActionBar actionBar;
	private TabHost tabHost;

	// Constants
	public static final String EXTRA_DEVICE_ADDRESS = "device_address";
	public static final String DEVICE_NAME = "device_name";
	public static final String TOAST = "toast";

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		// Set up the window layout
		setContentView(R.layout.main_panel);

		// Setup the action-bar
		actionBar = (ActionBar) findViewById(R.id.actionbar_main);
		actionBar.setTitle("Main Window");
		actionBar.addAction(new preferencesAction());

		setTabs();

		// Set up the text view for camera connected
		// mCameraConnectedText = (TextView) findViewById(R.id.camera_connected);
		// mCameraConnectedText.setText(R.string.camera_not_connected);

		// Obtain the sent bundle
		Bundle extras = getIntent().getExtras();
		if (extras != null)
		{
			mConnectedDeviceName = extras.getString(EXTRA_DEVICE_ADDRESS);
		}

		// Get local Bluetooth adapter
		/*
		 * mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		 * 
		 * // If the adapter is null, then Bluetooth is not supported if (mBluetoothAdapter == null) { Toast.makeText(this,
		 * "Bluetooth is not available", Toast.LENGTH_LONG).show(); finish(); return; }
		 */

		// Button definitions
		/*
		 * Button captureButton = (Button) findViewById(R.id.capture_button); captureButton.setOnClickListener(new OnClickListener() {
		 * public void onClick(View v) { mCameraControl.capture(); } });
		 */

	}

	@Override
	protected void onStart()
	{
		super.onStart();

		if (mCameraControl == null)
			mCameraControl = new cameraControl(mConnectedDeviceName, mHandler);

		// Attempt to connect to the device
		mCameraControl.connect();
	}

	@Override
	protected void onStop()
	{
		if (mCameraControl != null)
			mCameraControl.disconnect();
		mCameraControl = null;

		super.onStop();
	};

	@Override
	protected void onDestroy()
	{
		super.onDestroy();

		if (mCameraControl != null)
			mCameraControl.disconnect();
	};

	@Override
	public void onBackPressed()
	{
		Intent discoveryPanel = new Intent("afarsek.namespace.DEVICESELECTIONACTIVITY");
		startActivity(discoveryPanel);
		finish();
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.option_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle item selection
		switch (item.getItemId())
		{
		case R.id.preferences:
			// newGame();
			return true;
		case R.id.help:
			// showHelp();
			return true;
		case R.id.about:
        	Intent aboutPanel = new Intent("afarsek.namespace.ABOUTPANELACTIVITY");
			startActivity(aboutPanel);
			super.onOptionsItemSelected(item);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void setTabs()
	{
		tabHost = getTabHost();
		
		addTab("Gallery", R.drawable.tab_gallery, galleryTabPanelActivity.class);
		addTab("Capture", R.drawable.tab_capture, generalTabPanelActivity.class);
		addTab("Advanced", R.drawable.tab_advanced, advancedTabPanelActivity.class);
		
		tabHost.setCurrentTab(1);
	}

	private void addTab(String labelId, int drawableId, Class<?> c)
	{
		Intent intent = new Intent(this, c);
		TabHost.TabSpec spec = tabHost.newTabSpec("tab" + labelId);

		View tabIndicator = LayoutInflater.from(this).inflate(R.layout.tab_indicator, getTabWidget(), false);
		TextView title = (TextView) tabIndicator.findViewById(R.id.title);
		title.setText(labelId);
		ImageView icon = (ImageView) tabIndicator.findViewById(R.id.icon);
		icon.setImageResource(drawableId);

		spec.setIndicator(tabIndicator);
		spec.setContent(intent);
		tabHost.addTab(spec);
	}

	// The Handler that gets information back from the hardwareFacade
	private final Handler mHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
			case messageDefinitions.MESSAGE_STATE_CHANGE:
				switch (msg.arg1)
				{
				case hardwareFacade.STATE_CONNECTED:
					actionBar.setTitle("Connected to " + mConnectedDeviceName);
					break;
				case hardwareFacade.STATE_CONNECTING:
					actionBar.setTitle("Connecting...");
					break;
				case hardwareFacade.STATE_LISTEN:
				case hardwareFacade.STATE_NONE:
					actionBar.setTitle("Disconnected");
					break;
				}
				break;
			case messageDefinitions.MESSAGE_CAMERA_CONNECTION_STATE:
				// if (msg.arg1 == 1)
				// mCameraConnectedText.setText(R.string.camera_connected);
				// else
				// mCameraConnectedText.setText(R.string.camera_not_connected);
				break;
			case messageDefinitions.MESSAGE_DEVICE_NAME:
				// save the connected device's name
				mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
				Toast.makeText(getApplicationContext(), "Connected to " + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
				break;
			case messageDefinitions.MESSAGE_TOAST:
				Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST), Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};

	private class preferencesAction extends AbstractAction
	{

		public preferencesAction()
		{
			super(R.drawable.ic_menu_preferences);

		}

		public void performAction(View view)
		{
			// Create the result Intent and include the MAC address
			Intent preferncesMainPanel = new Intent("afarsek.namespace.PREFERENCESPANELACTIVITY");
			// startMainPanel.putExtra(EXTRA_DEVICE_ADDRESS, address);
			startActivity(preferncesMainPanel);
		}

	}

}

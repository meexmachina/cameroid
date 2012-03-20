package afarsek.namespace;

import widget.ActionBar;
import widget.ActionBar.AbstractAction;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class mainPanelActivity extends TabActivity
{

	private String mConnectedDeviceName = null; // Name of the connected device
	private cameraControl mCameraControl = null;
	private ActionBar actionBar;
	private TabHost tabHost;
	private int mCurrentTab = 1;

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
			return super.onOptionsItemSelected(item);
		case R.id.help:
			// showHelp();
			return super.onOptionsItemSelected(item);
		case R.id.about:
			Intent aboutPanel = new Intent("afarsek.namespace.ABOUTPANELACTIVITY");
			startActivity(aboutPanel);
			return super.onOptionsItemSelected(item);
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
		super.onConfigurationChanged(newConfig);

		mCurrentTab = tabHost.getCurrentTab();

		// Set up the window layout
		setContentView(R.layout.main_panel);

		// Setup the action-bar
		actionBar = (ActionBar) findViewById(R.id.actionbar_main);
		actionBar.setTitle("Main Window");
		actionBar.addAction(new preferencesAction());

		setTabs();
	}

	private void setTabs()
	{
		tabHost = getTabHost();

		addTab("Gallery", R.drawable.tab_gallery, galleryTabPanelActivity.class);
		addTab("Capture", R.drawable.tab_capture, generalTabPanelActivity.class);
		addTab("Advanced", R.drawable.tab_advanced, advancedTabPanelActivity.class);

		tabHost.setCurrentTab(mCurrentTab);

		tabHost.setOnTabChangedListener(new OnTabChangeListener()
		{
			public void onTabChanged(String tabId)
			{

			}
		});

		getTabWidget().getChildAt(1).setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				int curTab = tabHost.getCurrentTab();
				if (curTab==1)	// if we are already in capture
				{
					if (mCameraControl!=null) mCameraControl.capture();
				} 
				else tabHost.setCurrentTab(1);
			}
		});

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

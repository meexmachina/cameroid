package afarsek.namespace;

import java.util.Timer;
import java.util.TimerTask;

import ptp.DevicePropDesc;
import widget.ActionBar;
import widget.ActionBar.AbstractAction;
import widget.CameraControlData;
import widget.CameraControlData.controlType;
import afarsek.namespace.cameraControl.StatusTask;
import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
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
	private Timer timer;

	int[] mUsedProperties;

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
		actionBar.setOnTitleClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				Intent aboutCameraIntent = new Intent("afarsek.namespace.ABOUTCAMERAACTIVITY");

				if (mCameraControl.cameraAttached() == 1)
				{
					aboutCameraIntent.putExtra("Manufacturer", mCameraControl.mDeviceInfo.manufacturer);
					aboutCameraIntent.putExtra("Model", mCameraControl.mDeviceInfo.model);
					aboutCameraIntent.putExtra("Version", mCameraControl.mDeviceInfo.deviceVersion);
					aboutCameraIntent.putExtra("SerialNumber", mCameraControl.mDeviceInfo.serialNumber);
				} else
				{
					aboutCameraIntent.putExtra("Manufacturer", "n/a");
					aboutCameraIntent.putExtra("Model", "n/a");
					aboutCameraIntent.putExtra("Version", "n/a");
					aboutCameraIntent.putExtra("SerialNumber", "n/a");
				}

				startActivity(aboutCameraIntent);
			}
		});
		actionBar.addAction(new preferencesAction());

		setTabs();

		// Obtain the sent bundle
		Bundle extras = getIntent().getExtras();
		if (extras != null)
		{
			mConnectedDeviceName = extras.getString(EXTRA_DEVICE_ADDRESS);
		}

		timer = new Timer();
		timer.scheduleAtFixedRate(new StatusTask(), 1000, 200);
	}
	
	class StatusTask extends TimerTask
	{
		public void run()
		{
			// update the 
		}
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
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		Bundle extras = data.getExtras();

		mUsedProperties = extras.getIntArray("ChosenProperties");

		// TODO: Read those properties from the device
		int[] values = new int[mUsedProperties.length];

		// Rearrange the shown widgets
		controlType[] types = new controlType[mUsedProperties.length];
		for (int i = 0; i < types.length; i++)
		{
			switch (mUsedProperties[i])
			{
			case DevicePropDesc.BatteryLevel:
				types[i] = CameraControlData.controlType.controlType_Battery;
				break;
			case DevicePropDesc.WhiteBalance:
				types[i] = CameraControlData.controlType.controlType_WB;
				break;
			case DevicePropDesc.FStop:
				types[i] = CameraControlData.controlType.controlType_Aperture;
				break;
			case DevicePropDesc.FocalLength:
				types[i] = CameraControlData.controlType.controlType_FocalLength;
				break;
			case DevicePropDesc.FocusDistance:
				types[i] = CameraControlData.controlType.controlType_FocusDistance;
				break;
			case DevicePropDesc.FocusMode:
				types[i] = CameraControlData.controlType.controlType_FocusMode;
				break;
			case DevicePropDesc.FlashMode:
				types[i] = CameraControlData.controlType.controlType_Flash;
				break;
			case DevicePropDesc.ExposureTime:
				types[i] = CameraControlData.controlType.controlType_Shutter;
				break;
			case DevicePropDesc.ExposureIndex:
				types[i] = CameraControlData.controlType.controlType_ISO;
				break;
			}

			values[i] = 2;
		}

		((generalTabPanelActivity) (this.getLocalActivityManager().getCurrentActivity())).setupControlWidgets(types, values);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle item selection
		switch (item.getItemId())
		{
		case R.id.preferences:
			return super.onOptionsItemSelected(item);
		case R.id.help:
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
				if (curTab == 1) // if we are already in capture
				{
					if (mCameraControl != null)
						mCameraControl.capture();
				} else
					tabHost.setCurrentTab(1);
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
				// if connected then start negotiation
				// if not connected hide icons
				break;
				
			case messageDefinitions.MESSAGE_CAMERA_DEVICE_INFO:
				break;
				
			case messageDefinitions.MESSAGE_CAMERA_STORAGE_INFO:
				break;
				
			case messageDefinitions.MESSAGE_CAMERA_PROPERTY_INFO:
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
			if (mUsedProperties == null)
			{
				int[] temp =
				{ 0x5001, 0x5005, 0x5007, 0x5008, 0x5009, 0x500a, 0x500c, 0x500d, 0x500F };
				mUsedProperties = temp;
			}

			preferncesMainPanel.putExtra("CurrentChosen", mUsedProperties);

			if (mCameraControl.mDeviceInfo != null)
				preferncesMainPanel.putExtra("AvailableProperties", mCameraControl.mDeviceInfo.propertiesSupported);
			else
			{
				// for debug
				int[] properties =
				{ 0x5001, 0x5005, 0x5007, 0x5008, 0x5009, 0x500a, 0x500c, 0x500d, 0x500F };
				preferncesMainPanel.putExtra("AvailableProperties", properties);
			}

			// startMainPanel.putExtra(EXTRA_DEVICE_ADDRESS, address);
			startActivityForResult(preferncesMainPanel, 1);
		}
	}

}

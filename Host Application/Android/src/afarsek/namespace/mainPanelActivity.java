package afarsek.namespace;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import ptp.DevicePropDesc;
import afarsek.widget.ActionBar;
import afarsek.widget.ActionBar.AbstractAction;
import afarsek.widget.CameraControlData;
import afarsek.widget.CameraControlData.controlType;
import android.app.LocalActivityManager;
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
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class mainPanelActivity extends TabActivity
{
	/**************************************************************************************************
	 * Class Properties
	 */
	private String mConnectedDeviceName = null; // Name of the connected device
	private cameraControl mCameraControl = null;
	private ActionBar mActionBar;
	private TabHost mTabHost;
	private int mCurrentTab = 1;
	private Timer mStatusTimer;
	private LocalActivityManager mLocalActivityManager = null;
	private int mCurrentUpdatingProperty = 0;

	// Constants
	public static final String EXTRA_DEVICE_ADDRESS = "device_address";
	public static final String DEVICE_NAME = "device_name";
	public static final String TOAST = "toast";

	/**************************************************************************************************
	 * Class Methods
	 */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		// Set up the window layout
		setContentView(R.layout.main_panel);

		// Setup the action-bar
		mActionBar = (ActionBar) findViewById(R.id.actionbar_main);
		mActionBar.setOnTitleClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				Intent aboutCameraIntent = new Intent("afarsek.namespace.ABOUTCAMERAACTIVITY");

				if (mCameraControl.cameraAttached() == 0 || mCameraControl.mDeviceInfo == null)
				{
					aboutCameraIntent.putExtra("Manufacturer", "n/a");
					aboutCameraIntent.putExtra("Model", "n/a");
					aboutCameraIntent.putExtra("Version", "n/a");
					aboutCameraIntent.putExtra("SerialNumber", "n/a");
				} else
				{
					aboutCameraIntent.putExtra("Manufacturer", mCameraControl.mDeviceInfo.manufacturer);
					aboutCameraIntent.putExtra("Model", mCameraControl.mDeviceInfo.model);
					aboutCameraIntent.putExtra("Version", mCameraControl.mDeviceInfo.deviceVersion);
					aboutCameraIntent.putExtra("SerialNumber", mCameraControl.mDeviceInfo.serialNumber);

				}

				startActivity(aboutCameraIntent);
			}
		});

		mActionBar.addAction(new preferencesAction());

		setTabs();

		mLocalActivityManager = this.getLocalActivityManager();

		// Obtain the sent bundle
		Bundle extras = getIntent().getExtras();
		if (extras != null)
		{
			mConnectedDeviceName = extras.getString(EXTRA_DEVICE_ADDRESS);
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
		if (mStatusTimer != null)
			mStatusTimer.cancel();

		if (mCameraControl != null)
			mCameraControl.disconnect();
		mCameraControl = null;

		super.onStop();
	};

	@Override
	protected void onPause()
	{
		ArrayList<controlType> list = new ArrayList<controlType>();
		mCameraControl.setActivePropertyEvents(list);

		if (mStatusTimer != null)
			mStatusTimer.cancel();
		super.onPause();
	}

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

		int[] availableProperties = extras.getIntArray("AvailableProperties");
		boolean[] activeProperties = extras.getBooleanArray("ActiveProperties");
		int availablePropertyCount = extras.getInt("AvailablePropertiesCount");

		// get the current general panel reference
		generalTabPanelActivity tempActivity = ((generalTabPanelActivity) (mLocalActivityManager.getCurrentActivity()));
		tempActivity.setWidgetState(availableProperties, activeProperties, availablePropertyCount);

		ArrayList<controlType> list = ((generalTabPanelActivity) (mLocalActivityManager.getCurrentActivity())).getCurrentActiveProperties();
		mCameraControl.setActivePropertyEvents(list);
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

	private void setTabs()
	{
		mTabHost = getTabHost();

		addTab("Gallery", R.drawable.tab_gallery, galleryTabPanelActivity.class);
		addTab("Capture", R.drawable.tab_capture, generalTabPanelActivity.class);
		addTab("Advanced", R.drawable.tab_advanced, advancedTabPanelActivity.class);

		mTabHost.setCurrentTab(mCurrentTab);

		mTabHost.setOnTabChangedListener(new OnTabChangeListener()
		{
			public void onTabChanged(String tabId)
			{
				mCurrentTab = mTabHost.getCurrentTab();
				if (mCurrentTab == 1)
					((generalTabPanelActivity) (mLocalActivityManager.getCurrentActivity())).setMainClassHandlerObject(mGeneralTabHandler);
			}
		});

		getTabWidget().getChildAt(1).setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				int curTab = mTabHost.getCurrentTab();
				if (curTab == 1) // if we are already in capture
				{
					if (mCameraControl != null)
						mCameraControl.capture();
				} else
				{
					mTabHost.setCurrentTab(1);
				}
			}
		});

	}

	private void addTab(String labelId, int drawableId, Class<?> c)
	{
		Intent intent = new Intent(this, c);
		TabHost.TabSpec spec = mTabHost.newTabSpec("tab" + labelId);

		View tabIndicator = LayoutInflater.from(this).inflate(R.layout.tab_indicator, getTabWidget(), false);
		TextView title = (TextView) tabIndicator.findViewById(R.id.title);
		title.setText(labelId);
		ImageView icon = (ImageView) tabIndicator.findViewById(R.id.icon);
		icon.setImageResource(drawableId);

		spec.setIndicator(tabIndicator);
		spec.setContent(intent);
		mTabHost.addTab(spec);
	}

	// The Handler that gets information back from the hardwareFacade
	private final Handler mGeneralTabHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			// ((generalTabPanelActivity)
			// (mLocalActivityManager.getCurrentActivity())).updateControlWidgetData(controlType.getTypeFromCode(propCode1), propVal1);
			switch (msg.what)
			{
			case messageDefinitions.PROPERTY_CHOISE_WAS_CHANGED:
				ArrayList<controlType> list = ((generalTabPanelActivity) (mLocalActivityManager.getCurrentActivity()))
						.getCurrentActiveProperties();
				mCameraControl.setActivePropertyEvents(list);
				break;

			case messageDefinitions.PROPERTY_VAL_WAS_CHANGED:
				// set the new value to the hardware
				break;
			}
		}
	};

	private void updateAllRopertyDescs()
	{
		if (mStatusTimer != null)
			mStatusTimer.cancel();

		mStatusTimer = new Timer();
		mStatusTimer.scheduleAtFixedRate(new StatusTask(), 0, 500);

		mCurrentUpdatingProperty = 0;
	}

	/**************************************************************************************************
	 * Timer task which obtains the the current camera connection state
	 */
	class StatusTask extends TimerTask
	{
		public void run()
		{
			if (mCameraControl == null)
				return;

			if (mCameraControl.mCameraAttached == 0)
				return;

			ArrayList<controlType> list = ((generalTabPanelActivity) (mLocalActivityManager.getCurrentActivity()))
					.getCurrentActiveProperties();
			if (list.size() == 0)
			{
				if (mStatusTimer != null)
					mStatusTimer.cancel();
			}

			mCameraControl.getPropertiesDescriptions(list.get(mCurrentUpdatingProperty).getCode());

			if (mCurrentUpdatingProperty == (list.size() - 1))
			{
				if (mStatusTimer != null)
					mStatusTimer.cancel();
			}

			mCameraControl.getPropertiesDescriptions(list.get(mCurrentUpdatingProperty).getCode());
			mCurrentUpdatingProperty++;
		}
	}

	// The Handler that gets information back from the hardwareFacade
	private final Handler mHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{

			// THE STATE WAS CHANGED
			case messageDefinitions.MESSAGE_STATE_CHANGE:
				switch (msg.arg1)
				{
				case hardwareFacade.STATE_CONNECTED:
					mActionBar.setTitle("Connected to " + mConnectedDeviceName);
					break;
				case hardwareFacade.STATE_CONNECTING:
					mActionBar.setTitle("Connecting...");
					break;
				case hardwareFacade.STATE_LISTEN:
				case hardwareFacade.STATE_NONE:
					mActionBar.setTitle("Disconnected");
					break;
				}
				break;

			// CAMERA CONNECTION STATE HAS CHANGED
			case messageDefinitions.MESSAGE_CAMERA_CONNECTION_STATE:
				// if connected then start negotiation
				// if not connected hide icons

				break;

			// DEVICE INFO MESSAGE WAS RECEIVED
			case messageDefinitions.MESSAGE_CAMERA_DEVICE_INFO:
				if (mCameraControl.cameraAttached() == 1)
				{
					mActionBar.setTitle("Connected: " + mCameraControl.mDeviceInfo.manufacturer + " " + mCameraControl.mDeviceInfo.model);
					ArrayList<controlType> list = ((generalTabPanelActivity) (mLocalActivityManager.getCurrentActivity()))
							.getCurrentActiveProperties();
					mCameraControl.setActivePropertyEvents(list);

				} else
				{
					mActionBar.setTitle("Camera is disconnected.");
				}
				((generalTabPanelActivity) (mLocalActivityManager.getCurrentActivity())).setDeviceInfo(mCameraControl.mDeviceInfo);
				updateAllRopertyDescs();
				break;

			// STORAGE INFO MESSAGE WAS RECEIVED
			case messageDefinitions.MESSAGE_CAMERA_STORAGE_INFO:

				break;

			// CAMERA PROPERTY INFO WAS RECEIVED
			case messageDefinitions.MESSAGE_CAMERA_PROPERTY_INFO:
				int propCode = msg.arg1;
				if (mCurrentTab != 1)
					break;

				int propIndex = mCameraControl.findProperty(propCode);

				if (propIndex == -1)
					break;

				DevicePropDesc prop = mCameraControl.mPropertyArray.get(propIndex);
				controlType type = controlType.getTypeFromCode(propCode);

				((generalTabPanelActivity) (mLocalActivityManager.getCurrentActivity())).updateControlWidgetData(type, prop);
				break;

			// CAMERA PROPERTY VALUE
			case messageDefinitions.MESSAGE_CAMERA_PROPERTY_VAL:
				int propCode1 = msg.arg1;
				int propVal1 = msg.arg2;
				((generalTabPanelActivity) (mLocalActivityManager.getCurrentActivity())).updateControlWidgetData(
						controlType.getTypeFromCode(propCode1), propVal1);
				break;

			// DEVICE NAME MESSAGE WAS RECEIVED
			case messageDefinitions.MESSAGE_DEVICE_NAME:
				// save the connected device's name
				mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
				Toast.makeText(getApplicationContext(), "Connected to " + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
				break;

			// MESSAGE AFARSEK ID
			case messageDefinitions.MESSAGE_AFARSEK_ID:

				break;

			// TOAST NEEDS TO BE SHOWN
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
			// if accidently we came here while not being in the general panel then exit
			if (mCurrentTab != 1)
				return;

			// create the intent to go to the preferences panel
			Intent preferncesMainPanel = new Intent("afarsek.namespace.PREFERENCESPANELACTIVITY");

			// get the current general panel reference
			generalTabPanelActivity tempActivity = ((generalTabPanelActivity) (mLocalActivityManager.getCurrentActivity()));

			// the availableProperties is all the properties that are available by the connected camera
			// the activeProperties is the bitmap of usage
			int[] availableProperties = new int[CameraControlData.controlType.values().length];
			boolean[] activeProperties = new boolean[CameraControlData.controlType.values().length];
			int availablePropertyCount = 0;
			for (int i = 0; i < availableProperties.length; i++)
			{
				controlType type = tempActivity.getType(i);

				int code = type.getCode();
				boolean available = tempActivity.getAvailable(type);
				boolean active = tempActivity.getActivated(type);

				if (available == true)
				{
					availableProperties[availablePropertyCount] = code;
					activeProperties[availablePropertyCount] = active;
					availablePropertyCount++;
				}
			}

			preferncesMainPanel.putExtra("AvailableProperties", availableProperties);
			preferncesMainPanel.putExtra("ActiveProperties", activeProperties);
			preferncesMainPanel.putExtra("AvailablePropertiesCount", availablePropertyCount);

			startActivityForResult(preferncesMainPanel, 1);
		}
	}

}

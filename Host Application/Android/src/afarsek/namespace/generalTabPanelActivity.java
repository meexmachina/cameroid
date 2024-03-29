package afarsek.namespace;

import java.util.ArrayList;
import ptp.DeviceInfo;
import ptp.DevicePropDesc;
import afarsek.widget.CameraControlAdapter;
import afarsek.widget.CameraControlData;
import afarsek.widget.CameraControlData.controlType;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.Toast;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;

public class generalTabPanelActivity extends Activity
{
	/**************************************************************************************************
	 * Class Properties
	 */
	private GridView mControlGridView;
	private Handler mMainPanelHandler;
	private CameraControlAdapter mCameraControlAdapter;
	private ArrayList<CameraControlData> mControledList;
	private Context mContext;

	private controlType[] mTypes = controlType.values();
	private int[] mCurrentValues = new int[controlType.values().length];
	private boolean[] mReadOnly = new boolean[controlType.values().length];
	private boolean[] mActivated = new boolean[controlType.values().length];
	private boolean[] mAvailable = new boolean[controlType.values().length];
	private DevicePropDesc[] mPropertyDescs = new DevicePropDesc[mTypes.length];

	// private Vector<Object>[] mEnumerations = new Vector<Object>[controlType.values().length];

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		Log.d("General Activity", "Created a new 'generalTabPanelActivity'");
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		mContext = this;

		// Setup the window
		setContentView(R.layout.general_tab_panel);

		// initialize the lists
		for (int i = 0; i < mTypes.length; i++)
		{
			mCurrentValues[i] = mTypes[i].getInitialValue();
			mReadOnly[i] = mTypes[i].getInitialReadOnly();
			mActivated[i] = mTypes[i].getInitialActive();
			mAvailable[i] = mTypes[i].getInitialAvailable();
			mPropertyDescs[i] = null;
		}

		mControledList = new ArrayList<CameraControlData>();
		mControlGridView = (GridView) findViewById(R.id.camera_control_grid);
		mCameraControlAdapter = new CameraControlAdapter(this);
		mCameraControlAdapter.setCameraControlDataList(mControledList);
		mControlGridView.setAdapter(mCameraControlAdapter);

		registerForContextMenu(mControlGridView);
		mControlGridView.setOnItemClickListener(new OnItemClickListener()
		{
			public void onItemClick(AdapterView<?> gridView, View view, int pos, long id)
			{
				if (mPropertyDescs[pos]==null)
					return;
				
				if (mPropertyDescs[pos].writable==false)
				{
					Toast.makeText(getApplicationContext(), "This property '" + controlType.getTypeFromCode(mPropertyDescs[pos].propertyCode).toString() + "' is read-only.", Toast.LENGTH_SHORT).show();
					return;
				}
				
				propertyValueSelectionDialog dialog = new propertyValueSelectionDialog(mContext, mPropertyDescs[pos], mPropertyChangeHandler, 160);
				dialog.show();
			}
		});

		updateControlWidgets();
	}

	@Override
	public void onBackPressed()
	{
		Log.d("General Activity", "Back button was pressed. going back to device selection activity.");
		Intent discoveryPanel = new Intent("afarsek.namespace.DEVICESELECTIONACTIVITY");
		startActivity(discoveryPanel);
		finish();
	};

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo)
	{
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
		controlType type = mCameraControlAdapter.getItemType(info.position);
		String[] menuItems = null;

		// if the current item is read only then show a toast and return
		if (mReadOnly[type.ordinal()] == true)
		{
			Toast.makeText(getApplicationContext(), "This property '" + type.toString() + "' is read-only.", Toast.LENGTH_SHORT).show();
			return;
		}

		switch (type)
		{

		// BATTERY LEVEL
		case controlType_Battery:
			Log.d("General Activity", "ContextMenu - battery level was pressed.");
			return;

			// WHITE BALANCE
		case controlType_WB:
			Log.d("General Activity", "ContextMenu - white balance was pressed.");
			String[] temp1 =
			{ "Manual K", "Auto WB", "Single Auto WB", "Daylight", "Fluorescence", "Tungsten", "Flash" };
			menuItems = temp1.clone();
			break;

		// APERTURE
		case controlType_Aperture:
			Log.d("General Activity", "ContextMenu - aperture was pressed.");
			String[] temp2 =
			{ "F/1.0", "F/1.4", "F/1.8", "F/2.0", "F/2.8", "F/3.5", "F/4.0", "F/5.6", "F/8.0" };
			menuItems = temp2.clone();
			break;

		// FOCAL LENGTH
		case controlType_FocalLength:
			Log.d("General Activity", "ContextMenu - focal length was pressed.");
			break;

		// FOCUS DISTANCE
		case controlType_FocusDistance:
			Log.d("General Activity", "ContextMenu - focus distance was pressed.");
			String[] temp4 =
			{ "aa", "bb" };
			menuItems = temp4.clone();
			break;

		// FOCUS MODE
		case controlType_FocusMode:
			Log.d("General Activity", "ContextMenu - focus mode was pressed.");
			String[] temp5 =
			{ "Manual", "AF-S", "AF-C" };
			menuItems = temp5.clone();
			break;

		// FLASH MODE
		case controlType_Flash:
			Log.d("General Activity", "ContextMenu - flash mode was pressed.");
			String[] temp6 =
			{ "Auto Flash", "Flash Off", "Fill Flash", "Red-Eye Auto", "Red-Eye Fill", "External Sync" };
			menuItems = temp6.clone();
			break;

		// SHUTTER SPEED
		case controlType_Shutter:
			Log.d("General Activity", "ContextMenu - shutter speed was pressed.");
			String[] temp7 =
			{ "aa", "bb" };
			menuItems = temp7.clone();
			break;

		// ISO SPEED
		case controlType_ISO:
			Log.d("General Activity", "ContextMenu - ISO speed was pressed.");
			String[] temp8 =
			{ "100", "200", "400", "600", "800", "1000", "1250", "1600", "2000", "2400", "3200", "6400" };
			menuItems = temp8.clone();
			break;
		default:
		}

		menu.setHeaderTitle(type.toString());

		if (menuItems == null)
			return;

		for (int i = 0; i < menuItems.length; i++)
		{
			menu.add(Menu.NONE, i, i, menuItems[i]);
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item)
	{
		return (super.onOptionsItemSelected(item));
	}

	private void updateControlWidgets()
	{
		mCameraControlAdapter.clear();
		for (int i = 0; i < mTypes.length; i++)
		{
			if (mAvailable[i] == true && mActivated[i] == true)
			{
				CameraControlData view = new CameraControlData(this, mTypes[i], mCurrentValues[i]);
				view.setAvailable(mAvailable[i]);
				view.setActive(mActivated[i]);
				mControledList.add(view);
			}
		}
		mCameraControlAdapter.notifyDataSetChanged();
	}

	public void updateControlWidgetData(controlType type, int value)
	{
		Log.d("General Activity", "Update Widget - updating widget of type " + type.toString());

		for (int i = 0; i < mTypes.length; i++)
		{
			if (mTypes[i] == type)
			{
				mCurrentValues[i] = value;
			}
		}

		for (int i = 0; i < mControledList.size(); i++)
		{
			if (mControledList.get(i).getType() == type)
			{
				mControledList.get(i).setControlValue(value);
				mCameraControlAdapter.notifyDataSetChanged();
			}
		}
	}

	public void updateControlWidgetData(controlType type, DevicePropDesc prop)
	{
		int value = 0;
		boolean found = false;

		for (int i = 0; i < mTypes.length; i++)
		{
			if (mTypes[i] == type)
			{
				found = true;

				mCurrentValues[i] = Integer.parseInt(prop.getValue().toString());
				mReadOnly[i] = !prop.writable;
				value = mCurrentValues[i];
					
				mPropertyDescs[type.ordinal()] = prop;
				// if (mRanges[i]==null)
				// Vector
			}
		}

		if (found == false)
			return;

		for (int i = 0; i < mControledList.size(); i++)
		{
			if (mControledList.get(i).getType() == type)
			{
				mControledList.get(i).setControlValue(value);
				mCameraControlAdapter.notifyDataSetChanged();
			}
		}

	}

	public CameraControlAdapter getListAdapter()
	{
		return mCameraControlAdapter;
	}

	public ArrayList<controlType> getCurrentActiveProperties()
	{
		ArrayList<controlType> list = new ArrayList<controlType>();

		for (int i = 0; i < mTypes.length; i++)
		{
			if (getActivated(mTypes[i]) && getAvailable(mTypes[i]))
			{
				list.add(mTypes[i]);
			}
		}
		return list;
	}

	public void setWidgetState(int[] availableProperties, boolean[] activeProperties, int availablePropertyCount)
	{
		for (int i = 0; i < mTypes.length; i++)
		{
			for (int j = 0; j < availablePropertyCount; j++)
			{
				if (mTypes[i].getCode() == availableProperties[j])
				{
					if (mAvailable[i] == true)
					{
						mActivated[i] = activeProperties[j];
					} else
					{
						mActivated[i] = false;
					}
				}
			}
		}

		updateControlWidgets();
	}

	public controlType getType(int pos)
	{
		return mTypes[pos];
	}

	public int getCurrentValues(controlType type)
	{
		return mCurrentValues[type.ordinal()];
	}

	public boolean getReadOnly(controlType type)
	{
		return mReadOnly[type.ordinal()];
	}

	public boolean getActivated(controlType type)
	{
		return mActivated[type.ordinal()];
	}

	public boolean getAvailable(controlType type)
	{
		return mAvailable[type.ordinal()];
	}

	public void setDeviceInfo(DeviceInfo info)
	{
		// we need to filter out those properties that are not available in the camera
		int[] properties = info.propertiesSupported;

		for (int i = 0; i < mTypes.length; i++)
		{
			mAvailable[i] = false;

			for (int j = 0; j < properties.length; j++)
			{
				if (mTypes[i].getCode() == properties[j])
				{
					mAvailable[i] = true;
					break;
				}
			}

			// if not available then deactivate
			if (mAvailable[i] == false)
			{
				mActivated[i] = false;
			}
		}
		updateControlWidgets();
	}

	public void setMainClassHandlerObject(Handler handler)
	{
		mMainPanelHandler = handler;
	}

	// The Handler that gets information back from the hardwareFacade
	private final Handler mPropertyChangeHandler = new Handler()
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
					break;
				case hardwareFacade.STATE_CONNECTING:
					break;
				}
				break;
			}
		}
	};

}

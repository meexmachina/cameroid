package afarsek.namespace;

import java.util.ArrayList;

import ptp.DeviceInfo;
import ptp.DevicePropDesc;
import widget.CameraControlAdapter;
import widget.CameraControlData;
import widget.CameraControlData.controlType;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
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
	private CameraControlAdapter mCameraControlAdapter;
	private ArrayList<CameraControlData> mControledList;

	private controlType[] mTypes = controlType.values();
	private int[] mCurrentValues = new int[controlType.values().length];
	private boolean[] mReadOnly = new boolean[controlType.values().length];
	private boolean[] mActivated = new boolean[controlType.values().length];
	private boolean[] mAvailable = new boolean[controlType.values().length];
	private DevicePropDesc.Range[] mRanges = new DevicePropDesc.Range[controlType.values().length];

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		Log.d("General Activity", "Created a new 'generalTabPanelActivity'");
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		// Setup the window
		setContentView(R.layout.general_tab_panel);

		// initialize the lists
		for (int i = 0; i < mTypes.length; i++)
		{
			mCurrentValues[i] = mTypes[i].getInitialValue();
			mReadOnly[i] = mTypes[i].getInitialReadOnly();
			mActivated[i] = mTypes[i].getInitialActive();
			mAvailable[i] = mTypes[i].getInitialAvailable();
		}

		mControledList = new ArrayList<CameraControlData>();
		mControlGridView = (GridView) findViewById(R.id.camera_control_grid);
		mCameraControlAdapter = new CameraControlAdapter(this);
		mCameraControlAdapter.setCameraControlDataList(mControledList);
		mControlGridView.setAdapter(mCameraControlAdapter);

		registerForContextMenu(mControlGridView);

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

		switch (type)
		{
		case controlType_Battery:
			Log.d("General Activity", "ContextMenu - battery level was pressed.");
			Toast.makeText(getApplicationContext(), "This property '" + type.toString() + "' is read-only.", Toast.LENGTH_SHORT).show();
			return;
		case controlType_WB:
			Log.d("General Activity", "ContextMenu - white balance was pressed.");
			String[] temp1 =
			{ "Manual K", "Auto WB", "Single Auto WB", "Daylight", "Fluorescence", "Tungsten", "Flash" };
			menuItems = temp1.clone();
			break;
		case controlType_Aperture:
			Log.d("General Activity", "ContextMenu - aperture was pressed.");
			String[] temp2 =
			{ "F/1.0", "F/1.4", "F/1.8", "F/2.0", "F/2.8", "F/3.5", "F/4.0", "F/5.6", "F/8.0" };
			menuItems = temp2.clone();
			break;
		case controlType_FocalLength:
			Log.d("General Activity", "ContextMenu - focal length was pressed.");
			Toast.makeText(getApplicationContext(), "This property '" + type.toString() + "' is read-only.", Toast.LENGTH_SHORT).show();
			// String[] temp3 = {"aa","bb"};
			// menuItems = temp3.clone();
			break;
		case controlType_FocusDistance:
			Log.d("General Activity", "ContextMenu - focus distance was pressed.");
			String[] temp4 =
			{ "aa", "bb" };
			menuItems = temp4.clone();
			break;
		case controlType_FocusMode:
			Log.d("General Activity", "ContextMenu - focus mode was pressed.");
			String[] temp5 =
			{ "Manual", "AF-S", "AF-C" };
			menuItems = temp5.clone();
			break;
		case controlType_Flash:
			Log.d("General Activity", "ContextMenu - flash mode was pressed.");
			String[] temp6 =
			{ "Auto Flash", "Flash Off", "Fill Flash", "Red-Eye Auto", "Red-Eye Fill", "External Sync" };
			menuItems = temp6.clone();
			break;
		case controlType_Shutter:
			Log.d("General Activity", "ContextMenu - shutter speed was pressed.");
			String[] temp7 =
			{ "aa", "bb" };
			menuItems = temp7.clone();
			break;
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

	public void updateControlWidgetData(controlType type, int value, DevicePropDesc.Range range)
	{
		Log.d("General Activity", "Update Widget - updating widget of type " + type.toString());

		for (int i = 0; i < mTypes.length; i++)
		{
			if (mTypes[i] == type)
			{
				mCurrentValues[i] = value;
				mRanges[i] = range;
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

	public CameraControlAdapter getListAdapter()
	{
		return mCameraControlAdapter;
	}

	public void setWidgetState(int[] availableProperties, boolean[] activeProperties, int availablePropertyCount)
	{
		for (int i = 0; i < mTypes.length; i++)
		{
			for (int j=0; j<availablePropertyCount; j++)
			{
				if (mTypes[i].getCode()==availableProperties[j])
				{
					if (mAvailable[i]==true)
					{
						mActivated[i] = activeProperties[j];
					}
					else
					{
						mActivated[i] = false;
					}
				}
			}
		}
		
		updateControlWidgets();
	}
	
	public controlType getType (int pos)
	{
		return mTypes[pos];
	}
	
	public int getCurrentValues (controlType type)
	{
		return mCurrentValues[type.ordinal()];
	}
	
	public boolean getReadOnly (controlType type)
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
	
	public DevicePropDesc.Range getRange(controlType type)
	{
		return mRanges[type.ordinal()];
	}

	public void setDeviceInfo(DeviceInfo info)
	{
		// we need to filter out those properties that are not available in the camera
		int[] properties = info.propertiesSupported;
		
		for (int i=0; i<mTypes.length; i++)
		{
			mAvailable[i] = false;
			
			for (int j = 0; j<properties.length; j++)
			{
				if (mTypes[i].getCode() == properties[j])
				{
					mAvailable[i] = true;
					break;
				}
			}
			
			// if not available then deactivate 
			if (mAvailable[i]==false)
			{
				mActivated[i] = false;
			}
		}
	}

}

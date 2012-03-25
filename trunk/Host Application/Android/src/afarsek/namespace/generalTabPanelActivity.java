package afarsek.namespace;

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

	private controlType[] mTypes = controlType.values();
	/*
	 * { controlType.controlType_Battery, controlType.controlType_WB, controlType.controlType_Aperture, controlType.controlType_FocalLength,
	 * controlType.controlType_FocusDistance, controlType.controlType_FocusMode, controlType.controlType_Flash,
	 * controlType.controlType_Shutter, controlType.controlType_ISO };
	 */
	private int[] mCurrentValues =
	{ -1, -1, -1, -1, -1, -1, -1, -1, -1 };

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		Log.d("General Activity", "Created a new 'generalTabPanelActivity'");
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		// Setup the window
		setContentView(R.layout.general_tab_panel);

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

	@Override
	protected void onResume()
	{
		super.onResume();

		mControlGridView = (GridView) findViewById(R.id.camera_control_grid);
		mCameraControlAdapter = new CameraControlAdapter(this);
		mControlGridView.setAdapter(mCameraControlAdapter);

		registerForContextMenu(mControlGridView);

		setupControlWidgets(mTypes, mCurrentValues);
	}

	public void setupControlWidgets(controlType[] types, int[] values)
	{
		mCameraControlAdapter.clear();
		mTypes = types;
		mCurrentValues = values;
		for (int i = 0; i < types.length; i++)
		{
			CameraControlData view = new CameraControlData(this, types[i], values[i]);
			mCameraControlAdapter.add(view);
		}
	}

	public void updateControlWidgetData(controlType type, int value, DevicePropDesc.Range range)
	{
		Log.d("General Activity", "Update Widget - updating widget of type " + type.toString());
		for (int i = 0; i < mCameraControlAdapter.getCount(); i++)
		{
			CameraControlData view = (CameraControlData) mCameraControlAdapter.getItem(i);
			if (view.getType() == type)
			{
				view.setControlValue(value);
				mCameraControlAdapter.notifyDataSetChanged();
			}

			if (mTypes[i] == type)
			{
				mCurrentValues[i] = value;
			}
		}
	}

	public CameraControlAdapter getListAdapter()
	{
		return mCameraControlAdapter;
	}

	public void setWidgetState(int[] availableProperties, boolean[] activeProperties, int availablePropertyCount)
	{
		for (int i = 0; i<availablePropertyCount; i++)
		{
			// we need to find for each availableProperties[i] the node in the adapter
			int j = 0;
			boolean found = false;
			for (j = 0; j<mCameraControlAdapter.getCount(); j++)
			{
				if (((CameraControlData) mCameraControlAdapter.getItem(j)).getType().getCode()==availableProperties[i])
				{
					found = true;
				}
			}
			
			if (found == true)
			{
				if (((CameraControlData) mCameraControlAdapter.getItem(j)).getAvailable()==true)
				{
					((CameraControlData) mCameraControlAdapter.getItem(j)).setActive(activeProperties[i]);
				}
				else
				{
					((CameraControlData) mCameraControlAdapter.getItem(j)).setActive(false);
				}
			}
		}
	}
}

package afarsek.namespace;

import widget.CameraControlAdapter;
import widget.CameraControlData;
import widget.CameraControlData.controlType;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
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
	private GridView mControlGridView;
	private CameraControlAdapter mCameraControlAdapter;

	private controlType[] mTypes =
	{ controlType.controlType_Battery, controlType.controlType_WB, controlType.controlType_Aperture, controlType.controlType_FocalLength,
			controlType.controlType_FocusDistance, controlType.controlType_FocusMode, controlType.controlType_Flash,
			controlType.controlType_Shutter, controlType.controlType_ISO };
	private int[] mCurrentValues =
	{ -1, -1, -1, -1, -1, -1, -1, -1, -1 };

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		// Setup the window
		setContentView(R.layout.general_tab_panel);

	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo)
	{
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
		controlType type = mCameraControlAdapter.getItemType(info.position);
		String title = "";
		String[] menuItems = null;

		switch (type)
		{
		case controlType_Battery:
			Toast.makeText(getApplicationContext(), "This property is read-only.", Toast.LENGTH_SHORT).show();
			return;
		case controlType_WB:
			title = "White Balance";
			String[] temp1 = {"aa","bb"};
			menuItems = temp1.clone();
			break;
		case controlType_Aperture:
			title = "Aperture";
			String[] temp2 = {"aa","bb"};
			menuItems = temp2.clone();
			break;
		case controlType_FocalLength:
			title = "Focal Length";
			String[] temp3 = {"aa","bb"};
			menuItems = temp3.clone();
			break;
		case controlType_FocusDistance:
			title = "Focus Distance";
			String[] temp4 = {"aa","bb"};
			menuItems = temp4.clone();
			break;
		case controlType_FocusMode:
			title = "Focus Mode";
			String[] temp5 = {"aa","bb"};
			menuItems = temp5.clone();
			break;
		case controlType_Flash:
			title = "Flash Mode";
			String[] temp6 = {"aa","bb"};
			menuItems = temp6.clone();
			break;
		case controlType_Shutter:
			title = "Shutter Speed";
			String[] temp7 = {"aa","bb"};
			menuItems = temp7.clone();
			break;
		case controlType_ISO:
			title = "ISO Speed";
			String[] temp8 = {"aa","bb"};
			menuItems = temp8.clone();
			break;
		default:
		}

		menu.setHeaderTitle(title);
		
		if (menuItems==null) return;

		for (int i = 0; i < menuItems.length; i++)
		{
			menu.add(Menu.NONE, i, i, menuItems[i]);
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
		case 1:
			// AdapterView.AdapterContextMenuInfo info=
			// (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();

			// delete(info.id);
			// return(true);
		}

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

		/*
		 * mControlGridView.setOnItemClickListener(new OnItemClickListener() { // AdapterView<?> parent, View v, int position, long id
		 * public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) { Toast.makeText(getApplicationContext(), "Bla",
		 * Toast.LENGTH_SHORT).show();
		 * 
		 * } });
		 */
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
		super.onConfigurationChanged(newConfig);
		mCameraControlAdapter.notifyDataSetChanged();

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

	@Override
	public void onBackPressed()
	{
		Intent discoveryPanel = new Intent("afarsek.namespace.DEVICESELECTIONACTIVITY");
		startActivity(discoveryPanel);
		finish();
	};
}

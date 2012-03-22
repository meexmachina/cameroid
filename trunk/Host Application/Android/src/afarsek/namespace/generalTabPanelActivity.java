package afarsek.namespace;

import widget.CameraControlAdapter;
import widget.CameraControlData;
import widget.CameraControlData.controlType;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.Toast;

public class generalTabPanelActivity extends Activity
{
	private GridView mControlGridView;
	private CameraControlAdapter mCameraControlAdapter;

	private controlType[] mTypes =
	{ controlType.controlType_Battery, controlType.controlType_WB, controlType.controlType_Aperture, controlType.controlType_FocalLength,
			controlType.controlType_FocusDistance, controlType.controlType_FocusMode, controlType.controlType_Flash,
			controlType.controlType_Shutter, controlType.controlType_ISO };
	private int[] mCurrentValues = {-1, -1, -1, -1, -1, -1, -1, -1, -1};

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		// Setup the window
		setContentView(R.layout.general_tab_panel);

		mControlGridView = (GridView) findViewById(R.id.camera_control_grid);
		mCameraControlAdapter = new CameraControlAdapter(this);
		mControlGridView.setAdapter(mCameraControlAdapter);

		setupControlWidgets(mTypes, mCurrentValues);

		mControlGridView.setOnItemClickListener(new OnItemClickListener()
		{
			// AdapterView<?> parent, View v, int position, long id
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
			{
				Toast.makeText(getApplicationContext(), "Bla", Toast.LENGTH_SHORT).show();

			}
		});
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		// setupControlWidgets();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
		super.onConfigurationChanged(newConfig);
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

	@Override
	public void onBackPressed()
	{
		Intent discoveryPanel = new Intent("afarsek.namespace.DEVICESELECTIONACTIVITY");
		startActivity(discoveryPanel);
		finish();
	};
}

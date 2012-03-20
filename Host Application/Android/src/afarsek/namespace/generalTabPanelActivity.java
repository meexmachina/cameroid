package afarsek.namespace;

import widget.CameraControlAdapter;
import widget.CameraControlData;

import android.app.Activity;
import android.content.Intent;
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

		CameraControlData view = new CameraControlData(this, CameraControlData.controlType.controlType_Aperture, 280);
		mCameraControlAdapter.add(view);
		CameraControlData view1 = new CameraControlData(this, CameraControlData.controlType.controlType_Shutter, 2500);
		mCameraControlAdapter.add(view1);
		CameraControlData view2 = new CameraControlData(this, CameraControlData.controlType.controlType_WB, 4);
		mCameraControlAdapter.add(view2);
		CameraControlData view3 = new CameraControlData(this, CameraControlData.controlType.controlType_Flash, 4);
		mCameraControlAdapter.add(view3);

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
	public void onBackPressed()
	{
		Intent discoveryPanel = new Intent("afarsek.namespace.DEVICESELECTIONACTIVITY");
		startActivity(discoveryPanel);
		finish();
	};
}

package afarsek.namespace;

import java.util.ArrayList;
import java.util.List;

import widget.CameraControlView;

import afarsek.namespace.deviceSelectionActivity.ImageAdapter;
import afarsek.namespace.deviceSelectionActivity.ImageAdapter.CustomAdapterView;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

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
		
		CameraControlView view = new CameraControlView(this,0);
		view.setControlType(CameraControlView.controlType.controlType_Aperture);
		mCameraControlAdapter.add(view);
		CameraControlView view1 = new CameraControlView(this,1);
		view.setControlType(CameraControlView.controlType.controlType_Flash);
		mCameraControlAdapter.add(view1);
	}

	@Override
	public void onBackPressed()
	{
		Intent discoveryPanel = new Intent("afarsek.namespace.DEVICESELECTIONACTIVITY");
		startActivity(discoveryPanel);
		finish();
	};

	public class CameraControlAdapter extends BaseAdapter
	{
		Context MyContext;
		private List<CameraControlView> mControledList;

		public CameraControlAdapter(Context _MyContext)
		{
			MyContext = _MyContext;
			mControledList = new ArrayList<CameraControlView>();
		}

		public void add(CameraControlView view)
		{
			mControledList.add(view);
			notifyDataSetChanged();
		}

		public int getCount()
		{
			/* Set the number of element we want on the grid */
			return mControledList.size();
		}

		public void clear()
		{
			mControledList.clear();
			notifyDataSetChanged();
		}

		public View getView(int position, View convertView, ViewGroup parent)
		{
			CameraControlView CameraControlView = mControledList.get(position);
			return CameraControlView;
		}

		public Object getItem(int arg0)
		{
			if (arg0 >= 0 && arg0 < mControledList.size())
			{
				return mControledList.get(arg0);
			}

			return null;
		}

		public long getItemId(int arg0)
		{
			return arg0;
		}

		class CustomAdapterView extends LinearLayout
		{
			public CustomAdapterView(Context context, String deviceName, String deviceAddress, int id)
			{
				super(context);
				setId(id);

				// container is a horizontal layer
				setOrientation(LinearLayout.HORIZONTAL);
				setPadding(0, 6, 0, 6);

				// image:params
				LinearLayout.LayoutParams Params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				Params.setMargins(6, 0, 6, 0);
				// image:itself
				ImageView ivLogo = new ImageView(context);
				// load image
				ivLogo.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_camera));

				// image:add
				addView(ivLogo, Params);

				// vertical layer for text
				Params = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
				LinearLayout PanelV = new LinearLayout(context);
				PanelV.setOrientation(LinearLayout.VERTICAL);
				PanelV.setGravity(Gravity.BOTTOM);

				TextView textName = new TextView(context);
				textName.setTextSize(20);
				textName.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
				textName.setText(deviceName);
				PanelV.addView(textName);

				TextView textAddress = new TextView(context);
				textAddress.setTextSize(16);
				textAddress.setText(deviceAddress);
				PanelV.addView(textAddress);

				addView(PanelV, Params);
			}
		}
	}
}

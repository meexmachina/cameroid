package afarsek.namespace;

import java.util.ArrayList;
import widget.CameraControlView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
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

		CameraControlView view = new CameraControlView(this);
		view.setControlType(CameraControlView.controlType.controlType_Aperture);
		mCameraControlAdapter.add(view);
		CameraControlView view1 = new CameraControlView(this);
		view.setControlType(CameraControlView.controlType.controlType_Flash);
		mCameraControlAdapter.add(view1);

		mControlGridView.setOnItemClickListener(new OnItemClickListener()
		{
			//AdapterView<?> parent, View v, int position, long id
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
			{
				Toast.makeText(getApplicationContext(),"Bla", Toast.LENGTH_SHORT)
				.show();

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

	public class CameraControlAdapter extends BaseAdapter
	{
		Context context;
		private ArrayList<CameraControlView> mControledList;

		public CameraControlAdapter(Context _MyContext)
		{
			context = _MyContext;
			mControledList = new ArrayList<CameraControlView>();
		}

		public View getView(int position, View convertView, ViewGroup parent)
		{
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			View gridView;

			if (convertView == null)
			{

				gridView = new View(context);

				gridView = inflater.inflate(R.layout.camera_control_item, null);

				ImageView mSymbolView = (ImageView) gridView.findViewById(R.id.control_symbol);

				mSymbolView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_widget_shutter));
			} else
			{
				gridView = (View) convertView;
			}
			return gridView;
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
	}
}

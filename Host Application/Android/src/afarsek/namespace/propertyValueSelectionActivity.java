package afarsek.namespace;

import java.util.Vector;

import ptp.DevicePropDesc;
import widget.CameraControlData;
import widget.CameraControlData.controlType;
import afarsek.namespace.aboutCameraActivity.CameraPropertyArrayAdapter;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class propertyValueSelectionActivity extends Activity
{
	private Handler mGeneralPanelHandler;
	private DevicePropDesc mProp;
	private DevicePropDesc.Range mRange;
	private Vector<?> mEnumeration;
	private controlType mType;
	private String[] mListItems;
	private ArrayAdapter<String> mListAdapter;
	private ListView mListView;
	private TextView mCurrentValue;
	private TextView mPropertyName;
	private ImageView mPropertyIcon;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		Log.d("Camera Property Value Selection", "Created a new 'propertyValueSelectionActivity'");
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// Setup the window
		setContentView(R.layout.camera_value_selection_panel);

		Bundle extras = getIntent().getExtras();

		mProp = (DevicePropDesc) extras.get("PROPERTY DESCRIPTION");
		mGeneralPanelHandler = (Handler) extras.get("VALUE CHANGE HANDLER");
		mRange = mProp.getRange();
		mEnumeration = mProp.getEnumeration();
		mType = controlType.getTypeFromCode(mProp.getCode());
		mListView = (ListView) findViewById(R.id.property_value_list);
		mCurrentValue = (TextView) findViewById(R.id.property_cur_value);
		mPropertyName = (TextView) findViewById(R.id.property_text);
		mPropertyIcon = (ImageView) findViewById(R.id.property_icon);
		mPropertyName.setText(mType.toString());

		
		if (mRange != null)
		{
			mListView.setVisibility(View.GONE);
			DisplayMetrics metrics = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(metrics);
			int height = metrics.heightPixels;
			
			
		} else if (mEnumeration != null)
		{
			mListItems = CameraControlData.createProperyEnumList(mType, mEnumeration);
			
			mListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mListItems);
			mListView.setAdapter(mListAdapter);
			mListView.setVisibility(View.VISIBLE);
			
			// When item is tapped, toggle checked properties of CheckBox
			mListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
			{
				public void onItemClick(AdapterView<?> parent, View item, int position, long id)
				{
					mCurrentValue.setText(mListItems[position]);
					mGeneralPanelHandler.obtainMessage(messageDefinitions.PROPERTY_SEL_VALUE_CHANGED,
							Integer.getInteger(mEnumeration.get(position).toString()), 0);
				}
			});
		}
	}
}

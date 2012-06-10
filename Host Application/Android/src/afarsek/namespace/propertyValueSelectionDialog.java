package afarsek.namespace;

import java.util.Vector;

import ptp.DevicePropDesc;
import afarsek.widget.CameraControlData;
import afarsek.widget.CameraControlData.controlType;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class propertyValueSelectionDialog extends Dialog
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
	private LinearLayout mTouchPad;
	private Context mContext;
	private int mHeight;

	public propertyValueSelectionDialog(Context context, DevicePropDesc prop, Handler handler, int height)
	{
		super(context);
		this.mContext = context;

		mProp = prop;
		mGeneralPanelHandler = handler;
		mHeight = height;

		mRange = mProp.getRange();
		mEnumeration = mProp.getEnumeration();

		Log.d("Camera Property Value Selection", "Created a new 'propertyValueSelectionDialog'");

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// Setup the window
		setContentView(R.layout.camera_value_selection_panel);

		mType = controlType.getTypeFromCode(mProp.propertyCode);
		mListView = (ListView) findViewById(R.id.property_value_list);
		mCurrentValue = (TextView) findViewById(R.id.property_cur_value);
		mPropertyName = (TextView) findViewById(R.id.property_text);
		mPropertyIcon = (ImageView) findViewById(R.id.property_icon);
		mTouchPad = (LinearLayout) findViewById(R.id.property_value_touchpad);
		mPropertyName.setText(mType.toString());

		mCurrentValue.setText(CameraControlData.convertRawValue(mType, Integer.valueOf(prop.currentValue.toString())));

		if (mRange != null)
		{
			mListView.setVisibility(View.GONE);
			mTouchPad.setVisibility(View.VISIBLE);
			mTouchPad.setLayoutParams(new ViewGroup.LayoutParams(-1, mHeight));

			mTouchPad.setOnTouchListener(new View.OnTouchListener()
			{
				public boolean onTouch(View v, MotionEvent event)
				{

					// TODO Auto-generated method stub
					return false;
				}
			});

		} else if (mEnumeration != null)
		{
			mListItems = CameraControlData.createProperyEnumList(mType, mEnumeration);

			ArrayAdapter<String> mListAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_list_item_1, mListItems)
			{

				@Override
				public View getView(int position, View convertView, ViewGroup parent)
				{
					View view = super.getView(position, convertView, parent);
					TextView textView = (TextView) view.findViewById(android.R.id.text1);
					/* YOUR CHOICE OF COLOR */
					textView.setTextColor(Color.BLACK);
					return view;
				}
			};

			mListView.setAdapter(mListAdapter);
			mListView.setVisibility(View.VISIBLE);
			mTouchPad.setVisibility(View.GONE);

			// When item is tapped, toggle checked properties of CheckBox
			mListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
			{
				public void onItemClick(AdapterView<?> parent, View item, int position, long id)
				{
					mCurrentValue.setText(mListItems[position]);
					mGeneralPanelHandler.obtainMessage(messageDefinitions.PROPERTY_SEL_VALUE_CHANGED,
							Integer.valueOf(mEnumeration.get(position).toString()), 0).sendToTarget();
				}
			});
		}
	}
}

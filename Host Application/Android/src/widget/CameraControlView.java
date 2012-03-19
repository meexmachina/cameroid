package widget;

import widget.ActionBar.Action;
import afarsek.namespace.R;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CameraControlView extends LinearLayout implements OnClickListener
{
	public enum controlType
	{
		controlType_Aperture, controlType_Shutter, controlType_Focus, controlType_WB, controlType_ISO, controlType_Battery, controlType_Flash;
	};

	private controlType mControlType;
	private LayoutInflater mInflater;
	private LinearLayout mItemView;
	private LinearLayout mValueLayout;
	private ImageView mSymbolView;
	private TextView mValueView;
	
	private int mVal;

	public CameraControlView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mItemView = (LinearLayout) mInflater.inflate(R.layout.camera_control_item, this);
		addView(mItemView);
		
		mSymbolView = (ImageView) mItemView.findViewById(R.id.control_symbol);
		mValueLayout = (LinearLayout) mItemView.findViewById(R.id.value_layout);
		mValueView = (TextView) mValueLayout.findViewById(R.id.control_value);
		
		setControlType(controlType.controlType_Aperture);
		setControlValue(0);
	}
	
	
	
	public void setControlValue(int val)
	{
		mVal = val;
		switch (mControlType)
		{
		case controlType_Aperture:
			mValueView.setText(String.valueOf(val));
			break;
		case controlType_Shutter:
			mValueView.setText(String.valueOf(val));
			break;
		case controlType_Focus:
			mValueView.setText(String.valueOf(val));
			break;
		case controlType_WB:
			mValueView.setText(String.valueOf(val));
			break;
		case controlType_ISO:
			mValueView.setText(String.valueOf(val));
			break;
		case controlType_Battery:
			mValueView.setText(String.valueOf(val));
			break;
		case controlType_Flash:
			mValueView.setText(String.valueOf(val));
			break;
		default:
		}
	}

	public void setControlType(controlType type)
	{
		mControlType = type;
		switch (type)
		{
		case controlType_Aperture:
			mSymbolView.setImageResource(R.drawable.ic_widget_aperture);
			break;
		case controlType_Shutter:
			mSymbolView.setImageResource(R.drawable.ic_widget_shutter);
			break;
		case controlType_Focus:
			mSymbolView.setImageResource(R.drawable.ic_widget_focus);
			break;
		case controlType_WB:
			mSymbolView.setImageResource(R.drawable.ic_widget_wb);
			break;
		case controlType_ISO:
			mSymbolView.setImageResource(R.drawable.ic_widget_iso);
			break;
		case controlType_Battery:
			mSymbolView.setImageResource(R.drawable.ic_widget_battery);
			break;
		case controlType_Flash:
			mSymbolView.setImageResource(R.drawable.ic_widget_flash);
			break;
		default:
		}

		mSymbolView.setVisibility(View.VISIBLE);
	}

	public void onClick(View view)
	{
		final Object tag = view.getTag();
		if (tag instanceof Action)
		{
			final Action action = (Action) tag;
			action.performAction(view);
		}
	}

	public int getValue()
	{
		return mVal;
	}

	public controlType getType()
	{
		return mControlType;
	}

}

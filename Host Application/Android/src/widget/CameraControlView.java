package widget;

import afarsek.namespace.R;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CameraControlView extends LinearLayout
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

	public CameraControlView(Context context, AttributeSet attrs, controlType type)
	{
		super(context, attrs);
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mItemView = (LinearLayout) mInflater.inflate(R.layout.camera_control_item, this);
		addView(mItemView);
		
		mSymbolView = (ImageView) mItemView.findViewById(R.id.control_symbol);
		mValueLayout = (LinearLayout) mItemView.findViewById(R.id.value_layout);
		mValueView = (TextView) mValueLayout.findViewById(R.id.control_value);
		
		mControlType = type;

	}

}

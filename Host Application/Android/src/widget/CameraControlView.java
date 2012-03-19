package widget;

import afarsek.namespace.R;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

public class CameraControlView extends LinearLayout
{
	public enum controlType
	{
		controlType_Aperture, controlType_Shutter, controlType_Focus, controlType_WB, controlType_ISO, controlType_Battery, controlType_Flash;
	};
	
	controlType mControlType;

	public CameraControlView(Context context, AttributeSet attrs, controlType type)
	{
		super(context, attrs);
		LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = layoutInflater.inflate(R.layout.camera_control_item, this);
		
		mControlType = type;
		
	}

}

package widget;

import afarsek.namespace.R;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CameraControlData
{
	public enum controlType
	{
		controlType_Aperture, controlType_Shutter, controlType_Focus, controlType_WB, controlType_ISO, controlType_Battery, controlType_Flash;
	};
	
	private Context mContext;
	private controlType mType;
	private int mVal;
	private Drawable mIconDrawable;
	private String mActualText;
	private boolean mReadOnly;

	public CameraControlData(Context context, controlType type, int val)
	{
		mContext = context;
		setControlType(controlType.controlType_Aperture);
		setControlValue(0);
	}
	
	public Drawable getIcon ()
	{
		return mIconDrawable;
	}
	
	public boolean isReadOnly()
	{
		return mReadOnly;
	}
	
	public String getText ()
	{
		return mActualText;
	}

	public void setControlValue(int val)
	{
		mVal = val;
		switch (mType)
		{
		case controlType_Aperture:
			mActualText = String.valueOf(val);
			break;
		case controlType_Shutter:
			mActualText = String.valueOf(val);
			break;
		case controlType_Focus:
			mActualText = String.valueOf(val);
			break;
		case controlType_WB:
			mActualText = String.valueOf(val);
			break;
		case controlType_ISO:
			mActualText = String.valueOf(val);
			break;
		case controlType_Battery:
			mActualText = String.valueOf(val);
			break;
		case controlType_Flash:
			mActualText = String.valueOf(val);
			break;
		default:
		}
	}

	public void setControlType(controlType type)
	{
		mType = type;
		switch (type)
		{
		case controlType_Aperture:
			mIconDrawable = mContext.getResources().getDrawable(R.drawable.ic_widget_aperture);
			mReadOnly = false;
			break;
		case controlType_Shutter:
			mIconDrawable = mContext.getResources().getDrawable(R.drawable.ic_widget_shutter);
			mReadOnly = false;
			break;
		case controlType_Focus:
			mIconDrawable = mContext.getResources().getDrawable(R.drawable.ic_widget_focus);
			mReadOnly = false;
			break;
		case controlType_WB:
			mIconDrawable = mContext.getResources().getDrawable(R.drawable.ic_widget_wb);
			mReadOnly = false;
			break;
		case controlType_ISO:
			mIconDrawable = mContext.getResources().getDrawable(R.drawable.ic_widget_iso);
			mReadOnly = false;
			break;
		case controlType_Battery:
			mIconDrawable = mContext.getResources().getDrawable(R.drawable.ic_widget_battery);
			mReadOnly = true;
			break;
		case controlType_Flash:
			mIconDrawable = mContext.getResources().getDrawable(R.drawable.ic_widget_flash);
			mReadOnly = false;
			break;
		default:
		}
	}

	public int getValue()
	{
		return mVal;
	}

	public controlType getType()
	{
		return mType;
	}

}

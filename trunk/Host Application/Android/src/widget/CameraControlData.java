package widget;

import java.text.DecimalFormat;

import afarsek.namespace.R;
import android.content.Context;
import android.graphics.drawable.Drawable;

public class CameraControlData
{
	public enum controlType
	{
		controlType_Aperture, controlType_Shutter, controlType_Focus, controlType_WB, controlType_ISO, controlType_Battery, controlType_Flash;
	};

	private int[] mShutterValues =
	{ 2, 4, 8, 15, 30, 60, 125, 250, 500, 1000, 2000, 4000, 10000 };

	private Context mContext;
	private controlType mType;
	private int mVal;
	private Drawable mIconDrawable;
	private Drawable mValueBackground;
	private String mActualText;
	private boolean mReadOnly;
	private boolean mIsTextValue;

	public CameraControlData(Context context, controlType type, int val)
	{
		mContext = context;
		setControlType(type);
		setControlValue(val);
	}

	public Drawable getIcon()
	{
		return mIconDrawable;
	}

	public boolean isReadOnly()
	{
		return mReadOnly;
	}

	public Drawable getValueBackground()
	{
		return mValueBackground;
	}

	public String getText()
	{
		return mActualText;
	}

	public int getValue()
	{
		return mVal;
	}

	public controlType getType()
	{
		return mType;
	}

	public void setControlValue(int val)
	{
		mVal = val;
		switch (mType)
		{
		case controlType_Aperture:
			mActualText = "F/" + String.valueOf((double) (val) / 100.0);

			break;
		case controlType_Shutter:
			if (val >= 10000) // more then a second
			{
				double fScaled = val;
				DecimalFormat df = new DecimalFormat("#.#");
				mActualText = df.format(fScaled / 10000.0);
			} else
			{
				DecimalFormat zeroDForm = new DecimalFormat("#");
				double fScaled = val;
				Double closeVal = Double.valueOf(zeroDForm.format(10000.0 / fScaled));
				int iCloseVal = closeVal.intValue();
				mActualText = "1/" + String.valueOf(iCloseVal);
			}
			break;
		case controlType_Focus:
			mActualText = String.valueOf(val);
			break;
		case controlType_WB:
			mActualText = "";
			switch (val)
			{
			case 0x0001:		// manual K
				mValueBackground = mContext.getResources().getDrawable(R.drawable.ic_widget_wb_kelvin);
				break;
			case 0x0002:		// Auto
				mValueBackground = mContext.getResources().getDrawable(R.drawable.ic_widget_wb_auto);
				break;
			case 0x0003:		// One-Push auto
				mValueBackground = mContext.getResources().getDrawable(R.drawable.ic_widget_wb_auto);
				break;
			case 0x0004:		// Daylight
				mValueBackground = mContext.getResources().getDrawable(R.drawable.ic_widget_wb_sunny);
				break;					
			case 0x0005:		// Floura
				mValueBackground = mContext.getResources().getDrawable(R.drawable.ic_widget_wb_floura);	
				break;				
			case 0x0006:		// Tungsten
				mValueBackground = mContext.getResources().getDrawable(R.drawable.ic_widget_wb_tongstan);
				break;		
			case 0x0007:		// Flash
				mValueBackground = mContext.getResources().getDrawable(R.drawable.ic_widget_wb_flash);
				break;					
			}
			mValueBackground = mContext.getResources().getDrawable(R.drawable.ic_widget_wb_auto);
			break;
		case controlType_ISO:
			if (val == 0xFFFF)
			{
				mActualText = "AUTO";
			} else
			{
				mActualText = String.valueOf(val);
			}
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
			mValueBackground = mContext.getResources().getDrawable(R.drawable.ic_widget_bottom);
			mReadOnly = false;
			break;
		case controlType_Shutter:
			mIconDrawable = mContext.getResources().getDrawable(R.drawable.ic_widget_shutter);
			mValueBackground = mContext.getResources().getDrawable(R.drawable.ic_widget_bottom);
			mReadOnly = false;
			break;
		case controlType_Focus:
			mIconDrawable = mContext.getResources().getDrawable(R.drawable.ic_widget_focus);
			mValueBackground = mContext.getResources().getDrawable(R.drawable.ic_widget_bottom);
			mReadOnly = false;
			break;
		case controlType_WB:
			mIconDrawable = mContext.getResources().getDrawable(R.drawable.ic_widget_wb);
			mValueBackground = mContext.getResources().getDrawable(R.drawable.ic_widget_wb_auto);
			mReadOnly = false;
			break;
		case controlType_ISO:
			mIconDrawable = mContext.getResources().getDrawable(R.drawable.ic_widget_iso);
			mValueBackground = mContext.getResources().getDrawable(R.drawable.ic_widget_bottom);
			mReadOnly = false;
			break;
		case controlType_Battery:
			mIconDrawable = mContext.getResources().getDrawable(R.drawable.ic_widget_battery);
			mValueBackground = mContext.getResources().getDrawable(R.drawable.ic_widget_bottom);
			mReadOnly = true;
			break;
		case controlType_Flash:
			mIconDrawable = mContext.getResources().getDrawable(R.drawable.ic_widget_flash);
			mValueBackground = mContext.getResources().getDrawable(R.drawable.ic_widget_bottom);
			mReadOnly = false;
			break;
		default:
		}
	}
}

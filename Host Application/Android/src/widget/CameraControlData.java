package widget;

import java.text.DecimalFormat;
import ptp.DevicePropDesc;
import afarsek.namespace.R;
import android.content.Context;
import android.graphics.drawable.Drawable;

public class CameraControlData
{
	/**************************************************************************************************
	 * This enum gives meaning to the available camera properties we want to see in the application.
	 */
	public enum controlType
	{
		controlType_Battery(DevicePropDesc.BatteryLevel), controlType_WB(DevicePropDesc.WhiteBalance), controlType_Aperture(
				DevicePropDesc.FStop), controlType_FocalLength(DevicePropDesc.FocalLength), controlType_FocusDistance(
				DevicePropDesc.FocusDistance), controlType_FocusMode(DevicePropDesc.FocusMode), controlType_Flash(DevicePropDesc.FlashMode), controlType_Shutter(
				DevicePropDesc.ExposureTime), controlType_ISO(DevicePropDesc.ExposureIndex);

		private int propCode;

		private controlType(int c)
		{
			propCode = c;
		}

		public int getCode()
		{
			return propCode;
		}

		@Override
		public String toString()
		{
			return DevicePropDesc._getPropertyName(propCode);
		}
	};

	/**************************************************************************************************
	 * Private class properties
	 */
	private Context mContext;
	private controlType mType;
	private int mVal;
	private Drawable mIconDrawable;
	private Drawable mValueBackground;
	private String mActualText;
	private boolean mReadOnly;

	/**************************************************************************************************
	 * Methods
	 */
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

	/**************************************************************************************************
	 * setControlValue - This method gets the value in terms of the controlled camera and translates its value to understandable
	 * representation. Then fills in the appropriate variables (icon and text ans stuff)
	 */
	public void setControlValue(int val)
	{
		mVal = val;

		if (val == -1) // N/A
		{
			mActualText = "N/A";
			return;
		}

		switch (mType)
		{

		// FOCUS MODE
		case controlType_FocusMode:
			switch (val)
			{
			case 0x001:
				mActualText = "Manual";
				break;
			case 0x002:
				mActualText = "AF-S";
				break;
			case 0x003:
				mActualText = "AF-S";
				break;
			default:
				mActualText = "AF-C";
			}
			break;

		// FOCAL LENGTH
		case controlType_FocalLength:
			mActualText = String.valueOf((double) (val) / 100.0) + "mm";
			break;

		// APERTURE
		case controlType_Aperture:
			mActualText = "F/" + String.valueOf((double) (val) / 100.0);

			break;

		// SHUTTER SPEED
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

		// FOCUS DISTANCE
		case controlType_FocusDistance:
			mActualText = String.valueOf(val);
			break;

		// WHITE BALANCE
		case controlType_WB:
			mActualText = "";
			switch (val)
			{
			case 0x0001: // manual K
				mValueBackground = mContext.getResources().getDrawable(R.drawable.ic_widget_wb_kelvin);
				break;
			case 0x0002: // Auto
				mValueBackground = mContext.getResources().getDrawable(R.drawable.ic_widget_wb_auto);
				break;
			case 0x0003: // One-Push auto
				mValueBackground = mContext.getResources().getDrawable(R.drawable.ic_widget_wb_auto);
				break;
			case 0x0004: // Daylight
				mValueBackground = mContext.getResources().getDrawable(R.drawable.ic_widget_wb_sunny);
				break;
			case 0x0005: // Fluorescent
				mValueBackground = mContext.getResources().getDrawable(R.drawable.ic_widget_wb_floura);
				break;
			case 0x0006: // Tungsten
				mValueBackground = mContext.getResources().getDrawable(R.drawable.ic_widget_wb_tongstan);
				break;
			case 0x0007: // Flash
				mValueBackground = mContext.getResources().getDrawable(R.drawable.ic_widget_wb_flash);
				break;
			default:
				mValueBackground = mContext.getResources().getDrawable(R.drawable.ic_widget_wb_auto);
			}
			break;

		// ISO SPEED
		case controlType_ISO:
			if (val == 0xFFFF)
			{
				mActualText = "AUTO";
			} else
			{
				mActualText = String.valueOf(val);
			}
			break;

		// BATTERY LEVEL
		case controlType_Battery:
			mActualText = String.valueOf(val);
			break;

		// FLASH MODE
		case controlType_Flash:
			mActualText = "";
			switch (val)
			{
			case 0x0001: // auto flash
				mValueBackground = mContext.getResources().getDrawable(R.drawable.ic_widget_flash_auto);
				break;
			case 0x0002: // Flash off
				mValueBackground = mContext.getResources().getDrawable(R.drawable.ic_widget_flash_never);
				break;
			case 0x0003: // Fill flash
				mValueBackground = mContext.getResources().getDrawable(R.drawable.ic_widget_flash_always);
				break;
			case 0x0004: // Red eye auto
				mValueBackground = mContext.getResources().getDrawable(R.drawable.ic_widget_flash_auto_eye);
				break;
			case 0x0005: // Red eye fill
				mValueBackground = mContext.getResources().getDrawable(R.drawable.ic_widget_flash_flash_eye);
				break;
			case 0x0006: // External Sync
				mValueBackground = mContext.getResources().getDrawable(R.drawable.ic_widget_flash_never);
				break;
			default:
				mValueBackground = mContext.getResources().getDrawable(R.drawable.ic_widget_flash_never);
			}
			break;

		default:
		}
	}

	public void setControlType(controlType type)
	{
		mType = type;
		switch (type)
		{

		// FOCUS MODE
		case controlType_FocusMode:
			mIconDrawable = mContext.getResources().getDrawable(R.drawable.ic_widget_focus_mode);
			mValueBackground = mContext.getResources().getDrawable(R.drawable.ic_widget_bottom);
			mReadOnly = false;
			break;

		// FOCAL LENGTH
		case controlType_FocalLength:
			mIconDrawable = mContext.getResources().getDrawable(R.drawable.ic_widget_focal_length);
			mValueBackground = mContext.getResources().getDrawable(R.drawable.ic_widget_bottom);
			mReadOnly = false;
			break;

		// APERTURE
		case controlType_Aperture:
			mIconDrawable = mContext.getResources().getDrawable(R.drawable.ic_widget_aperture);
			mValueBackground = mContext.getResources().getDrawable(R.drawable.ic_widget_bottom);
			mReadOnly = false;
			break;

		// SHUTTER SPEED
		case controlType_Shutter:
			mIconDrawable = mContext.getResources().getDrawable(R.drawable.ic_widget_shutter);
			mValueBackground = mContext.getResources().getDrawable(R.drawable.ic_widget_bottom);
			mReadOnly = false;
			break;

		// FOCUS DISTANCE
		case controlType_FocusDistance:
			mIconDrawable = mContext.getResources().getDrawable(R.drawable.ic_widget_focus_distance);
			mValueBackground = mContext.getResources().getDrawable(R.drawable.ic_widget_bottom);
			mReadOnly = false;
			break;

		// WHITE BALANCE
		case controlType_WB:
			mIconDrawable = mContext.getResources().getDrawable(R.drawable.ic_widget_wb);
			mValueBackground = mContext.getResources().getDrawable(R.drawable.ic_widget_wb_auto);
			mReadOnly = false;
			break;

		// ISO SPEED
		case controlType_ISO:
			mIconDrawable = mContext.getResources().getDrawable(R.drawable.ic_widget_iso);
			mValueBackground = mContext.getResources().getDrawable(R.drawable.ic_widget_bottom);
			mReadOnly = false;
			break;

		// BATTERY LEVEL
		case controlType_Battery:
			mIconDrawable = mContext.getResources().getDrawable(R.drawable.ic_widget_battery);
			mValueBackground = mContext.getResources().getDrawable(R.drawable.ic_widget_bottom);
			mReadOnly = true;
			break;

		// FLASH MODE
		case controlType_Flash:
			mIconDrawable = mContext.getResources().getDrawable(R.drawable.ic_widget_flash);
			mValueBackground = mContext.getResources().getDrawable(R.drawable.ic_widget_bottom);
			mReadOnly = false;
			break;
		default:
		}
	}
}

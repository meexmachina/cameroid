package widget;

import java.text.DecimalFormat;
import java.util.Vector;

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
		private int initValue;
		private boolean initReadOnly;
		private boolean initAvailable;
		private boolean initActive;

		private controlType(int c)
		{
			propCode = c;

			switch (c)
			{
			case DevicePropDesc.BatteryLevel:
				initValue = 100;
				initReadOnly = true;
				break;

			case DevicePropDesc.WhiteBalance:
				initValue = 2;
				initReadOnly = false;
				break;

			case DevicePropDesc.FStop:
				initValue = 280;
				initReadOnly = false;
				break;

			case DevicePropDesc.FocalLength:
				initValue = 500;
				initReadOnly = true;
				break;

			case DevicePropDesc.FocusDistance:
				initValue = 500;
				initReadOnly = false;
				break;

			case DevicePropDesc.FocusMode:
				initValue = 2;
				initReadOnly = false;
				break;

			case DevicePropDesc.FlashMode:
				initValue = 1;
				initReadOnly = false;
				break;

			case DevicePropDesc.ExposureTime:
				initValue = 1000;
				initReadOnly = false;
				break;

			case DevicePropDesc.ExposureIndex:
				initValue = 100;
				initReadOnly = false;
				break;
			}

			initAvailable = true;
			initActive = true;
		}

		public int getInitialValue()
		{
			return initValue;
		}

		public boolean getInitialReadOnly()
		{
			return initReadOnly;
		}

		public boolean getInitialAvailable()
		{
			return initAvailable;
		}

		public boolean getInitialActive()
		{
			return initActive;
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

		public static controlType getTypeFromCode(int code)
		{
			controlType[] types = controlType.values();

			for (int i = 0; i < types.length; i++)
			{
				if (types[i].getCode() == code)
				{
					return types[i];
				}
			}

			return types[0];
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
	private boolean mActivated = true;
	private boolean mAvailable = true;

	/**************************************************************************************************
	 * Methods
	 */
	public CameraControlData(Context context, controlType type, int val)
	{
		mContext = context;
		setControlType(type);
		setControlValue(val);
		mActivated = true;
		mAvailable = true;
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

	public boolean getIsActive()
	{
		return mActivated;
	}

	public void setActive(boolean active)
	{
		mActivated = active;
	}

	public void setAvailable(boolean avail)
	{
		mAvailable = avail;
	}

	public boolean getAvailable()
	{
		return mAvailable;
	}

	public static String[] createProperyEnumList(controlType type, Vector<?> enumeration)
	{
		String[] st = new String[enumeration.size()];
		for (int i = 0; i < enumeration.size(); i++)
		{
			int curVal = Integer.parseInt(enumeration.get(i).toString());
			st[i] = convertRawValue(type, curVal);
		}
		return st;
	}

	public static String[] createPropertyRangeList(controlType type, DevicePropDesc.Range range)
	{
		int start = Integer.parseInt(range.getMinimum().toString());
		int stop = Integer.parseInt(range.getMaximum().toString());
		int step = Integer.parseInt(range.getMinimum().toString());
		String[] st = new String[(stop - start) / step + 1];
		for (int i = start; i <= stop; i += step)
		{
			st[i] = convertRawValue(type, i);
		}
		return st;
	}

	public static String convertRawValue(controlType type, int val)
	{
		String mActualText = "";

		if (val == -1) // N/A
		{
			mActualText = "N/A";
			return mActualText;
		}

		switch (type)
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
				Double closeVal;
				if (fScaled != 0)
					closeVal = Double.valueOf(zeroDForm.format(10000.0 / fScaled));
				else
					closeVal = 0.0;
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
				mActualText = "Manual K";
				break;
			case 0x0002: // Auto
				mActualText = "Auto WB";
				break;
			case 0x0003: // One-Push auto
				mActualText = "Single Auto WB";
				break;
			case 0x0004: // Daylight
				mActualText = "Daylight";
				break;
			case 0x0005: // Fluorescent
				mActualText = "Fluorescence";
				break;
			case 0x0006: // Tungsten
				mActualText = "Tungsten";
				break;
			case 0x0007: // Flash
				mActualText = "Flash";
				break;
			default:
				mActualText = "Auto WB";
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
				mActualText = "Auto Flash";
				break;
			case 0x0002: // Flash off
				mActualText = "Flash Off";
				break;
			case 0x0003: // Fill flash
				mActualText = "Fill Flash";
				break;
			case 0x0004: // Red eye auto
				mActualText = "Red-Eye Auto";
				break;
			case 0x0005: // Red eye fill
				mActualText = "Red-Eye Fill";
				break;
			case 0x0006: // External Sync
				mActualText = "External Sync";
				break;
			default:
				mActualText = "Auto Flash";
			}
			break;

		default:
		}
		return mActualText;
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
		// ISO SPEED
		case controlType_ISO:
			// BATTERY LEVEL
		case controlType_Battery:
			// FOCUS MODE
		case controlType_FocusMode:
			// FOCAL LENGTH
		case controlType_FocalLength:
			// APERTURE
		case controlType_Aperture:
			// SHUTTER SPEED
		case controlType_Shutter:
			// FOCUS DISTANCE
		case controlType_FocusDistance:
			mActualText = convertRawValue(mType, val);
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

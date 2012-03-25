package ptp;

import java.io.PrintStream;
import java.util.Vector;

/**
 * DeviceProperty descriptions provide metadata (code, type, factory defaults, is-it-writable, and perhaps value ranges or domains) and
 * current values of device properties.
 * 
 * <p>
 * The values exposed are typically <code>java.lang.Integer</code>, <code>java.lang.Long</code>, "int" or "long" arrays, or Strings, and
 * must be cast to more specific types. The 64 bit integral types are supported, but not the 128 bit ones; unsigned 64 bit values may not
 * print as intended.
 * 
 */
public class DevicePropDesc extends Data
{
	public int propertyCode;
	public int dataType;
	public boolean writable;
	public Object factoryDefault;
	public Object currentValue;
	public int formType;
	Object constraints;

	public DevicePropDesc(NameFactory f)
	{
		super(f);
	}

	public DevicePropDesc(NameFactory f, byte[] data)
	{
		super(true, data, data.length, f);
		parse();
	}

	void parse()
	{
		super.parse();

		// per 13.3.3, tables 23, 24, 25
		propertyCode = nextU16();
		dataType = nextU16();
		writable = nextU8() != 0;

		// FIXME use factories, as vendor hooks
		factoryDefault = DevicePropValue.get(dataType, this);
		currentValue = DevicePropValue.get(dataType, this);

		formType = nextU8();
		switch (formType)
		{
		case 0: // no more
			break;
		case 1: // range: min, max, step
			constraints = new Range(dataType, this);
			break;
		case 2: // enumeration: n, value1, ... valueN
			constraints = parseEnumeration();
			break;
		default:
			System.err.println("ILLEGAL prop desc form, " + formType);
			formType = 0;
			break;
		}
	}

	//
	// TODO: TO BE REMOVED
	//
	public void dump(PrintStream out)
	{
		super.dump(out);

		out.print(factory.getPropertyName(propertyCode));
		out.print(" = ");
		out.print(currentValue);
		if (!writable)
			out.print(", read-only");
		out.print(", ");
		out.print(DevicePropValue.getTypeName(dataType));
		switch (formType)
		{
		case 0:
			break;
		case 1:
		{
			Range r = (Range) constraints;
			out.print(" from ");
			out.print(r.getMinimum());
			out.print(" to ");
			out.print(r.getMaximum());
			out.print(" by ");
			out.print(r.getIncrement());
		}
			;
			break;
		case 2:
		{
			Vector<?> v = (Vector<?>) constraints;
			out.print(" { ");
			for (int i = 0; i < v.size(); i++)
			{
				if (i != 0)
					out.print(", ");
				out.print(v.elementAt(i));
			}
			out.print(" }");
		}
			break;
		default:
			out.print(" form ");
			out.print(formType);
			out.print(" (error)");
		}

		out.print(", default ");
		out.println(factoryDefault);
	}

	/** Returns true if the property is writable */
	public boolean isWritable()
	{
		return writable;
	}

	/** Returns the current value (treat as immutable!) */
	public Object getValue()
	{
		return currentValue;
	}

	/** Returns the factory default value (treat as immutable!) */
	public Object getDefault()
	{
		return factoryDefault;
	}

	// code values, per 13.3.5 table 26

	public static final int BatteryLevel = 0x5001;
	public static final int FunctionalMode = 0x5002;
	public static final int ImageSize = 0x5003;

	public static final int CompressionSetting = 0x5004;
	public static final int WhiteBalance = 0x5005;
	public static final int RGBGain = 0x5006;
	public static final int FStop = 0x5007;

	public static final int FocalLength = 0x5008;
	public static final int FocusDistance = 0x5009;
	public static final int FocusMode = 0x500a;
	public static final int ExposureMeteringMode = 0x500b;

	public static final int FlashMode = 0x500c;
	public static final int ExposureTime = 0x500d;
	public static final int ExposureProgramMode = 0x500e;
	public static final int ExposureIndex = 0x500f;

	public static final int ExposureBiasCompensation = 0x5010;
	public static final int DateTime = 0x5011;
	public static final int CaptureDelay = 0x5012;
	public static final int StillCaptureMode = 0x5013;

	public static final int Contrast = 0x5014;
	public static final int Sharpness = 0x5015;
	public static final int DigitalZoom = 0x5016;
	public static final int EffectMode = 0x5017;

	public static final int BurstNumber = 0x5018;
	public static final int BurstInterval = 0x5019;
	public static final int TimelapseNumber = 0x501a;
	public static final int TimelapseInterval = 0x501b;

	public static final int FocusMeteringMode = 0x501c;
	public static final int UploadURL = 0x501d;
	public static final int Artist = 0x501e;
	public static final int CopyrightInfo = 0x501f;

	public String getCodeName(int code)
	{
		return factory.getPropertyName(code);
	}

	static class NameMap
	{
		int value;
		String name;

		NameMap(int v, String n)
		{
			value = v;
			name = n;
		}
	}

	static NameMap names[] =
	{ new NameMap(BatteryLevel, "Battery Level"), new NameMap(FunctionalMode, "Functional Mode"), new NameMap(ImageSize, "Image Size"),
			new NameMap(CompressionSetting, "Compression Setting"), new NameMap(WhiteBalance, "White Balance"),
			new NameMap(RGBGain, "RGB Gain"), new NameMap(FStop, "Aperture"), new NameMap(FocalLength, "Focal Length"),
			new NameMap(FocusDistance, "Focus Distance"), new NameMap(FocusMode, "Focus Mode"),
			new NameMap(ExposureMeteringMode, "Exposure Metering Mode"), new NameMap(FlashMode, "Flash Mode"),
			new NameMap(ExposureTime, "Shutter Speed"), new NameMap(ExposureProgramMode, "Exposure Program Mode"),
			new NameMap(ExposureIndex, "ISO Speed"), new NameMap(ExposureBiasCompensation, "Exposure Bias Compensation"),
			new NameMap(DateTime, "Date/Time"), new NameMap(CaptureDelay, "Capture Delay"),
			new NameMap(StillCaptureMode, "Still Capture Mode"), new NameMap(Contrast, "Contrast"), new NameMap(Sharpness, "Sharpness"),
			new NameMap(DigitalZoom, "Digital Zoom"), new NameMap(EffectMode, "Effect Mode"), new NameMap(BurstNumber, "Burst Number"),
			new NameMap(BurstInterval, "Burst Interval"), new NameMap(TimelapseNumber, "Timelapse Number"),
			new NameMap(TimelapseInterval, "Timelapse Interval"), new NameMap(FocusMeteringMode, "Focus Metering Mode"),
			new NameMap(UploadURL, "Upload URL"), new NameMap(Artist, "Artist"), new NameMap(CopyrightInfo, "Copyright Info"), };

	public static String _getPropertyName(int code)
	{
		for (int i = 0; i < names.length; i++)
			if (names[i].value == code)
				return names[i].name;
		return Container.getCodeString(code);
	}

	/**
	 * Maps standard property names to property codes. Case is ignored in these comparisons.
	 * 
	 * @param name
	 *            string identifying that property.
	 * @return device property code, or -1
	 */
	public static int getPropertyCode(String name)
	{
		for (int i = 0; i < names.length; i++)
			if (names[i].name.equalsIgnoreCase(name))
				return names[i].value;

		// FIXME: delegate to superclass
		return Integer.parseInt(name, 16);
	}

	/**
	 * This class describes value ranges by minima, maxima, and permissible increments.
	 */
	public static final class Range
	{
		private Object min, max, step;

		Range(int dataType, DevicePropDesc desc)
		{
			min = DevicePropValue.get(dataType, desc);
			max = DevicePropValue.get(dataType, desc);
			step = DevicePropValue.get(dataType, desc);
		}

		/** Returns the maximum value of this range */
		public Object getMaximum()
		{
			return max;
		}

		/** Returns the minimum value of this range */
		public Object getMinimum()
		{
			return min;
		}

		/** Returns the increment of values in this range */
		public Object getIncrement()
		{
			return step;
		}
	}

	/** Returns any range constraints for this property's value, or null */
	public Range getRange()
	{
		if (formType == 1)
			return (Range) constraints;
		return null;
	}

	private Vector<Object> parseEnumeration()
	{
		int len = nextU16();
		Vector<Object> retval = new Vector<Object>(len);

		while (len-- > 0)
			retval.addElement(DevicePropValue.get(dataType, this));
		return retval;
	}

	/** Returns any enumerated options for this property's value, or null */
	public Vector<?> getEnumeration()
	{
		if (formType == 2)
			return (Vector<?>) constraints;
		return null;
	}
}
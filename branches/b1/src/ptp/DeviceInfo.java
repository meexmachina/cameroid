package ptp;

import java.io.PrintStream;
import java.io.Serializable;

/**
 * DeviceInfo describes device functionality such supported image formats, operations, events, and device properties.
 * 
 */
public class DeviceInfo extends Data
{
	// need some transport-neutral interface; this is USB-specific

	int standardVersion;
	int vendorExtensionId;
	int vendorExtensionVersion;
	String vendorExtensionDesc;

	int functionalMode; // may change;
	public int operationsSupported[]; // 10.2
	public int eventsSupported[]; // 12.5
	public int propertiesSupported[]; // 13.3.5

	int captureFormats[]; // 6
	int imageFormats[]; // 6
	String manufacturer;
	String model;

	String deviceVersion;
	String serialNumber;

	// FIXME add formal vendor hooks, which we'd consult for string
	// mappings ... we don't have any here.

	// Command, Response, ObjectInfo, Event, and DevicePropDesc can
	// all be subclassed; but we won't have instances here. And
	// there's also the vendor extension stuff here.

	// input -- we can't know buffer size yet
	DeviceInfo(NameFactory f)
	{
		super(true, null, 0, f);
	}

	public DeviceInfo(NameFactory f, byte[] data)
	{
		super(true, data, data.length, f);
		parse();
	}

	private boolean supports(int supported[], int code)
	{
		for (int i = 0; i < supported.length; i++)
		{
			if (code == supported[i])
				return true;
		}
		return false;
	}

	/** Returns true iff the device supports this operation */
	public boolean supportsOperation(int opCode)
	{
		return supports(operationsSupported, opCode);
	}

	/** Returns true iff the device supports this event */
	public boolean supportsEvent(int eventCode)
	{
		return supports(eventsSupported, eventCode);
	}

	/** Returns true iff the device supports this property */
	public boolean supportsProperty(int propCode)
	{
		return supports(propertiesSupported, propCode);
	}

	/** Returns true iff the device supports this capture format */
	public boolean supportsCaptureFormat(int formatCode)
	{
		return supports(captureFormats, formatCode);
	}

	/** Returns true iff the device supports this image format */
	public boolean supportsImageFormat(int formatCode)
	{
		return supports(imageFormats, formatCode);
	}

	// fit names to standard length lines
	private int addString(PrintStream out, int last, String s)
	{
		last += s.length();
		last++;
		if (last < 80)
		{
			out.print(s);
			out.print(" ");
		} else
		{
			out.println();
			out.print("\t");
			out.print(s);
			out.print(" ");
			last = 8 + s.length() + 1;
		}
		return last;
	}

	void parse()
	{
		super.parse();

		standardVersion = nextU16();
		vendorExtensionId = /* unsigned */nextS32();
		vendorExtensionVersion = nextU16();
		vendorExtensionDesc = nextString();

		functionalMode = nextU16();
		operationsSupported = nextU16Array();
		eventsSupported = nextU16Array();
		propertiesSupported = nextU16Array();

		captureFormats = nextU16Array();
		imageFormats = nextU16Array();
		manufacturer = nextString();
		model = nextString();

		deviceVersion = nextString();
		serialNumber = nextString();
	}

	void lines(PrintStream out)
	{
		if (manufacturer != null)
			out.println("Manufacturer: " + manufacturer);
		if (model != null)
			out.println("Model: " + model);
		if (deviceVersion != null)
			out.println("Device Version: " + deviceVersion);
		if (serialNumber != null)
			out.println("Serial Number: " + serialNumber);

		if (functionalMode != 0)
		{
			out.print("Functional Mode: ");
			out.println(funcMode(functionalMode));
		}

		if (vendorExtensionId != 0)
		{
			out.print("Extensions (");
			out.print(Integer.toString(vendorExtensionId));
			out.print(")");
			if (vendorExtensionDesc != null)
			{
				out.print(": ");
				out.print(vendorExtensionDesc);
			}
			out.println();

			// summarize extension: ops, props, events
		}
	}

	//
	// TODO: TO BE REMOVED
	//
	public void dump(PrintStream out)
	{
		if (operationsSupported == null)
		{
			// System.err.println ("... device info uninitted");
			return;
		}

		super.dump(out);
		out.println("DeviceInfo:");
		lines(out);

		out.println("PTP Version: " + (standardVersion / 100) + "." + (standardVersion % 100));

		String name;
		int last;

		// per chapter 10
		out.println("Operations Supported:");
		out.print("\t");
		last = 8;
		for (int i = 0; i < operationsSupported.length; i++)
		{
			name = factory.getOpcodeString(operationsSupported[i]);
			last = addString(out, last, name);
		}
		out.println();

		// per chapter 11
		out.println("Events Supported:");
		out.print("\t");
		last = 8;
		for (int i = 0; i < eventsSupported.length; i++)
		{
			name = factory.getEventString(eventsSupported[i]);
			last = addString(out, last, name);
		}
		out.println();

		// per chapter 13
		out.println("Device Properties Supported:");
		out.print("\t");
		last = 8;
		for (int i = 0; i < propertiesSupported.length; i++)
		{
			name = factory.getPropertyName(propertiesSupported[i]);
			last = addString(out, last, name);
		}
		out.println();

		// per 6.2
		out.println("Capture Formats Supported:");
		out.print("\t");
		last = 8;
		for (int i = 0; i < captureFormats.length; i++)
		{
			name = factory.getFormatString(captureFormats[i]);
			last = addString(out, last, name);
		}
		out.println();

		// per 6.2
		out.println("Image Formats Supported:");
		out.print("\t");
		last = 8;
		for (int i = 0; i < imageFormats.length; i++)
		{
			name = factory.getFormatString(imageFormats[i]);
			last = addString(out, last, name);
		}
		out.println();

		if (vendorExtensionId != 0)
		{
			out.print("Vendor Extension, id ");
			out.print(Integer.toString(vendorExtensionId));
			out.print(", version ");
			out.print(standardVersion / 100);
			out.print(".");
			out.println(standardVersion % 100);

			if (vendorExtensionDesc != null)
			{
				out.print("Description: ");
				out.println(vendorExtensionDesc);
			}
		}
	}

	static String funcMode(int functionalMode)
	{
		switch (functionalMode)
		{
		case 0:
			return "standard";
		case 1:
			return "sleeping";
		default:
			// FIXME add vendor hook
			StringBuffer buf = new StringBuffer();

			if ((functionalMode & 0x8000) == 0)
				buf.append("reserved 0x");
			else
				buf.append("vendor 0x");
			buf.append(Integer.toHexString(functionalMode & ~0x8000));
			return buf.toString();
		}
	}
}
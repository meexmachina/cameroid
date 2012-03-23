package ptp;

/**
 * Events are sent spontaneously from responders to initiators. Event codes, described in chapter 12 of the PTP specification, identify what
 * happened. Some events have a parameter, and additional data (for vendor extensions) may be available using the class control request
 * "Get Extended Event Data".
 * 
 */
public class Event extends ParamVector
{

	public Event(byte buf[], NameFactory f)
	{
		super(buf, buf.length, f);
	}

	/** EventCode: */
	public static final int Undefined = 0x4000;
	/** EventCode: */
	public static final int CancelTransaction = 0x4001;
	/** EventCode: */
	public static final int ObjectAdded = 0x4002;
	/** EventCode: */
	public static final int ObjectRemoved = 0x4003;
	/** EventCode: */
	public static final int StoreAdded = 0x4004;
	/** EventCode: */
	public static final int StoreRemoved = 0x4005;
	/** EventCode: */
	public static final int DevicePropChanged = 0x4006;
	/** EventCode: */
	public static final int ObjectInfoChanged = 0x4007;
	/** EventCode: */
	public static final int DeviceInfoChanged = 0x4008;
	/** EventCode: */
	public static final int RequestObjectTransfer = 0x4009;
	/** EventCode: */
	public static final int StoreFull = 0x400a;
	/** EventCode: */
	public static final int DeviceReset = 0x400b;
	/** EventCode: */
	public static final int StorageInfoChanged = 0x400c;
	/** EventCode: */
	public static final int CaptureComplete = 0x400d;
	/** EventCode: a status event was dropped (missed an interrupt) */
	public static final int UnreportedStatus = 0x400e;

	public String getCodeName(int code)
	{
		return factory.getEventString(code);
	}

	static protected String _getEventString(int code)
	{
		switch (code)
		{
		case Undefined:
			return "Undefined";
		case CancelTransaction:
			return "CancelTransaction";
		case ObjectAdded:
			return "ObjectAdded";
		case ObjectRemoved:
			return "ObjectRemoved";

		case StoreAdded:
			return "StoreAdded";
		case StoreRemoved:
			return "StoreRemoved";
		case DevicePropChanged:
			return "DevicePropChanged";
		case ObjectInfoChanged:
			return "ObjectInfoChanged";

		case DeviceInfoChanged:
			return "DeviceInfoChanged";
		case RequestObjectTransfer:
			return "RequestObjectTransfer";
		case StoreFull:
			return "StoreFull";
		case DeviceReset:
			return "DeviceReset";

		case StorageInfoChanged:
			return "StorageInfoChanged";
		case CaptureComplete:
			return "CaptureComplete";
		case UnreportedStatus:
			return "UnreportedStatus";

		}
		return getCodeString(code);
	}
}
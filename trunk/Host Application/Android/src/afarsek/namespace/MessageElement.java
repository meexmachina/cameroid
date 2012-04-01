package afarsek.namespace;

public class MessageElement
{
	enum MessageTags
	{

		ME_NO_MSG(0), ME_STATUS(1), ME_DEVICE_INFO(2), ME_STORAGE_INFO(3), ME_PROPERTY_DESC(4), ME_IDENT(5);

		private final int index;

		MessageTags(int idx)
		{
			this.index = idx;
		}

		public int getIndex()
		{
			return index;
		}
	}

	public static final int TP_COMMAND_START = (int) 0x00;
	public static final int TP_COMMAND_IDN = (int) 0x01;
	public static final int TP_COMMAND_GET_CAMERA_STATUS = (int) 0x02;
	public static final int TP_COMMAND_GET_CAMERA_INFO = (int) 0x03;
	public static final int TP_COMMAND_GET_STORAGE_IDS = (int) 0x04;
	public static final int TP_COMMAND_GET_STORAGE_INFO = (int) 0x05;
	public static final int TP_COMMAND_GET_SYSTEM_STATUS = (int) 0x06;
	public static final int TP_COMMAND_GET_OBJECT_INFO = (int) 0x07;
	public static final int TP_COMMAND_GET_OBJECT = (int) 0x08;
	public static final int TP_COMMAND_GET_THUMB = (int) 0x09;
	public static final int TP_COMMAND_CAPTURE = (int) 0x0A;
	public static final int TP_COMMAND_GET_PROP_DESC = (int) 0x0B;
	public static final int TP_COMMAND_GET_PROP_VAL = (int) 0x0C;
	public static final int TP_COMMAND_SET_PROP_VAL = (int) 0x0D;
	public static final int TP_COMMAND_SET_PROP_UPDATE	= (int)	0x0E;
	
	public static final int TP_COMMAND_END = (int) 0x30;
	public static final int TP_EVENT_START = (int) 0x30;
	public static final int TP_EVENT_CAMERA_CONNECTED = (int) 0x31;
	public static final int TP_EVENT_CAMERA_DISCONNECTED = (int) 0x32;
	public static final int TP_EVENT_CAPTURE_FINISHED = (int) 0x33;
	public static final int TP_EVENT_OBJECT_WRITTEN = (int) 0x34;
	public static final int TP_EVENT_IN_SLEEP_MODE = (int) 0x35;
	public static final int TP_EVENT_WAKING_UP = (int) 0x36;
	public static final int TP_EVENT_FRAMING_ERROR = (int) 0x37;
	public static final int TP_EVENT_PROPERTY_CHANGED = (int) 0x38;

	public static final int TP_EVENT_END = (int) 0xA0;
	public static final int TP_DATA_START = (int) 0xA0;
	public static final int TP_DATA_CAMERA_INFO = (int) 0xA1;
	public static final int TP_DATA_STORAGE_IDS = (int) 0xA2;
	public static final int TP_DATA_STORAGE_INFO = (int) 0xA3;
	public static final int TP_DATA_CAMERA_STATUS = (int) 0xA4;
	public static final int TP_DATA_OBJECT_INFO = (int) 0xA5;
	public static final int TP_DATA_OBJECT = (int) 0xA6;
	public static final int TP_DATA_THUMB_LIST = (int) 0xA7;
	public static final int TP_DATA_THUMB = (int) 0xA8;
	public static final int TP_DATA_PROP_DESC = (int) 0xA9;
	public static final int TP_DATA_PROP_VAL = (int) 0xAA;
	public static final int TP_DATA_IDN = (int) 0xAB;
	public static final int TP_DATA_PROPERTY_EVENT = (int) 0xAC;
	
	public static final int TP_DATA_END = (int) 0xFF;

	public byte[] mData;
	public MessageTags mTag;
	public int mTransactionID;

	public MessageElement(byte[] data, MessageTags tag, int transaction)
	{
		mData = data.clone();
		mTag = tag;
		mTransactionID = transaction;
	}
}

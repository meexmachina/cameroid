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

	public static final byte TP_COMMAND_START = (byte) 0x00;
	public static final byte TP_COMMAND_IDN = (byte) 0x01;
	public static final byte TP_COMMAND_GET_CAMERA_STATUS = (byte) 0x02;
	public static final byte TP_COMMAND_GET_CAMERA_INFO = (byte) 0x03;
	public static final byte TP_COMMAND_GET_STORAGE_IDS = (byte) 0x04;
	public static final byte TP_COMMAND_GET_STORAGE_INFO = (byte) 0x05;
	public static final byte TP_COMMAND_GET_SYSTEM_STATUS = (byte) 0x06;
	public static final byte TP_COMMAND_GET_OBJECT_INFO = (byte) 0x07;
	public static final byte TP_COMMAND_GET_OBJECT = (byte) 0x08;
	public static final byte TP_COMMAND_GET_THUMB = (byte) 0x09;
	public static final byte TP_COMMAND_CAPTURE = (byte) 0x0A;
	public static final byte TP_COMMAND_GET_PROP_DESC = (byte) 0x0B;
	public static final byte TP_COMMAND_GET_PROP_VAL = (byte) 0x0C;
	public static final byte TP_COMMAND_SET_PROP_VAL = (byte) 0x0D;
	
	public static final byte TP_COMMAND_END = (byte) 0x30;
	public static final byte TP_EVENT_START = (byte) 0x30;
	public static final byte TP_EVENT_CAMERA_CONNECTED = (byte) 0x31;
	public static final byte TP_EVENT_CAMERA_DISCONNECTED = (byte) 0x32;
	public static final byte TP_EVENT_CAPTURE_FINISHED = (byte) 0x33;
	public static final byte TP_EVENT_OBJECT_WRITTEN = (byte) 0x34;
	public static final byte TP_EVENT_IN_SLEEP_MODE = (byte) 0x35;
	public static final byte TP_EVENT_WAKING_UP = (byte) 0x36;
	public static final byte TP_EVENT_FRAMING_ERROR = (byte) 0x37;

	public static final byte TP_EVENT_END = (byte) 0xA0;
	public static final byte TP_DATA_START = (byte) 0xA0;
	public static final byte TP_DATA_CAMERA_INFO = (byte) 0xA1;
	public static final byte TP_DATA_STORAGE_IDS = (byte) 0xA2;
	public static final byte TP_DATA_STORAGE_INFO = (byte) 0xA3;
	public static final byte TP_DATA_CAMERA_STATUS = (byte) 0xA4;
	public static final byte TP_DATA_OBJECT_INFO = (byte) 0xA5;
	public static final byte TP_DATA_OBJECT = (byte) 0xA6;
	public static final byte TP_DATA_THUMB_LIST = (byte) 0xA7;
	public static final byte TP_DATA_THUMB = (byte) 0xA8;
	public static final byte TP_DATA_PROP_DESC = (byte) 0xA9;
	public static final byte TP_DATA_PROP_VAL = (byte) 0xAA;
	public static final byte TP_DATA_IDN = (byte) 0xAB;
	
	public static final byte TP_DATA_END = (byte) 0xFF;

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

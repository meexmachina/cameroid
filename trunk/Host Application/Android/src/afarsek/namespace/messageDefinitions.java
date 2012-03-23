package afarsek.namespace;

public class messageDefinitions
{
	// Message types sent from the Handler
	public static final int MESSAGE_STATE_CHANGE = 1;
	public static final int MESSAGE_READ = 2;
	public static final int MESSAGE_WRITE = 3;
	public static final int MESSAGE_DEVICE_NAME = 4;
	public static final int MESSAGE_TOAST = 5;
	public static final int MESSAGE_CAMERA_CONNECTION_STATE = 6;
	public static final int MESSAGE_CAMERA_DEVICE_INFO = 7;
	public static final int MESSAGE_CAMERA_STORAGE_INFO = 8;
	public static final int MESSAGE_CAMERA_PROPERTY_INFO = 9;

	public static final String MESSAGE_READ_LENGTH = "READ_LENGTH";
	public static final String MESSAGE_READ_DATA_BYTES = "READ_DATA_BYTES";
	public static final String MESSAGE_READ_TAG = "READ_DATA_TAG";
}

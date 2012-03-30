/**
 * 
 */
package afarsek.namespace;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import ptp.DeviceInfo;
import ptp.DevicePropDesc;
import ptp.NameFactory;
import ptp.StorageInfo;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class cameraControl
{
	private hardwareFacade mHardwareFacade = null;
	private BluetoothAdapter mBluetoothAdapter;
	private BluetoothDevice mBluetoothDevice;
	private final Handler mMainPanelHandler;
	public int mCameraAttached = 0;
	public DeviceInfo mDeviceInfo = null;
	public StorageInfo mStorageInfo = null;
	public ArrayList<DevicePropDesc> mPropertyArray = new ArrayList<DevicePropDesc>();
	private NameFactory mNameFactory = new NameFactory();
	private Timer timer;

	private String mConnectedDeviceName = null; // Name of the connected device

	public cameraControl(String devName, Handler handler)
	{
		super();

		Log.d("Camera Control Class", "C'tor CameraControl: DevName" + devName);

		mConnectedDeviceName = devName;
		mMainPanelHandler = handler;

		// Get BT adapter
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

		// Get the BT device from name
		mBluetoothDevice = mBluetoothAdapter.getRemoteDevice(mConnectedDeviceName);
		Log.d("Camera Control Class", "C'tor - got the bluetooth device from name: " + mConnectedDeviceName);
	}

	public int findProperty(int propCode)
	{
		Log.d("Camera Control Class", "findProperty - searching for propCode: " + Integer.toHexString(propCode));
		for (int i = 0; i < mPropertyArray.size(); i++)
		{
			if (mPropertyArray.get(i).propertyCode == propCode)
			{
				Log.d("Camera Control Class", "findProperty - found at index: " + Integer.toString(i));
				return i;
			}
		}
		Log.d("Camera Control Class", "findProperty - not found, returning -1.");
		return -1;
	}

	private void getID()
	{
		// Log.d("Camera Control Class", "getID - enqueuing 'idn_bin:' command.");
		// mHardwareFacade.write_queue("idn_bin:".getBytes(), MessageElement.MessageTags.ME_STATUS);
	}

	private void getStatus()
	{
		// Log.d("Camera Control Class", "getStatus - enqueuing 'status_bin:' command.");
		// mHardwareFacade.write_queue("status_bin:".getBytes(), MessageElement.MessageTags.ME_STATUS);
	}

	private void getDeviceInfo()
	{
		// Log.d("Camera Control Class", "getDeviceInfo - enqueuing 'get_dev_info_bin:' command.");
		// mHardwareFacade.write_queue("get_dev_info_bin:".getBytes(), MessageElement.MessageTags.ME_DEVICE_INFO);
	}

	private void getStorageInfo()
	{
		// Log.d("Camera Control Class", "getStorageInfo - enqueuing 'get_storage_info_bin 0:' command.");
		// mHardwareFacade.write_queue("get_storage_info_bin 0:".getBytes(), MessageElement.MessageTags.ME_DEVICE_INFO);
	}

	public int getPropertiesDescriptions(int propCode)
	{
		/*
		 * Log.d("Camera Control Class", "getPropertiesDescriptions - trying to get property info: " + Integer.toHexString(propCode)); if
		 * (mCameraAttached == 0) { Log.d("Camera Control Class", "getPropertiesDescriptions - camera is not attached yet."); return -1; }
		 * 
		 * if (mDeviceInfo == null) { Log.d("Camera Control Class", "getPropertiesDescriptions - DeviceInfo was not initialized yet.");
		 * return -2; }
		 * 
		 * if (mDeviceInfo.propertiesSupported.length == 0) { Log.d("Camera Control Class",
		 * "getPropertiesDescriptions - DeviceInfo.propertiesSupported was not initialized yet or empty."); return -3; }
		 * 
		 * int[] props = mDeviceInfo.propertiesSupported; boolean found = false;
		 * 
		 * for (int i = 0; i < props.length; i++) { if (props[i] == propCode) { found = true; break; } }
		 * 
		 * if (found == false) { Log.d("Camera Control Class", "getPropertiesDescriptions - propCode " + Integer.toHexString(propCode) +
		 * "was not foound in the supported properties."); return -4; }
		 * 
		 * Log.d("Camera Control Class", "getPropertiesDescriptions - enqueuing 'get_prop_desc_bin propCode:' command.");
		 * mHardwareFacade.write_queue(("prop_desc_bin " + String.valueOf(propCode) + ":").getBytes(),
		 * MessageElement.MessageTags.ME_PROPERTY_DESC);
		 */

		return 0;
	}

	class StatusTask extends TimerTask
	{
		public void run()
		{
			// getStatus();
		}
	}

	public void connect()
	{
		if (mHardwareFacade == null)
		{
			Log.d("Camera Control Class", "connect - Initializing mHardwareFacade.");
			mHardwareFacade = new hardwareFacade(mHardwareDataHandler);
		}

		Log.d("Camera Control Class", "connect - trying to connect mBTDevice.");
		// Try to connect
		mHardwareFacade.connect(mBluetoothDevice);

		timer = new Timer();
		timer.scheduleAtFixedRate(new StatusTask(), 4000, 1000);
	}

	public void disconnect()
	{
		Log.d("Camera Control Class", "disconnect - cancelling timer events.");
		timer.cancel();
		Log.d("Camera Control Class", "disconnect - stopping and erasing mHardwareFacade.");
		mHardwareFacade.stop();
		mHardwareFacade = null;
	}

	public void capture()
	{
		// TODO Auto-generated method stub
		Log.d("Camera Control Class", "capture - trying to send 'capture' command");
	}

	public int cameraAttached()
	{
		return mCameraAttached;
	}

	// The Handler that gets information back from the hardwareFacade
	private final Handler mHardwareDataHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
			/*******************************************************************************************
			 * All the following messages come from the hardware facade and sent directly to the main panel without thinking to much.
			 **/
			case messageDefinitions.MESSAGE_STATE_CHANGE:
			case messageDefinitions.MESSAGE_DEVICE_NAME:
			case messageDefinitions.MESSAGE_TOAST:
				Message new_msg = new Message();
				new_msg.copyFrom(msg);
				mMainPanelHandler.sendMessage(new_msg);
				break;

			/*******************************************************************************************
			 * Message acceptance handler from the hardware facade
			 **/
			case messageDefinitions.MESSAGE_READ:
				Log.d("Camera Control Class", "MSG HWFacade=>cameraControl - Read new info from stream.");
				byte[] data = null;

				switch (msg.arg1)
				{

				// ===============================================
				// EVENT: Camera Connected
				case MessageElement.TP_EVENT_CAMERA_CONNECTED:
					mMainPanelHandler.obtainMessage(messageDefinitions.MESSAGE_CAMERA_CONNECTION_STATE, 1, -1).sendToTarget();
					break;

				// ===============================================
				// EVENT: Camera Disconnected
				case MessageElement.TP_EVENT_CAMERA_DISCONNECTED:
					mMainPanelHandler.obtainMessage(messageDefinitions.MESSAGE_CAMERA_CONNECTION_STATE, 0, -1).sendToTarget();
					break;

				// ===============================================
				// EVENT: Capture Finished
				case MessageElement.TP_EVENT_CAPTURE_FINISHED:
					break;

				// ===============================================
				// EVENT: Object Written
				case MessageElement.TP_EVENT_OBJECT_WRITTEN:
					break;

				// ===============================================
				// EVENT: Sleep Mode Activated
				case MessageElement.TP_EVENT_IN_SLEEP_MODE:
					break;

				// ===============================================
				// EVENT: Waking Up
				case MessageElement.TP_EVENT_WAKING_UP:
					break;

				// ===============================================
				// EVENT: Communication Framing Error
				case MessageElement.TP_EVENT_FRAMING_ERROR:
					break;

				// ===============================================
				// DATA: Camera Info
				case MessageElement.TP_DATA_CAMERA_INFO:
					Log.d("Camera Control Class",
							"MSG HWFacade=>cameraControl - TP_DATA_CAMERA_INFO was accepted. Creating object and trying to get storage info.");
					data = msg.getData().getByteArray("GottenData");

					mDeviceInfo = new DeviceInfo(mNameFactory, data.clone());
					mMainPanelHandler.obtainMessage(messageDefinitions.MESSAGE_CAMERA_DEVICE_INFO, -1, -1).sendToTarget();
					break;

				// ===============================================
				// DATA: Storage IDs
				case MessageElement.TP_DATA_STORAGE_IDS:
					break;

				// ===============================================
				// DATA: Storage Info
				case MessageElement.TP_DATA_STORAGE_INFO:
					Log.d("Camera Control Class", "MSG HWFacade=>cameraControl - ME_STORAGE_INFO was accepted.");
					data = msg.getData().getByteArray("GottenData");
					mStorageInfo = new StorageInfo(mNameFactory, data.clone());
					mMainPanelHandler.obtainMessage(messageDefinitions.MESSAGE_CAMERA_STORAGE_INFO, -1, -1).sendToTarget();
					break;

				// ===============================================
				// DATA: Camera Status
				case MessageElement.TP_DATA_CAMERA_STATUS:
					break;

				// ===============================================
				// DATA: Object Info
				case MessageElement.TP_DATA_OBJECT_INFO:
					break;

				// ===============================================
				// DATA: Object Data
				case MessageElement.TP_DATA_OBJECT:
					break;

				// ===============================================
				// DATA: Thumbnail List
				case MessageElement.TP_DATA_THUMB_LIST:
					break;

				// ===============================================
				// DATA: Thumbnail Data
				case MessageElement.TP_DATA_THUMB:
					break;

				// ===============================================
				// DATA: Property Description Data
				case MessageElement.TP_DATA_PROP_DESC:
					Log.d("Camera Control Class", "MSG HWFacade=>cameraControl - ME_PROPERTY_DESC was accepted.");
					data = msg.getData().getByteArray("GottenData");
					DevicePropDesc prop = new DevicePropDesc(mNameFactory, data.clone());
					int pos = findProperty(prop.propertyCode);
					if (pos == -1) // not found
					{
						mPropertyArray.add(prop);
					} else
					{
						mPropertyArray.set(pos, prop);
					}
					mMainPanelHandler.obtainMessage(messageDefinitions.MESSAGE_CAMERA_PROPERTY_INFO, prop.propertyCode, -1).sendToTarget();
					break;

				// ===============================================
				// DATA: Property Value
				case MessageElement.TP_DATA_PROP_VAL:
					break;

				// ===============================================
				// DATA: Afarsek Identification
				case MessageElement.TP_DATA_IDN:
					break;

				default:
					break;
				}

				break;

			/*******************************************************************************************
			 * Message written event handler
			 **/
			case messageDefinitions.MESSAGE_WRITE:
				break;
			}
		}

	};

}

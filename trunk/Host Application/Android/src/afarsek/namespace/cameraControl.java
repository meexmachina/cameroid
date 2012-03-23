/**
 * 
 */
package afarsek.namespace;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
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

		mConnectedDeviceName = devName;
		mMainPanelHandler = handler;

		// Get BT adapter
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

		// Get the BT device from name
		mBluetoothDevice = mBluetoothAdapter.getRemoteDevice(mConnectedDeviceName);

	}
	
	public int findProperty(int propCode)
	{
		for (int i=0; i<mPropertyArray.size(); i++)
		{
			if (mPropertyArray.get(i).propertyCode==propCode)
				return i;
		}
		return -1;
	}

	private void getID()
	{
		mHardwareFacade.write_queue("idn_bin:".getBytes(), MessageElement.MessageTags.ME_STATUS);
	}

	private void getStatus()
	{
		mHardwareFacade.write_queue("status_bin:".getBytes(), MessageElement.MessageTags.ME_STATUS);
	}

	private void getDeviceInfo()
	{
		mHardwareFacade.write_queue("get_dev_info_bin:".getBytes(), MessageElement.MessageTags.ME_DEVICE_INFO);
	}

	private void getStorageInfo()
	{
		mHardwareFacade.write_queue("get_storage_info_bin 0:".getBytes(), MessageElement.MessageTags.ME_DEVICE_INFO);
	}

	public int getPropertiesDescriptions(int propCode)
	{
		if (mCameraAttached == 0)
		{
			return -1;
		}

		int[] props = mDeviceInfo.propertiesSupported;
		boolean found = false;

		for (int i = 0; i < props.length; i++)
		{
			if (props[i] == propCode)
			{
				found = true;
				break;
			}
		}

		if (found == false)
		{
			return -2;
		}

		mHardwareFacade.write_queue(("get_prop_desc_bin " + String.valueOf(propCode) + ":").getBytes(),
				MessageElement.MessageTags.ME_PROPERTY_DESC);

		return 0;
	}

	class StatusTask extends TimerTask
	{
		public void run()
		{
			getStatus();
		}
	}

	public void connect()
	{
		if (mHardwareFacade == null)
			mHardwareFacade = new hardwareFacade(mHardwareDataHandler);

		// Try to connect
		mHardwareFacade.connect(mBluetoothDevice);

		timer = new Timer();
		timer.scheduleAtFixedRate(new StatusTask(), 4000, 1000);
	}

	public void disconnect()
	{
		timer.cancel();
		mHardwareFacade.stop();
		mHardwareFacade = null;
	}

	public void capture()
	{
		// TODO Auto-generated method stub

	}

	public int cameraAttached()
	{
		return mCameraAttached;
	}

	// The Handler that gets information back from the hardwareFacade
	private final Handler mHardwareDataHandler = new Handler()
	{
		private int curLength = 0;
		private int lastWrittenLength = 0;
		private byte[] tempBuf;
		private int lastWrittentHeader = 0;
		private byte[] header = new byte[3];

		@Override
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
			case messageDefinitions.MESSAGE_STATE_CHANGE:
			case messageDefinitions.MESSAGE_DEVICE_NAME:
			case messageDefinitions.MESSAGE_TOAST:
				Message new_msg = new Message();
				new_msg.copyFrom(msg);
				mMainPanelHandler.sendMessage(new_msg);
				break;
			case messageDefinitions.MESSAGE_READ:
				byte[] data = msg.getData().getByteArray(messageDefinitions.MESSAGE_READ_DATA_BYTES);
				int length = msg.arg1;
				int tagIndex = msg.getData().getInt(messageDefinitions.MESSAGE_READ_TAG);
				int leftToWrite = 0;
				int actualWrite = 0;

				// start of new transaction - get the header
				if (lastWrittentHeader < 3)
				{
					leftToWrite = 3 - lastWrittentHeader;
					actualWrite = (length < leftToWrite) ? length : leftToWrite;
					System.arraycopy(data, 0, header, lastWrittentHeader, actualWrite);
					lastWrittentHeader += actualWrite;
					length -= actualWrite;
				}

				if (lastWrittentHeader == 3) // get the rest of the message
				{
					// Combine the header info
					if (lastWrittenLength == 0)
					{
						curLength = header[1] | (header[2] << 8);
						tempBuf = new byte[curLength];
					}

					if (lastWrittenLength == 0)
					{
						System.arraycopy(data, leftToWrite, tempBuf, lastWrittenLength, length);
						lastWrittenLength += length;
					} else
					{
						System.arraycopy(data, 0, tempBuf, lastWrittenLength, length);
						lastWrittenLength += length;
					}

					if (lastWrittenLength == curLength)
					{
						curLength = 0;
						lastWrittenLength = 0;
						lastWrittentHeader = 0;
						mHardwareFacade.release_from_queue();
					} else
					{
						// wait for the next transaction
						break;
					}
				} else
				{
					break;
				}

				// Analyze the incoming data
				if (tagIndex == MessageElement.MessageTags.ME_NO_MSG.getIndex())
				{

				} else if (tagIndex == MessageElement.MessageTags.ME_PROPERTY_DESC.getIndex())
				{
					DevicePropDesc prop = new DevicePropDesc(mNameFactory, tempBuf.clone());
					int pos = findProperty(prop.propertyCode);
					if (pos==-1)	// not found
					{
						mPropertyArray.add(prop);
					}
					else
					{
						mPropertyArray.set(pos, prop);
					}
					mMainPanelHandler.obtainMessage(messageDefinitions.MESSAGE_CAMERA_PROPERTY_INFO, -1, -1).sendToTarget();
				} else if (tagIndex == MessageElement.MessageTags.ME_STORAGE_INFO.getIndex())
				{
					mStorageInfo = new StorageInfo(mNameFactory, tempBuf.clone());
					mMainPanelHandler.obtainMessage(messageDefinitions.MESSAGE_CAMERA_STORAGE_INFO, -1, -1).sendToTarget();
				} else if (tagIndex == MessageElement.MessageTags.ME_DEVICE_INFO.getIndex())
				{
					mDeviceInfo = new DeviceInfo(mNameFactory, tempBuf.clone());
					mMainPanelHandler.obtainMessage(messageDefinitions.MESSAGE_CAMERA_DEVICE_INFO, -1, -1).sendToTarget();
					getStorageInfo();
				} else if (tagIndex == MessageElement.MessageTags.ME_STATUS.getIndex())
				{
					// if we recognize that a new camera was attached
					if (mCameraAttached == 0 && tempBuf[0] == 1)
					{
						getDeviceInfo();
					}

					mCameraAttached = tempBuf[0];
					mMainPanelHandler.obtainMessage(messageDefinitions.MESSAGE_CAMERA_CONNECTION_STATE, mCameraAttached, -1).sendToTarget();
				} else if (tagIndex == MessageElement.MessageTags.ME_IDENT.getIndex())
				{

				} else
				{

				}

				break;
			case messageDefinitions.MESSAGE_WRITE:
				break;
			}
		}

	};

}

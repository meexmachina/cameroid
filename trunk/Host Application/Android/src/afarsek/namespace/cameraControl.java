/**
 * 
 */
package afarsek.namespace;

import java.util.Timer;
import java.util.TimerTask;

import ptp.DeviceInfo;
import ptp.NameFactory;

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
	private int mCameraAttached = 0;
	private DeviceInfo mDeviceInfo = null;
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

	private void getID()
	{
		mHardwareFacade.write_queue("idn_bin::".getBytes(), MessageElement.MessageTags.ME_STATUS);
	}

	private void getStatus()
	{
		mHardwareFacade.write_queue("status_bin:".getBytes(), MessageElement.MessageTags.ME_STATUS);
	}

	private void getDeviceInfo()
	{
		mHardwareFacade.write_queue("get_dev_info_bin::".getBytes(), MessageElement.MessageTags.ME_DEVICE_INFO);
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
		timer.scheduleAtFixedRate(new StatusTask(), 5000, 7000);
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

	// The Handler that gets information back from the hardwareFacade
	private final Handler mHardwareDataHandler = new Handler()
	{
		private int curType = 0;
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

				// start of new transaction - get the header
				if (lastWrittentHeader < 3)
				{
					int leftToWrite = 3 - lastWrittentHeader;
					int actualWrite = (length < leftToWrite) ? length : leftToWrite;
					System.arraycopy(data, 0, header, lastWrittentHeader, actualWrite);
					lastWrittentHeader += actualWrite;
					length -= actualWrite;
				}

				if (lastWrittentHeader == 3) // get the rest of the message
				{
					// Combine the header info
					if (lastWrittenLength == 0)
					{
						curType = header[0];
						curLength = header[1] | (header[2] << 8);
						tempBuf = new byte[curLength];
					}

					if (lastWrittenLength == 0)
					{
						System.arraycopy(data, lastWrittentHeader, tempBuf, lastWrittenLength, length);
						lastWrittenLength += length;
					} else
					{
						System.arraycopy(data, 0, tempBuf, lastWrittenLength, length);
						lastWrittenLength += length;
					}

					if (lastWrittenLength == curLength)
					{
						curLength = 0;
						curType = 0;
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

				} else if (tagIndex == MessageElement.MessageTags.ME_STORAGE_INFO.getIndex())
				{
		
				} else if (tagIndex == MessageElement.MessageTags.ME_DEVICE_INFO.getIndex())
				{
					mDeviceInfo = new DeviceInfo(mNameFactory, tempBuf.clone());
				}else if (tagIndex == MessageElement.MessageTags.ME_STATUS.getIndex())
				{
					// if we recognize that a new camera was attached
					if (mCameraAttached==0 && tempBuf[0]==1) 
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

/**
 * 
 */
package afarsek.namespace;

import java.util.Timer;
import java.util.TimerTask;

import ptp.DeviceInfo;

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

	private void getStatus()
	{
		mHardwareFacade.write_queue("status:".getBytes(), MessageElement.MessageTags.ME_STATUS);
	}

	private void getDeviceInfo()
	{
		mHardwareFacade.write_queue("get_dev_info_bin:".getBytes(), MessageElement.MessageTags.ME_DEVICE_INFO);
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
		timer.scheduleAtFixedRate(new StatusTask(), 3000, 1000);
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
				char[] data = (new String(msg.getData().getByteArray(messageDefinitions.MESSAGE_READ_DATA_BYTES))).toCharArray();
				int tagIndex = msg.getData().getInt(messageDefinitions.MESSAGE_READ_TAG);

				// Analyze the incoming data
				if (tagIndex == MessageElement.MessageTags.ME_NO_MSG.getIndex())
				{

				} else if (tagIndex == MessageElement.MessageTags.ME_DEVICE_INFO.getIndex())
				{

				} else if (tagIndex == MessageElement.MessageTags.ME_PROPERTY_DESC.getIndex())
				{

				} else if (tagIndex == MessageElement.MessageTags.ME_STORAGE_INFO.getIndex())
				{

				} else if (tagIndex == MessageElement.MessageTags.ME_STATUS.getIndex())
				{
					mCameraAttached = data[0] - '0';
					mMainPanelHandler.obtainMessage(messageDefinitions.MESSAGE_CAMERA_CONNECTION_STATE, mCameraAttached, -1).sendToTarget();
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

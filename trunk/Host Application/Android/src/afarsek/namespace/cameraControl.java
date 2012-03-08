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
	private int mCurrentExecuting = 0;
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
	
	private void setQuiteMode(int onoff)
	{
		if (mCurrentExecuting != 0)
			return;

		String command = "set_quite " + String.valueOf(onoff) + ":";
		mHardwareFacade.write(command.getBytes());
		mCurrentExecuting = 0;
	}
	
	private void setEchoMode(int onoff)
	{
		if (mCurrentExecuting != 0)
			return;

		String command = null;
		
		if (onoff==1) command = "eon:";
		else command = "eof:";
		
		mHardwareFacade.write(command.getBytes());
		mCurrentExecuting = 0;
	}

	private void getStatus()
	{
		if (mCurrentExecuting != 0)
			return;

		mHardwareFacade.write("status:".getBytes());
		mCurrentExecuting = 10;
	}

	private void getDeviceInfo()
	{
		if (mCurrentExecuting != 0)
			return;

		mHardwareFacade.write("get_dev_info_bin:".getBytes());
		mCurrentExecuting = 1;
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
		mCurrentExecuting = 0;
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
				//int length = msg.getData().getInt(messageDefinitions.MESSAGE_READ_LENGTH);

				switch (mCurrentExecuting)
				{
				case 0:
					mCurrentExecuting = 0;
					break;
				case 1:
					mCurrentExecuting = 0;
					break;
				case 2:
					mCurrentExecuting = 0;
					break;
				case 3:
					mCurrentExecuting = 0;
					break;
				case 10:
					mCameraAttached = data[0]-'0';
					mCurrentExecuting = 0;
					break;
				case 11:
					mCurrentExecuting = 0;
					break;
				case 12:
					mCurrentExecuting = 0;
					break;
				
				}

				break;
			case messageDefinitions.MESSAGE_WRITE:
				break;
			}
		}
	};

}

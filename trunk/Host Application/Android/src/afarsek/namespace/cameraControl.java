/**
 * 
 */
package afarsek.namespace;

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
	
	// Message types sent from the Handler
	public static final int MESSAGE_STATE_CHANGE = 1;
	public static final int MESSAGE_READ = 2;
	public static final int MESSAGE_WRITE = 3;
	public static final int MESSAGE_DEVICE_NAME = 4;
	public static final int MESSAGE_TOAST = 5;

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
	
	public void connect()
	{
		if (mHardwareFacade == null)
			mHardwareFacade = new hardwareFacade(mHardwareDataHandler);
		
		// Try to connect
		mHardwareFacade.connect(mBluetoothDevice);
	}

	public void disconnect()
	{
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
		@SuppressWarnings("null")
		@Override
		public void handleMessage(Message msg)
		{
			Message new_msg = new Message(); 
			new_msg.copyFrom(msg);
			mMainPanelHandler.sendMessage(new_msg);
			
			switch (msg.what)
			{
			case MESSAGE_STATE_CHANGE:
				switch (msg.arg1)
				{
				case hardwareFacade.STATE_CONNECTED:
					break;
				case hardwareFacade.STATE_CONNECTING:
					break;
				case hardwareFacade.STATE_LISTEN:
				case hardwareFacade.STATE_NONE:
					break;
				}
				break;
			case MESSAGE_DEVICE_NAME:
				// save the connected device's name
				break;
			case MESSAGE_TOAST:
				break;
			case MESSAGE_READ:
				break;
			}
		}
	};


}

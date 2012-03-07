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
		
		// Send something
		mHardwareFacade.write("\r\n\r\n");
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
				int length = msg.getData().getInt(messageDefinitions.MESSAGE_READ_LENGTH);
				
				
				break;
			case messageDefinitions.MESSAGE_WRITE:
				break;
			}
		}
	};


}

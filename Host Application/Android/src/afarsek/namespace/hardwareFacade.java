/**
 * 
 */
package afarsek.namespace;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;

/**
 * @author david
 * 
 */
public class hardwareFacade
{

	/**
	 * Internal properties
	 */
	private String				m_macAddress;
	private BluetoothAdapter	m_BtAdapter;
	private BluetoothSocket		m_btSocket		= null;
	private OutputStream		m_outStream		= null;
	private InputStream			m_inStream		= null;
	private ConnectThread		m_ConnectThread	= null;
	private static final UUID	SPP_UUID		= UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	private Handler				m_Handler;

	/**
	 * Constructor
	 */
	public hardwareFacade(String mac)
	{
		// TODO Auto-generated constructor stub
		this.setMacAddress(mac);
		
		m_Handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// Check if bluetooth connection was made to selected device
				if (msg.what == 1) {

				} else {
					// Connection failed
					//failToast.show();
				}
			}
		};
		
		
		// Check whether bluetooth adapter exists
		m_BtAdapter = BluetoothAdapter.getDefaultAdapter();
		if (m_BtAdapter == null)
		{
			//Toast.makeText(this, R.string.main_panel_bt_not_available, Toast.LENGTH_LONG).show();
			return;
		}

		// If BT is not on, request that it be enabled.
		if (!m_BtAdapter.isEnabled())
		{

		}
		
		
	}
	
	public void connect ()
	{
		
	}
	
	public void disconnect ()
	{
		
	}
	
	public void dispose()
	{
		if (m_outStream != null) {
			try {
				disconnect();
				m_outStream.flush();
			} catch (IOException e) {
			}
		}
	}

	public void write(byte data) {
		if (m_outStream != null) {
			try {
				m_outStream.write(data);
			} catch (IOException e) {
			}
		}
	}
	
	public byte read( ) {
		byte data = 0;
		if (m_inStream != null) {
			try {
				data = (byte) m_inStream.read();
			} catch (IOException e) {
			}
		}
		return data;
	}
	
	/**
	 * Getters & Setters
	 */
	public String getMacAddress()
	{
		return m_macAddress;
	}

	public void setMacAddress(String macAddress)
	{
		this.m_macAddress = macAddress;
	}

	/** Thread used to connect to a specified Bluetooth Device */
	public class ConnectThread extends Thread
	{
		private String	address;
		private boolean	connectionStatus;

		ConnectThread(String MACaddress)
		{
			address = MACaddress;
			connectionStatus = true;
		}

		public void run()
		{
			// When this returns, it will 'know' about the server,
			// via it's MAC address.
			try
			{
				BluetoothDevice device = m_BtAdapter.getRemoteDevice(address);

				// We need two things before we can successfully connect
				// (authentication issues aside): a MAC address, which we
				// already have, and an RFCOMM channel.
				// Because RFCOMM channels (aka ports) are limited in
				// number, Android doesn't allow you to use them directly;
				// instead you request a RFCOMM mapping based on a service
				// ID. In our case, we will use the well-known SPP Service
				// ID. This ID is in UUID (GUID to you Microsofties)
				// format. Given the UUID, Android will handle the
				// mapping for you. Generally, this will return RFCOMM 1,
				// but not always; it depends what other BlueTooth services
				// are in use on your Android device.
				try
				{
					m_btSocket = device.createRfcommSocketToServiceRecord(SPP_UUID);
				} catch (IOException e)
				{
					connectionStatus = false;
				}
			} catch (IllegalArgumentException e)
			{
				connectionStatus = false;
			}

			// Discovery may be going on, e.g., if you're running a
			// 'scan for devices' search from your handset's Bluetooth
			// settings, so we call cancelDiscovery(). It doesn't hurt
			// to call it, but it might hurt not to... discovery is a
			// heavyweight process; you don't want it in progress when
			// a connection attempt is made.
			m_BtAdapter.cancelDiscovery();

			// Blocking connect, for a simple client nothing else can
			// happen until a successful connection is made, so we
			// don't care if it blocks.
			try
			{
				m_btSocket.connect();
			} catch (IOException e1)
			{
				try
				{
					m_btSocket.close();
				} catch (IOException e2)
				{
				}
			}

			// Create a data stream so we can talk to server.
			try
			{
				m_outStream = m_btSocket.getOutputStream();
				m_inStream = m_btSocket.getInputStream();
			} catch (IOException e2)
			{
				connectionStatus = false;
			}

			// Send final result
			if (connectionStatus)
			{
				m_Handler.sendEmptyMessage(1);
			} else
			{
				m_Handler.sendEmptyMessage(0);
			}
		}
	}

}

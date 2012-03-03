package afarsek.namespace;

import java.util.Set;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class deviceSelectionActivity extends Activity
{

	// Member fields
	private BluetoothAdapter m_BtAdapter;
	private ArrayAdapter<String> m_DevicesArrayAdapter;

	// Intent request codes
	private static final int REQUEST_ENABLE_BT = 2;

	// Passing Intent extra
	public static String EXTRA_DEVICE_ADDRESS = "device_address";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// Setup the window
		setContentView(R.layout.device_selection);

		// Initialize array adapter
		m_DevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.device_name);

		// Initialize the button to perform device discovery
		Button refreshButton = (Button) findViewById(R.id.refresh_button);
		refreshButton.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				refreshDeviceList();
			}
		});

		// Find and set up the ListView for paired devices
		ListView devicesListView = (ListView) findViewById(R.id.device_list_view);
		devicesListView.setAdapter(m_DevicesArrayAdapter);
		devicesListView.setOnItemClickListener(m_DeviceClickListener);

		// Register for broadcasts when a device is discovered
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		this.registerReceiver(m_Receiver, filter);

		// Register for broadcasts when discovery has finished
		filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		this.registerReceiver(m_Receiver, filter);

		// Get the local Bluetooth adapter
		m_BtAdapter = BluetoothAdapter.getDefaultAdapter();
		if (m_BtAdapter == null)
		{
			Toast.makeText(this, "Bluetooth hardware not detected.", Toast.LENGTH_LONG).show();
			finish();
			return;
		}

		// If BT is not on, request that it be enabled.
		if (!m_BtAdapter.isEnabled())
		{
			Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
		}

		// Get a set of currently paired devices
		Set<BluetoothDevice> pairedDevices = m_BtAdapter.getBondedDevices();

		// If there are paired devices, add each one to the ArrayAdapter
		if (pairedDevices.size() > 0)
		{
			for (BluetoothDevice device : pairedDevices)
			{
				m_DevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
			}
		} else
		{
			m_DevicesArrayAdapter.add("No paired devices...");
		}

		refreshDeviceList();
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();

		// Make sure we're not doing discovery anymore
		if (m_BtAdapter != null)
		{
			m_BtAdapter.cancelDiscovery();
		}

		// Unregister broadcast listeners
		this.unregisterReceiver(m_Receiver);
	}

	private OnItemClickListener m_DeviceClickListener = new OnItemClickListener()
	{
		public void onItemClick(AdapterView<?> arg0, View view, int arg2, long arg3)
		{
			// Cancel discovery because it's costly and we're about to connect
			m_BtAdapter.cancelDiscovery();

			// Get the device MAC address, which is the last 17 chars in the
			// View
			String info = ((TextView) view).getText().toString();
			try
			{
				// Attempt to extract a MAC address
				String address = info.substring(info.length() - 17);

				// Create the result Intent and include the MAC address
				Intent startMainPanel = new Intent("afarsek.namespace.MAINPANELACTIVITY");
				startMainPanel.putExtra(EXTRA_DEVICE_ADDRESS, address);
				startActivity(startMainPanel);
				finish();
			} catch (IndexOutOfBoundsException e)
			{
				// Extraction failed, set result and finish this Activity
				setResult(Activity.RESULT_CANCELED);
				finish();
			}
		}
	};

	// The BroadcastReceiver that listens for discovered devices and
	// changes the title when discovery is finished
	private final BroadcastReceiver m_Receiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			String action = intent.getAction();

			// When discovery finds a device
			if (BluetoothDevice.ACTION_FOUND.equals(action))
			{
				// Get the BluetoothDevice object from the Intent
				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				// If it's already paired, skip it, because it's been listed
				// already
				if (device.getBondState() != BluetoothDevice.BOND_BONDED)
				{
					m_DevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
				}
				// When discovery is finished, change the Activity title
			} else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action))
			{
				setProgressBarIndeterminateVisibility(false);
				if (m_DevicesArrayAdapter.getCount() == 0)
				{
					m_DevicesArrayAdapter.add("No devices...");
				}
			}
		}
	};

	/**
	 * Start device discover with the BluetoothAdapter
	 */
	private void refreshDeviceList()
	{
		setProgressBarIndeterminateVisibility(true);

		// If we're already discovering, stop it
		if (m_BtAdapter.isDiscovering())
		{
			m_BtAdapter.cancelDiscovery();
		}

		m_DevicesArrayAdapter.clear();
		// Get a set of currently paired devices
		Set<BluetoothDevice> pairedDevices = m_BtAdapter.getBondedDevices();

		// If there are paired devices, add each one to the ArrayAdapter
		if (pairedDevices.size() > 0)
		{
			for (BluetoothDevice device : pairedDevices)
			{
				m_DevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
			}
		} else
		{
			m_DevicesArrayAdapter.add("No paired devices...");
		}

		// Request discovery from BluetoothAdapter
		m_BtAdapter.startDiscovery();
	}

	@Override
	protected void onPause()
	{
		// TODO Auto-generated method stub
		super.onPause();
		finish();
	}

};

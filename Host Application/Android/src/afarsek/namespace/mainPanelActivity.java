package afarsek.namespace;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class mainPanelActivity extends Activity
{

	// Internal properties
	private TextView mTitle;
	private String mConnectedDeviceName = null; // Name of the connected device
	private hardwareFacade hwFacade = null;
	private BluetoothAdapter mBluetoothAdapter;

	// Constants
	public static final String EXTRA_DEVICE_ADDRESS = "device_address";
	public static final String DEVICE_NAME = "device_name";
	public static final String TOAST = "toast";

	// Message types sent from the Handler
	public static final int MESSAGE_STATE_CHANGE = 1;
	public static final int MESSAGE_READ = 2;
	public static final int MESSAGE_WRITE = 3;
	public static final int MESSAGE_DEVICE_NAME = 4;
	public static final int MESSAGE_TOAST = 5;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// Set up the window layout
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.main_panel);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title);

		// Set up the custom title
		mTitle = (TextView) findViewById(R.id.title_left_text);
		mTitle.setText(R.string.app_name);
		mTitle = (TextView) findViewById(R.id.title_right_text);

		// Obtain the sent bundle
		Bundle extras = getIntent().getExtras();
		if (extras != null)
		{
			mConnectedDeviceName = extras.getString(EXTRA_DEVICE_ADDRESS);
		}

		// Get local Bluetooth adapter
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

		// If the adapter is null, then Bluetooth is not supported
		if (mBluetoothAdapter == null)
		{
			Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
			finish();
			return;
		}

		// Button definitions
		Button captureButton = (Button) findViewById(R.id.capture_button);
		captureButton.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				capture();
			}
		});

	}

	@Override
	protected void onStart()
	{
		super.onStart();
		if (hwFacade == null)
			hwFacade = new hardwareFacade(this, mHandler);

		// Get the BT device from name
		BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(mConnectedDeviceName);

		// Attempt to connect to the device
		hwFacade.connect(device);
	}

	@Override
	protected void onStop() 
	{
		if (hwFacade != null)
			hwFacade.stop();
		hwFacade = null;
		
		super.onStop();
	};
	
	@Override
	protected void onDestroy()
	{
		super.onDestroy();

		if (hwFacade != null)
			hwFacade.stop();
	};

	@Override
	public void onBackPressed()
	{
		Intent discoveryPanel = new Intent("afarsek.namespace.DEVICESELECTIONACTIVITY");
		startActivity(discoveryPanel);
		finish();
	};

	// The Handler that gets information back from the hardwareFacade
	private final Handler mHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
			case MESSAGE_STATE_CHANGE:
				switch (msg.arg1)
				{
				case hardwareFacade.STATE_CONNECTED:
					mTitle.setText(R.string.title_connected_to);
					mTitle.append(mConnectedDeviceName);
					break;
				case hardwareFacade.STATE_CONNECTING:
					mTitle.setText(R.string.title_connecting);
					break;
				case hardwareFacade.STATE_LISTEN:
				case hardwareFacade.STATE_NONE:
					mTitle.setText(R.string.title_not_connected);
					break;
				}
				break;
			case MESSAGE_DEVICE_NAME:
				// save the connected device's name
				mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
				Toast.makeText(getApplicationContext(), "Connected to " + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
				break;
			case MESSAGE_TOAST:
				Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST), Toast.LENGTH_SHORT).show();
				break;
			case MESSAGE_READ:
				break;
			}
		}
	};

	private void capture()
	{
		// TODO Auto-generated method stub

	}
}

package afarsek.namespace;

import widget.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

public class mainPanelActivity extends Activity
{

	// Internal properties
	private TextView mCameraConnectedText;
	private String mConnectedDeviceName = null; // Name of the connected device
	private cameraControl mCameraControl = null;
	private ActionBar actionBar; 

	// Constants
	public static final String EXTRA_DEVICE_ADDRESS = "device_address";
	public static final String DEVICE_NAME = "device_name";
	public static final String TOAST = "toast";

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		// Set up the window layout
		setContentView(R.layout.main_panel);
		
		// Setup the action-bar
		actionBar = (ActionBar) findViewById(R.id.actionbar_main);
		actionBar.setTitle("Main Window");
		
		// Set up the text view for camera connected
		mCameraConnectedText = (TextView) findViewById(R.id.camera_connected);
		mCameraConnectedText.setText(R.string.camera_not_connected);

		// Obtain the sent bundle
		Bundle extras = getIntent().getExtras();
		if (extras != null)
		{
			mConnectedDeviceName = extras.getString(EXTRA_DEVICE_ADDRESS);
		}

		// Get local Bluetooth adapter
		/*
		 * mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		 * 
		 * // If the adapter is null, then Bluetooth is not supported if (mBluetoothAdapter == null) { Toast.makeText(this,
		 * "Bluetooth is not available", Toast.LENGTH_LONG).show(); finish(); return; }
		 */

		// Button definitions
	/*	Button captureButton = (Button) findViewById(R.id.capture_button);
		captureButton.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				mCameraControl.capture();
			}
		});*/

	}

	@Override
	protected void onStart()
	{
		super.onStart();

		if (mCameraControl == null)
			mCameraControl = new cameraControl(mConnectedDeviceName, mHandler);

		// Attempt to connect to the device
		mCameraControl.connect();
	}

	@Override
	protected void onStop()
	{
		if (mCameraControl != null)
			mCameraControl.disconnect();
		mCameraControl = null;

		super.onStop();
	};

	@Override
	protected void onDestroy()
	{
		super.onDestroy();

		if (mCameraControl != null)
			mCameraControl.disconnect();
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
			case messageDefinitions.MESSAGE_STATE_CHANGE:
				switch (msg.arg1)
				{
				case hardwareFacade.STATE_CONNECTED:
					actionBar.setTitle("Connected to " + mConnectedDeviceName);
					break;
				case hardwareFacade.STATE_CONNECTING:
					actionBar.setTitle("Connecting...");
					break;
				case hardwareFacade.STATE_LISTEN:
				case hardwareFacade.STATE_NONE:
					actionBar.setTitle("Disconnected");
					break;
				}
				break;
			case messageDefinitions.MESSAGE_CAMERA_CONNECTION_STATE:
				if (msg.arg1 == 1)
					mCameraConnectedText.setText(R.string.camera_connected);
				else
					mCameraConnectedText.setText(R.string.camera_not_connected);
				break;
			case messageDefinitions.MESSAGE_DEVICE_NAME:
				// save the connected device's name
				mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
				Toast.makeText(getApplicationContext(), "Connected to " + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
				break;
			case messageDefinitions.MESSAGE_TOAST:
				Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST), Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};

}

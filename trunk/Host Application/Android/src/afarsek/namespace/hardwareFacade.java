package afarsek.namespace;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Timer;
import java.util.TimerTask;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class hardwareFacade
{
	// Debugging
	private static final String TAG = "HardwareFacade";
	private static final boolean D = true;

	// Member fields
	// private final Context mContext;
	private final BluetoothAdapter mAdapter;
	private final Handler mHandler;
	private ConnectThread mConnectThread;
	private ConnectedThread mConnectedThread;
	private int mTransactionID = 0;
	private int mState;

	// Constants that indicate the current connection state
	public static final int STATE_NONE = 0; // we're doing nothing
	public static final int STATE_LISTEN = 1; // now listening for incoming connections
	public static final int STATE_CONNECTING = 2; // now initiating an outgoing connection
	public static final int STATE_CONNECTED = 3; // now connected to a remote device

	/**
	 * Constructor. Prepares a new BluetoothChat session.
	 * 
	 * @param context
	 *            The UI Activity Context
	 * @param handler
	 *            A Handler to send messages back to the UI Activity
	 */
	public hardwareFacade(Handler handler)
	{
		mAdapter = BluetoothAdapter.getDefaultAdapter();
		mState = STATE_NONE;
		// mConnectionLostCount = 0;
		mHandler = handler;
	}

	/**
	 * Set the current state of the chat connection
	 * 
	 * @param state
	 *            An integer defining the current connection state
	 */
	private synchronized void setState(int state)
	{
		if (D)
			Log.d(TAG, "setState() " + mState + " -> " + state);
		mState = state;

		// Give the new state to the Handler so the UI Activity can update
		mHandler.obtainMessage(messageDefinitions.MESSAGE_STATE_CHANGE, state, -1).sendToTarget();
	}

	/**
	 * Start the service. Specifically start AcceptThread to begin a session in listening (server) mode. Called by the Activity onResume()
	 */
	public synchronized void start()
	{
		if (D)
			Log.d(TAG, "start");

		// Cancel any thread attempting to make a connection
		if (mConnectThread != null)
		{
			mConnectThread.cancel();
			mConnectThread = null;
		}

		// Cancel any thread currently running a connection
		if (mConnectedThread != null)
		{
			mConnectedThread.cancel();
			mConnectedThread = null;
		}

		setState(STATE_LISTEN);
	}

	/**
	 * Start the ConnectThread to initiate a connection to a remote device.
	 * 
	 * @param device
	 *            The BluetoothDevice to connect
	 */
	public synchronized void connect(BluetoothDevice device)
	{
		if (D)
			Log.d(TAG, "connect to: " + device);

		// Cancel any thread attempting to make a connection
		if (mState == STATE_CONNECTING)
		{
			if (mConnectThread != null)
			{
				mConnectThread.cancel();
				mConnectThread = null;
			}
		}

		// Cancel any thread currently running a connection
		if (mConnectedThread != null)
		{
			mConnectedThread.cancel();
			mConnectedThread = null;
		}

		// Start the thread to connect with the given device
		mConnectThread = new ConnectThread(device);
		mConnectThread.start();
		setState(STATE_CONNECTING);
	}

	/**
	 * Return the current connection state.
	 */
	public synchronized int getState()
	{
		return mState;
	}

	/**
	 * Start the ConnectedThread to begin managing a Bluetooth connection
	 * 
	 * @param socket
	 *            The BluetoothSocket on which the connection was made
	 * @param device
	 *            The BluetoothDevice that has been connected
	 */
	public synchronized void connected(BluetoothSocket socket, BluetoothDevice device)
	{
		if (D)
			Log.d(TAG, "connected");

		// Cancel the thread that completed the connection
		if (mConnectThread != null)
		{
			mConnectThread.cancel();
			mConnectThread = null;
		}

		// Cancel any thread currently running a connection
		if (mConnectedThread != null)
		{
			mConnectedThread.cancel();
			mConnectedThread = null;
		}

		// Start the thread to manage the connection and perform transmissions
		mConnectedThread = new ConnectedThread(socket);
		mConnectedThread.start();

		// Send the name of the connected device back to the UI Activity
		Message msg = mHandler.obtainMessage(messageDefinitions.MESSAGE_DEVICE_NAME);
		Bundle bundle = new Bundle();
		bundle.putString(mainPanelActivity.DEVICE_NAME, device.getName());
		msg.setData(bundle);
		mHandler.sendMessage(msg);

		setState(STATE_CONNECTED);
	}

	/**
	 * Stop all threads
	 */
	public synchronized void stop()
	{
		if (D)
			Log.d(TAG, "stop");
		if (mConnectThread != null)
		{
			mConnectThread.cancel();
			mConnectThread = null;
		}
		if (mConnectedThread != null)
		{
			mConnectedThread.cancel();
			mConnectedThread = null;
		}

		setState(STATE_NONE);
	}

	private byte[] mDataToWrite;

	public void write_delayed(byte[] out)
	{
		mDataToWrite = out;
		Timer timer = new Timer();
		timer.schedule(new myTimer(), 10);
	}

	class myTimer extends TimerTask
	{
		@Override
		public void run()
		{
			if (mDataToWrite != null)
				write(mDataToWrite);
		}
	}

	/**
	 * Write to the ConnectedThread in an unsynchronized manner
	 * 
	 * @param out
	 *            The bytes to write
	 * @see ConnectedThread#write(byte[])
	 */
	public void write(byte[] out)
	{
		// Create temporary object
		ConnectedThread r;
		// Synchronize a copy of the ConnectedThread
		synchronized (this)
		{
			if (mState != STATE_CONNECTED)
				return;
			r = mConnectedThread;
		}
		// Perform the write unsynchronized
		r.write(out);
	}

	/**
	 * Write to the ConnectedThread in an unsynchronized manner
	 * 
	 * @param out
	 *            The string to write
	 * @see ConnectedThread#write(byte[])
	 */
	public void write(String out)
	{
		// Create temporary object
		ConnectedThread r;
		// Synchronize a copy of the ConnectedThread
		synchronized (this)
		{
			if (mState != STATE_CONNECTED)
				return;
			r = mConnectedThread;
		}
		// Perform the write unsynchronized
		r.write(out.getBytes());
	}

	public void write(int out)
	{
		// Create temporary object
		ConnectedThread r;
		// Synchronize a copy of the ConnectedThread
		synchronized (this)
		{
			if (mState != STATE_CONNECTED)
				return;
			r = mConnectedThread;
		}
		// Perform the write unsynchronized
		r.write(out);
	}

	/**
	 * Indicate that the connection attempt failed and notify the UI Activity.
	 */
	private void connectionFailed()
	{
		setState(STATE_LISTEN);

		// Send a failure message back to the Activity
		Message msg = mHandler.obtainMessage(messageDefinitions.MESSAGE_TOAST);
		Bundle bundle = new Bundle();
		bundle.putString(mainPanelActivity.TOAST, "Unable to connect device");
		msg.setData(bundle);
		mHandler.sendMessage(msg);
	}

	/**
	 * Indicate that the connection was lost and notify the UI Activity.
	 */
	private void connectionLost()
	{
		setState(STATE_LISTEN);
		// Send a failure message back to the Activity
		Message msg = mHandler.obtainMessage(messageDefinitions.MESSAGE_TOAST);
		Bundle bundle = new Bundle();
		bundle.putString(mainPanelActivity.TOAST, "Device connection was lost");
		msg.setData(bundle);
		mHandler.sendMessage(msg);
	}

	/**
	 * This thread runs while attempting to make an outgoing connection with a device. It runs straight through; the connection either
	 * succeeds or fails.
	 */
	private class ConnectThread extends Thread
	{
		private final BluetoothSocket mmSocket;
		private final BluetoothDevice mmDevice;

		public ConnectThread(BluetoothDevice device)
		{
			mmDevice = device;
			BluetoothSocket tmp = null;

			// Get a BluetoothSocket for a connection with the
			// given BluetoothDevice
			try
			{
				Method m;
				m = mmDevice.getClass().getMethod("createInsecureRfcommSocket", new Class[]
				{ int.class });
				tmp = (BluetoothSocket) m.invoke(mmDevice, Integer.valueOf(1));
			} catch (SecurityException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchMethodException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			mmSocket = tmp;
		}

		public void run()
		{
			Log.i(TAG, "BEGIN mConnectThread");
			setName("ConnectThread");

			// Always cancel discovery because it will slow down a connection
			mAdapter.cancelDiscovery();

			// Make a connection to the BluetoothSocket
			try
			{
				// This is a blocking call and will only return on a
				// successful connection or an exception
				mmSocket.connect();
			} catch (IOException e)
			{
				connectionFailed();
				// Close the socket
				try
				{
					mmSocket.close();
				} catch (IOException e2)
				{
					Log.e(TAG, "unable to close() socket during connection failure", e2);
				}
				// Start the service over to restart listening mode
				hardwareFacade.this.start();
				return;
			}

			// Reset the ConnectThread because we're done
			synchronized (hardwareFacade.this)
			{
				mConnectThread = null;
			}

			// Start the connected thread
			connected(mmSocket, mmDevice);
		}

		public void cancel()
		{
			try
			{
				mmSocket.close();
				// unpairDevice(mmDevice);
			} catch (IOException e)
			{
				Log.e(TAG, "close() of connect socket failed", e);
			}
		}
	}

	public static final int CT_STATE_GETTING_MSG_HEADER = 0x01;
	public static final int CT_STATE_GETTING_MSG_EVENT = 0x02;
	public static final int CT_STATE_GETTING_MSG_DATA = 0x03;

	public static final int CT_LENGTH_MSG_HEADER = 5; // 5 bytes
	public static final int CT_LENGTH_MSG_EVENT = 9; // 9 bytes

	public static int byteToInteger(byte b)
	{
		return (b < 0) ? (int) (256 + b) : (int) b;
	}

	/**
	 * This class implements the message header of Transfer Protocol
	 */
	public static class TP_header
	{
		public int mType;
		public int mLength;
		public int mTransID;

		public TP_header()
		{
			mType = 0;
			mLength = 0;
			mTransID = 0;
		}

		public TP_header(byte[] data)
		{
			mType = byteToInteger(data[0]);
			mLength = 0xff & byteToInteger(data[1]);
			mLength |= 0xff00 & (byteToInteger(data[2]) << 8);
			mTransID = 0xff & byteToInteger(data[3]);
			mTransID |= 0xff00 & (byteToInteger(data[4]) << 8);
		}

		public byte[] getByteArray()
		{
			byte[] arr = new byte[CT_LENGTH_MSG_HEADER];

			arr[0] = (byte) mType;
			arr[1] = (byte) ((mLength) & 0xff); // first LSB
			arr[2] = (byte) ((mLength >> 8) & 0xff); // second MSB
			arr[3] = (byte) ((mTransID) & 0xff); // first LSB
			arr[4] = (byte) ((mTransID >> 8) & 0xff); // second MSB

			return arr;
		}

		public byte getChecksum()
		{
			byte[] arr = getByteArray();
			int sum = 0;
			for (int i = 0; i < arr.length; i++)
				sum += (int) byteToInteger(arr[i]);

			return (byte) (sum & 0xff);
		}
	}

	/**
	 * This class implements the outgoing command message for Transfer Protocol
	 */
	public static class TP_command
	{
		public static final int CT_LENGTH_MSG_COMMAND = 18; // 18 bytes

		public TP_header header;
		public int arg1;
		public int arg2;
		public int arg3;
		private byte checksum;

		public TP_command(TP_header h, int a1, int a2, int a3)
		{
			header = new TP_header();
			header.mLength = h.mLength;
			header.mTransID = h.mTransID;
			header.mType = h.mType;
			arg1 = a1;
			arg2 = a2;
			arg3 = a3;
		}

		public byte[] getByteArray()
		{
			byte[] headerBytes = header.getByteArray();
			byte[] arr = new byte[CT_LENGTH_MSG_COMMAND];
			int i = 0;

			for (i = 0; i < headerBytes.length; i++)
				arr[i] = headerBytes[i];

			arr[i] = (byte) ((arg1 >> 0) & 0xff);
			arr[i + 1] = (byte) ((arg1 >> 8) & 0xff);
			arr[i + 2] = (byte) ((arg1 >> 16) & 0xff);
			arr[i + 3] = (byte) ((arg1 >> 24) & 0xff);
			arr[i + 4] = (byte) ((arg2 >> 0) & 0xff);
			arr[i + 5] = (byte) ((arg2 >> 8) & 0xff);
			arr[i + 6] = (byte) ((arg2 >> 16) & 0xff);
			arr[i + 7] = (byte) ((arg2 >> 24) & 0xff);
			arr[i + 8] = (byte) ((arg3 >> 0) & 0xff);
			arr[i + 9] = (byte) ((arg3 >> 8) & 0xff);
			arr[i + 10] = (byte) ((arg3 >> 16) & 0xff);
			arr[i + 11] = (byte) ((arg3 >> 24) & 0xff);

			int sum = 0;
			for (int j = 0; j < CT_LENGTH_MSG_COMMAND - 1; j++)
				sum += (int) byteToInteger(arr[j]);

			checksum = arr[i + 12] = (byte) (sum & 0xff); // checksum
			return arr;
		}
	}

	/**
	 * This thread runs during a connection with a remote device. It handles all incoming and outgoing transmissions.
	 */
	private class ConnectedThread extends Thread
	{
		private final BluetoothSocket mmSocket;
		private final InputStream mmInStream;
		private final OutputStream mmOutStream;

		public ConnectedThread(BluetoothSocket socket)
		{
			Log.d(TAG, "create ConnectedThread");
			mmSocket = socket;
			InputStream tmpIn = null;
			OutputStream tmpOut = null;

			// Get the BluetoothSocket input and output streams
			try
			{
				tmpIn = socket.getInputStream();
				tmpOut = socket.getOutputStream();
			} catch (IOException e)
			{
				Log.e(TAG, "temp sockets not created", e);
			}

			mmInStream = tmpIn;
			mmOutStream = tmpOut;
		}

		public void run()
		{
			Log.i(TAG, "BEGIN mConnectedThread");
			byte[] buffer = new byte[4096];
			int byteCount = 0;
			int iState = CT_STATE_GETTING_MSG_HEADER;
			int iRemainingtoGet = CT_LENGTH_MSG_HEADER;
			int currentOffset = 0;
			TP_header header = new TP_header();

			// Keep listening to the InputStream while connected
			while (true)
			{
				try
				{
					if (currentOffset < iRemainingtoGet)
						byteCount = mmInStream.read(buffer, currentOffset, buffer.length - currentOffset);
					else
						byteCount = 0;

					// ====== DEBUG - write the header to the logger
					String logged = "Trans Read Header: ";
					for (int i = currentOffset; i < (currentOffset + byteCount); i++)
					{
						logged += String.format("%x ", buffer[i]);
					}
					if (byteCount > 0)
						Log.d(TAG, logged);
					// ====== DEBUG - write the header to the logger

					// if we just got just enough bytes
					if (currentOffset + byteCount >= iRemainingtoGet)
					{
						int oldRemainingToGet = iRemainingtoGet;

						// Analyze
						if (iState == CT_STATE_GETTING_MSG_HEADER)
						{
							header = new TP_header(buffer);

							// if its an event
							if (header.mType > MessageElement.TP_EVENT_START && header.mType < MessageElement.TP_EVENT_END)
							{
								iState = CT_STATE_GETTING_MSG_EVENT;
								iRemainingtoGet = header.mLength;
							} else if (header.mType > MessageElement.TP_DATA_START && header.mType < MessageElement.TP_DATA_END)
							{
								iState = CT_STATE_GETTING_MSG_DATA;
								iRemainingtoGet = header.mLength;
							}
						} else if (iState == CT_STATE_GETTING_MSG_EVENT)
						{
							// if we just got the bytes
							int arg1 = ((int) buffer[0]) | (((int) buffer[1]) << 8) | (((int) buffer[2]) << 16) | (((int) buffer[3]) << 24);

							// send the info to camera-control class
							Message msg = mHandler.obtainMessage(messageDefinitions.MESSAGE_READ, header.mType, arg1);
							Bundle bundle = new Bundle();
							bundle.putInt("TransactionID", header.mTransID);
							msg.setData(bundle);
							mHandler.sendMessage(msg);

							iState = CT_STATE_GETTING_MSG_HEADER;
							iRemainingtoGet = CT_LENGTH_MSG_HEADER;
						} else if (iState == CT_STATE_GETTING_MSG_DATA)
						{
							// send the info to camera-control class
							Message msg = mHandler.obtainMessage(messageDefinitions.MESSAGE_READ, header.mType, header.mLength);
							Bundle bundle = new Bundle();
							bundle.putInt("TransactionID", header.mTransID);
							bundle.putByteArray("GottenData", buffer.clone());
							msg.setData(bundle);
							mHandler.sendMessage(msg);

							iState = CT_STATE_GETTING_MSG_HEADER;
							iRemainingtoGet = CT_LENGTH_MSG_HEADER;
						}

						// arrange stuff
						for (int ii = 0; ii < (currentOffset + byteCount - oldRemainingToGet); ii++)
						{
							buffer[ii] = buffer[oldRemainingToGet + ii];
						}
						currentOffset = currentOffset + byteCount - oldRemainingToGet;
					} else
					{
						currentOffset += byteCount;
					}

				} catch (IOException e)
				{
					Log.e(TAG, "disconnected", e);
					connectionLost();
					break;
				}
			}
		}

		/**
		 * Write to the connected OutStream.
		 * 
		 * @param buffer
		 *            The bytes to write
		 */
		public void write(byte[] buffer)
		{
			try
			{
				mmOutStream.write(buffer);

				// Share the sent message back to the UI Activity
				// mHandler.obtainMessage(BluetoothChat.MESSAGE_WRITE, -1, -1,
				// buffer)
				// .sendToTarget();
				String logged = "Trans Write: ";
				for (int i = 0; i < buffer.length; i++)
				{
					logged += String.format("%x ", buffer[i]);
				}
				Log.i(TAG, logged);
			} catch (IOException e)
			{
				Log.e(TAG, "Exception during write", e);
			}
		}

		public void write(int out)
		{
			try
			{
				mmOutStream.write(out);

				// Share the sent message back to the UI Activity
				// mHandler.obtainMessage(BluetoothChat.MESSAGE_WRITE, -1, -1,
				// buffer)
				// .sendToTarget();
				Log.i(TAG, "Trans Write: " + String.valueOf(out));
			} catch (IOException e)
			{
				Log.e(TAG, "Exception during write", e);
			}
		}

		public void cancel()
		{
			try
			{
				// mmOutStream.write(EXIT_CMD);
				mmOutStream.close();
				mmInStream.close();
				mmSocket.close();
			} catch (IOException e)
			{
				Log.e(TAG, "close() of connect socket failed", e);
			}

		}
	}

}

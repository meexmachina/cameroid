package afarsek.namespace;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;
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
	private Queue<MessageElement> mToSendQueue = new LinkedList<MessageElement>();
	private MessageElement.MessageTags mCurrentMessageTag = MessageElement.MessageTags.ME_NO_MSG;
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

	/**
	 * Push to the message queue
	 * 
	 * @param out
	 *            The string to write
	 * @param tag
	 *            The message type tag
	 * @see ConnectedThread#write(byte[])
	 */
	public void write_queue(String out, MessageElement.MessageTags tag)
	{
		Log.d(TAG, "write_queue - enqueuing '" + out + "' with tag " + Integer.toString(tag.getIndex()));
		write_queue(out.getBytes(), tag);
	}

	/**
	 * Push to the message queue
	 * 
	 * @param out
	 *            The byte array to write
	 * @param tag
	 *            The message type tag
	 * @see ConnectedThread#write(byte[])
	 */
	public void write_queue(byte[] out, MessageElement.MessageTags tag)
	{
		// if nothing is queued
		Log.d(TAG, "write_queue (byte[]) - enqueuing '" + (new String(out)) + "' with tag " + Integer.toString(tag.getIndex()));

		if (mCurrentMessageTag == MessageElement.MessageTags.ME_NO_MSG)
		{
			Log.d(TAG, "write_queue (byte[]) - Nothing in the queue so sending.");
			write(out);
			mCurrentMessageTag = tag;
			Timer timeoutTimer = new Timer();
			timeoutTimer.schedule(new TimeoutTask(), 200); // set a timer of 200 ms
			return;
		}

		// do not allow more then 20
		if (mToSendQueue.size() > 20)
		{
			Log.d(TAG, "write_queue (byte[]) - the write queue is bigger then 20. returning");
			return;
		}

		Log.d(TAG, "write_queue (byte[]) - Adding the new element to the queue");
		MessageElement me = new MessageElement(out, tag, mTransactionID++);
		mToSendQueue.add(me);
	}

	/**
	 * Write to the ConnectedThread through a queue
	 * 
	 * @see ConnectedThread#write(byte[])
	 */
	public void release_from_queue()
	{
		MessageElement me;
		try
		{
			me = mToSendQueue.remove();
		} catch (NoSuchElementException e)
		{
			mCurrentMessageTag = MessageElement.MessageTags.ME_NO_MSG;
			Log.d(TAG, "release_from_queue - currentTag = no_msg");
			return;
		}

		Log.d(TAG, "release_from_queue - releasing currentTag changed");
		mCurrentMessageTag = me.mTag;
		write(me.mData);

		Timer timeoutTimer = new Timer();
		timeoutTimer.schedule(new TimeoutTask(), 200); // set a timer of 200 ms
	}

	class TimeoutTask extends TimerTask
	{
		public void run()
		{
			//mCurrentMessageTag = MessageElement.MessageTags.ME_NO_MSG;
			//release_from_queue();
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

	/**
	 * This class implements the message header of Transfer Protocol
	 */
	public class TP_header
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
			mType = data[0];
			mLength = 0xff & data[1];
			mLength |= 0xff00 & (data[2] << 8);
			mTransID = 0xff & data[3];
			mTransID |= 0xff00 & (data[4] << 8);
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
			byte[] buffer = new byte[1024];
			int byteCount = 0;

			// Keep listening to the InputStream while connected
			while (true)
			{
				try
				{
					byteCount = mmInStream.read(buffer, 0, CT_LENGTH_MSG_HEADER);
					
					// write the header to the logger
					String logged = "Trans Read Header: ";
					for (int i = 0; i < byteCount; i++)
					{
						logged += String.format("%x ", buffer[i]);
					}
					Log.d(TAG, logged);

					// if we just got the 5 bytes analyze them
					if (byteCount != 5)
					{
						// error
						continue;
					}

					TP_header header = new TP_header(buffer);

					// if its an event
					if (header.mType > MessageElement.TP_EVENT_START && header.mType < MessageElement.TP_EVENT_END)
					{
						byteCount = mmInStream.read(buffer, 0, header.mLength);

						// if we just got the bytes
						int arg1 = ((int) buffer[0]) | (((int) buffer[1]) << 8) | (((int) buffer[2]) << 16) | (((int) buffer[3]) << 24);

						// send the info to camera-control class
						Message msg = mHandler.obtainMessage(messageDefinitions.MESSAGE_READ, header.mType, arg1);
						Bundle bundle = new Bundle();
						bundle.putInt("TransactionID", header.mTransID);
						msg.setData(bundle);
						mHandler.sendMessage(msg);
					} else if (header.mType > MessageElement.TP_DATA_START && header.mType < MessageElement.TP_DATA_END)
					{
						int remainingBytesToRead = header.mLength;
						byte[] data = new byte[remainingBytesToRead];

						byteCount = mmInStream.read(data, 0, remainingBytesToRead);

						// send the info to camera-control class
						Message msg = mHandler.obtainMessage(messageDefinitions.MESSAGE_READ, header.mType, header.mLength);
						Bundle bundle = new Bundle();
						bundle.putInt("TransactionID", header.mTransID);
						bundle.putByteArray("GottenData", data.clone());
						msg.setData(bundle);
						mHandler.sendMessage(msg);
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

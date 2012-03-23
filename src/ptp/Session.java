package ptp;

/**
 * Encapsulates the session between a PTP initiator and responder.
 * 
 */
class Session
{
	private int sessionId;
	private int xid;
	private boolean active;
	private NameFactory factory;

	Session()
	{
	}

	void setFactory(NameFactory f)
	{
		factory = f;
	}

	NameFactory getFactory()
	{
		return factory;
	}

	int getNextXID()
	{
		return (active ? xid++ : 0);
	}

	int getNextSessionID()
	{
		if (!active)
			return ++sessionId;
		throw new IllegalStateException("already active");
	}

	boolean isActive()
	{
		return active;
	}

	void open()
	{
		xid = 1;
		active = true;
	}

	void close()
	{
		active = false;
	}

	int getSessionId()
	{
		return sessionId;
	}

	// track objects and their info by handles;
	// hookup to marshaling system and event framework
}
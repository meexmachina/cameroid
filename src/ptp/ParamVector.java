package ptp;

import java.io.PrintStream;

/**
 * This class is used for PTP messages consisting of only a set of thirty-two bit parameters, such as commands, responses, and events.
 * 
 */
public class ParamVector extends Container
{
	ParamVector(byte buf[], NameFactory f)
	{
		super(buf, f);
	}

	ParamVector(byte buf[], int len, NameFactory f)
	{
		super(buf, len, f);
	}

	/** Returns the first positional parameter. */
	public final int getParam1()
	{
		return getS32(12);
	}

	/** Returns the second positional parameter. */
	public final int getParam2()
	{
		return getS32(16);
	}

	/** Returns the third positional parameter. */
	public final int getParam3()
	{
		return getS32(20);
	}

	/** Returns the number of parameters in this data block */
	public final int getNumParams()
	{
		return (length - MIN_LEN) / 4;
	}

	// no params
	static final int MIN_LEN = HDR_LEN;

	// allegedly some responses could have five params
	static final int MAX_LEN = 32;

	// NOTE: params in the spec are numbered from one, not zero
	int getParam(int i)
	{
		return getS32(12 + (4 * i));
	}

	void dump(PrintStream out)
	{
		out.print(this.toString());
	}
}
package ptp;

/**
 * The optional middle phase of a PTP transaction involves sending data to or from the responder.
 * 
 */
public class Data extends Container
{
	private boolean in;

	public Data(NameFactory f)
	{
		this(true, null, 0, f);
	}

	public Data(boolean isIn, byte buf[], NameFactory f)
	{
		super(buf, f);
		in = isIn;
	}

	public Data(boolean isIn, byte buf[], int len, NameFactory f)
	{
		super(buf, len, f);
		in = isIn;
	}

	boolean isIn()
	{
		return in;
	}

	public String getCodeName(int code)
	{
		return factory.getOpcodeString(code);
	}

	public String toString()
	{
		StringBuffer temp = new StringBuffer();
		int code = getCode();

		temp.append("{ ");
		temp.append(getBlockTypeName(getBlockType()));
		if (in)
			temp.append(" IN");
		else
			temp.append(" OUT");
		temp.append("; len ");
		temp.append(Integer.toString(getLength()));
		temp.append("; ");
		temp.append(factory.getOpcodeString(code));
		temp.append("; xid ");
		temp.append(Integer.toString(getXID()));
		temp.append("}");
		return temp.toString();
	}
}

package ptp;

/**
 * This class packages the vendor extension published by Eastman Kodak Corporation, which has promised to openly publish the complete
 * specification needed to use these.
 * 
 */
class KodakExtension extends NameFactory
{
	// FIXME: this should eventually host APIs for the
	// specialized commands, so it'll need to subclass
	// at least BaselineInitiator (so it can do I/O).

	KodakExtension()
	{
	}

	/*-------------------------------------------------------------*/

	/**
	 * This is like SendObjectInfo. However, things like the association ("MUSIC") need to be filled in; there are no optional parameters.
	 */
	public static final int SendFileObjectInfo = 0x9005;

	/**
	 * This is SendObject, except that it was preceded by SendFileObjectInfo
	 */
	public static final int SendFileObject = 0x9006;

	public String getOpcodeString(int code)
	{
		switch (code)
		{
		case SendFileObjectInfo:
			return "Kodak_SendFileObjectInfo";
		case SendFileObject:
			return "Kodak_SendFileObject";
		}
		return Command._getOpcodeString(code);
	}

	/*-------------------------------------------------------------*/

	/** ResponseCode: */
	public static final int FilenameRequired = 0xa001;

	/** ResponseCode: */
	public static final int FilenameConflicts = 0xa002;

	/** ResponseCode: */
	public static final int FilenameInvalid = 0xa003;

	public String getResponseString(int code)
	{
		switch (code)
		{
		case FilenameRequired:
			return "Kodak_FilenameRequired";
		case FilenameConflicts:
			return "Kodak_FilenameConflicts";
		case FilenameInvalid:
			return "Kodak_FilenameInvalid";
		}
		return Response._getResponseString(code);
	}

	/*-------------------------------------------------------------*/

	/** ObjectFormatCode: ".fw" file for device firmware. */
	public static final int Firmware = 0xb001;

	/** ObjectFormatCode: ".m3u" style MP3 playlist. */
	public static final int M3U = 0xb002;

	public String getFormatString(int code)
	{
		switch (code)
		{
		case Firmware:
			return "Kodak_Firmware";
		case M3U:
			return "Kodak_M3U";
		}
		return ObjectInfo._getFormatString(code);
	}

	/*-------------------------------------------------------------*/

	/** Property code: */
	public static final int prop1 = 0xd001;

	/** Property code: */
	public static final int prop2 = 0xd002;

	/** Property code: */
	public static final int prop3 = 0xd003;

	/** Property code: */
	public static final int prop4 = 0xd004;

	/** Property code: */
	public static final int prop5 = 0xd005;

	/** Property code: */
	public static final int prop6 = 0xd006;

	public String getPropertyName(int code)
	{
		switch (code)
		{
		case prop1:
			return "Kodak_prop1";
		case prop2:
			return "Kodak_prop2";
		case prop3:
			return "Kodak_prop3";
		case prop4:
			return "Kodak_prop4";
		case prop5:
			return "Kodak_prop5";
		case prop6:
			return "Kodak_prop6";
		}
		return DevicePropDesc._getPropertyName(code);
	}
}
package ptp;

/**
 * Command messages start PTP transactions, and are sent from initiator to responder. They include an operation code, either conform to
 * chapter 10 of the PTP specification or are vendor-specific commands.
 * 
 * <p>
 * Create these objects in helper routines which package intelligence about a given Operation. That is, it'll know the command code, how
 * many command and response parameters may be used, particularly significant response code, and whether the transaction has a data phase
 * (and its direction).
 * 
 */
public class Command extends ParamVector
{

	private Command(int nparams, int code, Session s)
	{
		super(new byte[HDR_LEN + (4 * nparams)], s.getFactory());
		putHeader(data.length, 1 /* OperationCode */, code, s.getNextXID());
	}

	/**
	 * This creates a zero-parameter command.
	 * 
	 * @param code
	 *            as defined in section 10, table 18
	 * @param s
	 *            session this command is associated with
	 */
	Command(int code, Session s)
	{
		this(0, code, s);
	}

	/**
	 * This creates a one-parameter command.
	 * 
	 * @param code
	 *            as defined in section 10, table 18
	 * @param s
	 *            session this command is associated with
	 * @param param1
	 *            first operation parameter
	 */
	Command(int code, Session s, int param1)
	{
		this(1, code, s);
		put32(param1);
	}

	/**
	 * This creates a two-parameter command.
	 * 
	 * @param code
	 *            as defined in section 10, table 18
	 * @param s
	 *            session this command is associated with
	 * @param param1
	 *            first operation parameter
	 * @param param2
	 *            second operation parameter
	 */
	Command(int code, Session s, int param1, int param2)
	{
		this(2, code, s);
		put32(param1);
		put32(param2);
	}

	/**
	 * This creates a three-parameter command.
	 * 
	 * @param code
	 *            as defined in section 10, table 18
	 * @param s
	 *            session this command is associated with
	 * @param param1
	 *            first operation parameter
	 * @param param2
	 *            second operation parameter
	 * @param param3
	 *            third operation parameter
	 */
	Command(int code, Session s, int param1, int param2, int param3)
	{
		this(3, code, s);
		put32(param1);
		put32(param2);
		put32(param3);
	}

	// allegedly some commands could have up to five params
	public static final int GetDeviceInfo = 0x1001;
	public static final int OpenSession = 0x1002;
	public static final int CloseSession = 0x1003;
	public static final int GetStorageIDs = 0x1004;
	public static final int GetStorageInfo = 0x1005;
	public static final int GetNumObjects = 0x1006;
	public static final int GetObjectHandles = 0x1007;
	public static final int GetObjectInfo = 0x1008;
	public static final int GetObject = 0x1009;
	public static final int GetThumb = 0x100a;
	public static final int DeleteObject = 0x100b;
	public static final int SendObjectInfo = 0x100c;
	public static final int SendObject = 0x100d;
	public static final int InitiateCapture = 0x100e;
	public static final int FormatStore = 0x100f;
	public static final int ResetDevice = 0x1010;
	public static final int SelfTest = 0x1011;
	public static final int SetObjectProtection = 0x1012;
	public static final int PowerDown = 0x1013;
	public static final int GetDevicePropDesc = 0x1014;
	public static final int GetDevicePropValue = 0x1015;
	public static final int SetDevicePropValue = 0x1016;
	public static final int ResetDevicePropValue = 0x1017;
	public static final int TerminateOpenCapture = 0x1018;
	public static final int MoveObject = 0x1019;
	public static final int CopyObject = 0x101a;
	public static final int GetPartialObject = 0x101b;
	public static final int InitiateOpenCapture = 0x101c;
	public static final int EosGetStorageIds = 0x9101;
	public static final int EosGetStorageInfo = 0x9102;
	public static final int EosGetObjectInfo = 0x9103;
	public static final int EosGetObject = 0x9104;
	public static final int EosDeleteObject = 0x9105;
	public static final int EosFormatStore = 0x9106;
	public static final int EosGetPartialObject = 0x9107;
	public static final int EosGetDeviceInfoEx = 0x9108;
	public static final int EosGetObjectInfoEx = 0x9109;
	public static final int EosGetThumbEx = 0x910a;
	public static final int EosSendPartialObject = 0x910b;
	public static final int EosSetObjectProperties = 0x910c;
	public static final int EosGetObjectTime = 0x910d;
	public static final int EosSetObjectTime = 0x910e;
	public static final int EosRemoteRelease = 0x910f;
	public static final int EosSetDevicePropValueEx = 0x9110;
	public static final int EosSendObjectEx = 0x9111;
	public static final int EosCreageObject = 0x9112;
	public static final int EosGetRemoteMode = 0x9113;
	public static final int EosSetRemoteMode = 0x9114;
	public static final int EosSetEventMode = 0x9115;
	public static final int EosGetEvent = 0x9116;
	public static final int EosTransferComplete = 0x9117;
	public static final int EosCancelTransfer = 0x9118;
	public static final int EosResetTransfer = 0x9119;
	public static final int EosPCHDDCapacity = 0x911a;
	public static final int EosSetUILock = 0x911b;
	public static final int EosResetUILock = 0x911c;
	public static final int EosKeepDeviceOn = 0x911d;
	public static final int EosSetNullPacketmode = 0x911e;
	public static final int EosUpdateFirmware = 0x911f;
	public static final int EosUpdateTransferCompleteDt = 0x9120;
	public static final int EosCancelTransferDt = 0x9121;
	public static final int EosSetFWTProfile = 0x9122;
	public static final int EosGetFWTProfile = 0x9123;
	public static final int EosSetProfileToWTF = 0x9124;
	public static final int EosBulbStart = 0x9125;
	public static final int EosBulbEnd = 0x9126;
	public static final int EosRequestDevicePropValue = 0x9127;
	public static final int EosRemoeReleaseOn = 0x9128;
	public static final int EosRemoeReleaseOff = 0x9129;
	public static final int EosRegistBackgroundImage = 0x912a;
	public static final int EosChangePhotoStadIOMode = 0x912b;
	public static final int EosGetPartialObjectEx = 0x912c;
	public static final int EosResetMirrorLockupState = 0x9130;
	public static final int EosPopupBuiltinFlash = 0x9131;
	public static final int EosEndGetPartialObjectEx = 0x9132;
	public static final int EosMovieSelectSWOn = 0x9133;
	public static final int EosMovieSelectSWOff = 0x9134;
	public static final int EosInitiateViewFinder = 0x9151;
	public static final int EosTerminateViewFinder = 0x9152;
	public static final int EosGetViewFinderData = 0x9153;
	public static final int EosDoAF = 0x9154;
	public static final int EosDriveLens = 0x9155;
	public static final int EosDepthOfFieldPreview = 0x9156;
	public static final int EosClickWB = 0x9157;
	public static final int EosZoom = 0x9158;
	public static final int EosZoomPosition = 0x9159;
	public static final int EosSetLiveAFFrame = 0x915a;
	public static final int EosAFCancel = 0x9160;
	public static final int EosFapiMessageTx = 0x91fe;
	public static final int EosFapiMessageRx = 0x91ff;
	public static final int MtpGetObjectPropsSupported = 0x9801;
	public static final int MtpGetObjectPropDesc = 0x9802;
	public static final int MtpGetObjectPropValue = 0x9803;
	public static final int MtpSetObjectPropValue = 0x9804;
	public static final int MtpGetObjPropList = 0x9805;

	public String getCodeName(int code)
	{
		return factory.getOpcodeString(code);
	}

	static String _getOpcodeString(int code)
	{
		switch (code)
		{
		case GetDeviceInfo:
			return "GetDeviceInfo";
		case OpenSession:
			return "OpenSession";
		case CloseSession:
			return "CloseSession";
		case GetStorageIDs:
			return "GetStorageIDs";
		case GetStorageInfo:
			return "GetStorageInfo";
		case GetNumObjects:
			return "GetNumObjects";
		case GetObjectHandles:
			return "GetObjectHandles";
		case GetObjectInfo:
			return "GetObjectInfo";
		case GetObject:
			return "GetObject";
		case GetThumb:
			return "GetThumb";
		case DeleteObject:
			return "DeleteObject";
		case SendObjectInfo:
			return "SendObjectInfo";
		case SendObject:
			return "SendObject";
		case InitiateCapture:
			return "InitiateCapture";
		case FormatStore:
			return "FormatStore";
		case ResetDevice:
			return "ResetDevice";
		case SelfTest:
			return "SelfTest";
		case SetObjectProtection:
			return "SetObjectProtection";
		case PowerDown:
			return "PowerDown";
		case GetDevicePropDesc:
			return "GetDevicePropDesc";
		case GetDevicePropValue:
			return "GetDevicePropValue";
		case SetDevicePropValue:
			return "SetDevicePropValue";
		case ResetDevicePropValue:
			return "ResetDevicePropValue";
		case TerminateOpenCapture:
			return "TerminateOpenCapture";
		case MoveObject:
			return "MoveObject";
		case CopyObject:
			return "CopyObject";
		case GetPartialObject:
			return "GetPartialObject";
		case InitiateOpenCapture:
			return "InitiateOpenCapture";
		case EosSetDevicePropValueEx:
			return "EosSetDevicePropValueEx";
		case EosCancelTransfer:
			return "EosCancelTransfer";
		case EosCreageObject:
			return "EosCreageObject";
		case EosDeleteObject:
			return "EosDeleteObject";
		case EosFapiMessageRx:
			return "EosFapiMessageRx";
		case EosFapiMessageTx:
			return "EosFapiMessageTx";
		case EosFormatStore:
			return "EosFormatStore";
		case EosGetDeviceInfoEx:
			return "EosGetDeviceInfoEx";
		case EosGetEvent:
			return "EosGetEvent";
		case EosGetObject:
			return "EosGetObject";
		case EosGetObjectInfo:
			return "EosGetObjectInfo";
		case EosGetObjectInfoEx:
			return "EosGetObjectInfoEx";
		case EosGetObjectTime:
			return "EosGetObjectTime";
		case EosGetPartialObject:
			return "EosGetPartialObject";
		case EosGetRemoteMode:
			return "EosGetRemoteMode";
		case EosGetStorageIds:
			return "EosGetStorageIds";
		case EosGetStorageInfo:
			return "EosGetStorageInfo";
		case EosGetThumbEx:
			return "EosGetThumbEx";
		case EosKeepDeviceOn:
			return "EosKeepDeviceOn";
		case EosPCHDDCapacity:
			return "EosPCHDDCapacity";
		case EosRemoteRelease:
			return "EosRemoteRelease";
		case EosResetUILock:
			return "EosResetUILock";
		case EosResetTransfer:
			return "EosResetTransfer";
		case EosSendObjectEx:
			return "EosSendObjectEx";
		case EosSendPartialObject:
			return "EosSendPartialObject";
		case EosSetNullPacketmode:
			return "EosSetNullPacketmode";
		case EosSetObjectProperties:
			return "EosSetObjectProperties";
		case EosSetObjectTime:
			return "EosSetObjectTime";
		case EosSetRemoteMode:
			return "EosSetRemoteMode";
		case EosSetUILock:
			return "EosSetUILock";
		case EosTransferComplete:
			return "EosGetTransferComplete";
		case EosUpdateFirmware:
			return "EosUpdateFirmware";
		case EosRequestDevicePropValue:
			return "EosRequestDevicePropValue";
		case EosUpdateTransferCompleteDt:
			return "EosUpdateTransferCompleteDt";
		case EosCancelTransferDt:
			return "EosCancelTransferDt";
		case EosInitiateViewFinder:
			return "EosInitiateViewFinder";
		case EosTerminateViewFinder:
			return "EosTerminateViewFinder";
		case EosGetViewFinderData:
			return "EosgetViewFinderData";
		case EosDriveLens:
			return "EosDriveLens";
		case EosDepthOfFieldPreview:
			return "EosDepthOfFieldPreview";
		case EosClickWB:
			return "EosClickWB";
		case EosSetLiveAFFrame:
			return "EosSetLiveAFFrame";
		case EosZoom:
			return "EosZoom";
		case EosZoomPosition:
			return "EosZoomPostion";
		case EosGetFWTProfile:
			return "EosGetFWTProfile";
		case EosSetFWTProfile:
			return "EosSetFWTProfile";
		case EosSetProfileToWTF:
			return "EosSetProfileToWTF";
		case EosBulbStart:
			return "EosBulbStart";
		case EosBulbEnd:
			return "EosBulbEnd";
		case EosRemoeReleaseOn:
			return "EosRemoeReleaseOn";
		case EosRemoeReleaseOff:
			return "EosRemoeReleaseOff";
		case EosDoAF:
			return "EosDoAF";
		case EosAFCancel:
			return "EosAFCancel";
		case EosRegistBackgroundImage:
			return "EosRegistBackgroundImage";
		case EosChangePhotoStadIOMode:
			return "EosChangePhotoStadIOMode";
		case EosGetPartialObjectEx:
			return "EosGetPartialObjectEx";
		case EosResetMirrorLockupState:
			return "EosResetMirrorLockupState";
		case EosPopupBuiltinFlash:
			return "EosPopupBuiltinFlash";
		case EosEndGetPartialObjectEx:
			return "EosEndGetPartialObjectEx";
		case EosMovieSelectSWOn:
			return "EosMovieSelectSWOn";
		case EosMovieSelectSWOff:
			return "EosMovieSelectSWOff";
		case EosSetEventMode:
			return "EosSetEventMode";
		case MtpGetObjectPropsSupported:
			return "MtpGetObjectPropsSupported";
		case MtpGetObjectPropDesc:
			return "MtpGetObjectPropDesc";
		case MtpGetObjectPropValue:
			return "MtpGetObjectPropValue";
		case MtpSetObjectPropValue:
			return "MtpSetObjectPropValue";
		case MtpGetObjPropList:
			return "GetObjPropList";
		}

		return Container.getCodeString(code);
	}
}
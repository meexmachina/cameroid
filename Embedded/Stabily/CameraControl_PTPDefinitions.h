#ifndef _CAMERA_CONTROL_PTPDEFINISTIONS_H_
#define _CAMERA_CONTROL_PTPDEFINISTIONS_H_


/* Vendor IDs */
typedef enum {
	PTP_VENDOR_EASTMAN_KODAK							=	0x00000001,
	PTP_VENDOR_SEIKO_EPSON								=	0x00000002,
	PTP_VENDOR_AGILENT									=	0x00000003,
	PTP_VENDOR_POLAROID									=	0x00000004,
	PTP_VENDOR_AGFA_GEVAERT								=	0x00000005,
	PTP_VENDOR_MICROSOFT								=	0x00000006,
	PTP_VENDOR_EQUINOX									=	0x00000007,
	PTP_VENDOR_VIEWQUEST								=	0x00000008,
	PTP_VENDOR_STMICROELECTRONICS						=	0x00000009,
	PTP_VENDOR_NIKON									=	0x0000000A,
	PTP_VENDOR_CANON									=	0x0000000B,
} PTP_VENDOR_EN;

/* Operation Codes */
typedef enum {
	PTP_OC_Undefined	               					=	0x1000,
	PTP_OC_GetDeviceInfo            					=	0x1001,
	PTP_OC_OpenSession              					=	0x1002,
	PTP_OC_CloseSession             					=	0x1003,
	PTP_OC_GetStorageIDs            					=	0x1004,
	PTP_OC_GetStorageInfo           					=	0x1005,
	PTP_OC_GetNumObjects            					=	0x1006,
	PTP_OC_GetObjectHandles         					=	0x1007,
	PTP_OC_GetObjectInfo            					=	0x1008,
	PTP_OC_GetObject                					=	0x1009,
	PTP_OC_GetThumb                 					=	0x100A,
	PTP_OC_DeleteObject             					=	0x100B,
	PTP_OC_SendObjectInfo           					=	0x100C,
	PTP_OC_SendObject               					=	0x100D,
	PTP_OC_InitiateCapture          					=	0x100E,
	PTP_OC_FormatStore              					=	0x100F,
	PTP_OC_ResetDevice              					=	0x1010,
	PTP_OC_SelfTest                 					=	0x1011,
	PTP_OC_SetObjectProtection      					=	0x1012,
	PTP_OC_PowerDown                					=	0x1013,
	PTP_OC_GetDevicePropDesc        					=	0x1014,
	PTP_OC_GetDevicePropValue       					=	0x1015,
	PTP_OC_SetDevicePropValue       					=	0x1016,
	PTP_OC_ResetDevicePropValue     					=	0x1017,
	PTP_OC_TerminateOpenCapture     					=	0x1018,
	PTP_OC_MoveObject               					=	0x1019,
	PTP_OC_CopyObject               					=	0x101A,
	PTP_OC_GetPartialObject         					=	0x101B,
	PTP_OC_InitiateOpenCapture      					=	0x101C,

	/* Eastman Kodak extension Operation Codes */
	PTP_OC_EK_SendFileObjectInfo						=	0x9005,
	PTP_OC_EK_SendFileObject							=	0x9006,

	/* Canon extension Operation Codes */
	PTP_OC_CANON_GetObjectSize							=	0x9001,
	PTP_OC_CANON_StartShootingMode						=	0x9008,
	PTP_OC_CANON_EndShootingMode						=	0x9009,
	PTP_OC_CANON_ViewfinderOn							=	0x900B,
	PTP_OC_CANON_ViewfinderOff							=	0x900C,
	PTP_OC_CANON_ReflectChanges							=	0x900D,
	PTP_OC_CANON_CheckEvent								=	0x9013,
	PTP_OC_CANON_FocusLock								=	0x9014,
	PTP_OC_CANON_FocusUnlock							=	0x9015,
	PTP_OC_CANON_InitiateCaptureInMemory				=	0x901A,
	PTP_OC_CANON_GetPartialObject						=	0x901B,
	PTP_OC_CANON_GetViewfinderImage						=	0x901d,
	PTP_OC_CANON_GetChanges								=	0x9020,
	PTP_OC_CANON_GetFolderEntries						=	0x9021,

	/* Nikon extensiion Operation Codes */
	PTP_OC_NIKON_DirectCapture							=	0x90C0,
	PTP_OC_NIKON_SetControlMode							=	0x90C2,
	PTP_OC_NIKON_CheckEvent								=	0x90C7,
	PTP_OC_NIKON_KeepAlive								=	0x90C8,

	/* Proprietary vendor extension operations mask */
	PTP_OC_EXTENSION_MASK								=	0xF000,
	PTP_OC_EXTENSION									=	0x9000,
} PTP_OPCODES_EN;

/* Response Codes */
typedef enum {
	PTP_RC_Undefined	               					=	0x2000,
	PTP_RC_OK                       					=	0x2001,
	PTP_RC_GeneralError             					=	0x2002,
	PTP_RC_SessionNotOpen           					=	0x2003,
	PTP_RC_InvalidTransactionID							=	0x2004,
	PTP_RC_OperationNotSupported    					=	0x2005,
	PTP_RC_ParameterNotSupported    					=	0x2006,
	PTP_RC_IncompleteTransfer       					=	0x2007,
	PTP_RC_InvalidStorageId         					=	0x2008,
	PTP_RC_InvalidObjectHandle      					=	0x2009,
	PTP_RC_DevicePropNotSupported   					=	0x200A,
	PTP_RC_InvalidObjectFormatCode  					=	0x200B,
	PTP_RC_StoreFull                					=	0x200C,
	PTP_RC_ObjectWriteProtected     					=	0x200D,
	PTP_RC_StoreReadOnly            					=	0x200E,
	PTP_RC_AccessDenied             					=	0x200F,
	PTP_RC_NoThumbnailPresent       					=	0x2010,
	PTP_RC_SelfTestFailed           					=	0x2011,
	PTP_RC_PartialDeletion          					=	0x2012,
	PTP_RC_StoreNotAvailable        					=	0x2013,
	PTP_RC_SpecificationByFormatUnsupported				=	0x2014,
	PTP_RC_NoValidObjectInfo        					=	0x2015,
	PTP_RC_InvalidCodeFormat        					=	0x2016,
	PTP_RC_UnknownVendorCode        					=	0x2017,
	PTP_RC_CaptureAlreadyTerminated 					=	0x2018,
	PTP_RC_DeviceBusy               					=	0x2019,
	PTP_RC_InvalidParentObject      					=	0x201A,
	PTP_RC_InvalidDevicePropFormat  					=	0x201B,
	PTP_RC_InvalidDevicePropValue   					=	0x201C,
	PTP_RC_InvalidParameter         					=	0x201D,
	PTP_RC_SessionAlreadyOpened     					=	0x201E,
	PTP_RC_TransactionCanceled      					=	0x201F,
	PTP_RC_SpecificationOfDestUnsupported				=	0x2020,

	/* Eastman Kodak extension Response Codes */
	PTP_RC_EK_FilenameRequired							=	0xA001,
	PTP_RC_EK_FilenameConflicts							=	0xA002,
	PTP_RC_EK_FilenameInvalid							=	0xA003,

	/* NIKON extension Response Codes */
	PTP_RC_NIKON_PropertyReadOnly						=	0xA005,

	/* Proprietary vendor extension response code mask */
	PTP_RC_EXTENSION_MASK								=	0xF000,
	PTP_RC_EXTENSION									=	0xA000,
} PTP_RESPCODE_EN;

/* Extended ERROR codes */
typedef enum {
	PTP_ERROR_IO										=	0x02FF,
	PTP_ERROR_DATA_EXPECTED								=	0x02FE,
	PTP_ERROR_RESP_EXPECTED								=	0x02FD,
	PTP_ERROR_BADPARAM									=	0x02FC,
} PTP_EXTERROR_EN;

/* PTP Event Codes */
typedef enum {
	PTP_EC_Undefined									=	0x4000,
	PTP_EC_CancelTransaction							=	0x4001,
	PTP_EC_ObjectAdded									=	0x4002,
	PTP_EC_ObjectRemoved								=	0x4003,
	PTP_EC_StoreAdded									=	0x4004,
	PTP_EC_StoreRemoved									=	0x4005,
	PTP_EC_DevicePropChanged							=	0x4006,
	PTP_EC_ObjectInfoChanged							=	0x4007,
	PTP_EC_DeviceInfoChanged							=	0x4008,
	PTP_EC_RequestObjectTransfer						=	0x4009,
	PTP_EC_StoreFull									=	0x400A,
	PTP_EC_DeviceReset									=	0x400B,
	PTP_EC_StorageInfoChanged							=	0x400C,
	PTP_EC_CaptureComplete								=	0x400D,
	PTP_EC_UnreportedStatus								=	0x400E,

	/* Canon extension Event Codes */
	PTP_EC_CANON_DeviceInfoChanged						=	0xC008,
	PTP_EC_CANON_RequestObjectTransfer					=	0xC009,
	PTP_EC_CANON_CameraModeChanged						=	0xC00C,
	
	/* Nikon extension Event Codes */
	PTP_EC_NIKON_ObjectReady							=	0xC101,
	PTP_EC_NIKON_CaptureOverflow						=	0xC102,
} PTP_EVENTCODE_EN;

/* Ancillary/Image formats */
typedef enum {
	PTP_OFC_Undefined									=	0x3000,
	PTP_OFC_Association									=	0x3001,
	PTP_OFC_Script										=	0x3002,
	PTP_OFC_Executable									=	0x3003,
	PTP_OFC_Text										=	0x3004,
	PTP_OFC_HTML										=	0x3005,
	PTP_OFC_DPOF										=	0x3006,
	PTP_OFC_AIFF	 									=	0x3007,
	PTP_OFC_WAV											=	0x3008,
	PTP_OFC_MP3											=	0x3009,
	PTP_OFC_AVI											=	0x300A,
	PTP_OFC_MPEG										=	0x300B,
	PTP_OFC_ASF											=	0x300C,
	PTP_OFC_QT											=	0x300D, /* guessing */
	
	/* Image formats */
	PTP_OFC_IM_Undefined								=	0x3800,
	PTP_OFC_EXIF_JPEG									=	0x3801,
	PTP_OFC_TIFF_EP										=	0x3802,
	PTP_OFC_FlashPix									=	0x3803,
	PTP_OFC_BMP											=	0x3804,
	PTP_OFC_CIFF										=	0x3805,
	PTP_OFC_Undefined_0x3806							=	0x3806,
	PTP_OFC_GIF											=	0x3807,
	PTP_OFC_JFIF										=	0x3808,
	PTP_OFC_PCD											=	0x3809,
	PTP_OFC_PICT										=	0x380A,
	PTP_OFC_PNG											=	0x380B,
	PTP_OFC_Undefined_0x380C							=	0x380C,
	PTP_OFC_TIFF										=	0x380D,
	PTP_OFC_TIFF_IT										=	0x380E,
	PTP_OFC_JP2											=	0x380F,
	PTP_OFC_JPX											=	0x3810,

	/* Eastman Kodak extension ancillary format */
	PTP_OFC_EK_M3U										=	0xB002,
} PTP_FMT_EN;

/* PTP Association Types */
typedef enum {
	PTP_AT_Undefined									=	0x0000,
	PTP_AT_GenericFolder								=	0x0001,
	PTP_AT_Album										=	0x0002,
	PTP_AT_TimeSequence									=	0x0003,
	PTP_AT_HorizontalPanoramic							=	0x0004,
	PTP_AT_VerticalPanoramic							=	0x0005,
	PTP_AT_2DPanoramic									=	0x0006,
	PTP_AT_AncillaryData								=	0x0007,
} PTP_ASSOCTYPES_EN;

/* PTP Protection Status */
typedef enum {
	PTP_PS_NoProtection									=	0x0000,
	PTP_PS_ReadOnly										=	0x0001,
} PTP_PROTSTAT_EN;

/* PTP Storage Types */
typedef enum {
	PTP_ST_Undefined									=	0x0000,
	PTP_ST_FixedROM										=	0x0001,
	PTP_ST_RemovableROM									=	0x0002,
	PTP_ST_FixedRAM										=	0x0003,
	PTP_ST_RemovableRAM									=	0x0004,
} PTP_STORETYPE_EN;

/* PTP FilesystemType Values */
typedef enum {
	PTP_FST_BASE										=	0x0000,
	PTP_FST_GenericFlat									=	0x0001,
	PTP_FST_GenericHierarchical							=	0x0002,
	PTP_FST_DCF											=	0x0003,
} PTP_FSTYPE_EN;

/* PTP StorageInfo AccessCapability Values */
typedef enum {
	PTP_AC_ReadWrite									=	0x0000,
	PTP_AC_ReadOnly										=	0x0001,
	PTP_AC_ReadOnly_with_Object_Deletion				=	0x0002,
} PTP_ACCESSCAP_EN;

/* DataType Codes */
typedef enum {
	PTP_DTC_UNDEF										=	0x0000,
	PTP_DTC_INT8										=	0x0001,
	PTP_DTC_UINT8										=	0x0002,
	PTP_DTC_INT16										=	0x0003,
	PTP_DTC_UINT16										=	0x0004,
	PTP_DTC_INT32										=	0x0005,
	PTP_DTC_UINT32										=	0x0006,
	PTP_DTC_INT64										=	0x0007,
	PTP_DTC_UINT64										=	0x0008,
	PTP_DTC_INT128										=	0x0009,
	PTP_DTC_UINT128										=	0x000A,
	PTP_DTC_AINT8										=	0x4001,
	PTP_DTC_AUINT8										=	0x4002,
	PTP_DTC_AINT16										=	0x4003,
	PTP_DTC_AUINT16										=	0x4004,
	PTP_DTC_AINT32										=	0x4005,
	PTP_DTC_AUINT32										=	0x4006,
	PTP_DTC_AINT64										=	0x4007,
	PTP_DTC_AUINT64										=	0x4008,
	PTP_DTC_AINT128										=	0x4009,
	PTP_DTC_AUINT128									=	0x400A,
	PTP_DTC_STR											=	0xFFFF,
} PTP_DATATYPE_EN;

/* Device Properties Codes */
typedef enum {
	PTP_DPC_Undefined									=	0x5000,
	PTP_DPC_BatteryLevel								=	0x5001,
	PTP_DPC_FunctionalMode								=	0x5002,
	PTP_DPC_ImageSize									=	0x5003,
	PTP_DPC_CompressionSetting							=	0x5004,
	PTP_DPC_WhiteBalance								=	0x5005,
	PTP_DPC_RGBGain										=	0x5006,
	PTP_DPC_FNumber										=	0x5007,
	PTP_DPC_FocalLength									=	0x5008,
	PTP_DPC_FocusDistance								=	0x5009,
	PTP_DPC_FocusMode									=	0x500A,
	PTP_DPC_ExposureMeteringMode						=	0x500B,
	PTP_DPC_FlashMode									=	0x500C,
	PTP_DPC_ExposureTime								=	0x500D,
	PTP_DPC_ExposureProgramMode							=	0x500E,
	PTP_DPC_ExposureIndex								=	0x500F,
	PTP_DPC_ExposureBiasCompensation					=	0x5010,
	PTP_DPC_DateTime									=	0x5011,
	PTP_DPC_CaptureDelay								=	0x5012,
	PTP_DPC_StillCaptureMode							=	0x5013,
	PTP_DPC_Contrast									=	0x5014,
	PTP_DPC_Sharpness									=	0x5015,
	PTP_DPC_DigitalZoom									=	0x5016,
	PTP_DPC_EffectMode									=	0x5017,
	PTP_DPC_BurstNumber									=	0x5018,
	PTP_DPC_BurstInterval								=	0x5019,
	PTP_DPC_TimelapseNumber								=	0x501A,
	PTP_DPC_TimelapseInterval							=	0x501B,
	PTP_DPC_FocusMeteringMode							=	0x501C,
	PTP_DPC_UploadURL									=	0x501D,
	PTP_DPC_Artist										=	0x501E,
	PTP_DPC_CopyrightInfo								=	0x501F,

/* Proprietary vendor extension device property mask */
	PTP_DPC_EXTENSION_MASK								=	0xF000,
	PTP_DPC_EXTENSION									=	0xD000,

/* Vendor Extensions device property codes */
/* --------------------------------------- */
/* Eastman Kodak extension device property codes */
	PTP_DPC_EK_ColorTemperature							=	0xD001,
	PTP_DPC_EK_DateTimeStampFormat						=	0xD002,
	PTP_DPC_EK_BeepMode									=	0xD003,
	PTP_DPC_EK_VideoOut									=	0xD004,
	PTP_DPC_EK_PowerSaving								=	0xD005,
	PTP_DPC_EK_UI_Language								=	0xD006,

/* Canon extension device property codes */
	PTP_DPC_CANON_BeepMode								=	0xD001,
	PTP_DPC_CANON_ViewfinderMode						=	0xD003,
	PTP_DPC_CANON_ImageQuality							=	0xD006,
	PTP_DPC_CANON_D007									=	0xD007,
	PTP_DPC_CANON_ImageSize								=	0xD008,
	PTP_DPC_CANON_FlashMode								=	0xD00A,
	PTP_DPC_CANON_TvAvSetting							=	0xD00C,
	PTP_DPC_CANON_MeteringMode							=	0xD010,
	PTP_DPC_CANON_MacroMode								=	0xD011,
	PTP_DPC_CANON_FocusingPoint							=	0xD012,
	PTP_DPC_CANON_WhiteBalance							=	0xD013,
	PTP_DPC_CANON_ISOSpeed								=	0xD01C,
	PTP_DPC_CANON_Aperture								=	0xD01D,
	PTP_DPC_CANON_ShutterSpeed							=	0xD01E,
	PTP_DPC_CANON_ExpCompensation						=	0xD01F,
	PTP_DPC_CANON_D029									=	0xD029,
	PTP_DPC_CANON_Zoom									=	0xD02A,
	PTP_DPC_CANON_SizeQualityMode						=	0xD02C,
	PTP_DPC_CANON_FlashMemory							=	0xD031,
	PTP_DPC_CANON_CameraModel							=	0xD032,
	PTP_DPC_CANON_CameraOwner							=	0xD033,
	PTP_DPC_CANON_UnixTime								=	0xD034,
	PTP_DPC_CANON_ViewfinderOutput						=	0xD036,
	PTP_DPC_CANON_RealImageWidth						=	0xD039,
	PTP_DPC_CANON_PhotoEffect							=	0xD040,
	PTP_DPC_CANON_AssistLight							=	0xD041,
	PTP_DPC_CANON_D045									=	0xD045,

/* Nikon extension device property codes */
	PTP_DPC_NIKON_ShootingBank							=	0xD010,
	PTP_DPC_NIKON_ShootingBankNameA 					=	0xD011,
	PTP_DPC_NIKON_ShootingBankNameB						=	0xD012,
	PTP_DPC_NIKON_ShootingBankNameC						=	0xD013,
	PTP_DPC_NIKON_ShootingBankNameD						=	0xD014,
	PTP_DPC_NIKON_RawCompression						=	0xD016,
	PTP_DPC_NIKON_WhiteBalanceAutoBias					=	0xD017,
	PTP_DPC_NIKON_WhiteBalanceTungstenBias				=	0xD018,
	PTP_DPC_NIKON_WhiteBalanceFlourescentBias			=	0xD019,
	PTP_DPC_NIKON_WhiteBalanceDaylightBias				=	0xD01A,
	PTP_DPC_NIKON_WhiteBalanceFlashBias					=	0xD01B,
	PTP_DPC_NIKON_WhiteBalanceCloudyBias				=	0xD01C,
	PTP_DPC_NIKON_WhiteBalanceShadeBias					=	0xD01D,
	PTP_DPC_NIKON_WhiteBalanceColorTemperature			=	0xD01E,
	PTP_DPC_NIKON_ImageSharpening						=	0xD02A,
	PTP_DPC_NIKON_ToneCompensation						=	0xD02B,
	PTP_DPC_NIKON_ColorMode								=	0xD02C,
	PTP_DPC_NIKON_HueAdjustment							=	0xD02D,
	PTP_DPC_NIKON_NonCPULensDataFocalLength				=	0xD02E,
	PTP_DPC_NIKON_NonCPULensDataMaximumAperture			=	0xD02F,
	PTP_DPC_NIKON_CSMMenuBankSelect						=	0xD040,
	PTP_DPC_NIKON_MenuBankNameA							=	0xD041,
	PTP_DPC_NIKON_MenuBankNameB							=	0xD042,
	PTP_DPC_NIKON_MenuBankNameC							=	0xD043,
	PTP_DPC_NIKON_MenuBankNameD							=	0xD044,
	PTP_DPC_NIKON_A1AFCModePriority						=	0xD048,
	PTP_DPC_NIKON_A2AFSModePriority						=	0xD049,
	PTP_DPC_NIKON_A3GroupDynamicAF						=	0xD04A,
	PTP_DPC_NIKON_A4AFActivation						=	0xD04B,
	PTP_DPC_NIKON_A5FocusAreaIllumManualFocus			=	0xD04C,
	PTP_DPC_NIKON_FocusAreaIllumContinuous				=	0xD04D,
	PTP_DPC_NIKON_FocusAreaIllumWhenSelected 			=	0xD04E,
	PTP_DPC_NIKON_FocusAreaWrap							=	0xD04F,
	PTP_DPC_NIKON_A7VerticalAFON						=	0xD050,
	PTP_DPC_NIKON_ISOAuto								=	0xD054,
	PTP_DPC_NIKON_B2ISOStep								=	0xD055,
	PTP_DPC_NIKON_EVStep								=	0xD056,
	PTP_DPC_NIKON_B4ExposureCompEv						=	0xD057,
	PTP_DPC_NIKON_ExposureCompensation					=	0xD058,
	PTP_DPC_NIKON_CenterWeightArea						=	0xD059,
	PTP_DPC_NIKON_AELockMode							=	0xD05E,
	PTP_DPC_NIKON_AELAFLMode							=	0xD05F,
	PTP_DPC_NIKON_MeterOff								=	0xD062,
	PTP_DPC_NIKON_SelfTimer								=	0xD063,
	PTP_DPC_NIKON_MonitorOff							=	0xD064,
	PTP_DPC_NIKON_D1ShootingSpeed						=	0xD068,
	PTP_DPC_NIKON_D2MaximumShots						=	0xD069,
	PTP_DPC_NIKON_D3ExpDelayMode						=	0xD06A,
	PTP_DPC_NIKON_LongExposureNoiseReduction			=	0xD06B,
	PTP_DPC_NIKON_FileNumberSequence					=	0xD06C,
	PTP_DPC_NIKON_D6ControlPanelFinderRearControl		=	0xD06D,
	PTP_DPC_NIKON_ControlPanelFinderViewfinder			=	0xD06E,
	PTP_DPC_NIKON_D7Illumination						=	0xD06F,
	PTP_DPC_NIKON_E1FlashSyncSpeed						=	0xD074,
	PTP_DPC_NIKON_FlashShutterSpeed						=	0xD075,
	PTP_DPC_NIKON_E3AAFlashMode							=	0xD076,
	PTP_DPC_NIKON_E4ModelingFlash						=	0xD077,
	PTP_DPC_NIKON_BracketSet							=	0xD078,
	PTP_DPC_NIKON_E6ManualModeBracketing				=	0xD079,	
	PTP_DPC_NIKON_BracketOrder							=	0xD07A,
	PTP_DPC_NIKON_E8AutoBracketSelection				=	0xD07B,
	PTP_DPC_NIKON_BracketingSet							=	0xD07C,
	PTP_DPC_NIKON_F1CenterButtonShootingMode			=	0xD080,
	PTP_DPC_NIKON_CenterButtonPlaybackMode				=	0xD081,
	PTP_DPC_NIKON_F2Multiselector						=	0xD082,
	PTP_DPC_NIKON_F3PhotoInfoPlayback					=	0xD083,
	PTP_DPC_NIKON_F4AssignFuncButton					=	0xD084,
	PTP_DPC_NIKON_F5CustomizeCommDials					=	0xD085,
	PTP_DPC_NIKON_ReverseCommandDial					=	0xD086,
	PTP_DPC_NIKON_ApertureSetting						=	0xD087,
	PTP_DPC_NIKON_MenusAndPlayback						=	0xD088,
	PTP_DPC_NIKON_F6ButtonsAndDials						=	0xD089,
	PTP_DPC_NIKON_NoCFCard								=	0xD08A,
	PTP_DPC_NIKON_ImageCommentString					=	0xD090,
	PTP_DPC_NIKON_ImageCommentAttach					=	0xD091,
	PTP_DPC_NIKON_ImageRotation							=	0xD092,
	PTP_DPC_NIKON_Bracketing							=	0xD0C0,
	PTP_DPC_NIKON_ExposureBracketingIntervalDist		=	0xD0C1,
	PTP_DPC_NIKON_BracketingProgram						=	0xD0C2,
	PTP_DPC_NIKON_WhiteBalanceBracketStep				=	0xD0C4,
	PTP_DPC_NIKON_LensID								=	0xD0E0,
	PTP_DPC_NIKON_FocalLengthMin						=	0xD0E3,
	PTP_DPC_NIKON_FocalLengthMax						=	0xD0E4,
	PTP_DPC_NIKON_MaxApAtMinFocalLength					=	0xD0E5,
	PTP_DPC_NIKON_MaxApAtMaxFocalLength					=	0xD0E6,
	PTP_DPC_NIKON_ExposureTime							=	0xD100,
	PTP_DPC_NIKON_ACPower								=	0xD101,
	PTP_DPC_NIKON_MaximumShots							=	0xD103,
	PTP_DPC_NIKON_AFLLock								=	0xD104,
	PTP_DPC_NIKON_AutoExposureLock						=	0xD105,
	PTP_DPC_NIKON_AutoFocusLock							=	0xD106,
	PTP_DPC_NIKON_AutofocusLCDTopMode2					=	0xD107,
	PTP_DPC_NIKON_AutofocusArea							=	0xD108,
	PTP_DPC_NIKON_LightMeter							=	0xD10A,
	PTP_DPC_NIKON_CameraOrientation						=	0xD10E,
	PTP_DPC_NIKON_ExposureApertureLock					=	0xD111,
	PTP_DPC_NIKON_BeepOff								=	0xD160,
	PTP_DPC_NIKON_AutofocusMode							=	0xD161,
	PTP_DPC_NIKON_AFAssist								=	0xD163,
	PTP_DPC_NIKON_PADVPMode								=	0xD164,
	PTP_DPC_NIKON_ImageReview							=	0xD165,
	PTP_DPC_NIKON_AFAreaIllumination					=	0xD166,
	PTP_DPC_NIKON_FlashMode								=	0xD167,
	PTP_DPC_NIKON_FlashCommanderMode					=	0xD168,
	PTP_DPC_NIKON_FlashSign								=	0xD169,
	PTP_DPC_NIKON_FlashModeManualPower					=	0xD16D,
	PTP_DPC_NIKON_FlashModeCommanderPower				=	0xD16E,
	PTP_DPC_NIKON_RemoteTimeout							=	0xD16B,
	PTP_DPC_NIKON_GridDisplay							=	0xD16C,
	PTP_DPC_NIKON_BracketingIncrement					=	0xD190,
	PTP_DPC_NIKON_LowLight								=	0xD1B0,
	PTP_DPC_NIKON_FlashOpen								=	0xD1C0,
	PTP_DPC_NIKON_FlashCharged							=	0xD1C1,
	PTP_DPC_NIKON_FlashExposureCompensation				=	0xD126,
	PTP_DPC_NIKON_CSMMenu			        			=	0xD180,
	PTP_DPC_NIKON_OptimizeImage		        			=	0xD140,
	PTP_DPC_NIKON_Saturation		        			=	0xD142,
} PTP_DEVPROPERTY_EN;

/* Device Property Form Flag */
typedef enum {
	PTP_DPFF_None										=	0x00,
	PTP_DPFF_Range										=	0x01,
	PTP_DPFF_Enumeration								=	0x02,
} PTP_DEVPROPFORM_EN;

/* Device Property GetSet type */
typedef enum {
	PTP_DPGS_Get										=	0x00,
	PTP_DPGS_GetSet										=	0x01,
} PTP_DEVPROPGETSET_EN;

#endif //_CAMERA_CONTROL_PTPDEFINISTIONS_H_

#include "CameraControl_DeviceInfo.h"
#include "CameraControl_General.h"
#include "TransferProtocol.h"


//==============================================================================
// 			V A R I A B L E S 
//==============================================================================
volatile 	uint8_t					g_nStage = 0;
volatile	uint8_t					g_iDataIsValid = 0;
CameraControl_DeviceInfo_ST			g_stDeviceInfo = {0};

//==============================================================================
// 			F U N C T I O N S 
//==============================================================================

/*------------------------------------------------------------------------------
 * CameraControl_DeviceInfo_PrintString
 */
bool CameraControl_DeviceInfo_PrintString(uint8_t **pp, uint16_t *count)
{
	uint8_t arrayLength = *(uint8_t*)(*pp);
	printf_P(PSTR("(len.%d)"), arrayLength);
	(*pp)+=1; (*count)-=1;

	for (; arrayLength>0; arrayLength--, (*count)-=2, (*pp)+=2)
	{
		printf_P(PSTR("%c"), (char)(**pp) );
	}
	return (arrayLength == 0);
}

/*------------------------------------------------------------------------------
 * CameraControl_DeviceInfo_CopyString
 */
bool CameraControl_DeviceInfo_CopyString(	uint8_t **pp, uint16_t *count, 
											char *dest, uint8_t maxLen)
{
	uint8_t arrayLength = *(uint8_t*)(*pp);
	uint8_t pos = 0;
	(*pp)+=1; (*count)-=1;

	for (; arrayLength>0; arrayLength--, (*count)-=2, (*pp)+=2, pos++)
	{
		if ( pos == (maxLen-1) )
			break;

		dest[pos] = (char)(**pp);
	}

	(*count)-=arrayLength*2;
	(*pp)+=arrayLength*2;

	dest[pos] = '\0';

	return (arrayLength == 0);
}

/*------------------------------------------------------------------------------
 * CameraControl_DeviceInfo_CopyArray
 */
bool CameraControl_DeviceInfo_CopyArray(	uint8_t **pp, uint16_t *count, uint8_t elSize, 
											uint64_t *dest, uint16_t base)
{
	uint32_t iMSB = 0;
	uint32_t iLSB = 0;

	uint32_t arrayLength = *(uint32_t*)(*pp);
	(*pp)+=4; (*count)-=4;

	*dest = 0;

	for ( ; arrayLength>0; arrayLength--, (*pp)+=elSize, (*count)-=elSize )
	{
		uint16_t currentEl = *((uint16_t*)(*pp));

		if ( currentEl<base )
			continue;

		uint16_t distance = currentEl - base; 

		if (distance<32) iLSB |= (1<<(distance));
		else if (distance<64) iMSB |= (1<<(distance-32));
	}

	*dest = (uint64_t)(iLSB)|((uint64_t)(iMSB)<<32);

	return (arrayLength == 0);
}


/*------------------------------------------------------------------------------
 * CameraControl_DeviceInfo_PrintArray
 */
bool CameraControl_DeviceInfo_PrintArray(uint8_t **pp, uint16_t *count, uint8_t elementSize)
{
	uint32_t arrayLength = *(uint32_t*)(*pp);
	printf_P(PSTR("	%d values array\r\n"), arrayLength);
	(*pp)+=4; (*count)-=4;
		
	for ( ; arrayLength>0; arrayLength--, (*pp)+=elementSize, (*count)-=elementSize )
	{
		printf_P(PSTR("	(-) 0x%04x\r\n"), *((uint16_t*)(*pp)));  		
	}

	return (arrayLength == 0);
}

/*------------------------------------------------------------------------------
 * CameraControl_DeviceInfo_Parse
 */
void CameraControl_DeviceInfo_Parse(uint16_t len, uint8_t *pbuf)
{
	uint16_t	count	= (uint16_t)len;
	uint8_t		*p		= (uint8_t*)pbuf;

	switch (g_nStage)
	{
	case 0:
		g_stDeviceInfo.iStandardVersion = *(uint16_t*)p;
		p += 2;		count -= 2;

		g_stDeviceInfo.iVendorExtensionID = *((uint32_t*)p);
		p += 4; 	count -= 4;

		g_stDeviceInfo.iVendorExtensionVersion = *((uint16_t*)p);
		p += 2;		count -=2;

		g_nStage ++;
	case 1:
		// Vendor Extendion Description
		CameraControl_DeviceInfo_CopyString(&p, &count, 
											g_stDeviceInfo.sVendorExtensionDescription, 
											DEVINFO_MAX_STRING_SIZE);
		g_nStage ++;
	case 2:
		// Function Mode
		g_stDeviceInfo.iFunctionMode = *((uint16_t*)p);
		p += 2;		count -=2;
		g_nStage ++;
	case 3:
		// Operations Supported
		CameraControl_DeviceInfo_CopyArray(&p, &count, 2, &g_stDeviceInfo.iOperationsSupported, OPERATION_SUPPORT_BASE);
		g_nStage ++;
	case 4:
		// Events Supported
		CameraControl_DeviceInfo_CopyArray(&p, &count, 2, &g_stDeviceInfo.iEventsSupported, EVENT_SUPPORT_BASE);
		g_nStage ++;
	case 5:
		// Device Properties Supported
		CameraControl_DeviceInfo_CopyArray(&p, &count, 2, &g_stDeviceInfo.iPropertiesSupported, PROPERTY_SUPPORT_BASE);
		g_nStage ++;
	case 6:
		// Capture formats
		CameraControl_DeviceInfo_CopyArray(&p, &count, 2, &g_stDeviceInfo.iCaptureFormatsSupported, CAP_FORMAT_SUPPORT_BASE);
		g_nStage ++;
	case 7:
		// Image Formats
		CameraControl_DeviceInfo_CopyArray(&p, &count, 2, &g_stDeviceInfo.iImageFormatsSupported, IM_FORMAT_SUPPORT_BASE);
		g_nStage ++;
	case 8:
		// Manufacturer
		CameraControl_DeviceInfo_CopyString(&p, &count, 
											g_stDeviceInfo.sManufacturer, 
											DEVINFO_MAX_STRING_SIZE);
		g_nStage ++;
	case 9:
		// Model
		CameraControl_DeviceInfo_CopyString(&p, &count, 
											g_stDeviceInfo.Model, 
											DEVINFO_MAX_STRING_SIZE);
		g_nStage ++;
	case 10:
		// Device version
		CameraControl_DeviceInfo_CopyString(&p, &count, 
											g_stDeviceInfo.DeviceVersion, 
											DEVINFO_MAX_STRING_SIZE);
		g_nStage ++;
	case 11:
		// Serial number
		CameraControl_DeviceInfo_CopyString(&p, &count, 
											g_stDeviceInfo.SerialNumber, 
											DEVINFO_MAX_STRING_SIZE);
		g_nStage = 0;
	}
}

/*------------------------------------------------------------------------------
 * CameraControl_DeviceInfo_GetInfo
 */
uint8_t CameraControl_DeviceInfo_GetInfo ( USB_ClassInfo_SI_Host_t* SIInterfaceInfo )
{
	uint16_t DeviceInfoSize;
	uint8_t ErrorCode;

	CHECK_CAMERA_CONNECTION;

	SIInterfaceInfo->State.TransactionID = 0;

	// Create PIMA message block
	PIMA_Container_t PIMABlock = (PIMA_Container_t)
		{
			.DataLength    = CPU_TO_LE32(PIMA_COMMAND_SIZE(0)),
			.Type          = CPU_TO_LE16(PIMA_CONTAINER_CommandBlock),
			.Code          = CPU_TO_LE16(PTP_OC_GetDeviceInfo),
			.TransactionID = CPU_TO_LE32(0x00000000),
			.Params        = {},
		};

	ErrorCode = CameraControl_InitiateTransaction ( SIInterfaceInfo, &PIMABlock );

	// Get the size (in bytes) of the device info structure
	DeviceInfoSize = (PIMABlock.DataLength - PIMA_COMMAND_SIZE(0));
	printf_P(PSTR(ESC_FG_CYAN "	Got device info of %d bytes.\r\n" ESC_FG_WHITE), DeviceInfoSize);

	// Create a buffer large enough to hold the entire device info
	uint8_t DeviceInfo[DeviceInfoSize];

	// Read in the data block data (containing device info)
	SI_Host_ReadData(SIInterfaceInfo, DeviceInfo, DeviceInfoSize);

	// Once all the data has been read, the pipe must be cleared before the response can be sent
	Pipe_ClearIN();

	// Create a pointer for walking through the info dataset 
	uint8_t* DeviceInfoPos = DeviceInfo;

	g_nStage = 0;
	CameraControl_DeviceInfo_Parse(DeviceInfoSize, DeviceInfoPos);

	g_iDataIsValid = 1;
	
	// Receive the final response block from the device 
	return CameraControl_GetResponseAndCheck (SIInterfaceInfo, &PIMABlock);
}

/*------------------------------------------------------------------------------
 * CameraControl_DeviceInfo_Printout
 */
uint8_t CameraControl_DeviceInfo_Printout ( USB_ClassInfo_SI_Host_t* SIInterfaceInfo )
{
	uint8_t 	errorCode = 0;

	CHECK_CAMERA_CONNECTION;

	if ( !g_iDataIsValid )
	{
		errorCode = CameraControl_DeviceInfo_GetInfo ( SIInterfaceInfo );
		if (PIPE_RWSTREAM_NoError != errorCode )
		{
			printf_P(PSTR("Error reading device info\r\n"));
			return errorCode;	
		}
	}

	printf_P(PSTR("Std.Ver.:\t%d\r\n"), g_stDeviceInfo.iStandardVersion);
	printf_P(PSTR("Vend.Ext.ID.:\t%d\r\n"), g_stDeviceInfo.iVendorExtensionID);
	printf_P(PSTR("Vend.Ext.Ver.:\t%d\r\n"), g_stDeviceInfo.iVendorExtensionVersion);
	printf_P(PSTR("\r\nVend.Ext.Desc.:\t%s\r\n"), g_stDeviceInfo.sVendorExtensionDescription);
	printf_P(PSTR("Func.Mode:\t%d\r\n"), g_stDeviceInfo.iFunctionMode);
	
	PRINT_CAPS(g_stDeviceInfo.iOperationsSupported,"\r\nOperations supported:\r\n",OPERATION_SUPPORT_BASE);
	PRINT_CAPS(g_stDeviceInfo.iEventsSupported,"\r\nEvents supported:\r\n",EVENT_SUPPORT_BASE);
	PRINT_CAPS(g_stDeviceInfo.iPropertiesSupported,"\r\nDevice properties supported:\r\n",PROPERTY_SUPPORT_BASE);
	PRINT_CAPS(g_stDeviceInfo.iCaptureFormatsSupported,"\r\nCapture formats:\r\n",CAP_FORMAT_SUPPORT_BASE);
	PRINT_CAPS(g_stDeviceInfo.iImageFormatsSupported,"\r\nImage Formats:\r\n",IM_FORMAT_SUPPORT_BASE);

	printf_P(PSTR("\r\nManufacturer:\t%s\r\n"), g_stDeviceInfo.sManufacturer);
	printf_P(PSTR("Model:\t\t%s\r\n"), g_stDeviceInfo.Model);
	printf_P(PSTR("Device ver.:\t%s\r\n"), g_stDeviceInfo.DeviceVersion);
	printf_P(PSTR("Serial Num.:\t%s\r\n"), g_stDeviceInfo.SerialNumber);

	return 0;
}

/*------------------------------------------------------------------------------
 * CameraControl_DeviceInfo_Bin
 */
uint8_t CameraControl_DeviceInfo_Bin ( USB_ClassInfo_SI_Host_t* SIInterfaceInfo, uint16_t transID )
{
	uint16_t 	DeviceInfoSize;
	uint8_t 	ErrorCode = 0;
	uint16_t	i;

	CHECK_CAMERA_CONNECTION;

	SIInterfaceInfo->State.TransactionID = 0;

	// Create PIMA message block
	PIMA_Container_t PIMABlock = (PIMA_Container_t)
		{
			.DataLength    = CPU_TO_LE32(PIMA_COMMAND_SIZE(0)),
			.Type          = CPU_TO_LE16(PIMA_CONTAINER_CommandBlock),
			.Code          = CPU_TO_LE16(PTP_OC_GetDeviceInfo),
			.TransactionID = CPU_TO_LE32(0x00000000),
			.Params        = {},
		};

	ErrorCode = CameraControl_InitiateTransaction ( SIInterfaceInfo, &PIMABlock );

	// Get the size (in bytes) of the device info structure
	DeviceInfoSize = (PIMABlock.DataLength - PIMA_COMMAND_SIZE(0));
	
	// Create a buffer large enough to hold the entire device info
	uint8_t DeviceInfo[DeviceInfoSize];

	// Read in the data block data (containing device info)
	SI_Host_ReadData(SIInterfaceInfo, DeviceInfo, DeviceInfoSize);

	// Once all the data has been read, the pipe must be cleared before the response can be sent
	Pipe_ClearIN();

	TP_Header_ST header;
	header.length = DeviceInfoSize;
	header.transID = transID;
	header.type = TP_DATA_CAMERA_INFO;
	SendHeader(&header);
	for (i=0; i<DeviceInfoSize; i++)
		putchar(DeviceInfo[i]);

	// Receive the final response block from the device 
	CameraControl_GetResponseAndCheck (SIInterfaceInfo, &PIMABlock);

	return 0;
}

#include "CameraControl_DeviceOperation.h"
#include "CameraControl_StorageInfo.h"

/*------------------------------------------------------------------------------
 * CameraControl_DeviceOperation_Capture
 */
uint16_t CameraControl_DeviceOperation_Capture ( USB_ClassInfo_SI_Host_t* SIInterfaceInfo,
												 PTP_STORETYPE_EN enStorageType,
												 PTP_FMT_EN enFileFormat )
{
	uint8_t iError = 0;
	uint32_t iStorageID = 0;
	
	CHECK_CAMERA_CONNECTION;

	// Open a new session
	iError = CameraControl_OpenSession(SIInterfaceInfo);
	if ( iError != PIPE_RWSTREAM_NoError )
	{
		iError = -1;
		goto ExitFunction;
	}

	// Search for the appropriate storage ID given the type
	if ( CameraControl_GetStorageID ( enStorageType, &iStorageID )!=0 )
	{
		printf_P(PSTR("\r\nStorage not found. Run get_storage_info..."));
		iError = -2;
		goto ExitFunction;
	}

	// Initiate capture operation
	//  OpCode 0x100E (PTP_OC_InitiateCapture)
	//  Param1 Storage ID
	//  Param2 Object Format
	PIMA_Container_t PIMABlock = (PIMA_Container_t)
		{
			.DataLength    = CPU_TO_LE32(PIMA_COMMAND_SIZE(2)),
			.Type          = CPU_TO_LE16(PIMA_CONTAINER_CommandBlock),
			.Code          = CPU_TO_LE16(PTP_OC_InitiateCapture),
			.Params        = {iStorageID, enFileFormat},
		};
	
	iError = CameraControl_InitiateTransaction ( SIInterfaceInfo, &PIMABlock );
	if ( iError != PIPE_RWSTREAM_NoError )	goto ExitFunction;

ExitFunction:
	// Close the session
	iError = CameraControl_CloseSession(SIInterfaceInfo);
	if ( iError != PIPE_RWSTREAM_NoError )
	{
	}

	return 0;
}

/*------------------------------------------------------------------------------
 * CameraControl_DeviceOperation_GetPropertyDesc
 */
uint16_t CameraControl_DeviceOperation_GetPropertyDesc ( USB_ClassInfo_SI_Host_t* SIInterfaceInfo,
												 		 PTP_DEVPROPERTY_EN enPropertyType )
{
	uint8_t iError = 0;
	uint8_t iTemp1 = 0;
	uint8_t iTemp2 = 0;

	CHECK_CAMERA_CONNECTION;

	// Open a new session
	iError = CameraControl_OpenSession(SIInterfaceInfo);
	if ( iError != PIPE_RWSTREAM_NoError )
	{
		iError = -1;
		return iError;
	}

	// Initiate capture operation
	//  OpCode 0x1014 (PTP_OC_GetDevicePropDesc)
	PIMA_Container_t PIMABlock = (PIMA_Container_t)
		{
			.DataLength    = CPU_TO_LE32(PIMA_COMMAND_SIZE(1)),
			.Type          = CPU_TO_LE16(PIMA_CONTAINER_CommandBlock),
			.Code          = CPU_TO_LE16(PTP_OC_GetDevicePropDesc),
			.Params        = {enPropertyType},
		};
	
	iError = CameraControl_InitiateTransaction ( SIInterfaceInfo, &PIMABlock );
	if ( iError != PIPE_RWSTREAM_NoError )	return -1;

	// Get the size (in bytes) of the dataset
	uint16_t DatasetSize = (PIMABlock.DataLength - PIMA_COMMAND_SIZE(0));

	if (DatasetSize==0) return -1;

	printf_P(PSTR(ESC_FG_CYAN "	Got property info of %d bytes.\r\n" ESC_FG_WHITE), DatasetSize);	

	// Create a buffer large enough to hold the entire device info
	uint8_t Dataset[DatasetSize];

	// Read in the data block data
	SI_Host_ReadData(SIInterfaceInfo, Dataset, DatasetSize);

	// Once all the data has been read, the pipe must be cleared before the response can be sent
	Pipe_ClearIN();

	// Create a pointer for walking through the info dataset 
	uint8_t* DatasetPos = Dataset;
	iTemp1 = DatasetSize;

	// Read out the description
	while (iTemp1--)
	{
		if ( !(iTemp2 % 16) ) 
		{
			putchar('\r'); putchar('\n');
		}

		printf_P ( PSTR("%02x "), *DatasetPos );

		DatasetPos++;	
		iTemp2++; 
	}
	putchar('\r'); putchar('\n');

	// Receive the final response block from the device 
	iError = CameraControl_GetResponseAndCheck (SIInterfaceInfo, &PIMABlock);

	// Close the session
	iError = CameraControl_CloseSession(SIInterfaceInfo);
	if ( iError != PIPE_RWSTREAM_NoError )
	{
	}

	return 0;
}


/*------------------------------------------------------------------------------
 * CameraControl_DeviceOperation_GetPropertyDesc
 */
uint16_t CameraControl_DeviceOperation_GetPropertyDescBin ( USB_ClassInfo_SI_Host_t* SIInterfaceInfo,
															PTP_DEVPROPERTY_EN enPropertyType )
{
	uint16_t i;
	uint8_t iError = 0;

	CHECK_CAMERA_CONNECTION;

	// Open a new session
	iError = CameraControl_OpenSession(SIInterfaceInfo);
	if ( iError != PIPE_RWSTREAM_NoError )
	{
		iError = -1;
		return iError;
	}

	// Initiate capture operation
	//  OpCode 0x1014 (PTP_OC_GetDevicePropDesc)
	PIMA_Container_t PIMABlock = (PIMA_Container_t)
		{
			.DataLength    = CPU_TO_LE32(PIMA_COMMAND_SIZE(1)),
			.Type          = CPU_TO_LE16(PIMA_CONTAINER_CommandBlock),
			.Code          = CPU_TO_LE16(PTP_OC_GetDevicePropDesc),
			.Params        = {enPropertyType},
		};
	
	iError = CameraControl_InitiateTransaction ( SIInterfaceInfo, &PIMABlock );
	if ( iError != PIPE_RWSTREAM_NoError )	return -1;

	// Get the size (in bytes) of the dataset
	uint16_t DatasetSize = (PIMABlock.DataLength - PIMA_COMMAND_SIZE(0));

	if (DatasetSize==0) return -1;

	//printf_P(PSTR(ESC_FG_CYAN "	Got property info of %d bytes.\r\n" ESC_FG_WHITE), DatasetSize);	

	// Create a buffer large enough to hold the entire device info
	uint8_t Dataset[DatasetSize];

	// Read in the data block data
	SI_Host_ReadData(SIInterfaceInfo, Dataset, DatasetSize);

	// Once all the data has been read, the pipe must be cleared before the response can be sent
	Pipe_ClearIN();

	putchar(RET_CODE_PROP_DESC);
	putchar((uint8_t)(DatasetSize&0xFF));
	putchar((uint8_t)((DatasetSize>>8)&0xFF));
	for (i=0; i<DatasetSize; i++)
		putchar(Dataset[i]);

	// Receive the final response block from the device 
	iError = CameraControl_GetResponseAndCheck (SIInterfaceInfo, &PIMABlock);

	// Close the session
	iError = CameraControl_CloseSession(SIInterfaceInfo);
	if ( iError != PIPE_RWSTREAM_NoError )
	{
	}

	return 0;
}

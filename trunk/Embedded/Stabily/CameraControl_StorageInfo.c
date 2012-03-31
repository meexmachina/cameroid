#include "CameraControl_StorageInfo.h"
#include "Stabily_Shell.h"

//==============================================================================
// 			V A R I A B L E S 
//==============================================================================
volatile uint32_t						g_aiStorageIDs[MAX_NUM_STORAGES];
volatile CameraControl_StorageInfo_ST	g_astStorageInfo[MAX_NUM_STORAGES];
volatile uint8_t						g_iNumOfStorages = 0;

//==============================================================================
// 			F U N C T I O N S 
//==============================================================================


/*------------------------------------------------------------------------------
 * CameraControl_GetStorageIDs
 */
uint8_t CameraControl_GetStorageIDs ( USB_ClassInfo_SI_Host_t* SIInterfaceInfo )
{
/*	uint16_t ArrayLength;
	uint8_t ErrorCode;
	uint8_t	iCount = 0;
	uint8_t iIDNum = 0;

	CHECK_CAMERA_CONNECTION;
	
	// Create PIMA message block
	PIMA_Container_t PIMABlock = (PIMA_Container_t)
		{
			.DataLength    = CPU_TO_LE32(PIMA_COMMAND_SIZE(0)),
			.Type          = CPU_TO_LE16(PIMA_CONTAINER_CommandBlock),
			.Code          = CPU_TO_LE16(PTP_OC_GetStorageIDs),
			.Params        = {},
		};


	// Send the command and get response
	ErrorCode = CameraControl_InitiateTransaction ( SIInterfaceInfo, &PIMABlock );
	if ( ErrorCode != PIPE_RWSTREAM_NoError ) return ErrorCode;

	// Get the size (in bytes) of the device info structure
	ArrayLength = (PIMABlock.DataLength - PIMA_COMMAND_SIZE(0));
	printf_P(PSTR(ESC_FG_CYAN "	Got storage IDs of %d bytes.\r\n" ESC_FG_WHITE), ArrayLength);

	// Create a buffer large enough to hold the entire Storage IDs list
	uint8_t StorageIDs[ArrayLength];

	// Read in the data block data (containing device info)
	SI_Host_ReadData(SIInterfaceInfo, StorageIDs, ArrayLength);

	// Once all the data has been read, the pipe must be cleared before the response can be sent
	Pipe_ClearIN();

	// Create a pointer for walking through the info dataset 
	uint32_t* StorageInfoPos = (uint32_t*)((void*)(StorageIDs));

	g_iNumOfStorages = *StorageInfoPos++;
	iCount = (g_iNumOfStorages>MAX_NUM_STORAGES)?MAX_NUM_STORAGES:g_iNumOfStorages;
	iIDNum = 0;

	while (iCount--)
	{
		g_aiStorageIDs[iIDNum++]=*StorageInfoPos++;
	}

	// Receive the final response block from the device 
	return CameraControl_GetResponseAndCheck (SIInterfaceInfo, &PIMABlock);*/
	return 0;
}

/*------------------------------------------------------------------------------
 * CameraControl_GetStorageInfo
 */
uint8_t CameraControl_GetStorageInfo ( USB_ClassInfo_SI_Host_t* SIInterfaceInfo, 
									   uint8_t iStorageIndex )
{
/*	uint16_t StorageInfoSize;
	uint8_t ErrorCode;
	uint32_t iStorageID;
	uint8_t iTemp1, iTemp2;

	CHECK_CAMERA_CONNECTION;

	if ( iStorageIndex >= g_iNumOfStorages )
	{
		// The index is higher thet the available storage IDs
		printf_P(PSTR("Error getting storage info - storage index %d is bigger then num of storages %d.\r\n"), 
							iStorageIndex, g_iNumOfStorages);
		return SI_ERROR_LOGICAL_CMD_FAILED;
	}

	iStorageID = g_aiStorageIDs[iStorageIndex];

	// Create PIMA message block
	PIMA_Container_t PIMABlock = (PIMA_Container_t)
		{
			.DataLength    = CPU_TO_LE32(PIMA_COMMAND_SIZE(1)),
			.Type          = CPU_TO_LE16(PIMA_CONTAINER_CommandBlock),
			.Code          = CPU_TO_LE16(PTP_OC_GetStorageInfo),
			.Params        = {iStorageID},
		};

	// Send the command and get response
	ErrorCode = CameraControl_InitiateTransaction ( SIInterfaceInfo, &PIMABlock );
	if ( ErrorCode != PIPE_RWSTREAM_NoError ) return ErrorCode;

	// Get the size (in bytes) of the device info structure
	StorageInfoSize = (PIMABlock.DataLength - PIMA_COMMAND_SIZE(0));
	printf_P(PSTR(ESC_FG_CYAN "	Got storage info of %d bytes.\r\n" ESC_FG_WHITE), StorageInfoSize);

	// Create a buffer large enough to hold the entire device info
	uint8_t StorageInfo[StorageInfoSize];

	// Read in the data block data (containing device info)
	SI_Host_ReadData(SIInterfaceInfo, StorageInfo, StorageInfoSize);

	// Once all the data has been read, the pipe must be cleared before the response can be sent
	Pipe_ClearIN();

	// Create a pointer for walking through the info dataset 
	uint8_t* DeviceInfoPos = StorageInfo;

	g_astStorageInfo[iStorageIndex].iStorageType 	= *((uint16_t*)(DeviceInfoPos)); DeviceInfoPos+=2;
	g_astStorageInfo[iStorageIndex].iFSType 		= *((uint16_t*)(DeviceInfoPos)); DeviceInfoPos+=2;
	g_astStorageInfo[iStorageIndex].iAccessCap		= *((uint16_t*)(DeviceInfoPos)); DeviceInfoPos+=2;
	g_astStorageInfo[iStorageIndex].iMaxCapacity	= *((uint64_t*)(DeviceInfoPos)); DeviceInfoPos+=8;
	g_astStorageInfo[iStorageIndex].iFreeSpaceBytes	= *((uint64_t*)(DeviceInfoPos)); DeviceInfoPos+=8;
	g_astStorageInfo[iStorageIndex].iFreeSpaceImages= *((uint32_t*)(DeviceInfoPos)); DeviceInfoPos+=4;
	
	iTemp1 = *DeviceInfoPos++;	// get the length of the Storage Description field string
	if ( iTemp1 > (STORAGE_MAX_STRING_SIZE-1) ) iTemp1 = STORAGE_MAX_STRING_SIZE-1;
	iTemp2 = 0;	

	while (iTemp1--)
	{
		g_astStorageInfo[iStorageIndex].sDescription[iTemp2++] = *DeviceInfoPos;
		DeviceInfoPos += 2;
	}
	g_astStorageInfo[iStorageIndex].sDescription[iTemp2] = '\0';

	iTemp1 = *DeviceInfoPos++;	// get the length of the Volume Label field string
	if ( iTemp1 > (STORAGE_MAX_STRING_SIZE-1) ) iTemp1 = STORAGE_MAX_STRING_SIZE-1;
	iTemp2 = 0;	

	while (iTemp1--)
	{
		g_astStorageInfo[iStorageIndex].sVolumeLabel[iTemp2++] = *DeviceInfoPos;
		DeviceInfoPos += 2;
	}
	g_astStorageInfo[iStorageIndex].sVolumeLabel[iTemp2] = '\0';

	// Receive the final response block from the device 
	return CameraControl_GetResponseAndCheck (SIInterfaceInfo, &PIMABlock);*/
	return 0;
}


/*------------------------------------------------------------------------------
 * CameraControl_StorageInfo_Printout - shows all storage information
 */
uint8_t CameraControl_StorageInfo_Printout 	( USB_ClassInfo_SI_Host_t* SIInterfaceInfo )
{
	uint8_t ErrorCode = PIPE_RWSTREAM_NoError;
	uint8_t storageIdx = 0;	

	CHECK_CAMERA_CONNECTION;	

	ErrorCode = CameraControl_GetStorageIDs ( SIInterfaceInfo );
	if ( ErrorCode != PIPE_RWSTREAM_NoError ) return ErrorCode;

	for (	storageIdx=0; storageIdx < g_iNumOfStorages; storageIdx ++ )
	{
		printf_P(PSTR("Retrieving storage index %d (ID=0x%X)\r\n"), storageIdx, g_aiStorageIDs[storageIdx]);

		ErrorCode = CameraControl_GetStorageInfo ( SIInterfaceInfo, storageIdx );
		if (ErrorCode != PIPE_RWSTREAM_NoError)  return ErrorCode;

		printf_P(PSTR("	Storage type: 0x%X\r\n"), g_astStorageInfo[storageIdx].iStorageType );
		printf_P(PSTR("	File-system type: 0x%X\r\n"), g_astStorageInfo[storageIdx].iFSType );
		printf_P(PSTR("	Access capability: 0x%X\r\n"), g_astStorageInfo[storageIdx].iAccessCap );
		printf_P(PSTR("	Max capacity: 0x%X\r\n"), g_astStorageInfo[storageIdx].iMaxCapacity );
		printf_P(PSTR("	Free space: 0x%X\r\n"), g_astStorageInfo[storageIdx].iFreeSpaceBytes );
		printf_P(PSTR("	Free space for images: 0x%X\r\n"), g_astStorageInfo[storageIdx].iFreeSpaceImages );
		printf_P(PSTR("	Storage description: '%s'\r\n"), g_astStorageInfo[storageIdx].sDescription );
		printf_P(PSTR("	Volume label: '%s'\r\n"), g_astStorageInfo[storageIdx].sVolumeLabel );
	}

	return ErrorCode;
}

/*------------------------------------------------------------------------------
 * CameraControl_StorageInfo_Bin - binary transfer
 */
uint8_t CameraControl_StorageInfo_Bin 	( USB_ClassInfo_SI_Host_t* SIInterfaceInfo,  uint8_t iStorageIndex, uint16_t transID )
{
	uint32_t iStorageID;

	if ( iStorageIndex >= g_iNumOfStorages )
	{
		// The index is higher then the available storage IDs
		//printf_P(PSTR("Error getting storage info - storage index %d is bigger then num of storages %d.\r\n"), 
		//					iStorageIndex, g_iNumOfStorages);
		return SI_ERROR_LOGICAL_CMD_FAILED;
	}

	iStorageID = g_aiStorageIDs[iStorageIndex];

	return CameraControl_GeneralStream_Bin (SIInterfaceInfo, 
											PTP_OC_GetStorageInfo,
											iStorageID,
											0,
											0, 
											1,
											TP_DATA_STORAGE_INFO,
											transID );
}

/*------------------------------------------------------------------------------
 * CameraControl_GetStorageID - Search for storage ID from its type
 */
uint8_t CameraControl_GetStorageID 	( PTP_STORETYPE_EN enStorageType, uint32_t* iStorageID )
{
	uint8_t i;

	for (i=0; i<g_iNumOfStorages; i++)
	{
		if (g_astStorageInfo[i].iStorageType==enStorageType)
		{
			if (iStorageID) *iStorageID=g_aiStorageIDs[i];
			return 0;
		}
	}

	return -1;
}

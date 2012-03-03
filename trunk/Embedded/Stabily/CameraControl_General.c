#include "CameraControl_General.h"

/*------------------------------------------------------------------------------
 * CameraControl_PTPErrorDescription
 */
void CameraControl_PTPErrorDescription ( uint16_t errNum )
{
	if (errNum==0x2001)
		printf_P(PSTR(ESC_FG_CYAN "		[0x%04x] " ESC_FG_WHITE), errNum); 
	else
		printf_P(PSTR(ESC_FG_RED "		[0x%04x] " ESC_FG_WHITE), errNum); 

	switch (errNum)
	{
		case 0x2001: printf_P(PSTR(ESC_FG_CYAN "OK\r\n" ESC_FG_WHITE)); break;
		case 0x2002: printf_P(PSTR(ESC_FG_RED "General Error\r\n" ESC_FG_WHITE)); break; 
		case 0x2003: printf_P(PSTR(ESC_FG_RED "Session Not Open\r\n" ESC_FG_WHITE)); break; 
		case 0x2004: printf_P(PSTR(ESC_FG_RED "Invalid TransactionID\r\n" ESC_FG_WHITE)); break; 
		case 0x2005: printf_P(PSTR(ESC_FG_RED "Operation Not Supported\r\n" ESC_FG_WHITE)); break; 
		case 0x2006: printf_P(PSTR(ESC_FG_RED "Parameter Not Supported\r\n" ESC_FG_WHITE)); break;
		case 0x2007: printf_P(PSTR(ESC_FG_RED "Incomplete Transfer\r\n" ESC_FG_WHITE)); break; 
		case 0x2008: printf_P(PSTR(ESC_FG_RED "Invalid StorageID\r\n" ESC_FG_WHITE)); break;
		case 0x2009: printf_P(PSTR(ESC_FG_RED "Invalid ObjectHandle\r\n" ESC_FG_WHITE)); break; 
		case 0x200A: printf_P(PSTR(ESC_FG_RED "DeviceProp Not Supported\r\n" ESC_FG_WHITE)); break; 
		case 0x200B: printf_P(PSTR(ESC_FG_RED "Invalid ObjectFormatCode\r\n" ESC_FG_WHITE)); break;
		case 0x200C: printf_P(PSTR(ESC_FG_RED "Store Full\r\n" ESC_FG_WHITE)); break;
		case 0x200D: printf_P(PSTR(ESC_FG_RED "Object WriteProtected\r\n" ESC_FG_WHITE)); break; 
		case 0x200E: printf_P(PSTR(ESC_FG_RED "Store Read-Only\r\n" ESC_FG_WHITE)); break;
		case 0x200F: printf_P(PSTR(ESC_FG_RED "Access Denied\r\n" ESC_FG_WHITE)); break;
		case 0x2010: printf_P(PSTR(ESC_FG_RED "No Thumbnail Present\r\n" ESC_FG_WHITE)); break;
		case 0x2011: printf_P(PSTR(ESC_FG_RED "SelfTest Failed\r\n" ESC_FG_WHITE)); break;
		case 0x2012: printf_P(PSTR(ESC_FG_RED "Partial Deletion\r\n" ESC_FG_WHITE)); break;
		case 0x2013: printf_P(PSTR(ESC_FG_RED "Store Not Available\r\n" ESC_FG_WHITE)); break;
		case 0x2014: printf_P(PSTR(ESC_FG_RED "Specification By Format Unsupported\r\n" ESC_FG_WHITE)); break; 
		case 0x2015: printf_P(PSTR(ESC_FG_RED "No Valid ObjectInfo\r\n" ESC_FG_WHITE)); break;
		case 0x2016: printf_P(PSTR(ESC_FG_RED "Invalid Code Format\r\n" ESC_FG_WHITE)); break; 
		case 0x2017: printf_P(PSTR(ESC_FG_RED "Unknown Vendor Code\r\n" ESC_FG_WHITE)); break; 
		case 0x2018: printf_P(PSTR(ESC_FG_RED "Capture Already Terminated\r\n" ESC_FG_WHITE)); break;
		case 0x2019: printf_P(PSTR(ESC_FG_RED "Device Busy\r\n" ESC_FG_WHITE)); break; 
		case 0x201A: printf_P(PSTR(ESC_FG_RED "Invalid ParentObject\r\n" ESC_FG_WHITE)); break; 
		case 0x201B: printf_P(PSTR(ESC_FG_RED "Invalid DeviceProp Format\r\n" ESC_FG_WHITE)); break; 
		case 0x201C: printf_P(PSTR(ESC_FG_RED "Invalid DeviceProp Value\r\n" ESC_FG_WHITE)); break; 
		case 0x201D: printf_P(PSTR(ESC_FG_RED "Invalid Parameter\r\n" ESC_FG_WHITE)); break;
		case 0x201E: printf_P(PSTR(ESC_FG_RED "Session Already Open\r\n" ESC_FG_WHITE)); break;
		case 0x201F: printf_P(PSTR(ESC_FG_RED "Transaction Cancelled\r\n" ESC_FG_WHITE)); break;
		case 0x2020: printf_P(PSTR(ESC_FG_RED "Specification of Destination Unsupported\r\n" ESC_FG_WHITE)); break;
		default: 	 printf_P(PSTR(ESC_FG_RED "Undefined or Vendor Specific\r\n" ESC_FG_WHITE)); break;
	}
}

/*------------------------------------------------------------------------------
 * CameraControl_DescribePIPE_RWSTREAMError
 */
void CameraControl_DescribePIPE_RWSTREAMError ( uint8_t uiStatus )
{
	printf_P(PSTR("PIMA Error (%d): "), uiStatus);

	switch (uiStatus)
	{
		case PIPE_RWSTREAM_PipeStalled:
			printf_P(PSTR("The device stalled the pipe during the transfer.\r\n"));
			break;
		case PIPE_RWSTREAM_DeviceDisconnected:
			printf_P(PSTR("Device was disconnected from the host during the transfer.\r\n"));
			break;
		case PIPE_RWSTREAM_Timeout:
			printf_P(PSTR("The device failed to accept or send the next packet within the software timeout period set by the USB_STREAM_TIMEOUT_MS macro.\r\n"));
			break;
		case PIPE_RWSTREAM_IncompleteTransfer:
			printf_P(PSTR("Indicates that the pipe bank became full/empty before the complete contents of the stream could be transferred.\r\n"));
			break;
		case SI_ERROR_LOGICAL_CMD_FAILED:
			printf_P(PSTR("Error code for some Still Image Host functions, indicating a logical (and not hardware) error.\r\n"));
			break;
		default:
			printf_P(PSTR("Unrecognized error.\r\n"));
	}

}

/*------------------------------------------------------------------------------
 * UnicodeToASCII
 */
void UnicodeToASCII(uint8_t* UnicodeString, char* Buffer)
{
	/* Get the number of characters in the string, skip to the start of the string data */
	uint8_t CharactersRemaining = *(UnicodeString++);

	/* Loop through the entire unicode string */
	while (CharactersRemaining--)
	{
		/* Load in the next unicode character (only the lower byte, as only Unicode coded ASCII is supported) */
		*(Buffer++) = *UnicodeString;

		/* Jump to the next unicode character */
		UnicodeString += 2;
	}

	/* Null terminate the string */
	*Buffer = 0;
}

/*------------------------------------------------------------------------------
 * CameraControl_GetResponseAndCheck
 */
uint8_t CameraControl_GetResponseAndCheck ( USB_ClassInfo_SI_Host_t* SIInterfaceInfo,
											PIMA_Container_t *PIMABlock )
{
	uint8_t ErrorCode;

	CHECK_CAMERA_CONNECTION;

	// Receive the final response block from the device 
	if ((ErrorCode = SI_Host_ReceiveBlockHeader(SIInterfaceInfo, PIMABlock)) != PIPE_RWSTREAM_NoError)
	{
		CameraControl_DescribePIPE_RWSTREAMError (ErrorCode);
		if ( ErrorCode != SI_ERROR_LOGICAL_CMD_FAILED )
			return ErrorCode;
	}

	// Verify that the command completed successfully
	if ((PIMABlock->Type != PIMA_CONTAINER_ResponseBlock) || (PIMABlock->Code != PTP_RC_OK))
	{
		CameraControl_PTPErrorDescription ( PIMABlock->Code );
		return SI_ERROR_LOGICAL_CMD_FAILED;
	}

	return PIPE_RWSTREAM_NoError;
}

/*------------------------------------------------------------------------------
 * CameraControl_InitiateTransaction
 */
uint8_t CameraControl_InitiateTransaction ( USB_ClassInfo_SI_Host_t* SIInterfaceInfo,
											PIMA_Container_t *PIMABlock )
{
	uint8_t ErrorCode;

	CHECK_CAMERA_CONNECTION;

	// Check connectivity
	if (CAMERA_CONTROL_NOT_CONNECTED)
	{
		CameraControl_DescribePIPE_RWSTREAMError (PIPE_RWSTREAM_DeviceDisconnected);
		return PIPE_RWSTREAM_DeviceDisconnected;
	}

	// Send the block - info request
	if ((ErrorCode = SI_Host_SendBlockHeader(SIInterfaceInfo, PIMABlock)) != PIPE_RWSTREAM_NoError)
	{
		return ErrorCode;
	}

	// Get the answer in the same memory block
	if ((ErrorCode = SI_Host_ReceiveBlockHeader(SIInterfaceInfo, PIMABlock)) != PIPE_RWSTREAM_NoError)
	{
		CameraControl_DescribePIPE_RWSTREAMError (ErrorCode);
		if ( ErrorCode != SI_ERROR_LOGICAL_CMD_FAILED )
			return ErrorCode;
	}
	
	return PIPE_RWSTREAM_NoError;
}

/*------------------------------------------------------------------------------
 * CameraControl_OpenSession
 */
uint8_t CameraControl_OpenSession(USB_ClassInfo_SI_Host_t* SIInterfaceInfo)
{
	uint8_t ErrorCode;

	CHECK_CAMERA_CONNECTION;

	SIInterfaceInfo->State.TransactionID = 0;
	SIInterfaceInfo->State.IsSessionOpen = false;

	PIMA_Container_t PIMABlock = (PIMA_Container_t)
		{
			.DataLength    = CPU_TO_LE32(PIMA_COMMAND_SIZE(1)),
			.Type          = CPU_TO_LE16(PIMA_CONTAINER_CommandBlock),
			.Code          = CPU_TO_LE16(PTP_OC_OpenSession),
			.Params        = {CPU_TO_LE32(1)},
		};

	ErrorCode = CameraControl_InitiateTransaction ( SIInterfaceInfo, &PIMABlock );

	if ( ErrorCode == PIPE_RWSTREAM_NoError ) SIInterfaceInfo->State.IsSessionOpen = true;

	return ErrorCode;
}

/*------------------------------------------------------------------------------
 * CameraControl_CloseSession
 */
uint8_t CameraControl_CloseSession(USB_ClassInfo_SI_Host_t* SIInterfaceInfo)
{
	uint8_t ErrorCode;

	CHECK_CAMERA_CONNECTION;

	PIMA_Container_t PIMABlock = (PIMA_Container_t)
		{
			.DataLength    = CPU_TO_LE32(PIMA_COMMAND_SIZE(0)),
			.Type          = CPU_TO_LE16(PIMA_CONTAINER_CommandBlock),
			.Code          = CPU_TO_LE16(PTP_OC_CloseSession),
		};

	ErrorCode = CameraControl_InitiateTransaction ( SIInterfaceInfo, &PIMABlock );

	if ( ErrorCode == PIPE_RWSTREAM_NoError ) SIInterfaceInfo->State.IsSessionOpen = false;

	return ErrorCode;
}

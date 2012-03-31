#include "CameraControl_DeviceEvents.h"


/*------------------------------------------------------------------------------
 * DevEventHandler_t - event handler list
 */
DevEventHandler_t event_hander_tbl[] = 
{
	{NULL,	0},
	{NULL,	0},
	{NULL,	0},
	{NULL,	0},
	{NULL,	0},
	{NULL,	0},
	{NULL,	0},
	{NULL,	0},
	{NULL,	0},
	{NULL,	0},
	{NULL,	0},
	{NULL,	0},
	{NULL,	0},
	{NULL,	0},
	{NULL,	0},
	{NULL,	0},
	{NULL,	0},
	{NULL,	0},
	{NULL,	0},
	{NULL,	0}
};


/*------------------------------------------------------------------------------
 * CameraControl_DeviceEvents_PollEvents
 */
uint8_t CameraControl_DeviceEvents_PollEvents( USB_ClassInfo_SI_Host_t* SIInterfaceInfo )
{
	uint8_t iError = 0;
	PIMA_Container_t PIMABlock;

	if (CAMERA_CONTROL_NOT_CONNECTED) return 0;
	
	// Check if a message is waiting
	if ( !SI_Host_IsEventReceived ( SIInterfaceInfo ) )
	{
		return 0;
	}
	
	//printf(PSTR("\r\nNew event:"));
		
	// Get the event data
	iError =  SI_Host_ReceiveEventHeader(SIInterfaceInfo, &PIMABlock) ;
	if ( iError != PIPE_RWSTREAM_NoError )	goto ExitFunction;

		
	// typedef struct
	// {
	//		uint32_t DataLength; /**< Length of the container and data, in bytes. */
	//		uint16_t Type; /**< Container type, a value from the \ref PIMA_Container_Types_t enum. */
	//		uint16_t Code; /**< Command, event or response code of the container. */
	//		uint32_t TransactionID; /**< Unique container ID to link blocks together. */
	//		uint32_t Params[3]; /**< Block parameters to be issued along with the block code (command blocks only). */
	//  } ATTR_PACKED PIMA_Container_t;
	
	if ( PIMABlock.Type != PIMA_CONTAINER_EventBlock )
	{
		iError = -1;
		goto ExitFunction;
	}
	
	// Print out the transaction ID and event message
	// printf_P(PSTR("\r\nNew event:"));
	
	// Get the response code
	switch ( PIMABlock.Code )
	{
		//===================================================================
		case PTP_EC_Undefined:
			{	
				if ( event_hander_tbl[0].func_event_hander != NULL )
				{
					// 
					break;
				}
				
				printf_P(PSTR("EVENT: Undefined.\r\n"));
			}
			break;

		//===================================================================
		case PTP_EC_CancelTransaction:					
			{	
				if ( event_hander_tbl[1].func_event_hander != NULL )
				{
					// 
					break;
				}
				
				printf_P(PSTR("EVENT: Cancel transaction.\r\n"));
			}
			break;

		//===================================================================
		case PTP_EC_ObjectAdded:						
			{	
				if ( event_hander_tbl[2].func_event_hander != NULL )
				{
					// 
					break;
				}
				
				printf_P(PSTR("EVENT: Object added ID: 0x%x.\r\n"), PIMABlock.Params[0]);
			}	
			// param1 = objectHandle
			break;

		//===================================================================
		case PTP_EC_ObjectRemoved:						
			{	
				if ( event_hander_tbl[3].func_event_hander != NULL )
				{
					// 
					break;
				}
				
				printf_P(PSTR("EVENT: Object removed ID: 0x%x.\r\n"), PIMABlock.Params[0]);
			}	
			// param1 = objectHandle
			break;

		//===================================================================
		case PTP_EC_StoreAdded:				
			{	
				if ( event_hander_tbl[4].func_event_hander != NULL )
				{
					// 
					break;
				}
				
				printf_P(PSTR("EVENT: Storage added ID: 0x%x.\r\n"), PIMABlock.Params[0]);
			}	
			//	param1 = storageId
			//	check if already in list, else re-arange		
			break;

		//===================================================================
		case PTP_EC_StoreRemoved:				
			{	
				if ( event_hander_tbl[5].func_event_hander != NULL )
				{
					// 
					break;
				}
				
				printf_P(PSTR("EVENT: Object removed ID: 0x%x.\r\n"), PIMABlock.Params[0]);
			}	
			// param1 = storageId
			// rearange
			break;

		//===================================================================
		case PTP_EC_DevicePropChanged:				
			{	
				if ( event_hander_tbl[6].func_event_hander != NULL )
				{
					// 
					break;
				}
				
				printf_P(PSTR("EVENT: Property changed CODE: 0x%x.\r\n"), PIMABlock.Params[0]);
			}		
			// param1 = devicePropCode
			break;

		//===================================================================
		case PTP_EC_ObjectInfoChanged:
			{	
				if ( event_hander_tbl[7].func_event_hander != NULL )
				{
					// 
					break;
				}
				
				printf_P(PSTR("EVENT: Object info changed ID: 0x%x.\r\n"), PIMABlock.Params[0]);
			}	
			// param1 = objectHandle		
			break;

		//===================================================================
		case PTP_EC_DeviceInfoChanged:
			{	
				if ( event_hander_tbl[8].func_event_hander != NULL )
				{
					// 
					break;
				}
				
				printf_P(PSTR("EVENT: Dev info changed.\r\n"));
			}	
			//	get new info	
			break;

		//===================================================================
		case PTP_EC_RequestObjectTransfer:				
			// we don't need this one
			break;

		//===================================================================
		case PTP_EC_StoreFull:							
			{	
				if ( event_hander_tbl[10].func_event_hander != NULL )
				{
					// 
					break;
				}
				
				printf_P(PSTR("EVENT: Storage full ID: 0x%x.\r\n"), PIMABlock.Params[0]);
			}	
			// param1 = storageId
			break;

		//===================================================================
		case PTP_EC_DeviceReset:							
			break;

		//===================================================================
		case PTP_EC_StorageInfoChanged:			
			{	
				if ( event_hander_tbl[12].func_event_hander != NULL )
				{
					// 
					break;
				}
				
				printf_P(PSTR("EVENT: Storage info changed ID: 0x%x.\r\n"), PIMABlock.Params[0]);
			}			
			// param1 = storageId
			break;

		//===================================================================
		case PTP_EC_CaptureComplete:					
			{	
				if ( event_hander_tbl[13].func_event_hander != NULL )
				{
					// 
					break;
				}
				
				printf_P(PSTR("EVENT: Capture complete TransID: 0x%x.\r\n"), PIMABlock.Params[0]);
			}		
			// param1 = transactionId of the capture request
			break;

		//===================================================================
		case PTP_EC_UnreportedStatus:					
			break;
		
		/* Canon extension Event Codes */
		//===================================================================
		case PTP_EC_CANON_DeviceInfoChanged:				
			break;
		
		//===================================================================
		case PTP_EC_CANON_RequestObjectTransfer:			
			break;

		//===================================================================
		case PTP_EC_CANON_CameraModeChanged:				
			break;
		
		/* Nikon extension Event Codes */
		//===================================================================
		case PTP_EC_NIKON_ObjectReady:					
			{	
				if ( event_hander_tbl[18].func_event_hander != NULL )
				{
					// 
					break;
				}
				
				printf_P(PSTR("EVENT [NIKON]: Object ready ID: 0x%x.\r\n"), PIMABlock.Params[0]);
			}	
			break;
		//===================================================================
		case PTP_EC_NIKON_CaptureOverflow:				
			{	
				if ( event_hander_tbl[19].func_event_hander != NULL )
				{
					// 
					break;
				}
				
				printf_P(PSTR("EVENT [NIKON]: Capture overflow ID: 0x%x.\r\n"), PIMABlock.Params[0]);
			}	
			break;
			
		default:
			break;
	}


ExitFunction:
	return iError;
}

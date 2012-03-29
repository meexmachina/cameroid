/*
 * TransferProtocol.c
 *
 * Created: 3/28/2012 10:57:10 PM
 *  Author: david
 */ 

#include <stdio.h>
#include "TransferProtocol.h"
#include "ISRUart.h"
#include "CameraControl_General.h"
#include "CameraControl_StorageInfo.h"

/*------------------------------------------------------------------------------
 * Global / Extern Variables
 */
volatile uint32_t					g_iEventID = 0;
volatile uint32_t					g_iCommandID = 0;
TP_Incoming_Command_ST				g_stCurrentCommand;
volatile uint8_t					g_iInCommandPos = 0;
volatile uint8_t					g_iCurrentCheckSum = 0;

extern USB_ClassInfo_SI_Host_t		DigitalCamera_SI_Interface;
extern volatile uint32_t			g_aiStorageIDs[MAX_NUM_STORAGES];
extern volatile uint8_t				g_iNumOfStorages;

TP_Outgoing_Event_ST				g_stOutgoingEventQueue[TP_EVENT_QUEUE_SIZE];
volatile uint8_t					g_iEventQueueStart = 0;
volatile uint8_t					g_iEventQueueEnd = 0;
volatile uint8_t					g_iEventQueueSize = 0;

/*------------------------------------------------------------------------------
 * Function Implementation
 */
uint8_t TP_GetIncomingCommand ( void )
{
	char *iInCommand = (char*)((void*)(&g_stCurrentCommand));
	uint8_t iError = 0;
	
	while (uart_rx_ready( ))
	{
		// get the new char
		iInCommand[g_iInCommandPos] = uart_getc ( stdin );
		g_iCurrentCheckSum += *((uint8_t*)(iInCommand+g_iInCommandPos));
		g_iInCommandPos ++;
		
		// check if the command was finished
		if (g_iInCommandPos == TP_COMMAND_SIZE)				
		{
			// check the checksum
			if (g_stCurrentCommand.checksum != g_iCurrentCheckSum)
			{
				// checksum error
				// send framing error event
				// flush all incoming buffers
				return 1;
			}
			
			// assume that the message is OK
			g_iCurrentCheckSum = 0;
			g_iInCommandPos = 0;
			iError = TP_RespondTo (&g_stCurrentCommand);
			
			if (iError != 0)
			{
				// sending error
				return 2;
			}
		}
	}	
	
	return 0;
}

//------------------------------------------------------------------------------
uint8_t TP_SendEvent (TP_Outgoing_Event_ST* event)
{
	return 0;
}

//------------------------------------------------------------------------------
uint8_t TP_RespondTo (TP_Incoming_Command_ST* command)
{
	TP_Header_ST	header;
	
	header.transID = command->header.transID;
	
	switch (command->header.type)
	{
		case TP_COMMAND_IDN:
			header.length = 3;
			header.type = TP_DATA_IDN;
			
			TP_SendHeader (&header);
			uart_putc(1, stdout);		// version
			uart_putc(0, stdout);		// sub-version
			uart_putc(16, stdout);		// clock-speed
		break;	
				
		case TP_COMMAND_GET_CAMERA_STATUS:
			header.length = 1;
			header.type = TP_DATA_CAMERA_STATUS;
			TP_SendHeader (&header);
			uart_putc (CameraControl_CameraConnected(&DigitalCamera_SI_Interface), stdout);			
		break;
		
		case TP_COMMAND_GET_CAMERA_INFO:
			// if not connected
			if (CameraControl_CameraConnected(&DigitalCamera_SI_Interface)==0)
			{
				header.length = 0;
				header.type = TP_DATA_CAMERA_INFO;
				TP_SendHeader (&header);
			}
			else
			{
				CameraControl_DeviceInfo_Bin ( DigitalCamera_SI_Interface, command->header.transID );
			}
			
		break;
		
		case TP_COMMAND_GET_STORAGE_IDS:	
			CameraControl_OpenSession( &DigitalCamera_SI_Interface );
			CameraControl_GetStorageIDs ( &DigitalCamera_SI_Interface );
			CameraControl_CloseSession( &DigitalCamera_SI_Interface );
			header.length = g_iNumOfStorages*4;
			header.type = TP_DATA_STORAGE_IDS;
			TP_SendHeader(&header);
			
			// write the data
			for ( uint8_t i=0; i<g_iNumOfStorages; i++ )
			{
				char* tempPos = (char*)((void*)(&g_aiStorageIDs[i]));
				uart_putc(tempPos[0], stdout);		// msb
				uart_putc(tempPos[1], stdout);
				uart_putc(tempPos[2], stdout);
				uart_putc(tempPos[3], stdout);
			}
			
		break;
		
		case TP_COMMAND_GET_STORAGE_INFO:
			CameraControl_OpenSession( &DigitalCamera_SI_Interface );
			CameraControl_GetStorageIDs ( &DigitalCamera_SI_Interface );
			CameraControl_StorageInfo_Bin ( &DigitalCamera_SI_Interface, command->arg1, command->header.transID );
			CameraControl_CloseSession( &DigitalCamera_SI_Interface );
		break;
		
		case TP_COMMAND_GET_SYSTEM_STATUS:
		break;
		
		case TP_COMMAND_GET_OBJECT_INFO:
		break;
		
		case TP_COMMAND_GET_OBJECT:	
		break;
		
		case TP_COMMAND_GET_THUMB:	
		break;
		
		case TP_COMMAND_CAPTURE:			
		break;
		
		case TP_COMMAND_GET_PROP_DESC:		
			CameraControl_DeviceOperation_GetPropertyDescBin ( &DigitalCamera_SI_Interface,	command->arg1, command->header.transID );
		break;
		
		case TP_COMMAND_GET_PROP_VAL:	
			CameraControl_DeviceOperation_GetPropertyValBin ( &DigitalCamera_SI_Interface,	command->arg1, command->header.transID );
		break;
		
		case TP_COMMAND_SET_PROP_VAL:	
			
		break;
		
		default:
		break;
	}
	
	return 0;
}

//------------------------------------------------------------------------------
void TP_SendHeader(TP_Header_ST *header)
{
	char *pt = (char*)((void*)(header));
	uint8_t length = TP_HEADER_SIZE;
	
	while (length--)
	{
		uart_putc( pt++, stdout);
	}
}

//------------------------------------------------------------------------------
uint8_t TP_PushEvent(TP_Outgoing_Event_ST *event)
{
	// if queue is full return error
	if ( g_iEventQueueSize == TP_EVENT_QUEUE_SIZE )
		return 1;
		
	
	TP_Outgoing_Event_ST *newEvent = &g_stOutgoingEventQueue[g_iEventQueueStart];
	
	newEvent->header.length=event->header.length;
	newEvent->header.type=event->header.type;
	newEvent->header.transID=g_iEventID++;
	newEvent->arg1 = event->arg1;
	
	g_iEventQueueSize ++;
	g_iEventQueueStart ++;
	
	if (g_iEventQueueStart>=TP_EVENT_QUEUE_SIZE)
		g_iEventQueueStart = 0;
		
	return 0;
}

//------------------------------------------------------------------------------
TP_Outgoing_Event_ST* TP_TopEvent(void)
{
	// if queue is full return error
	if ( g_iEventQueueSize == 0 )
		return NULL;
	
	return &g_stOutgoingEventQueue[g_iEventQueueEnd];
}

//------------------------------------------------------------------------------
TP_Outgoing_Event_ST* TP_PopEvent(void)
{
	uint8_t cur_end = g_iEventQueueEnd;
	
	// if queue is full return error
	if ( g_iEventQueueSize == 0 )
		return NULL;
		
	g_iEventQueueEnd ++;
	if (g_iEventQueueEnd>=TP_EVENT_QUEUE_SIZE)
		g_iEventQueueEnd = 0;
	
	g_iEventQueueSize --;
	
	return &g_stOutgoingEventQueue[cur_end];
}
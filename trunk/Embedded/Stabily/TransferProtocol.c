/*
 * TransferProtocol.c
 *
 * Created: 3/28/2012 10:57:10 PM
 *  Author: david
 */ 

#include <stdio.h>
#include "TransferProtocol.h"
#include "ISRUart.h"

/*------------------------------------------------------------------------------
 * Global Variables
 */
volatile uint32_t					g_iEventID = 0;
volatile uint32_t					g_iCommandID = 0;
volatile TP_Incoming_Command_ST		g_stCurrentCommand;
volatile uint8_t					g_iInCommandPos = 0;
volatile uint8_t					g_iCurrentCheckSum = 0;
extern USB_ClassInfo_SI_Host_t DigitalCamera_SI_Interface;

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
		iInCommandPos ++;
		
		// check if the command was finished
		if (iInCommandPos == TP_COMMAND_SIZE)				
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
}

//------------------------------------------------------------------------------
uint8_t TP_SendEvent (TP_Outgoing_Event_ST* event)
{
	
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
			
			SendHeader (&header);
			uart_putc(1);		// version
			uart_putc(0);		// sub-version
			uart_putc(16);		// clock-speed
		break;	
				
		case TP_COMMAND_GET_CAMERA_STATUS:
			header.length = 1;
			header.type = TP_DATA_CAMERA_STATUS;
			SendHeader (&header);
			uart_putc (CameraControl_CameraConnected(&DigitalCamera_SI_Interface));			
		break;
		
		case TP_COMMAND_GET_CAMERA_INFO:
			// if not connected
			if (CameraControl_CameraConnected(&DigitalCamera_SI_Interface)==0)
			{
				header.length = 0;
				header.type = TP_DATA_CAMERA_INFO;
				SendHeader (&header);
			}
			else
			{
				CameraControl_DeviceInfo_Bin ( DigitalCamera_SI_Interface, command->header.transID );
			}
			
		break;
		
		case TP_COMMAND_GET_STORAGE_IDS:	
		
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
		
		case TP_COMMAND_GET_THUMB_LIST:		
		break;
		
		case TP_COMMAND_GET_THUMB:	
		break;
		
		case TP_COMMAND_CAPTURE:			
		break;
		
		case TP_COMMAND_GET_PROP_DESC:		
			CameraControl_DeviceOperation_GetPropertyDescBin ( &DigitalCamera_SI_Interface,	command->arg1, command->header.transID );
		break;
		
		case TP_COMMAND_SET_PROP_VAL:	
		break;
		
		default:
	}
}

//------------------------------------------------------------------------------
void SendHeader(TP_Header_ST *header)
{
	char *pt = (char*)((void*)(header));
	uint8_t length = TP_HEADER_SIZE;
	
	while (length--)
	{
		uart_putc( pt++, stdout);
	}
}
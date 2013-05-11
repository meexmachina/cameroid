/*
 * TransferProtocol.h
 *
 * Created: 3/28/2012 10:56:57 PM
 *  Author: david
 */ 


#ifndef TRANSFERPROTOCOL_H_
#define TRANSFERPROTOCOL_H_

#include <avr/io.h>
#include <avr/wdt.h>
#include <avr/pgmspace.h>
#include <avr/power.h>
#include <avr/interrupt.h>
#include <stdio.h>
#include <stdarg.h>
#include "ISRUart.h"

#include "CameraControl_General.h"
#include "CameraControl_StorageInfo.h"
#include "TransferProtocolDefinitions.h"
#include "CameraControl_DeviceOperation.h"
#include "CameraControl_DeviceInfo.h"
#include "Stabily.h"


/*------------------------------------------------------------------------------
 * Function Definitions
 */
uint8_t TP_GetIncomingCommand ( void );
void TP_CollectEvents ( void );
uint8_t TP_SendEvent ( void );
void TP_RespondTo ( volatile TP_Incoming_Command_ST* command );
void TP_SendHeader(volatile TP_Header_ST *header);
uint8_t TP_PushEvent(volatile TP_Outgoing_Event_ST *event);
volatile TP_Outgoing_Event_ST* TP_TopEvent(void);
volatile TP_Outgoing_Event_ST* TP_PopEvent(void);
void TP_CheckPropertyEvents ( void );
void TP_SendDebugLog ( const char * str );
void TP_SendSyncWord ( void );



/*--------------------------------------------
 * Externals
 */
extern volatile uint8_t					g_leftDataToGet;
extern volatile uint8_t					g_EchoOnOff;
extern volatile uint32_t				g_iEventID;
extern volatile uint32_t				g_iCommandID;
extern volatile TP_Incoming_Command_ST	g_stCurrentCommand;
extern volatile uint8_t					g_iInCommandPos;
extern volatile uint8_t					g_iCurrentCheckSum;
extern volatile TP_Outgoing_Event_ST	g_stOutgoingEventQueue[TP_EVENT_QUEUE_SIZE];
extern volatile uint8_t					g_iEventQueueStart;
extern volatile uint8_t					g_iEventQueueEnd;
extern volatile uint8_t					g_iEventQueueSize;

extern volatile uint16_t				g_iPropEventVector;
extern volatile uint16_t				g_iPropDescEventVector;
extern volatile uint16_t				g_iPropEventFastMode;
extern volatile uint16_t				g_iCurrentPropEventVector;
extern volatile uint32_t				g_iCurrentPropValuesVector[16];

#endif /* TRANSFERPROTOCOL_H_ */
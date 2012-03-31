#ifndef STABILY_SHELL_H
#define STABILY_SHELL_H

#include <avr/io.h>
#include <avr/wdt.h>
#include <avr/pgmspace.h>
#include <avr/power.h>
#include <avr/interrupt.h>
#include <stdio.h>
#include <stdlib.h>

#include "CameraControl_DeviceInfo.h"
#include "CameraControl_DeviceEvents.h"
#include "CameraControl_DeviceInfo.h"
#include "CameraControl_DeviceOperation.h"
#include "CameraControl_General.h"
#include "CameraControl_StorageInfo.h"
#include "ISRUart.h"
#include "CameraControl_General.h"

#define 	COMMAND_PROMPT			"STABILY >> "
#define 	MAX_MSG_SIZE   			128
#define 	STABILY_VER				1
#define		STABILY_SUBVER			0
#define		STABILY_CLOCK_SPEED		16
#define 	RET_CODE_IDN			20
#define 	RET_CODE_STATUS			21
#define		PUT_HEADER(id, s)		{putchar((id));putchar((uint8_t)((s)&0xFF));putchar((uint8_t)(((s)>>8)&0xFF));}

/*--------------------------------------------
 * Type definitions
 */
typedef struct
{
    char *cmd;
    void (*func)(uint8_t argc, char **argv);
	char *usage;
} cmd_t;

/*--------------------------------------------
 * Function definitions
 */
void Stabily_Shell_Menu( void );
void Stabily_Shell_Parse(char *cmd);
void Stabily_ShellRX( void );

/*--------------------------------------------
 * Command handler definitions
 */
void tm_cmd_help					(uint8_t argc, char **argv);
void tm_cmd_echo_off				(uint8_t argc, char **argv);
void tm_cmd_echo_on					(uint8_t argc, char **argv);
void tm_cmd_idn						(uint8_t argc, char **argv);
void tm_cmd_idn_bin					(uint8_t argc, char **argv);
void tm_cmd_status					(uint8_t argc, char **argv);
void tm_cmd_status_bin				(uint8_t argc, char **argv);
void tm_cmd_get_dev_info			(uint8_t argc, char **argv);
//void tm_cmd_get_dev_info_bin		(uint8_t argc, char **argv);
void tm_cmd_get_storage_info		(uint8_t argc, char **argv);
//void tm_cmd_get_storage_info_bin	(uint8_t argc, char **argv);
void tm_cmd_capture					(uint8_t argc, char **argv);
void tm_cmd_prop_desc				(uint8_t argc, char **argv);
//void tm_cmd_prop_desc_bin			(uint8_t argc, char **argv);
void tm_cmd_set_quite_mode			(uint8_t argc, char **argv);
void tm_cmd_configure_bt			(uint8_t argc, char **argv);


/*------------------------------------------------------------------------------
 * Message Type Definitions
 */
#define TP_COMMAND_IDN					0x01
#define TP_COMMAND_GET_CAMERA_STATUS	0x02
#define TP_COMMAND_GET_CAMERA_INFO		0x03
#define TP_COMMAND_GET_STORAGE_IDS		0x04
#define TP_COMMAND_GET_STORAGE_INFO		0x05
#define TP_COMMAND_GET_SYSTEM_STATUS	0x06
#define TP_COMMAND_GET_OBJECT_INFO		0x07
#define TP_COMMAND_GET_OBJECT			0x08
#define TP_COMMAND_GET_THUMB			0x09
#define TP_COMMAND_CAPTURE				0x0A
#define TP_COMMAND_GET_PROP_DESC		0x0B
#define TP_COMMAND_GET_PROP_VAL			0x0C
#define TP_COMMAND_SET_PROP_VAL			0x0D

#define TP_EVENT_CAMERA_CONNECTED		0x31
#define TP_EVENT_CAMERA_DISCONNECTED	0x32
#define TP_EVENT_CAPTURE_FINISHED		0x33
#define TP_EVENT_OBJECT_WRITTEN			0x34
#define TP_EVENT_IN_SLEEP_MODE			0x35
#define TP_EVENT_WAKING_UP				0x36
#define TP_EVENT_FRAMING_ERROR			0x37

#define TP_DATA_CAMERA_INFO				0xA1
#define TP_DATA_STORAGE_IDS				0xA2
#define TP_DATA_STORAGE_INFO			0xA3
#define TP_DATA_CAMERA_STATUS			0xA4
#define TP_DATA_OBJECT_INFO				0xA5
#define TP_DATA_OBJECT					0xA6
#define TP_DATA_THUMB_LIST				0xA7
#define TP_DATA_THUMB					0xA8
#define TP_DATA_PROP_DESC				0xA9
#define TP_DATA_PROP_VAL				0xAA
#define TP_DATA_IDN						0xAB

/*------------------------------------------------------------------------------
 * Message Definitions
 */
typedef struct 
{
	uint8_t			type;		// 1 bytes
	uint16_t		length;		// 2 bytes
	uint16_t		transID;	// 2 bytes
} TP_Header_ST;					// 5 bytes
#define TP_HEADER_SIZE	5

typedef struct  
{
	TP_Header_ST	header;		// 5 bytes
	uint32_t		arg1;		// 4 bytes
	uint32_t		arg2;		// 4 bytes
	uint32_t		arg3;		// 4 bytes
	uint8_t			checksum;	// 1 bytes
} TP_Incoming_Command_ST;		// 18 bytes
#define TP_COMMAND_SIZE	(TP_HEADER_SIZE+13)

typedef struct  
{
	TP_Header_ST	header;		// 5 bytes
	uint32_t		arg1;		// 4 bytes
} TP_Outgoing_Event_ST;			// 9 bytes
#define TP_EVENT_SIZE	(TP_HEADER_SIZE+4)

#define TP_EVENT_QUEUE_SIZE		5

/*------------------------------------------------------------------------------
 * Function Definitions
 */
uint8_t TP_GetIncomingCommand ( void );
uint8_t TP_SendEvent ( void );
void TP_RespondTo ( volatile TP_Incoming_Command_ST* command );
void TP_SendHeader(volatile TP_Header_ST *header);
uint8_t TP_PushEvent(volatile TP_Outgoing_Event_ST *event);
volatile TP_Outgoing_Event_ST* TP_TopEvent(void);
volatile TP_Outgoing_Event_ST* TP_PopEvent(void);



/*--------------------------------------------
 * Externals
 */
extern volatile uint8_t					msg[MAX_MSG_SIZE];
extern volatile uint8_t					last_msg[MAX_MSG_SIZE];
extern volatile uint8_t					*msg_ptr;
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

#endif // STABILY_SHELL_H
